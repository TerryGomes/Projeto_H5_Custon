package l2f.gameserver.vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.BatchStatement;
import l2f.gameserver.utils.Log;

public class RuVotesHolder
{
	private static final Logger LOG = LoggerFactory.getLogger(RuVotesHolder.class);
	private static final String LAST_RU_WIPE_VAR_NAME = "LastRUVotesWipeDate";
	private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("[0-9A-Za-z=.*\\]\\[]{1,16}");

	private final Map<RuVoteProtectionType, List<String>> votes = new EnumMap<RuVoteProtectionType, List<String>>(RuVoteProtectionType.class);

	private RuVotesHolder()
	{
		if (!Config.ENABLE_RU_VOTE_SYSTEM)
		{
			return;
		}

		for (RuVoteProtectionType type : RuVoteProtectionType.values())
		{
			votes.put(type, new ArrayList<String>());
		}
		loadVotes();
		checkForMissedWipe();
		ThreadPoolManager.getInstance().schedule(new RuVotesCleaner(), Config.RU_VOTE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis());
	}

	private void loadVotes()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM ru_votes"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final RuVoteProtectionType type = RuVoteProtectionType.valueOf(rset.getString("value_type"));
				votes.get(type).add(rset.getString("value"));
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Ru Votes!", e);
		}
	}

	private void checkForMissedWipe()
	{
		final long lastWipe = ServerVariables.getLong(LAST_RU_WIPE_VAR_NAME, 0L);
		final long nextWipe = Config.RU_VOTE_PATTERN.next(System.currentTimeMillis());
		if (nextWipe - lastWipe > TimeUnit.HOURS.toMillis(24L))
		{
			Log.logRuVotes("Wiping Votes. Last Wipe Date: " + lastWipe + " Next Wipe Date: " + nextWipe);
			wipeVotes();
		}
	}

	public List<RuVote> getJustCorrectVotes(List<RuVote> newVotes)
	{
		final List<RuVote> finalVotes = new ArrayList<RuVote>(newVotes.size());
		for (RuVote vote : newVotes)
		{
			if (isVoteDataCorrect(vote, finalVotes))
			{
				finalVotes.add(vote);
			}
		}
		return finalVotes;
	}

	private boolean isVoteDataCorrect(RuVote vote, List<RuVote> finalVotes)
	{
		for (RuVoteProtectionType type : RuVoteProtectionType.values())
		{
			String value = "";
			switch (type)
			{
			case IP:
			{
				value = vote.getIp();
				break;
			}
			case PLAYER_NAME:
			{
				value = vote.getNickname();
				if (!RuVotesHolder.ALLOWED_CHARS_PATTERN.matcher(value).matches())
				{
					Log.logRuVotes("Player " + vote.getNickname() + " Couldn't get rewarded because of Bad Nickname" + value);
					return false;
				}
				break;
			}
			case HWID:
			{
				final Player loggedInPlayer = GameObjectsStorage.getPlayer(vote.getNickname());
				if (loggedInPlayer != null && loggedInPlayer.isOnline() && !loggedInPlayer.getHWID().equals("<not connected>"))
				{
					vote.setHwid(loggedInPlayer.getHWID());
					break;
				}
				vote.setHwid(CharacterDAO.getLastHWIDByName(vote.getNickname()));
				break;
			}
			}
			if (value != null && !value.isEmpty())
			{
				if (vote.getVoteType() != 2 && (votes.get(type).contains(value) || checkAlreadyVoted(finalVotes, vote, type)))
				{
					Log.logRuVotes("Player " + vote.getNickname() + " Couldn't get rewarded because of " + value);
					return false;
				}
				if (value.length() > 45)
				{
					Log.logRuVotes("Vote Protection Type " + type.toString() + " with Value " + value + " is too Large!");
					return false;
				}
			}
		}
		return true;
	}

	private static boolean checkAlreadyVoted(List<RuVote> votes, RuVote newVote, RuVoteProtectionType type)
	{
		if (!type.isEnabled())
		{
			return false;
		}
		final String value = newVote.getValue(type);
		for (RuVote oldVote : votes)
		{
			if (oldVote.getValue(type).equals(value))
			{
				return true;
			}
		}
		return false;
	}

	public void saveNewVotes(List<RuVote> correctVotes)
	{
		for (RuVote vote : correctVotes)
		{
			if (vote.getVoteType() != 2)
			{
				for (RuVoteProtectionType type : RuVoteProtectionType.values())
				{
					final String value = vote.getValue(type);
					if (!value.isEmpty())
					{
						votes.get(type).add(vote.getValue(type));
					}
				}
			}
		}
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "INSERT INTO ru_votes VALUES (?,?)"))
		{
			for (RuVote vote2 : correctVotes)
			{
				for (RuVoteProtectionType type2 : RuVoteProtectionType.values())
				{
					statement.setString(1, type2.toString());
					statement.setString(2, vote2.getValue(type2));
					statement.addBatch();
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while trying to Save RU Votes to Database!", e);
		}
	}

	public boolean checkHavePenalty(Player player, boolean log)
	{
		for (RuVoteProtectionType type : RuVoteProtectionType.values())
		{
			if (type.isEnabled())
			{
				final List<String> votesFromType = votes.get(type);
				String value = "";
				switch (type)
				{
				case IP:
				{
					value = player.getIP();
					break;
				}
				case PLAYER_NAME:
				{
					value = player.getName().toLowerCase();
					break;
				}
				case HWID:
				{
					value = player.getHWID();
					break;
				}
				}
				if (votesFromType.contains(value))
				{
					if (log)
					{
						Log.logRuVotes("Player " + player.toString() + " have got " + type.toString() + " Penalty. Value: " + value);
					}
					return true;
				}
			}
		}
		return false;
	}

	public void wipeVotes()
	{
		LOG.info("Wiping RU Votes!");
		Log.logRuVotes("Wiping RU Votes!");
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM ru_votes"))
		{
			statement.execute();
		}
		catch (SQLException e)
		{
			LOG.error("Error while wiping RU Votes!", e);
		}
		ServerVariables.set(LAST_RU_WIPE_VAR_NAME, System.currentTimeMillis());
		for (RuVoteProtectionType type : RuVoteProtectionType.values())
		{
			votes.get(type).clear();
		}
	}

	private static class RuVotesCleaner extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			RuVotesHolder.getInstance().wipeVotes();
			ThreadPoolManager.getInstance().schedule(new RuVotesCleaner(), Config.RU_VOTE_PATTERN.next(System.currentTimeMillis()));
		}
	}

	public static RuVotesHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final RuVotesHolder instance = new RuVotesHolder();
	}
}

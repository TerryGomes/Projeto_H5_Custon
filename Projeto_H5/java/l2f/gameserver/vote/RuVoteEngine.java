package l2f.gameserver.vote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.dao.ItemsDAO;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.BatchStatement;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Log;

public class RuVoteEngine extends RunnableImpl
{
	private static final Logger LOG = LoggerFactory.getLogger(RuVoteEngine.class);
	private static final String LAST_REWARDED_VOTE_ID = "LastRuVoteId";

	protected static final int SMS_VOTE_ID = 2;

	public static void startThread()
	{
		if (!Config.ENABLE_RU_VOTE_SYSTEM)
		{
			return;
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RuVoteEngine(), Config.RU_VOTE_THREAD_DELAY, Config.RU_VOTE_THREAD_DELAY);
		RuVotesHolder.getInstance();
		RuVoteAnnounceTask.getInstance();
	}

	@Override
	public void runImpl()
	{
		if (!Config.ENABLE_RU_VOTE_SYSTEM)
		{
			return;
		}

		final List<RuVote> newVotes = getNewVotes();
		if (newVotes == null)
		{
			return;
		}
		final List<RuVote> correctVotes = RuVotesHolder.getInstance().getJustCorrectVotes(newVotes);
		RuVotesHolder.getInstance().saveNewVotes(correctVotes);
		rewardNewVotes(correctVotes);
	}

	private static List<RuVote> getNewVotes()
	{
		final List<RuVote> newVotes = new ArrayList<RuVote>();
		long lastVoteId = ServerVariables.getLong(LAST_REWARDED_VOTE_ID, 0L);
		boolean siteIsEmpty = true;
		try
		{
			final URL url = new URL(Config.RU_VOTE_LINK);
			try (InputStreamReader isr = new InputStreamReader(url.openStream()); final BufferedReader br = new BufferedReader(isr))
			{
				String strLine;
				while ((strLine = br.readLine()) != null)
				{
					final String[] block = strLine.split("\t");
					if (block.length == 5)
					{
						final long voteId = Long.parseLong(block[0]);
						if (voteId > lastVoteId)
						{
							final String ip = block[2];
							final String nick = block[3].toLowerCase();
							final int voteType = Integer.parseInt(block[4]);
							newVotes.add(new RuVote(nick, ip, voteType));
							lastVoteId = voteId;
						}
					}
					siteIsEmpty = false;
				}
			}
		}
		catch (MalformedURLException e)
		{
			_log.error("MalformedURLException while reading Russian Votes:", e);
			return null;
		}
		catch (IOException e2)
		{
			_log.error("IOException while reading Russian Votes:", e2);
			return null;
		}
		if (siteIsEmpty && newVotes.isEmpty())
		{
			RuVotesHolder.getInstance().wipeVotes();
		}
		ServerVariables.set(LAST_REWARDED_VOTE_ID, lastVoteId);
		RuVoteEngine.LOG.info("Loaded Ru Votes until " + lastVoteId);
		Log.logRuVotes("Loaded Ru Votes until " + lastVoteId);
		return newVotes;
	}

	private static void rewardNewVotes(List<RuVote> correctVotes)
	{
		final Map<Integer, List<VoteRewardEntry>> rewardsToDatabase = new HashMap<Integer, List<VoteRewardEntry>>();
		final Say2 successPacket = new Say2(0, ChatType.TELL, "L2Tales", getVoteSuccessMsg());
		for (RuVote vote : correctVotes)
		{
			final List<VoteRewardEntry> rewards = getRewards(vote.getVoteType());
			if (!rewards.isEmpty())
			{
				final Player player = GameObjectsStorage.getPlayer(vote.getNickname());
				if (player != null)
				{
					for (VoteRewardEntry reward : rewards)
					{
						Functions.addItem(player, reward.getTemplate().getItemId(), reward.getCount(), "Ru VoteReward");
					}
					Log.logRuVotes("Player " + player.toString() + " Got Rewarded while being Online From RU Vote.");
					player.sendPacket(successPacket);
				}
				else
				{
					final int playerObjectId = CharacterDAO.getInstance().getObjectIdByName(vote.getNickname());
					if (playerObjectId <= 0)
					{
						continue;
					}
					rewardsToDatabase.put(playerObjectId, rewards);
					Log.logRuVotes("Player Name " + vote.getNickname() + " Id: " + playerObjectId + " Got Rewarded while being OFFLINE From RU Vote.");
				}
			}
		}
		addRewardsToDatabase(rewardsToDatabase);
		RuVoteAnnounceTask.getInstance().sendFailMessages();
	}

	private static String getVoteSuccessMsg()
	{
		final long timeToWipe = Config.RU_VOTE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis();
		final String msg = Config.RU_VOTE_SUCCESS_MSG;
		return msg.replace("%nextVoteIn%", String.valueOf(timeToWipe / TimeUnit.HOURS.toMillis(1L)));
	}

	protected static List<VoteRewardEntry> getRewards(int voteType)
	{
		final String config = voteType == 2 ? "RuVoteSmsRewardGroup" : "RuVoteRewardGroup";
		final List<VoteRewardEntry> rewards = new ArrayList<VoteRewardEntry>();
		for (int i = 1; i < 100; ++i)
		{
			if (!ConfigHolder.getInstance().checkExists(config + i, double[][].class))
			{
				return rewards;
			}
			final double[][] rewardGroup = ConfigHolder.getMultiDoubleArray(config + i);
			if (rewardGroup.length > 0)
			{
				final VoteRewardEntry reward = giveRewardFromGroup(rewardGroup);
				if (reward != null && reward.getCount() > 0L)
				{
					rewards.add(reward);
				}
			}
		}
		return rewards;
	}

	private static VoteRewardEntry giveRewardFromGroup(double[][] rewardGroup)
	{
		for (double[] rewardItem : rewardGroup)
		{
			if (Rnd.chance(rewardItem[3]))
			{
				final ItemTemplate template = ItemHolder.getInstance().getTemplate((int) rewardItem[0]);
				final long count = Rnd.get((long) rewardItem[1], (long) rewardItem[2]);
				return new VoteRewardEntry(template, count);
			}
		}
		return null;
	}

	public static void addRewardsToDatabase(Map<Integer, List<VoteRewardEntry>> rewardsByPlayerId)
	{
		if (rewardsByPlayerId.isEmpty())
		{
			return;
		}
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT object_id FROM items WHERE item_id=? AND owner_id=? AND loc='INVENTORY'"))
		{
			for (Map.Entry<Integer, List<VoteRewardEntry>> playerReward : rewardsByPlayerId.entrySet())
			{
				for (VoteRewardEntry singleReward : playerReward.getValue())
				{
					if (singleReward.getTemplate().isStackable())
					{
						statement.setInt(1, singleReward.getTemplate().getItemId());
						statement.setInt(2, playerReward.getKey());
						try (ResultSet rset = statement.executeQuery())
						{
							if (rset.next())
							{
								singleReward.setObjectIdInDatabase(rset.getInt("object_id"));
							}
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while selecting Item Id in Database while rewarding from RU Votes:", e);
		}

		for (Map.Entry<Integer, List<VoteRewardEntry>> playerReward2 : rewardsByPlayerId.entrySet())
		{
			for (VoteRewardEntry singleReward2 : playerReward2.getValue())
			{
				if (singleReward2.getObjectIdInDatabase() <= 0)
				{
					final ItemInstance item = ItemFunctions.createItem(singleReward2.getTemplate().getItemId());
					item.setCount(singleReward2.getCount());
					item.setOwnerId(playerReward2.getKey());
					item.setLocation(ItemInstance.ItemLocation.INVENTORY);
					ItemsDAO.getInstance().save(item);
				}
			}
		}
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "UPDATE items SET count=count+? WHERE object_id=?"))
		{
			for (Map.Entry<Integer, List<VoteRewardEntry>> playerReward : rewardsByPlayerId.entrySet())
			{
				for (VoteRewardEntry singleReward : playerReward.getValue())
				{
					if (singleReward.getObjectIdInDatabase() > 0)
					{
						statement.setLong(1, singleReward.getCount());
						statement.setInt(2, singleReward.getObjectIdInDatabase());
						statement.addBatch();
						ItemsDAO.getInstance().getCache().remove(singleReward.getObjectIdInDatabase());
					}
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			_log.error("Error while adding Reward from RU Vote to Database:", e);
		}
	}

	private static class VoteRewardEntry
	{
		private final ItemTemplate template;
		private final long count;
		private int objectIdInDatabase;

		VoteRewardEntry(ItemTemplate template, long count)
		{
			super();
			this.template = template;
			this.count = count;
		}

		public ItemTemplate getTemplate()
		{
			return template;
		}

		public long getCount()
		{
			return count;
		}

		public void setObjectIdInDatabase(int objectIdInDatabase)
		{
			this.objectIdInDatabase = objectIdInDatabase;
		}

		public int getObjectIdInDatabase()
		{
			return objectIdInDatabase;
		}
	}
}

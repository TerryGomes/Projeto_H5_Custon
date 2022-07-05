package l2f.gameserver.vote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;

public class VoteMain
{
	private static boolean _hasVotedHop;
	private static boolean _hasVotedTop;

	private static final Logger _log = LoggerFactory.getLogger(VoteMain.class);

	public static void load()
	{
		_log.info("Loaded: Vote System - Individual Reward.");
		TriesResetTask.getInstance();
		MonthlyResetTask.getInstance();
	}

	protected static int getHopZoneVotes()
	{
		int votes = -1;
		URL url = null;
		URLConnection con = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		try
		{
			url = new URL(Config.VOTE_LINK_HOPZONE);
			con = url.openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.76");
			is = con.getInputStream();
			isr = new InputStreamReader(is);
			in = new BufferedReader(isr);
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("rank anonymous tooltip"))
				{
					votes = Integer.valueOf(inputLine.split(">")[2].replace("</span", "")).intValue();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return votes;
	}

	protected static int getTopZoneVotes()
	{
		int votes = -1;
		URL url = null;
		URLConnection con = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		try
		{
			url = new URL(Config.VOTE_LINK_TOPZONE);
			con = url.openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.76");
			is = con.getInputStream();
			isr = new InputStreamReader(is);
			in = new BufferedReader(isr);
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("Votes"))
				{
					String votesLine = in.readLine();

					votes = Integer.valueOf(votesLine.split(">")[5].replace("</font", "")).intValue();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return votes;
	}

	public static String hopCd(Player player)
	{
		long hopCdMs = 0L;
		long voteDelay = 43200000L;
		PreparedStatement statement = null;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT lastVoteHopzone FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				hopCdMs = rset.getLong("lastVoteHopzone");
			}
		}
		catch (Exception e)
		{
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

		Date resultdate = new Date(hopCdMs + voteDelay);
		return sdf.format(resultdate);
	}

	public static String topCd(Player player)
	{
		long topCdMs = 0L;
		long voteDelay = 43200000L;
		PreparedStatement statement = null;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT lastVoteTopzone FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				topCdMs = rset.getLong("lastVoteTopzone");
			}
		}
		catch (Exception e)
		{
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

		Date resultdate = new Date(topCdMs + voteDelay);
		return sdf.format(resultdate);
	}

	public static String whosVoting()
	{
		for (Player voter : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (voter.isVoting())
			{
				return voter.getName();
			}
		}
		return "None";
	}

	public static void hopvote(Player player)
	{
		long lastVoteHopzone = 0L;
		long voteDelay = 43200000L;
		final int firstvoteshop;

		firstvoteshop = getHopZoneVotes();

		class hopvotetask implements Runnable
		{
			private final Player p;

			public hopvotetask(Player player)
			{
				p = player;
			}

			@Override
			public void run()
			{
				if (firstvoteshop < getHopZoneVotes())
				{
					p.setIsVoting(false);
					VoteMain.setHasVotedHop(player);
					p.sendMessage("Thank you for voting for us!");
					VoteMain.updateLastVoteHopzone(p);
					VoteMain.updateVotes(p);
				}
				else
				{
					p.setIsVoting(false);
					p.sendMessage("You did not vote.Please try again.");
					VoteMain.setTries(player, VoteMain.getTries(p) - 1);
				}
			}

		}

		PreparedStatement statement = null;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT lastVoteHopzone FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				lastVoteHopzone = rset.getLong("lastVoteHopzone");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (getTries(player) <= 0)
		{
			player.sendMessage("Due to your multiple failures in voting you lost your chance to vote today");
		}
		else if (((lastVoteHopzone + voteDelay) < System.currentTimeMillis()) && (getTries(player) > 0))
		{
			for (Player j : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (j.isVoting())
				{
					player.sendMessage("Someone is already voting.Wait for your turn please!");
					return;
				}
			}

			player.setIsVoting(true);
			player.sendMessage("Go fast on the site and vote on the hopzone banner!");
			player.sendMessage("You have " + Config.SECS_TO_VOTE + " seconds.Hurry!");
			ThreadPoolManager.getInstance().schedule(new hopvotetask(player), Config.SECS_TO_VOTE * 1000);
		}
		else if ((getTries(player) <= 0) && ((lastVoteHopzone + voteDelay) < System.currentTimeMillis()))
		{
			for (Player j : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (j.isVoting())
				{
					player.sendMessage("Someone is already voting.Wait for your turn please!");
					return;
				}
			}

			player.setIsVoting(true);
			player.sendMessage("Go fast on the site and vote on the hopzone banner!");
			player.sendMessage("You have " + Config.SECS_TO_VOTE + " seconds.Hurry!");
			ThreadPoolManager.getInstance().schedule(new hopvotetask(player), Config.SECS_TO_VOTE * 1000);

		}
		else
		{
			player.sendMessage("12 hours have to pass till you are able to vote again.");
		}

	}

	public static void topvote(Player player)
	{
		long lastVoteTopzone = 0L;
		long voteDelay = 43200000L;
		final int firstvotestop;

		firstvotestop = getTopZoneVotes();

		class topvotetask implements Runnable
		{
			private final Player p;

			public topvotetask(Player player)
			{
				p = player;
			}

			@Override
			public void run()
			{
				if (firstvotestop < getTopZoneVotes())
				{
					p.setIsVoting(false);
					VoteMain.setHasVotedTop(p);
					p.sendMessage("Thank you for voting for us!");
					VoteMain.updateLastVoteTopzone(p);
					VoteMain.updateVotes(p);
				}
				else
				{
					p.setIsVoting(false);
					p.sendMessage("You did not vote.Please try again.");
					VoteMain.setTries(p, VoteMain.getTries(p) - 1);
				}
			}

		}

		PreparedStatement statement = null;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT lastVoteTopzone FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				lastVoteTopzone = rset.getLong("lastVoteTopzone");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (getTries(player) <= 0)
		{
			player.sendMessage("Due to your multiple failures in voting you lost your chance to vote today");
		}
		else if ((getTries(player) <= 0) && ((lastVoteTopzone + voteDelay) < System.currentTimeMillis()))
		{
			for (Player j : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (j.isVoting())
				{
					player.sendMessage("Someone is already voting.Wait for your turn please!");
					return;
				}
			}
			player.setIsVoting(true);
			player.sendMessage("Go fast on the site and vote on the topzone banner!");
			player.sendMessage((new StringBuilder()).append("You have ").append(Config.SECS_TO_VOTE).append(" seconds.Hurry!").toString());
			ThreadPoolManager.getInstance().schedule(new topvotetask(player), Config.SECS_TO_VOTE * 1000);
		}
		else if (((lastVoteTopzone + voteDelay) < System.currentTimeMillis()) && (getTries(player) > 0))
		{
			for (Player j : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (j.isVoting())
				{
					player.sendMessage("Someone is already voting.Wait for your turn please!");
					return;
				}
			}
			player.setIsVoting(true);
			player.sendMessage("Go fast on the site and vote on the topzone banner!");
			player.sendMessage((new StringBuilder()).append("You have ").append(Config.SECS_TO_VOTE).append(" seconds.Hurry!").toString());
			ThreadPoolManager.getInstance().schedule(new topvotetask(player), Config.SECS_TO_VOTE * 1000);
		}
		else
		{
			player.sendMessage("12 hours have to pass till you are able to vote again.");
		}

	}

	public static boolean hasVotedHop(Player player)
	{
		int hasVotedHop = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT hasVotedHop FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				hasVotedHop = rset.getInt("hasVotedHop");
			}

			if (hasVotedHop == 1)
			{
				setHasVotedHop(true);
				return true;
			}
			if (hasVotedHop == 0)
			{
				setHasVotedHop(false);
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasVotedTop(Player player)
	{
		int hasVotedTop = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT hasVotedTop FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				hasVotedTop = rset.getInt("hasVotedTop");
			}

			if (hasVotedTop == 1)
			{
				setHasVotedTop(true);
				return true;
			}
			if (hasVotedTop == 0)
			{
				setHasVotedTop(false);
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void updateVotes(Player activeChar)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET monthVotes=?, totalVotes=? WHERE obj_Id=?");

			statement.setInt(1, getMonthVotes(activeChar) + 1);
			statement.setInt(2, getTotalVotes(activeChar) + 1);
			statement.setInt(3, activeChar.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setHasVotedHop(Player activeChar)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasVotedHop=? WHERE obj_Id=?");

			statement.setInt(1, 1);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setHasVotedTop(Player activeChar)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasVotedTop=? WHERE obj_Id=?");

			statement.setInt(1, 1);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setHasNotVotedHop(Player activeChar)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasVotedHop=? WHERE obj_Id=?");

			statement.setInt(1, 0);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setHasNotVotedTop(Player activeChar)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hasVotedTop=? WHERE obj_Id=?");

			statement.setInt(1, 0);
			statement.setInt(2, activeChar.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int getTries(Player player)
	{
		int tries = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT tries FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());
			for (ResultSet rset = statement.executeQuery(); rset.next();)
			{
				tries = rset.getInt("tries");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tries;
	}

	public static void setTries(Player player, int tries)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET tries=? WHERE obj_Id=?");

			statement.setInt(1, tries);
			statement.setInt(2, player.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int getMonthVotes(Player player)
	{
		int monthVotes = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT monthVotes FROM characters WHERE obj_Id=?");

			statement.setInt(1, player.getObjectId());
			for (ResultSet rset = statement.executeQuery(); rset.next();)
			{
				monthVotes = rset.getInt("monthVotes");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return monthVotes;
	}

	public static int getTotalVotes(Player player)
	{
		int totalVotes = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT totalVotes FROM characters WHERE obj_Id=?");

			statement.setInt(1, player.getObjectId());
			for (ResultSet rset = statement.executeQuery(); rset.next();)
			{
				totalVotes = rset.getInt("totalVotes");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return totalVotes;
	}

	public static int getBigTotalVotes(Player player)
	{
		int bigTotalVotes = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT SUM(totalVotes) FROM characters");

			for (ResultSet rset = statement.executeQuery(); rset.next();)
			{
				bigTotalVotes = rset.getInt("SUM(totalVotes)");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bigTotalVotes;
	}

	public static int getBigMonthVotes(Player player)
	{
		int bigMonthVotes = -1;
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT SUM(monthVotes) FROM characters");

			for (ResultSet rset = statement.executeQuery(); rset.next();)
			{
				bigMonthVotes = rset.getInt("SUM(monthVotes)");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bigMonthVotes;
	}

	public static void updateLastVoteHopzone(Player player)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET lastVoteHopzone=? WHERE obj_Id=?");
			statement.setLong(1, System.currentTimeMillis());
			statement.setInt(2, player.getObjectId());
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void updateLastVoteTopzone(Player player)
	{
		try
		{
			Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET lastVoteTopzone=? WHERE obj_Id=?");
			statement.setLong(1, System.currentTimeMillis());
			statement.setInt(2, player.getObjectId());
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static boolean hasVotedHop()
	{
		return _hasVotedHop;
	}

	public static void setHasVotedHop(boolean hasVotedHop)
	{
		_hasVotedHop = hasVotedHop;
	}

	public static boolean hasVotedTop()
	{
		return _hasVotedTop;
	}

	public static void setHasVotedTop(boolean hasVotedTop)
	{
		_hasVotedTop = hasVotedTop;
	}
}
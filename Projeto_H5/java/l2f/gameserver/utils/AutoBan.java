package l2f.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;

public final class AutoBan
{
	private static final Logger _log = LoggerFactory.getLogger(AutoBan.class);// TODO Player quickVar <chatBan, endDate>
	// TODO tylko jeden thread do Jail
	private final List<Punishment> punishmentList;

	private AutoBan()
	{
		punishmentList = loadPunishments();
	}

	public enum PunishType
	{
		BAN_ACCOUNT, BAN_CHAR, BAN_HWID, BAN_IP, CHAT_BAN, JAIL
	}

	private static class Punishment
	{
		private final PunishType type;
		private final String value;
		private final String charNames;
		private final long endDate;
		private final String reason;
		private final String gm;

		private Punishment(PunishType type, String value, String charNames, long endDate, String reason, String gm)
		{
			this.type = type;
			this.value = value;
			this.charNames = charNames;
			this.endDate = endDate;
			this.reason = reason;
			this.gm = gm;
		}

		private PunishType getType()
		{
			return type;
		}

		public String getValue()
		{
			return value;
		}

		public String getCharNames()
		{
			return charNames;
		}

		public long getEndDate()
		{
			return endDate;
		}

		public String getReason()
		{
			return reason;
		}

		public String getGM()
		{
			return gm;
		}
	}

	private static List<Punishment> loadPunishments()// TODO on shutdown, delete all and insert one more time
	{
		List<Punishment> tempList = new ArrayList<>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM punishments"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				PunishType type = PunishType.valueOf(rset.getString("banType"));
				String value = rset.getString("value");
				String charNames = rset.getString("charNames");
				long endDate = rset.getLong("endDate") * 1000L;// TODO make const
				String reason = rset.getString("reason");
				String gm = rset.getString("GM");
				Punishment punishment = new Punishment(type, value, charNames, endDate, reason, gm);
				tempList.add(punishment);
			}
		}
		catch (IllegalArgumentException e)
		{
			_log.warn("IllegalArgumentException in Loading AutoBan: ", e);
		}
		catch (SQLException e)
		{
			_log.warn("SQLException in Loading AutoBan: ", e);
		}

		return new CopyOnWriteArrayList<>(tempList);
	}

	public void savePunishments()// TODO call
	{
		String insertQuery = prepareSavePunishmentsQuery();
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM punishments"))
		{
			statement.executeUpdate();
			if (!insertQuery.isEmpty())
			{
				try (PreparedStatement insertStatement = con.prepareStatement(insertQuery))
				{
					insertStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("SQLException in Saving AutoBan: ", e);
		}
	}

	private String prepareSavePunishmentsQuery()
	{
		StringBuilder builder = new StringBuilder();
		if (!punishmentList.isEmpty())
		{
			builder.append("INSERT INTO punishments VALUES ");
			int index = 0;
			for (Punishment punishment : punishmentList)
			{
				if (index > 0)
				{
					builder.append(',');
				}
				builder.append('(');
				builder.append('\'').append(punishment.getType().name()).append("\',");
				builder.append('\'').append(punishment.getValue()).append("\',");
				builder.append('\'').append(punishment.getCharNames()).append("\',");
				builder.append(punishment.getEndDate() / 1000L).append(',');
				builder.append('\'').append(punishment.getReason()).append("\',");
				builder.append('\'').append(punishment.getGM()).append('\'');
				builder.append(')');
				index++;
			}
		}
		return builder.toString();
	}

	// public void punishPlayer()
	/*
	 * BAN_ACCOUNT,
	 * BAN_CHAR,
	 * BAN_HWID,
	 * BAN_IP,
	 * CHAT_BAN,
	 * JAIL
	 */
	public boolean checkIsCharBanned(int charObjectId)
	{
		for (Punishment punish : punishmentList)
		{
			if (punish.getType() == PunishType.BAN_CHAR && Integer.parseInt(punish.getValue()) == charObjectId)
			{
				if (punish.getEndDate() >= System.currentTimeMillis())
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkIsAccountBanned(String accountName)
	{
		for (Punishment punish : punishmentList)
		{
			if (punish.getType() == PunishType.BAN_ACCOUNT && punish.getValue().equals(accountName))
			{
				if (punish.getEndDate() >= System.currentTimeMillis())
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkIsIPBanned(String ip)
	{
		for (Punishment punish : punishmentList)
		{
			if (punish.getType() == PunishType.BAN_IP && punish.getValue().equals(ip))
			{
				if (punish.getEndDate() >= System.currentTimeMillis())
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkIsHWIDBanned(String hwid)
	{
		for (Punishment punish : punishmentList)
		{
			if (punish.getType() == PunishType.BAN_HWID && punish.getValue().equals(hwid))
			{
				if (punish.getEndDate() >= System.currentTimeMillis())
				{
					return true;
				}
			}
		}
		return false;
	}

	// TODO chat ban + jail

	public static boolean checkIsBanned(int objectId)
	{
		boolean res = false;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT MAX(endban) AS endban FROM bans WHERE obj_Id=? AND endban IS NOT NULL");
			statement.setInt(1, objectId);
			rset = statement.executeQuery();

			if (rset.next())
			{
				Long endban = rset.getLong("endban") * 1000L;
				res = endban > System.currentTimeMillis();
			}
		}
		catch (Exception e)
		{
			_log.warn("Could not restore ban data: " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return res;
	}

	public static void unBaned(int objId)
	{
		Connection con = null;
		PreparedStatement pr = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			pr = con.prepareStatement("DELETE FROM `bans` WHERE `obj_id` = ?");
			pr.setInt(1, objId);
			pr.execute();
		}
		catch (Exception localException)
		{
			_log.warn("Could not restore ban data: " + localException);
		}
		finally
		{
			DbUtils.closeQuietly(con, pr);
		}
	}

	public static void Banned(Player actor, int period, String msg, String GM)
	{
		int endban = 0;
		if (period == -1)
		{
			endban = Integer.MAX_VALUE;
		}
		else if (period > 0)
		{
			Calendar end = Calendar.getInstance();
			end.add(Calendar.DAY_OF_MONTH, period);
			endban = (int) (end.getTimeInMillis() / 1000);
		}
		else
		{
			_log.warn("Negative ban period: " + period);
			return;
		}

		String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
		String enddate = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(endban * 1000L));
		if (endban * 1000L <= Calendar.getInstance().getTimeInMillis())
		{
			_log.warn("Negative ban period | From " + date + " to " + enddate);
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, actor.getAccountName());
			statement.setInt(2, actor.getObjectId());
			statement.setString(3, date);
			statement.setString(4, enddate);
			statement.setString(5, msg);
			statement.setString(6, GM);
			statement.setLong(7, endban);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warn("could not store bans data:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	// offline
	public static boolean Banned(String actor, int acc_level, int period, String msg, String GM)
	{
		boolean res;
		int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
		res = obj_id > 0;
		if (!res)
		{
			return false;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE obj_Id=?");
			statement.setInt(1, acc_level);
			statement.setInt(2, obj_id);
			statement.executeUpdate();
			DbUtils.close(statement);

			if (acc_level < 0)
			{
				int endban = 0;
				if (period == -1)
				{
					endban = Integer.MAX_VALUE;
				}
				else if (period > 0)
				{
					Calendar end = Calendar.getInstance();
					end.add(Calendar.DAY_OF_MONTH, period);
					endban = (int) (end.getTimeInMillis() / 1000);
				}
				else
				{
					_log.warn("Negative ban period: " + period);
					return false;
				}

				String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
				String enddate = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date(endban * 1000L));
				if (endban * 1000L <= Calendar.getInstance().getTimeInMillis())
				{
					_log.warn("Negative ban period | From " + date + " to " + enddate);
					return false;
				}

				statement = con.prepareStatement("INSERT INTO bans (obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?)");
				statement.setInt(1, obj_id);
				statement.setString(2, date);
				statement.setString(3, enddate);
				statement.setString(4, msg);
				statement.setString(5, GM);
				statement.setLong(6, endban);
				statement.execute();
			}
			else
			{
				statement = con.prepareStatement("DELETE FROM bans WHERE obj_id=?");
				statement.setInt(1, obj_id);
				statement.execute();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.warn("could not store bans data:" + e);
			res = false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		return res;
	}

	public static void Karma(Player actor, int karma, String msg, String GM)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			String date = new SimpleDateFormat("yy.MM.dd H:mm:ss").format(new Date());
			msg = "Add karma(" + karma + ") " + msg;
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, reason, GM) VALUES(?,?,?,?,?)");
			statement.setString(1, actor.getAccountName());
			statement.setInt(2, actor.getObjectId());
			statement.setString(3, date);
			statement.setString(4, msg);
			statement.setString(5, GM);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warn("could not store bans data:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static void Banned(Player actor, int period, String msg)
	{
		Banned(actor, period, msg, "AutoBan");
	}

	public static boolean ChatBan(String actor, int period, String msg, String GM)
	{
		boolean res = true;
		long NoChannel = period * 60000;
		int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
		if (obj_id == 0)
		{
			return false;
		}
		Player plyr = World.getPlayer(actor);

		Connection con = null;
		PreparedStatement statement = null;
		if (plyr != null)
		{

			plyr.sendMessage(new CustomMessage("l2f.Util.AutoBan.ChatBan", plyr).addString(GM).addNumber(period));
			plyr.updateNoChannel(NoChannel);
		}
		else
		{
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
				statement.setLong(1, NoChannel > 0 ? NoChannel / 1000 : NoChannel);
				statement.setInt(2, obj_id);
				statement.executeUpdate();
			}
			catch (Exception e)
			{
				res = false;
				_log.warn("Could not activate nochannel:" + e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}

		return res;
	}

	public static boolean ChatUnBan(String actor, String GM)
	{
		boolean res = true;
		Player plyr = World.getPlayer(actor);
		int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
		if (obj_id == 0)
		{
			return false;
		}

		Connection con = null;
		PreparedStatement statement = null;
		if (plyr != null)
		{
			plyr.sendMessage(new CustomMessage("l2f.Util.AutoBan.ChatUnBan", plyr).addString(GM));
			plyr.updateNoChannel(0);
		}
		else
		{
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
				statement.setLong(1, 0);
				statement.setInt(2, obj_id);
				statement.executeUpdate();
			}
			catch (Exception e)
			{
				res = false;
				_log.warn("Could not activate nochannel:" + e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}

		return res;
	}

	public static boolean RemoveFromJail(String actor, String GM)
	{
		// Also remove chat ban of the player
		ChatUnBan(actor, GM);

		Player player = World.getPlayer(actor);
		if (player != null) // чар в мире
		{
			if (player.getVar("jailed") == null)
			{
				_log.warn("Trying to unjail a non-jailed character.");
				return false;
			}

			String[] re = player.getVar("jailedFrom").split(";");

			player.stopUnjailTask();
			player.unsetVar("jailedFrom");
			player.unsetVar("jailed");
			player.unblock();
			player.standUp();
			player.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);
			player.teleToLocation(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));

			return true;
		}
		else
		{
			int objId = CharacterDAO.getInstance().getObjectIdByName(actor);
			if (objId == 0)
			{
				_log.warn("Char not found");
				return false;
			}

			String jailed = CharacterDAO.getInstance().getUserVar(objId, "jailed");
			if (jailed == null)
			{
				_log.warn("Trying to unjail a non-jailed character.");
				return false;
			}

			CharacterDAO.getInstance().deleteUserVar(objId, "jailed");

			String[] re = CharacterDAO.getInstance().getUserVar(objId, "jailedFrom").split(";");
			CharacterDAO.getInstance().setDbLocatio(objId, Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));

			return true;
		}
	}

	/**
	 * @param actor
	 * @param period in seconds
	 * @param msg
	 * @param GM
	 * @return
	 */
	public static boolean Jail(String actor, int period, String msg, Player GM)
	{
		if (period < 1)
		{
			period = -1;
		}

		Player player = World.getPlayer(actor);
		if (player != null) // enchantment of the world
		{
			doJailPlayer(player, period * 60 * 1000L, true);

			if (period > 0)
			{
				String per = TimeUtils.minutesToFullString(period);
				// Announcements.getInstance().announceToAll("Player " + player.getName() + " jailed for " + per);

				// pm by admin
				player.sendPacket(new Say2(0, ChatType.TELL, "Administration", "You go to jail for " + per + " , cause: " + msg));
			}
			else
			{
				// Announcements.getInstance().announceToAll("Player " + player.getName() + " jailed indefinitely");

				// pm by admin
				player.sendPacket(new Say2(0, ChatType.TELL, "Administration", "You are now jailed for indefinitely, the reason iss: " + msg));
			}

			return true;
		}
		else
		{
			int objId = CharacterDAO.getInstance().getObjectIdByName(actor);
			if (objId == 0)
			{
				if (GM != null)
				{
					GM.sendMessage("Char not found");
				}
				return false;
			}

			Location loc = CharacterDAO.getInstance().getLocation(objId);
			if (loc == null)
			{
				if (GM != null)
				{
					GM.sendMessage("Char location was not loaded");
				}
				return false;
			}

			if (period > 0)
			{
				String per = TimeUtils.minutesToFullString(period);
				// Announcements.getInstance().announceToAll("Player " + actor + " jailed for " + per);
			}
			else
			{
				// Announcements.getInstance().announceToAll("Player " + actor + " jailed indefinitely!");
			}

			mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", objId, "jailedFrom", loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";0", -1);
			mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", objId, "jailed", "1", (period <= 0 ? -1 : System.currentTimeMillis() + (period * 60000)));

			return true;
		}
	}

	public static void doJailPlayer(String playerName, long period, boolean msg)
	{
		Player player = GameObjectsStorage.getPlayer(playerName);
		if (player == null)
		{
			int objId = CharacterDAO.getInstance().getObjectIdByName(playerName);
			Location loc = CharacterDAO.getInstance().getLocation(objId);
			mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", objId, "jailedFrom", loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";0", -1);
			mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", objId, "jailed", "1", System.currentTimeMillis() + period);
		}
		else
		{
			doJailPlayer(player, period, msg);
		}
	}

	public static void doJailPlayer(Player player, long period, boolean msg)
	{
		player.setVar("jailedFrom", player.getX() + ";" + player.getY() + ';' + player.getZ() + ';' + player.getReflectionId(), -1L);
		if (period <= 0)
		{
			player.setVar("jailed", 1, -1);
		}
		else
		{
			player.setVar("jailed", 1, System.currentTimeMillis() + period);
			player.startUnjailTask(player, period, msg);
		}
		player.teleToLocation(Location.findPointToStay(player, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);

		player.sitDown(null);
		player.block();

		if (player.isInStoreMode())
		{
			TradeHelper.cancelStore(player);
		}
	}
}
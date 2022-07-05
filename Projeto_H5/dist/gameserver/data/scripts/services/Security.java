//package services;
//
//import l2f.commons.dbutils.DbUtils;
//import l2f.commons.lang.ArrayUtils;
//import l2f.gameserver.Config;
//import l2f.gameserver.database.DatabaseFactory;
//import l2f.gameserver.model.Player;
//import l2f.gameserver.network.GameClient;
//import l2f.gameserver.network.loginservercon.AuthServerCommunication;
//import l2f.gameserver.network.loginservercon.gspackets.ChangeAllowedHwid;
//import l2f.gameserver.network.loginservercon.gspackets.ChangeAllowedIp;
//import l2f.gameserver.network.serverpackets.MagicSkillUse;
//import l2f.gameserver.network.serverpackets.components.CustomMessage;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Security
//{
//	private static Map<Integer, Long> bind_ip = new HashMap<>();
//	private static Map<Integer, Long> bind_hwid = new HashMap<>();
//	private static final Logger _log = LoggerFactory.getLogger(Security.class);
//
//	public static String PIN(Player player, boolean status)
//	{
//		String result = null;
//		if (player == null)
//		{
//			return result;
//		}
//		GameClient client = player.getNetConnection();
//		CharSelectInfo csi = (CharSelectInfo) ArrayUtils.valid(client.getCharacters(), client.getSelectedIndex());
//		if (csi != null)
//		{
//			if (status)
//			{
//				result = !csi.isPasswordEnable() ? "<font color=\"FF0000\">No</font>" : "<font color=\"18FF00\">Yes</font>";
//			}
//			else
//			{
//				result = !csi.isPasswordEnable() ? "bypass _bbsbypass:auth.password.enable true" : csi.getPassword() == null ? "bypass _bbsbypass:auth.password.open" : "bypass _bbsbypass:auth.password.enable false";
//			}
//		}
//		return result;
//	}
//
//	public static String check(Player player, boolean ip_bind, boolean hwid_bind, boolean ip, boolean hwid, boolean isshared, boolean shared)
//	{
//		if (Config.DATABASE_LOGIN_URL.equals(""))
//		{
//			return "...";
//		}
//		String allow_hwid = "";
//		String allow_ip = "";
//		boolean share = false;
//		String result = "...";
//
//		Connection con = null;
//		PreparedStatement statement = null;
//		ResultSet rset = null;
//		try
//		{
//			con = DatabaseFactory.getInstance().getConnection();
//			statement = con.prepareStatement("SELECT allow_hwid, allow_ip, isShare FROM " + Config.DATABASE_LOGIN_URL + ".accounts_lock WHERE login=? LIMIT 1");
//			statement.setString(1, player.getAccountName());
//			rset = statement.executeQuery();
//			if (rset.next())
//			{
//				allow_hwid = rset.getString("allow_hwid");
//				allow_ip = rset.getString("allow_ip");
//				share = rset.getBoolean("isShare");
//			}
//		}
//		catch (SQLException e)
//		{
//			_log.warn("SQL Error: " + e);
//			_log.error("", e);
//		}
//		finally
//		{
//			DbUtils.closeQuietly(con, statement, rset);
//		}
//		boolean IP = (allow_ip.equals("")) || (allow_ip.equals("NoGuard"));
//		boolean HWID = (allow_hwid.equals("")) || (allow_hwid.equals("NoGuard"));
//		boolean canShare = (!IP) || (!HWID);
//		if (ip)
//		{
//			result = IP ? "<font color=\"FF0000\">No</font>" : "<font color=\"18FF00\">Yes</font>";
//		}
//		else if (hwid)
//		{
//			result = HWID ? "<font color=\"FF0000\">No</font>" : "<font color=\"18FF00\">Yes</font>";
//		}
//		else if (isshared)
//		{
//			result = share ? "<font color=\"18FF00\">Yes</font>" : !canShare ? "<font color=\"FF0000\">No</font>" : "<font color=\"FF0000\">No</font>";
//		}
//		else if (ip_bind)
//		{
//			result = IP ? "bypass _bbscabinet:security:lockip" : "bypass _bbscabinet:security:unlockip";
//		}
//		else if (hwid_bind)
//		{
//			result = HWID ? "bypass _bbscabinet:security:lockhwid" : "bypass _bbscabinet:security:unlockhwid";
//		}
//		else if (shared)
//		{
//			result = (canShare) && (!share) ? "bypass _bbscabinet:security:share" : "bypass _bbscabinet:security:unshare";
//		}
//		return result;
//	}
//
//	public static void lock(Player player, boolean ip, boolean hwid)
//	{
//		if (player.getNetConnection().getShareBlock())
//		{
//			player.sendMessage("You are not owner account. Action disable.");
//			return;
//		}
//		if (ip)
//		{
//			if (!Config.BBS_ALLOW_IP_LOCK)
//			{
//				player.sendMessage("This Service is turned off.");
//				return;
//			}
//			if (bind_ip.containsKey(Integer.valueOf(player.getObjectId())))
//			{
//				int time = (int) ((bind_ip.get(Integer.valueOf(player.getObjectId())).longValue() - System.currentTimeMillis()) / 1000L);
//				if (time > 0)
//				{
//					player.sendMessage("Wait " + time + " seconds for use this function.");
//					return;
//				}
//			}
//			bind_ip.put(Integer.valueOf(player.getObjectId()), Long.valueOf(System.currentTimeMillis() + 60000L));
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(player.getAccountName(), player.getIP()));
//			player.sendMessage("Access to your account is available now only from IP: " + player.getIP());
//			player.broadcastPacket(new MagicSkillUse(player, player, 5662, 1, 0, 0));
//			return;
//		}
//		if (hwid)
//		{
//			if (!Config.BBS_ALLOW_HWID_LOCK)
//			{
//				player.sendMessage("This Service is turned off.");
//				return;
//			}
//			if (bind_hwid.containsKey(Integer.valueOf(player.getObjectId())))
//			{
//				int time = (int) ((bind_hwid.get(Integer.valueOf(player.getObjectId())).longValue() - System.currentTimeMillis()) / 1000L);
//				if (time > 0)
//				{
//					player.sendMessage("Wait " + time + " seconds for use this function.");
//					return;
//				}
//			}
//			bind_hwid.put(Integer.valueOf(player.getObjectId()), Long.valueOf(System.currentTimeMillis() + 60000L));
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedHwid(player.getAccountName(), player.getNetConnection().getHWID()));
//			player.sendMessage("Access to your account is available now only from your PC");
//			player.broadcastPacket(new MagicSkillUse(player, player, 5662, 1, 1000, 0));
//			return;
//		}
//	}
//
//	public static void unlock(Player player, boolean ip, boolean hwid)
//	{
//		if (player.getNetConnection().getShareBlock())
//		{
//			player.sendMessage(new CustomMessage("account.share.action.disable"));
//			return;
//		}
//		if (ip)
//		{
//			if (!Config.BBS_ALLOW_IP_LOCK)
//			{
//				player.sendMessage("This Service is turned off.");
//				return;
//			}
//			if (bind_ip.containsKey(Integer.valueOf(player.getObjectId())))
//			{
//				int time = (int) ((bind_ip.get(Integer.valueOf(player.getObjectId())).longValue() - System.currentTimeMillis()) / 1000L);
//				if (time > 0)
//				{
//					player.sendMessage("Wait " + time + " seconds for use this function.");
//					return;
//				}
//			}
//			bind_ip.put(Integer.valueOf(player.getObjectId()), Long.valueOf(System.currentTimeMillis() + 60000L));
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(player.getAccountName(), ""));
//			player.sendMessage("Bind by IP is removed");
//			player.broadcastPacket(new MagicSkillUse(player, player, 6802, 1, 1000, 0));
//			return;
//		}
//		if (hwid)
//		{
//			if (!Config.BBS_ALLOW_HWID_LOCK)
//			{
//				player.sendMessage("This Service is turned off.");
//				return;
//			}
//			if (bind_hwid.containsKey(Integer.valueOf(player.getObjectId())))
//			{
//				int time = (int) ((bind_hwid.get(Integer.valueOf(player.getObjectId())).longValue() - System.currentTimeMillis()) / 1000L);
//				if (time > 0)
//				{
//					player.sendMessage("Wait " + time + " seconds for use this function.");
//					return;
//				}
//			}
//			bind_hwid.put(Integer.valueOf(player.getObjectId()), Long.valueOf(System.currentTimeMillis() + 60000L));
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedHwid(player.getAccountName(), ""));
//			player.sendMessage("Bind by HWID is removed");
//			player.broadcastPacket(new MagicSkillUse(player, player, 6802, 1, 1000, 0));
//			return;
//		}
//	}
//
//	public static void share(Player player, boolean share, boolean unshare)
//	{
//		GameClient client = player.getNetConnection();
//		if (client.getShareBlock())
//		{
//			player.sendMessage(new CustomMessage("account.share.action.disable"));
//			return;
//		}
//		if (!client.hasLocked())
//		{
//			player.sendMessage("Account share: First bind your account by ip or hwid and do relogin!");
//			return;
//		}
//		if (!Config.BBS_ALLOW_SHARE_ACCOUNT)
//		{
//			player.sendMessage("This Service is turned off.");
//			return;
//		}
//		if (share)
//		{
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedShare(player.getAccountName(), true));
//			player.sendMessage("Account share: ON");
//			player.broadcastPacket(new MagicSkillUse(player, player, 5662, 1, 1000, 0));
//			return;
//		}
//		if (unshare)
//		{
//			AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedShare(player.getAccountName(), false));
//			player.sendMessage("Account share: OFF");
//			player.broadcastPacket(new MagicSkillUse(player, player, 6802, 1, 1000, 0));
//			return;
//		}
//	}
//}

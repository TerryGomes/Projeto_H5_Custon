//package services;
//
//import l2mv.commons.dbutils.DbUtils;
//import l2mv.commons.util.Rnd;
//import l2mv.gameserver.Config;
//import l2mv.gameserver.data.htm.HtmCache;
//import l2mv.gameserver.data.xml.holder.PremiumHolder;
//import l2mv.gameserver.database.DatabaseFactory;
//import l2mv.gameserver.listener.actor.player.OnAnswerListener;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.base.ClassId;
//import l2mv.gameserver.model.items.ItemInstance;
//import l2mv.gameserver.model.items.PcInventory;
//import l2mv.gameserver.model.pledge.Alliance;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.model.premium.PremiumAccount;
//import l2mv.gameserver.network.serverpackets.ShowBoard;
//import l2mv.gameserver.network.serverpackets.components.CustomMessage;
//import l2mv.gameserver.scripts.Functions;
//import l2mv.gameserver.templates.item.ItemTemplate;
//import l2mv.gameserver.utils.DeclensionKey;
//import l2mv.gameserver.utils.HtmlUtils;
//import l2mv.gameserver.utils.TimeUtils;
//import l2mv.gameserver.utils.Util;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Cabinet extends Functions
//{
//	private static final Logger _log = LoggerFactory.getLogger(Cabinet.class);
//	private static String _msg;
//	private static Cabinet _instance = new Cabinet();
//
//	public static Cabinet getInstance()
//	{
//		return _instance;
//	}
//
//	public String[] getBypassCommands()
//	{
//		return new String[]
//		{
//			"_bbscabinet"
//		};
//	}
//
//	public void onBypassCommand(Player player, String bypass)
//	{
//		if (!Config.COMMUNITYBOARD_ENABLED)
//		{
//			player.sendMessage("This Service is turned off.");
//			Util.communityNextPage(player, "_bbshome");
//			return;
//		}
//		String html = "";
//		if (bypass.startsWith("_bbscabinet"))
//		{
//			String[] page = bypass.split(":");
//			if (page[1].startsWith("security"))
//			{
//				if (page[2].startsWith("lockip"))
//				{
//					Security.lock(player, true, false);
//				}
//				else if (page[2].startsWith("unlockip"))
//				{
//					Security.unlock(player, true, false);
//				}
//				else if (page[2].startsWith("lockhwid"))
//				{
//					Security.lock(player, false, true);
//				}
//				else if (page[2].startsWith("unlockhwid"))
//				{
//					Security.unlock(player, false, true);
//				}
//				else if (page[2].startsWith("share"))
//				{
//					Security.share(player, true, false);
//				}
//				else if (page[2].startsWith("unshare"))
//				{
//					Security.share(player, false, true);
//				}
//				String[] s = bypass.split(";");
//				Util.communityNextPage(player, s[1]);
//				return;
//			}
//			if ((page[1].equals("show")) || (page[1].equals("games")))
//			{
//				if ((page[1].equals("show")) && (page[2].equals("security")))
//				{
//					html = HtmCache.getInstance().getNotNull(Config.BBS_DEFAULT + "/cabinet/security.htm", player);
//					DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//
//					AccountLog data = getLogAccount(player.getAccountName());
//					for (int i = 0; i < 10; i++)
//					{
//						try
//						{
//							html = html.replace("{last_ip_" + (i + 1) + "}", ip[i]);
//							html = html.replace("{last_time_" + (i + 1) + "}", time[i].longValue() == 0L ? "..." : TIME_FORMAT.format(new Date(time[i].longValue() * 1000L)));
//						}
//						catch (NullPointerException e)
//						{
//							html = html.replace("{last_ip_" + (i + 1) + "}", "...");
//							html = html.replace("{last_time_" + (i + 1) + "}", "...");
//						}
//					}
//					html = html.replace("{bypass_ip}", Security.check(player, true, false, false, false, false, false));
//					html = html.replace("{bypass_hwid}", Security.check(player, false, true, false, false, false, false));
//					html = html.replace("{status_ip}", Security.check(player, false, false, true, false, false, false));
//					html = html.replace("{status_hwid}", Security.check(player, false, false, false, true, false, false));
//					html = html.replace("{status_share}", Security.check(player, false, false, false, false, true, false));
//					html = html.replace("{bypass_share}", Security.check(player, false, false, false, false, false, true));
//					html = html.replace("{status_pin}", Security.PIN(player, true));
//					html = html.replace("{bypass_pin}", Security.PIN(player, false));
//				}
//				else
//				{
//					html = HtmCache.getInstance().getNotNull(Config.BBS_DEFAULT + "/cabinet/" + page[2] + ".htm", player);
//				}
//				html = html.replace("<?player_name?>", String.valueOf(player.getName()));
//				html = html.replace("<?player_class?>", String.valueOf(Util.getFullClassName(player.getClassId().getId())));
//				html = html.replace("<?player_clan1?>", String.valueOf(player.getClan() != null ? player.getClan().getName() : "<font color=\"FF0000\">No</font>"));
//				html = html.replace("<?player_ally?>", String.valueOf((player.getClan() != null) && (player.getClan().getAlliance() != null) ? player.getClan().getAlliance().getAllyName() : "<font color=\"FF0000\">No</font>"));
//				html = html.replace("<?player_level?>", String.valueOf(player.getLevel()));
//				html = html.replace("<?player_pvp?>", String.valueOf(player.getPvpKills()));
//				html = html.replace("<?player_pk?>", String.valueOf(player.getPkKills()));
//				html = html.replace("<?online_time?>", TimeUtils.formatTime((int) player.getOnlineTime() / 1000));
//				html = html.replace("<?premium_img?>", String.valueOf(images(player)));
//			}
//		}
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	public int doCaptcha(boolean n1, boolean n2)
//	{
//		int captcha = 0;
//		if (n1)
//		{
//			captcha = Rnd.get(1, 499);
//		}
//		if (n2)
//		{
//			captcha = Rnd.get(1, 499);
//		}
//		return captcha;
//	}
//
//	public String images(Player player)
//	{
//		if (player.hasBonus())
//		{
//			_msg = "<img src=\"branchsys.primeitem_symbol\" width=\"14\" height=\"14\">";
//		}
//		else
//		{
//			_msg = "<img src=\"branchsys.br_freeserver_mark\" width=\"14\" height=\"14\">";
//		}
//		return _msg;
//	}
//
//	public AccountLog getLogAccount(String account)
//	{
//		AccountLog data = new AccountLog();
//		if (Config.DATABASE_LOGIN_URL.equals(""))
//		{
//			for (int i = 0; i < 10; i++)
//			{
//				ip[i] = "...";
//				time[i] = Long.valueOf(0L);
//			}
//			return data;
//		}
//		Connection con = null;
//		PreparedStatement statement = null;
//		ResultSet rset = null;
//		int number = 0;
//		try
//		{
//			con = DatabaseFactory.getInstance().getConnection();
//			statement = con.prepareStatement("SELECT * FROM " + Config.DATABASE_LOGIN_URL + ".account_log WHERE login=? ORDER BY time DESC LIMIT 0, 10;");
//			statement.setString(1, account);
//			rset = statement.executeQuery();
//			while (rset.next())
//			{
//				if (!rset.getString("login").isEmpty())
//				{
//					ip[number] = rset.getString("ip");
//					time[number] = Long.valueOf(rset.getLong("time"));
//				}
//				else
//				{
//					ip[number] = "...";
//					time[number] = Long.valueOf(0L);
//				}
//				number++;
//			}
//		}
//		catch (Exception e)
//		{
//			_log.error("", e);
//		}
//		finally
//		{
//			DbUtils.closeQuietly(con, statement, rset);
//		}
//		return data;
//	}
//
//	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
//	{
//	}
//
//	public class AccountLog
//	{
//		private String[] ip = new String[10];
//		private Long[] time = new Long[10];
//
//		public AccountLog()
//		{
//		}
//	}
//}

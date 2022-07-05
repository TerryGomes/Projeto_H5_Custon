package l2f.gameserver.instancemanager;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.worldwind.formats.dds.DDSConverter;
import javolution.util.FastMap;
import javolution.util.FastTable;
import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.PledgeCrest;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.randoms.CaptchaImage;
import l2f.gameserver.skills.AbnormalEffect;
import l2f.gameserver.tables.GmListTable;
import l2f.gameserver.utils.AutoHuntingPunish;

public class AutoHuntingManager
{
	private static final Logger _log = LoggerFactory.getLogger(AutoHuntingManager.class);
	private static AutoHuntingManager _instance;
	private static FastMap<Integer, String[]> _unread;
	// Number of reportes made over each player
	private static FastMap<Integer, FastTable<Player>> _reportedCount = new FastMap<Integer, FastTable<Player>>();
	// Reporters blocked by time
	private static FastMap<Integer, Long> _lockedReporters = new FastMap<Integer, Long>();
	// Blocked ips
	private static Set<String> _lockedIps = new HashSet<String>();
	// Blocked accounts
	private static Set<String> _lockedAccounts = new HashSet<String>();

	private AutoHuntingManager()
	{
		loadUnread();
	}

	public static AutoHuntingManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new AutoHuntingManager();
		}
		return _instance;
	}

	/**
	 * Check if the reported player is online
	 *
	 * @param player
	 * @return true if World contains that player, else returns false
	 */
	private static boolean reportedIsOnline(Player player)
	{
		return World.getPlayer(player.getObjectId()) != null;
	}

	/**
	 * Will save the report in database
	 *
	 * @param reported (the L2PcInstance who was reported)
	 * @param reporter (the L2PcInstance who reported the bot)
	 */
	public synchronized void reportBot(Player reported, Player reporter, String typeOfReport, String moreInfo)
	{
		if (!reportedIsOnline(reported) && reported.getPrivateStoreType() == Player.STORE_PRIVATE_NONE)
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message1", reporter));
			return;
		}
		if (typeOfReport.equalsIgnoreCase("FakeShop"))
		{
			if (reported.getPrivateStoreType() == Player.STORE_PRIVATE_NONE)
			{
				reporter.sendMessage("You cannot report player for Fake Shop while he is not in store mode.");
				return;
			}
			moreInfo += " - Title: " + (reported.getSellStoreName() == null ? "No title" : reported.getSellStoreName());
		}
		if (typeOfReport.equalsIgnoreCase("Bot"))
		{
			if (reported.isInZone(ZoneType.SIEGE))
			{
				reporter.sendMessage("You cannot report players that are in siege zone.");
				return;
			}
			Creature lastAttacker = reported.getLastAttacker();
			if (lastAttacker == null || !lastAttacker.isMonster() || reported.getLastAttackDate() + 5000L < System.currentTimeMillis())
			{
				if (reported.getTarget() == null || !reported.getTarget().isMonster())
				{
					reporter.sendMessage("You cannot report players, that aren't fighting with monsters.");
					return;
				}
				lastAttacker = (Creature) reported.getTarget();
			}
			if (lastAttacker.isBoss() || lastAttacker.isSiegeGuard())
			{
				reporter.sendMessage("You cannot report players, that aren't fighting with monsters!");
				return;
			}
		}
		/*
		 * if (!reported.isInCombat() && !reported.isInPeaceZone()) { reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message7", reporter));
		 * return; }
		 */
		// karma/pvp flag players can be reported for abuse, others no.
		if (reported.getPvpFlag() > 0 || reported.getKarma() > 0 && !typeOfReport.equalsIgnoreCase("Abuse"))
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message8", reporter));
			return;
		}
		_lockedReporters.put(reporter.getObjectId(), System.currentTimeMillis());
		_lockedIps.add(reporter.getIP());
		_lockedAccounts.add(reporter.getAccountName());
		final long date = Calendar.getInstance().getTimeInMillis();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			if (!_reportedCount.containsKey(reported))
			{
				final FastTable<Player> p = new FastTable<Player>();
				p.add(reported);
				_reportedCount.put(reporter.getObjectId(), p);
			}
			else
			{
				if (_reportedCount.get(reporter).contains(reported.getObjectId()))
				{
					reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message2", reporter));
					return;
				}
				_reportedCount.get(reporter).add(reported);
			}
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO `bot_report`(`reported_name`, `reported_objectId`, `reporter_name`, `reporter_objectId`, `date`, `reportType`, `info`) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, reported.getName());
			statement.setInt(2, reported.getObjectId());
			statement.setString(3, reporter.getName());
			statement.setInt(4, reporter.getObjectId());
			statement.setLong(5, date);
			statement.setString(6, typeOfReport);
			statement.setString(7, moreInfo);
			statement.executeUpdate();
			rset = statement.getGeneratedKeys();
			rset.next();
			final int maxId = rset.getInt(1);
			_unread.put(maxId, new String[]
			{
				reported.getName(),
				reporter.getName(),
				String.valueOf(date),
				typeOfReport,
				moreInfo
			});
		}
		catch (final Exception e)
		{
			_log.warn("Could not save reported bot " + reported.getName() + " by " + reporter.getName() + " at " + date + ".");
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		final String msgContent = reporter.getName() + " has reported player: " + reported.getName() + " for " + typeOfReport + " ";
		GmListTable.broadcastToGMs(new Say2(0, ChatType.HERO_VOICE, "REPORT", msgContent));
		// Send captcha test to the reported player, except if he is offline or have none private store type and reportype is not a FakeShop...
		if (reportedIsOnline(reported) && !reported.isInPeaceZone() && typeOfReport.equalsIgnoreCase("Bot"))
		{
			if (reported.getVarLong("LastCaptchaTest", 0) + Config.CAPTCHA_TIME_BETWEEN_TESTED_SECONDS * 1000L > System.currentTimeMillis())
			{
				// Ignore captcha if he was tested recently.
				GmListTable.broadcastToGMs(new Say2(0, ChatType.HERO_VOICE, "REPORT", reported.getName() + " was tested lately, ignoring CAPTCHA request."));
				return;
			}
			final String customHtm = HtmCache.getInstance().getNotNull("mods/Captcha/ShowCaptchaWindow.htm", reported);
			// Safe check for pet.
			if (reported.getPet() != null)
			{
				reported.getPet().abortAttack(true, true);
				reported.getPet().abortCast(true, true);
				reported.getPet().stopMove();
			}
			// Safe check to prevent double captcha.
			reported.abortAttack(true, true);
			reported.abortCast(true, true);
			reported.stopMove();
			// Random image file name
			final int imgId = IdFactory.getInstance().getNextId();
			try
			{
				// Increase captcha requests.
				CaptchaImage.increaseCaptchaRequests(reported);
				CaptchaImage.newCaptchaCode(reported);
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(CaptchaImage.generateCaptcha(CaptchaImage.getCaptchaCode(reported)), "png", baos);
				baos.flush();
				final ByteBuffer buffer = DDSConverter.convertToDDS(ByteBuffer.wrap(baos.toByteArray()), "lol/png");
				reported.sendPacket(new PledgeCrest(imgId, buffer.array()));
				baos.close();
				reported.startAbnormalEffect(AbnormalEffect.REAL_TARGET);
				if (!reported.isParalyzed())
				{
					reported.startParalyzed();
				}
				reported.setIsInvul(true);
				if (!reported.isBlocked())
				{
					reported.block();
				}
				CaptchaImage.starTasksPunishment(reported, true);
				CaptchaImage.starTasksMessage(reported);
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setHtml(customHtm);
				html.replace("%imgId%", "" + imgId);
				html.replace("%serverId%", "" + reported.getClient().getServerId());
				html.replace("%playerName%", "" + reported.getName());
				html.replace("%tries%", "" + CaptchaImage.getCaptchaTries(reported));
				html.replace("%punishmentType%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
				html.replace("%punishmentTime%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME);
				reported.sendPacket(html);
				reported.setVar("LastCaptchaTest", System.currentTimeMillis());
				GmListTable.broadcastToGMs(new Say2(0, ChatType.HERO_VOICE, "REPORT", reported.getName() + "  is being tested with CAPTCHA code..."));
			}
			catch (final Exception e)
			{
				_log.error("", e);
			}
		}
		final String htmlreportsuccess = HtmCache.getInstance().getNotNull("command/report-success.htm", reporter);
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(htmlreportsuccess);
		html.replace("%reported%", reported.getName());
		html.replace("%typeofreport%", typeOfReport);
		reporter.sendPacket(html);
	}

	/**
	 * Will load the data from all unreaded reports (used to load reports in a window for admins/GMs)
	 *
	 * @return a FastMap<Integer, String[]> (Integer - report id, String[] - reported name, report name, date)
	 */
	private void loadUnread()
	{
		_unread = new FastMap<Integer, String[]>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `report_id`, `reported_name`, `reporter_name`, `date`, `reportType`, `info` FROM `bot_report` WHERE `read` = ?");
			statement.setString(1, "false");
			rset = statement.executeQuery();
			while (rset.next())
			{
				// Not loading objectIds to increase performance
				// World.getInstance().getPlayer(name).getObjectId();
				final String[] data = new String[5];
				data[0] = rset.getString("reported_name");
				data[1] = rset.getString("reporter_name");
				data[2] = rset.getString("date");
				data[3] = rset.getString("reportType");
				data[4] = rset.getString("info");
				_unread.put(rset.getInt("report_id"), data);
			}
		}
		catch (final Exception e)
		{
			_log.warn("Could not load data from bot_report:\n" + e.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	/**
	 * Return a FastMap holding all the reports data to be viewed by any GM
	 *
	 * @return _unread
	 */
	public FastMap<Integer, String[]> getUnread()
	{
		return _unread;
	}

	/**
	 * Marks a reported bot as readed (from admin menu)
	 *
	 * @param id (the report id)
	 */
	public void markAsRead(int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE `bot_report` SET `read` = ? WHERE `report_id` = ?");
			statement.setString(1, "true");
			statement.setInt(2, id);
			statement.execute();
			_unread.remove(id);
			_log.info("Reported bot marked as read, id was: " + id);
		}
		catch (final Exception e)
		{
			_log.warn("Could not mark as read the reported bot: " + id + ":\n" + e.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Returns the number of times the player has been reported
	 *
	 * @param reported
	 * @return int
	 */
	public int getPlayerReportsCount(Player reported)
	{
		if (reported == null)
		{
			return 0;
		}
		int count = 0;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(*) FROM `bot_report` WHERE `reported_objectId` = ?");
			statement.setInt(1, reported.getObjectId());
			rset = statement.executeQuery();
			if (rset.next())
			{
				count = rset.getInt(1);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return count;
	}

	/**
	 * Will save the punish being suffered to player in database (at player logs out), to be restored next time players enter in server
	 *
	 * @param punished
	 */
	public void savePlayerPunish(Player punished)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE `bot_reported_punish` SET `time_left` = ? WHERE `charId` = ?");
			statement.setLong(1, punished.getPlayerPunish().getPunishTimeLeft());
			statement.setInt(2, punished.getObjectId());
			statement.execute();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Retail report restrictions (Validates the player - reporter relationship)
	 *
	 * @param reported (the reported bot)
	 * @param reporter
	 * @return
	 */
	public boolean validateBot(Player reported, Player reporter)
	{
		if (reported == null || reporter == null)
		{
			return false;
		}
		// Cannot report while reported is inside olympiad
		if (reported.isInOlympiadMode())
		{
			reporter.sendPacket(new SystemMessage2(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT));
			return false;
		}
		// Cannot report while in peace zone and there is no priveate store.
		if (reported.isInPeaceZone() && reported.getPrivateStoreType() == Player.STORE_PRIVATE_NONE)
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message3", reporter));
			return false;
		}
		/*
		 * // Cannot report if reported and reporter are in war if (reported.getClan() != null && reporter.getClan() != null) { if (reported.getClan().isAtWarWith(reporter.getClanId()))
		 * { reporter.sendPacket(new SystemMessage2(SystemMsg.CANNOT_REPORT_TARGET_IN_CLAN_WAR)); return false; } }
		 */
		// Cannot report itself
		if (reporter.getName().equalsIgnoreCase(reported.getName()))
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message4", reporter));
			return false;
		}
		if (reporter.getLevel() < Config.CAPTCHA_MIN_LEVEL)
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message6", reporter));
			return false;
		}
		// Same HWID, ip
		if (reporter.isDualbox(reported))
		{
			reporter.sendMessage(new CustomMessage("l2f.gameserver.instancemanager.autohuntingmanager.message5", reporter));
			return false;
		}
		if (reported.isInZoneBattle() || reported.isInZone(ZoneType.epic))
		{
			reporter.sendMessage("You cannot report players in this zone!");
			return false;
		}
		/*
		 * usless so disabled. // Cannot report if the reported didnt earn exp since he logged in if (!reported.hasEarnedExp()) { reporter.sendPacket(new
		 * SystemMessage2(SystemMsg.CANNOT_REPORT_CHARACTER_WITHOUT_GAINEXP)); return false; }
		 */
		// Cannot report twice or more a player
		if (_reportedCount.containsKey(reporter))
		{
			for (Player p : _reportedCount.get(reporter))
			{
				if (reported == p)
				{
					reporter.sendPacket(new SystemMessage2(SystemMsg.C1_REPORTED_AS_BOT));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Retail report restrictions (Validates the reporter state)
	 *
	 * @param reporter
	 * @return
	 */
	public synchronized boolean validateReport(Player reporter)
	{
		if (reporter == null)
		{
			return false;
		}
		if (reporter.isGM())
		{
			return true;
		}
		if (reporter.getReportedAccount() == null)
		{
			reporter.setReportedAccount(reporter.getAccountName());
		}
		// The player has a 30 mins lock before be able to report anyone again
		if (reporter.getReportedAccount().getReportsPoints() == 0)
		{
			final SystemMessage2 sm = new SystemMessage2(SystemMsg.YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT);
			sm.addNumber(0);
			sm.addNumber(0);
			reporter.sendPacket(sm);
			return false;
		}
		// 30 mins must pass before report again
		else if (_lockedReporters.containsKey(reporter.getObjectId()))
		{
			final long delay = System.currentTimeMillis() - _lockedReporters.get(reporter.getObjectId());
			if (delay <= Config.CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS * 1000)
			{
				final int left = (int) (Config.CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS * 1000 - delay) / 60000;
				final SystemMessage2 sm = new SystemMessage2(SystemMsg.YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT);
				sm.addNumber(left);
				sm.addNumber(reporter.getReportedAccount().getReportsPoints());
				reporter.sendPacket(sm);
				return false;
			}
			else
			{
				ThreadPoolManager.getInstance().execute(new ReportClear(reporter));
			}
		}
		// In those 30 mins, the ip which made the first report cannot report again
		else if (_lockedIps.contains(reporter.getIP()))
		{
			reporter.sendPacket(new SystemMessage2(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_));
			return false;
		}
		// In those 30 mins, the account which made report cannot report again
		else if (_lockedAccounts.contains(reporter.getAccountName()))
		{
			reporter.sendPacket(new SystemMessage2(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_BECAUSE_ANOTHER_CHARACTER_FROM_THIS_ACCOUNT_HAS_ALREADY_DONE_SO));
			return false;
		}
		// If any clan/ally mate has reported any bot, you cannot report till he releases his lock
		else if (reporter.getClan() != null)
		{
			for (int i : _lockedReporters.keySet())
			{
				// Same clan
				final Player p = World.getPlayer(i);
				if (p == null)
				{
					continue;
				}
				if (p.getClanId() == reporter.getClanId())
				{
					reporter.sendPacket(new SystemMessage2(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_));
					return false;
				}
				// Same ally
				else if (reporter.getClan().getAllyId() != 0)
				{
					if (p.getClan() != null && p.getClan().getAllyId() == reporter.getClan().getAllyId())
					{
						reporter.sendPacket(new SystemMessage2(SystemMsg.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_));
						return false;
					}
				}
			}
		}
		reporter.getReportedAccount().reducePoints();
		return true;
	}

	/**
	 * Will manage needed actions on enter
	 *
	 * @param activeChar
	 */
	public void onEnter(Player activeChar)
	{
		restorePlayerBotPunishment(activeChar);
		activeChar.setReportedAccount(activeChar.getAccountName());
	}

	/**
	 * Will retore the player punish on enter
	 *
	 * @param activeChar
	 */
	private void restorePlayerBotPunishment(Player activeChar)
	{
		String punish = "";
		long delay = 0;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `punish_type`, `time_left` FROM `bot_reported_punish` WHERE `charId` = ?");
			statement.setInt(1, activeChar.getObjectId());
			rset = statement.executeQuery();
			while (rset.next())
			{
				punish = rset.getString("punish_type");
				delay = rset.getLong("time_left");
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		if (!punish.isEmpty() && AutoHuntingPunish.Punish.valueOf(punish) != null)
		{
			if (delay < 0)
			{
				final AutoHuntingPunish.Punish p = AutoHuntingPunish.Punish.valueOf(punish);
				final long left = -delay / 1000 / 60;
				activeChar.setPunishDueBotting(p, (int) left);
			}
			else
			{
				activeChar.endPunishment();
			}
		}
	}

	/**
	 * Manages the reporter restriction data clean up to be able to report again
	 */
	private class ReportClear implements Runnable
	{
		private final Player _reporter;

		private ReportClear(Player reporter)
		{
			_reporter = reporter;
		}

		@Override
		public void run()
		{
			_lockedReporters.remove(_reporter.getObjectId());
			_lockedIps.remove(_reporter.getClient());
			_lockedAccounts.remove(_reporter.getAccountName());
		}
	}
}

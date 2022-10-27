package l2mv.gameserver.handler.voicecommands.impl.BotReport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.utils.TimeUtils;

/**
 * Class handling Timer which calculates if Targets solved Captcha in Time
 * It is also taking care of failedBotReporters, List of AccountNames, IPs and HWIDs of the players that reported Bot
 */
public class CaptchaTimer
{
	private final List<CaptchaEvent> captchaEventList;
	private final List<FailedBotReporter> failedBotReporters;

	protected CaptchaTimer()
	{
		captchaEventList = new CopyOnWriteArrayList<>();
		failedBotReporters = new CopyOnWriteArrayList<>();
		ThreadPoolManager.getInstance().execute(new CaptchaTimerThread());
	}

	/**
	 * Adding new Bot Reporter to the List
	 * If he already exists, just changing time of Last Report
	 * Otherwise Account Name, IP, HWID and current time are saved.
	 * @param player Player that will be added to Bot Reporters List
	 */
	public void addBotReporter(Player player)
	{
		FailedBotReporter reporter = getBotReporter(player);
		if (reporter != null)
		{
			reporter.setLastReportTime(System.currentTimeMillis());
		}
		else
		{
			failedBotReporters.add(new FailedBotReporter(player.getAccountName(), player.getIP(), player.getHWID(), System.currentTimeMillis()));
		}
	}

	/**
	 * Removing Bot Reporter from the List
	 * He will now be able to Report Players
	 * @param player Player that will be removed from Bot Reporters List
	 */
	public void removeBotReporter(Player player)
	{
		FailedBotReporter reporter = getBotReporter(player);
		if (reporter != null)
		{
			failedBotReporters.remove(reporter);
		}
	}

	/**
	 * Checking If Player can report Bot now.
	 * Account Name, IP and HWID are being Checked
	 * @param player that wants to report somebody
	 * @return can he report?
	 */
	public boolean canReportBotAgain(Player player)
	{
		FailedBotReporter reporter = getBotReporter(player);
		return reporter == null || reporter.canReportAgain();
	}

	private FailedBotReporter getBotReporter(Player player)
	{
		for (FailedBotReporter reporter : failedBotReporters)
		{
			if (reporter.isBotReporter(player))
			{
				return reporter;
			}
		}
		return null;
	}

	/**
	 * Creating new CaptchaEvent and adding it to the List
	 * CaptchaEventThread will now check, if player solved Captcha in time
	 * @param actor Player that reported Target
	 * @param target Player that was reported and is solving Captcha
	 * @param correctCaptcha Correct Captcha that <code>target</code> has to write
	 */
	public void addCaptchaTimer(Player actor, Player target, String correctCaptcha)
	{
		if (actor == null || target == null)
		{
			return;
		}

		target.block();
		target.setIsInvul(true);
		target.startAbnormalEffect(AbnormalEffect.REDCIRCLE);
		captchaEventList.add(new CaptchaEvent(actor, target, correctCaptcha, System.currentTimeMillis()));
	}

	/**
	 * Removing CaptchaEvent from the List
	 * Captcha Thread will no longer check this CaptchaEvent
	 * @param event Event that player was solving
	 */
	public void removeCaptchaTimer(CaptchaEvent event)
	{
		Player target = GameObjectsStorage.getPlayer(event.getTargetName());
		if (target != null)
		{
			target.unblock();
			target.setIsInvul(false);
			target.stopAbnormalEffect(AbnormalEffect.REDCIRCLE);
		}
		captchaEventList.remove(event);
	}

	/**
	 * Getting CaptchaEvent by Name of the Player
	 * It's checking only Names of Players that are solving Captchas, not reporters
	 * Returning NULL if CaptchEvent wasn't found
	 * @param target Player that is solving Captcha
	 * @return Captcha Event
	 */
	public CaptchaEvent getMyEvent(Player target)
	{
		for (CaptchaEvent event : captchaEventList)
		{
			if (event.getTargetName().equals(target.getName()))
			{
				return event;
			}
		}
		return null;
	}

	/**
	 * Getting Iteration of captchaEventList
	 * It gives all Captcha Events that Players are currently Solving
	 * @return captchaEventList
	 */
	protected Iterable<CaptchaEvent> getCaptchaEventList()
	{
		return captchaEventList;
	}

	protected class CaptchaTimerThread implements Runnable
	{
		private static final long CAPTCHA_TIMER_DELAY = 500L;

		@Override
		public void run()
		{
			long currentTime = System.currentTimeMillis();
			for (CaptchaEvent event : getCaptchaEventList())
			{
				if (event.getStartDate() + Config.CAPTCHA_ANSWER_SECONDS * TimeUtils.SECOND_IN_MILLIS <= currentTime)
				{
					CaptchaHandler.onFailedCaptcha(event);
				}
			}

			ThreadPoolManager.getInstance().schedule(this, CAPTCHA_TIMER_DELAY);
		}
	}

	private static class FailedBotReporter
	{
		private final String accountName;
		private final String ip;
		private final String hwid;
		private long lastReportTime;

		private FailedBotReporter(String accountName, String ip, String hwid, long lastReportTime)
		{
			this.accountName = accountName;
			this.ip = ip;
			this.hwid = hwid;
			this.lastReportTime = lastReportTime;
		}

		public void setLastReportTime(long lastReportTime)
		{
			this.lastReportTime = lastReportTime;
		}

		private boolean isBotReporter(Player player)
		{
			if (player.getAccountName().equals(accountName) || player.getIP().equals(ip) || player.getHWID().equals(hwid))
			{
				return true;
			}
			return false;
		}

		private boolean canReportAgain()
		{
			return lastReportTime + Config.CAPTCHA_TIME_BETWEEN_REPORTS_SECONDS * TimeUtils.SECOND_IN_MILLIS < System.currentTimeMillis();
		}
	}

	/**
	 * Getting the only instance of CaptchaTimer
	 * @return CaptchaTimer
	 */
	public static CaptchaTimer getInstance()
	{
		return CaptchaTimerHolder.instance;
	}

	private static class CaptchaTimerHolder
	{
		protected static final CaptchaTimer instance = new CaptchaTimer();
	}
}

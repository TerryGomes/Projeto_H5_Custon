package l2mv.gameserver.model.entity.olympiad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.network.serverpackets.SystemMessage;

public class OlympiadEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadEndTask.class);

	@Override
	public void runImpl()
	{
		if (Olympiad._inCompPeriod) // Если бои еще не закончились, откладываем окончание олимпиады на минуту
		{
			ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), 60000);
			return;
		}

		Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_ENDED).addNumber(Olympiad._currentCycle));
		Announcements.getInstance().announceToAll("Olympiad Validation Period has began");

		Olympiad._isOlympiadEnd = true;
		if (Olympiad._scheduledManagerTask != null)
		{
			Olympiad._scheduledManagerTask.cancel(false);
		}
		if (Olympiad._scheduledWeeklyTask != null)
		{
			Olympiad._scheduledWeeklyTask.cancel(false);
		}

		Olympiad._validationEnd = Olympiad._olympiadEnd + Config.ALT_OLY_VPERIOD;

		OlympiadDatabase.saveNobleData();
		Olympiad._period = 1;
		Hero.getInstance().clearHeroes();

		try
		{
			OlympiadDatabase.save();
		}
		catch (Exception e)
		{
			_log.error("Olympiad System: Failed to save Olympiad configuration!", e);
		}

		_log.info("Olympiad System: Starting Validation period. Time to end validation:" + Olympiad.getMillisToValidationEnd() / (60 * 1000));

		if (Olympiad._scheduledValdationTask != null)
		{
			Olympiad._scheduledValdationTask.cancel(false);
		}
		Olympiad._scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), Olympiad.getMillisToValidationEnd());
	}
}
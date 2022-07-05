package l2f.gameserver.model.entity.olympiad;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;

/**
 * Doing Weekly Task(giving points and new fight to all Noble Players)
 */
public class WeeklyTask implements Runnable
{
	private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

	@Override
	public void run()
	{
		Olympiad.doWeekTasks();
		_log.info("Olympiad System: Added weekly points to nobles");

		Calendar nextChange = Calendar.getInstance();
		Olympiad._nextWeeklyChange = nextChange.getTimeInMillis() + Config.ALT_OLY_WPERIOD;
	}
}
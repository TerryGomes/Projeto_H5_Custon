package l2mv.gameserver.model.entity.olympiad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.network.serverpackets.SystemMessage;

class CompEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(CompEndTask.class);

	@Override
	public void runImpl()
	{
		if (Olympiad.isOlympiadEnd())
		{
			return;
		}

		Olympiad._inCompPeriod = false;

		try
		{
			OlympiadManager manager = Olympiad._manager;

			// Если остались игры, ждем их завершения еще одну минуту
			if (manager != null && !manager.getOlympiadGames().isEmpty())
			{
				ThreadPoolManager.getInstance().schedule(new CompEndTask(), 60000);
				return;
			}

			Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_ENDED));
			_log.info("Olympiad System: Olympiad Game Ended");
			OlympiadDatabase.save();
		}
		catch (Exception e)
		{
			_log.warn("Olympiad System: Failed to save Olympiad configuration", e);
		}
		Olympiad.init();
	}
}
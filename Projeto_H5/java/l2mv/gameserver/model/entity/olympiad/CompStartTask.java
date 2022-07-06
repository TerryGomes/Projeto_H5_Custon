package l2mv.gameserver.model.entity.olympiad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.network.serverpackets.SystemMessage;

class CompStartTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(CompStartTask.class);

	@Override
	public void runImpl()
	{
		if (Olympiad.isOlympiadEnd())
		{
			return;
		}

		Olympiad._manager = new OlympiadManager();
		Olympiad._inCompPeriod = true;

		new Thread(Olympiad._manager).start();

		ThreadPoolManager.getInstance().schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

		Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_STARTED));
		_log.info("Olympiad System: Olympiad Game Started");
	}
}
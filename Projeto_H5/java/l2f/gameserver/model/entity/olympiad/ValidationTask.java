package l2f.gameserver.model.entity.olympiad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.instancemanager.OlympiadHistoryManager;
import l2f.gameserver.model.entity.Hero;

public class ValidationTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(ValidationTask.class);

	@Override
	public void runImpl()
	{
		OlympiadHistoryManager.getInstance().switchData();

		OlympiadDatabase.sortHerosToBe();
		OlympiadDatabase.saveNobleData();
		if (!Hero.getInstance().computeNewHeroes(Olympiad._heroesToBe))
		{
			_log.warn("Olympiad: Error while computing new heroes!");
			// Announcements.getInstance().announceToAll("Olympiad Validation Period has ended"); //TODO [VISTALL] что за хренЬ?
		}

		Olympiad._period = 0;
		Olympiad._currentCycle++;

		OlympiadDatabase.cleanupNobles();
		OlympiadDatabase.loadNoblesRank();
		OlympiadDatabase.setNewOlympiadEnd();

		Olympiad.init();
		OlympiadDatabase.save();
	}
}
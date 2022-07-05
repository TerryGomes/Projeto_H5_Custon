package l2f.gameserver.taskmanager.actionrunner.tasks;

import l2f.gameserver.model.entity.olympiad.OlympiadDatabase;

public class OlympiadSaveTask extends AutomaticTask
{
	public OlympiadSaveTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		OlympiadDatabase.save();
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return System.currentTimeMillis() + 600000L;
	}
}

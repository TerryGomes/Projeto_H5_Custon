package l2f.gameserver.taskmanager.actionrunner.tasks;

import l2f.gameserver.taskmanager.actionrunner.ActionRunner;
import l2f.gameserver.taskmanager.actionrunner.ActionWrapper;

public abstract class AutomaticTask extends ActionWrapper
{
	public static final String TASKS = "automatic_tasks";

	public AutomaticTask()
	{
		super(TASKS);
	}

	public abstract void doTask() throws Exception;

	public abstract long reCalcTime(boolean start);

	@Override
	public void runImpl0() throws Exception
	{
		try
		{
			doTask();
		}
		finally
		{
			ActionRunner.getInstance().register(reCalcTime(false), this);
		}
	}
}

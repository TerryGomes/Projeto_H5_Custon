package l2f.gameserver.taskmanager;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.threading.SteppingRunnableQueueManager;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;

public class AiTaskManager extends SteppingRunnableQueueManager
{
	private final static long TICK = 250L;

	private final static AiTaskManager[] _instances = new AiTaskManager[Config.AI_TASK_MANAGER_COUNT];
	static
	{
		for (int i = 0; i < _instances.length; i++)
		{
			_instances[i] = new AiTaskManager();
		}
	}

	private static int randomizer = 0;

	public final static AiTaskManager getInstance()
	{
		return _instances[randomizer++ & _instances.length - 1];
	}

	private AiTaskManager()
	{
		super(TICK);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
		// Очистка каждую минуту
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				AiTaskManager.this.purge();
			}

		}, 60000L, 60000L);
	}

	public CharSequence getStats(int num)
	{
		return _instances[num].getStats();
	}
}

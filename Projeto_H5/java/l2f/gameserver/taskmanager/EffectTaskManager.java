package l2f.gameserver.taskmanager;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.threading.SteppingRunnableQueueManager;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;

public class EffectTaskManager extends SteppingRunnableQueueManager
{
	private final static long TICK = 250L;

	private final static EffectTaskManager[] _instances = new EffectTaskManager[Config.EFFECT_TASK_MANAGER_COUNT];
	static
	{
		for (int i = 0; i < _instances.length; i++)
		{
			_instances[i] = new EffectTaskManager();
		}
	}

	private static int randomizer = 0;

	public final static EffectTaskManager getInstance()
	{
		return _instances[randomizer++ & _instances.length - 1];
	}

	private EffectTaskManager()
	{
		super(TICK);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);

		// Очистка каждые 30 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				EffectTaskManager.this.purge();
			}

		}, 30000L, 30000L);
	}

	public CharSequence getStats(int num)
	{
		return _instances[num].getStats();
	}
}
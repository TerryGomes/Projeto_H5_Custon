package l2f.gameserver.taskmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Playable;

public class CancelTaskManager
{
	private static final Logger LOG = LoggerFactory.getLogger(CancelTaskManager.class);

	private static final long DELAY_BETWEEN_BUFFS = 500;

	private final List<CancelledPlayable> _tasksToRemoveEffects = new CopyOnWriteArrayList<>();

	private CancelTaskManager()
	{
		if (Config.BUFF_RETURN_OLYMPIAD_TIME <= 0 && Config.BUFF_RETURN_AUTO_EVENTS_TIME <= 0 && Config.BUFF_RETURN_NORMAL_LOCATIONS_TIME <= 0)
		{
			return;
		}

		ThreadPoolManager.getInstance().scheduleAtFixedDelay(new ReturnEffectsThread(), DELAY_BETWEEN_BUFFS, DELAY_BETWEEN_BUFFS);
	}

	public void addNewCancelTask(Playable playable, LinkedList<Effect> buffs)
	{
		if (buffs.isEmpty())
		{
			return;
		}

		int delay;
		if (playable.isInOlympiadMode() || playable.getPlayer().getOlympiadGame() != null)
		{
			delay = Config.BUFF_RETURN_OLYMPIAD_TIME;
		}
		else if (playable.getPlayer().isInFightClub())
		{
			delay = Config.BUFF_RETURN_AUTO_EVENTS_TIME;
		}
		else
		{
			delay = Config.BUFF_RETURN_NORMAL_LOCATIONS_TIME;
		}
		if (delay <= 0)
		{
			return;
		}

		playable.sendMessage("Buffs will be returned in " + delay + " seconds");
		final long timeToReturn = System.currentTimeMillis() + delay * 1000;
		_tasksToRemoveEffects.add(new CancelledPlayable(playable, buffs, timeToReturn));
	}

	public void cancelPlayerTasks(Playable playable)
	{
		for (CancelledPlayable task : _tasksToRemoveEffects)
		{
			if (task != null && task.getPlayable().equals(playable))
			{
				_tasksToRemoveEffects.remove(task);
			}
		}
	}

	public static CancelTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final CancelTaskManager _instance = new CancelTaskManager();
	}

	private static class CancelledPlayable
	{
		private final Playable playable;
		private final LinkedList<Effect> cancelledEffects;
		private final long timeToReturn;

		private CancelledPlayable(Playable playable, LinkedList<Effect> cancelledEffects, long timeToReturn)
		{
			this.playable = playable;
			this.cancelledEffects = cancelledEffects;
			this.timeToReturn = timeToReturn;
		}

		private Playable getPlayable()
		{
			return playable;
		}

		public LinkedList<Effect> getCancelledEffects()
		{
			return cancelledEffects;
		}

		public long getTimeToReturn()
		{
			return timeToReturn;
		}
	}

	private class ReturnEffectsThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			final long current = System.currentTimeMillis();
			for (CancelledPlayable task : _tasksToRemoveEffects)
			{
				if (task.getTimeToReturn() > current)
				{
					continue;
				}

				if (task.getCancelledEffects().isEmpty())
				{
					LOG.warn("Cancelled Buff Task removed at the beginning of the list!");
					_tasksToRemoveEffects.remove(task);
				}
				else
				{
					while (true)
					{
						final Playable playable = task.getPlayable();
						final Effect effect = task.getCancelledEffects().removeLast();
						final Effect effectToGive = effect.copyEffect();
						playable.getEffectList().addEffect(effectToGive);
						if (!task.getCancelledEffects().isEmpty())
						{
							continue;
						}

						playable.updateStats();
						playable.updateEffectIcons();
						playable.sendMessage("Cancelled buffs returned!");
						_tasksToRemoveEffects.remove(task);
						break;
					}
				}
			}
		}
	}
}

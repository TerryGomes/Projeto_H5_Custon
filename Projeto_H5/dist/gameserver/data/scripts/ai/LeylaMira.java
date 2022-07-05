package ai;

import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.utils.Location;

/**
 * @author claww
  * - AI for Leyla Mira (32431).
  * - Indicates the social sphere, proigrovaet music.
  * - AI is tested and works.
 */
public class LeylaMira extends DefaultAI
{
	private static int count = 0;

	public LeylaMira(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		addTaskMove(new Location(-56657, -56338, -2006), true);
		doTask();
		ThreadPoolManager.getInstance().schedule(new ScheduleStart(1, actor), 5000);
		ThreadPoolManager.getInstance().schedule(new ScheduleMoveFinish(), 220000);
		super.onEvtSpawn();
	}

	private class ScheduleStart implements Runnable
	{
		private int _taskId;
		private NpcInstance _actor;

		public ScheduleStart(int taskId, NpcInstance actor)
		{
			_taskId = taskId;
			_actor = actor;
		}

		@Override
		public void run()
		{
			switch (_taskId)
			{
			case 1:
				_actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "NS22_F", 1, 0, _actor.getLoc()));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(2, _actor), 100);
				break;
			case 2:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 3));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 9000);
				break;
			case 3:
				if (count < 10)
				{
					count++;
					_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 3000);
				}
				else
				{
					count = 0;
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(4, _actor), 100);
				}
				break;
			case 4:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(5, _actor), 3000);
				break;
			case 5:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(6, _actor), 36000);
				break;
			case 6:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 3000);
				break;
			case 7:
				if (count < 2)
				{
					count++;
					_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 3000);
				}
				else
				{
					count = 0;
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(8, _actor), 100);
				}
				break;
			case 8:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(9, _actor), 3000);
				break;
			case 9:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 21000);
				break;
			case 10:
				if (count < 3)
				{
					count++;
					_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 3000);
				}
				else
				{
					count = 0;
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(11, _actor), 2000);
				}
				break;
			case 11:
				_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 2));
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
				break;
			case 12:
				if (count < 2)
				{
					count++;
					_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
				}
				else
				{
					count = 0;
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 21000);
				}
				break;
			case 13:
				if (count < 18)
				{
					count++;
					_actor.broadcastPacket(new SocialAction(_actor.getObjectId(), 1));
					ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 3000);
				}
				else
				{
					count = 0;
				}
				break;
			}
		}
	}

	private class ScheduleMoveFinish implements Runnable
	{
		@Override
		public void run()
		{
			addTaskMove(new Location(-56594, -56064, -1988), true);
			doTask();
		}
	}
}
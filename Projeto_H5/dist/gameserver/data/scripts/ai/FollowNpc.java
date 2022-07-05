package ai;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class FollowNpc extends DefaultAI
{
	private static final Logger LOG = LoggerFactory.getLogger(FollowNpc.class);
	private boolean _thinking = false;
	private ScheduledFuture<?> _followTask;

	public FollowNpc(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		if (getActor() instanceof MonsterInstance)
		{
			return true;
		}

		return false;
	}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		if (_thinking || actor.isActionsDisabled() || actor.isAfraid() || actor.isDead() || actor.isMovementDisabled())
		{
			return;
		}

		_thinking = true;
		try
		{
			if (!Config.BLOCK_ACTIVE_TASKS && (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE))
			{
				thinkActive();
			}
			else if (getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
			{
				thinkFollow();
			}
		}
		catch (RuntimeException e)
		{
			LOG.error("Error while thinking on FollowNpc", e);
		}
		finally
		{
			_thinking = false;
		}
	}

	protected void thinkFollow()
	{
		NpcInstance actor = getActor();

		Creature target = actor.getFollowTarget();

		// Находимся слишком далеко цели, либо цель не пригодна для следования, либо не можем перемещаться
		if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || actor.isMovementDisabled())
		{
			clientActionFailed();
			return;
		}

		// Уже следуем за этой целью
		if (actor.isFollow && actor.getFollowTarget() == target)
		{
			clientActionFailed();
			return;
		}

		// Находимся достаточно близко
		if (actor.isInRange(target, Config.FOLLOW_RANGE + 20))
		{
			clientActionFailed();
		}

		if (_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
	}

	protected class ThinkFollow extends RunnableImpl
	{
		public NpcInstance getActor()
		{
			return FollowNpc.this.getActor();
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor == null)
			{
				return;
			}

			Creature target = actor.getFollowTarget();

			if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000)
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			if (!actor.isInRange(target, Config.FOLLOW_RANGE + 20) && (!actor.isFollow || actor.getFollowTarget() != target))
			{
				Location loc = new Location(target.getX() + Rnd.get(-60, 60), target.getY() + Rnd.get(-60, 60), target.getZ());
				actor.followToCharacter(loc, target, Config.FOLLOW_RANGE, false);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
		}
	}
}
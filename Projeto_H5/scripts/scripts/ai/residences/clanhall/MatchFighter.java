package ai.residences.clanhall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 7:15/24.04.2011
 */
public abstract class MatchFighter extends Fighter
{
	public MatchFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isActionsDisabled())
		{
			return true;
		}
		if (_def_think)
		{
			if (doTask())
			{
				clearTasks();
			}
			return true;
		}

		long now = System.currentTimeMillis();
		if (now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			final List<Creature> knowns = World.getAroundCharacters(actor);
			final List<Creature> aggroList = new ArrayList<>();

			for (Creature cha : knowns)
			{
				if (checkAggression(cha, true))
				{
					aggroList.add(cha);
				}
			}

			if (!aggroList.isEmpty())
			{
				Collections.sort(aggroList, _nearestTargetComparator);

				for (Creature cha : aggroList)
				{
					if (cha != null && !cha.isDead())
					{
						if (checkAggression(cha, false))
						{
							return true;
						}
					}
				}
			}
		}

		if (randomWalk())
		{
			return true;
		}

		return false;
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		CTBBossInstance actor = getActor();

		if ((getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || target.isAlikeDead() || target.isInvul() || !actor.isAttackable(target))
		{
			return false;
		}
		if (!GeoEngine.canSeeTarget(actor, target, false))
		{
			return false;
		}

		if (!avoidAttack)
		{
			actor.getAggroList().addDamageHate(target, 0, 2);

			if ((target.isSummon() || target.isPet()))
			{
				actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
			}

			startRunningTask(AI_TASK_ATTACK_DELAY);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}

		return true;
	}

	@Override
	protected boolean canAttackCharacter(Creature target)
	{
		NpcInstance actor = getActor();
		return actor.isAttackable(target);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();
		CTBBossInstance actor = getActor();

		int x = (int) (actor.getX() + 800 * Math.cos(actor.headingToRadians(actor.getHeading() - 32768)));
		int y = (int) (actor.getY() + 800 * Math.sin(actor.headingToRadians(actor.getHeading() - 32768)));

		actor.setSpawnedLoc(new Location(x, y, actor.getZ()));
		addTaskMove(actor.getSpawnedLoc(), true);
		doTask();
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public CTBBossInstance getActor()
	{
		return (CTBBossInstance) super.getActor();
	}
}

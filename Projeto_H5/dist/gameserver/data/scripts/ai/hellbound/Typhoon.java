package ai.hellbound;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Typhoon extends Fighter
{
	static final Location[] points =
	{
		// spawn: -15864,250872,-3013
		new Location(-16696, 250232, -2956),
		new Location(-17944, 251000, -3213),
		new Location(-19000, 252312, -3385),
		new Location(-20840, 253000, -3343),
		new Location(-20792, 255320, -3267),
		new Location(-19368, 256936, -3175),
		new Location(-16968, 255656, -3207),
		new Location(-17160, 253208, -3461),
		new Location(-15240, 253576, -3476),
		new Location(-13128, 254792, -3424),
		new Location(-10776, 256120, -3340),
		new Location(-8600, 256712, -3234),
		new Location(-4792, 254344, -3143),
		new Location(-4024, 252360, -3325),
		new Location(-5320, 251224, -3240),
		new Location(-8424, 251432, -2950),
		new Location(-11240, 252856, -3114),
		new Location(-12616, 254168, -3150),
		new Location(-14120, 254280, -3463),
		new Location(-17128, 251896, -3388),
		new Location(-16712, 250520, -3029),
		new Location(-15864, 250872, -3013)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Typhoon(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return false;
		}

		if (!avoidAttack)
		{
			// Движение от точки к точке
			if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE && current_point > -1)
			{
				current_point--;
			}

			actor.getAggroList().addDamageHate(target, 0, 1);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}

		return true;
	}

	@Override
	public boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
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

		// BUFF
		if (super.thinkActive())
		{
			return true;
		}

		if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if (!wait && current_point == 31)
			{
				wait_timeout = System.currentTimeMillis() + 30000;
				wait = true;
				return true;
			}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if (current_point >= points.length)
			{
				current_point = 0;
			}

			actor.setWalking();

			addTaskMove(points[current_point], true);
			doTask();
			return true;
		}

		if (randomAnimation())
		{
			return false;
		}

		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
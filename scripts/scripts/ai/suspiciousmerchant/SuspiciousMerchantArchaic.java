package ai.suspiciousmerchant;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SuspiciousMerchantArchaic extends DefaultAI
{
	static final Location[] points =
	{
		new Location(105447, -139845, -3120),
		new Location(104918, -140382, -3256),
		new Location(105507, -142515, -3648),
		new Location(106533, -143107, -3656),
		new Location(106714, -143825, -3656),
		new Location(107510, -144024, -3656),
		new Location(108092, -144888, -3656),
		new Location(109499, -145168, -3664),
		new Location(110064, -146169, -3456),
		new Location(110186, -147427, -3096),
		new Location(112389, -147779, -3256),
		new Location(110186, -147427, -3096),
		new Location(110064, -146169, -3456),
		new Location(109499, -145168, -3664),
		new Location(108092, -144888, -3656),
		new Location(107510, -144024, -3656),
		new Location(106714, -143825, -3656),
		new Location(106533, -143107, -3656),
		new Location(105507, -142515, -3648),
		new Location(104918, -140382, -3256),
		new Location(105447, -139845, -3120)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantArchaic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return true;
		}

		if (_def_think)
		{
			doTask();
			return true;
		}

		if (actor.isMoving)
		{
			return true;
		}

		if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if (!wait)
			{
				switch (current_point)
				{
				case 0:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 6:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 10:
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 14:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 20:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				}
			}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if (current_point >= points.length)
			{
				current_point = 0;
			}

			addTaskMove(points[current_point], false);
			doTask();
			return true;
		}

		if (randomAnimation())
		{
			return true;
		}

		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}
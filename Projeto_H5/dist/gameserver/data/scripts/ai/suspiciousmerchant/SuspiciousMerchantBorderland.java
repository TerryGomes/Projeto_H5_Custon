package ai.suspiciousmerchant;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class SuspiciousMerchantBorderland extends DefaultAI
{
	static final Location[] points =
	{
		new Location(161876, -73407, -2984),
		new Location(161795, -75288, -3088),
		new Location(159678, -77671, -3584),
		new Location(158917, -78117, -3760),
		new Location(158989, -77130, -3720),
		new Location(158757, -75951, -3720),
		new Location(158157, -74161, -3592),
		new Location(157547, -73326, -3400),
		new Location(153815, -71497, -3392),
		new Location(153086, -70701, -3488),
		new Location(152262, -70352, -3568),
		new Location(155193, -69617, -3008),
		new Location(152262, -70352, -3568),
		new Location(153086, -70701, -3488),
		new Location(153815, -71497, -3392),
		new Location(157547, -73326, -3400),
		new Location(158157, -74161, -3592),
		new Location(158757, -75951, -3720),
		new Location(158989, -77130, -3720),
		new Location(158917, -78117, -3760),
		new Location(159678, -77671, -3584),
		new Location(161795, -75288, -3088),
		new Location(161876, -73407, -2984)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantBorderland(NpcInstance actor)
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
				case 11:
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 16:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 22:
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
package ai.suspiciousmerchant;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SuspiciousMerchantTanor extends DefaultAI
{
	static final Location[] points =
	{
		new Location(58314, 136319, -2000),
		new Location(57078, 137124, -2216),
		new Location(54644, 137366, -2600),
		new Location(58696, 134202, -3096),
		new Location(60967, 134154, -3416),
		new Location(62813, 134744, -3592),
		new Location(65158, 135007, -3728),
		new Location(64278, 139384, -3176),
		new Location(63711, 140599, -2720),
		new Location(63187, 141192, -2440),
		new Location(62811, 142466, -2064),
		new Location(63187, 141192, -2440),
		new Location(63711, 140599, -2720),
		new Location(64278, 139384, -3176),
		new Location(65158, 135007, -3728),
		new Location(62813, 134744, -3592),
		new Location(60967, 134154, -3416),
		new Location(58696, 134202, -3096),
		new Location(54644, 137366, -2600),
		new Location(57078, 137124, -2216),
		new Location(58314, 136319, -2000)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantTanor(NpcInstance actor)
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
				case 3:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 10:
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 13:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 17:
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
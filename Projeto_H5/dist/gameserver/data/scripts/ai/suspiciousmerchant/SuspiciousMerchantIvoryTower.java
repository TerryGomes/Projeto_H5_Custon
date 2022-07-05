package ai.suspiciousmerchant;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class SuspiciousMerchantIvoryTower extends DefaultAI
{
	static final Location[] points =
	{
		new Location(74725, 1671, -3128),
		new Location(76651, 1505, -3552),
		new Location(79421, 4977, -3080),
		new Location(77357, 7197, -3208),
		new Location(76287, 9164, -3568),
		new Location(72447, 8196, -3264),
		new Location(71780, 7467, -3160),
		new Location(72447, 8196, -3264),
		new Location(76287, 9164, -3568),
		new Location(77357, 7197, -3208),
		new Location(79421, 4977, -3080),
		new Location(76651, 1505, -3552),
		new Location(74725, 1671, -3128)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantIvoryTower(NpcInstance actor)
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
				case 10:
					wait_timeout = System.currentTimeMillis() + 60000;
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
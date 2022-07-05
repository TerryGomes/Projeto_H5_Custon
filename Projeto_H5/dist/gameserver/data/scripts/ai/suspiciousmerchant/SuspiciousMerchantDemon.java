package ai.suspiciousmerchant;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class SuspiciousMerchantDemon extends DefaultAI
{
	static final Location[] points =
	{
		new Location(104150, -57163, -848),
		new Location(106218, -59401, -1344),
		new Location(106898, -59553, -1664),
		new Location(107352, -60168, -2000),
		new Location(107651, -61177, -2400),
		new Location(109094, -62678, -3248),
		new Location(108266, -62657, -3104),
		new Location(105169, -61226, -2616),
		new Location(102968, -59982, -2384),
		new Location(100070, -60173, -2792),
		new Location(98764, -61095, -2768),
		new Location(94946, -60039, -2432),
		new Location(96103, -59078, -1992),
		new Location(96884, -59043, -1656),
		new Location(97064, -57884, -1256),
		new Location(96884, -59043, -1656),
		new Location(96103, -59078, -1992),
		new Location(94946, -60039, -2432),
		new Location(98764, -61095, -2768),
		new Location(100070, -60173, -2792),
		new Location(102968, -59982, -2384),
		new Location(105169, -61226, -2616),
		new Location(108266, -62657, -3104),
		new Location(109094, -62678, -3248),
		new Location(107651, -61177, -2400),
		new Location(107352, -60168, -2000),
		new Location(106898, -59553, -1664),
		new Location(106218, -59401, -1344),
		new Location(104150, -57163, -848)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantDemon(NpcInstance actor)
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
				case 2:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 14:
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 26:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 28:
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
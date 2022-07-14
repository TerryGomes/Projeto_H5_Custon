package ai.suspiciousmerchant;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SuspiciousMerchantHunters extends DefaultAI
{
	static final Location[] points =
	{
		new Location(121072, 93215, -2736),
		new Location(122718, 92355, -2320),
		new Location(126171, 91910, -2216),
		new Location(126353, 90422, -2296),
		new Location(125796, 87720, -2432),
		new Location(124803, 85970, -2464),
		new Location(125036, 83836, -2376),
		new Location(128886, 83331, -1416),
		new Location(129697, 84969, -1256),
		new Location(126291, 86712, -2240),
		new Location(126599, 88950, -2325),
		new Location(126847, 90713, -2264),
		new Location(126599, 88950, -2325),
		new Location(126291, 86712, -2240),
		new Location(129697, 84969, -1256),
		new Location(128886, 83331, -1416),
		new Location(125036, 83836, -2376),
		new Location(124803, 85970, -2464),
		new Location(125796, 87720, -2432),
		new Location(126353, 90422, -2296),
		new Location(126171, 91910, -2216),
		new Location(122718, 92355, -2320),
		new Location(121072, 93215, -2736)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantHunters(NpcInstance actor)
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
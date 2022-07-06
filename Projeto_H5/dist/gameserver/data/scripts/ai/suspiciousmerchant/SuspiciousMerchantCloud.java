package ai.suspiciousmerchant;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SuspiciousMerchantCloud extends DefaultAI
{
	static final Location[] points =
	{
		new Location(-56032, 86017, -3259),
		new Location(-57329, 86006, -3640),
		new Location(-57470, 85306, -3664),
		new Location(-58892, 85159, -3768),
		new Location(-59030, 80150, -3632),
		new Location(-57642, 77591, -3512),
		new Location(-53971, 77664, -3224),
		new Location(-53271, 85126, -3552),
		new Location(-53971, 77664, -3224),
		new Location(-57642, 77591, -3512),
		new Location(-59030, 80150, -3632),
		new Location(-58892, 85159, -3768),
		new Location(-57470, 85306, -3664),
		new Location(-57329, 86006, -3640),
		new Location(-56032, 86017, -3259)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantCloud(NpcInstance actor)
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
				case 5:
					wait_timeout = System.currentTimeMillis() + 20000;
					wait = true;
					return true;
				case 6:
					wait_timeout = System.currentTimeMillis() + 40000;
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 8:
					wait_timeout = System.currentTimeMillis() + 40000;
					wait = true;
					return true;
				case 9:
					wait_timeout = System.currentTimeMillis() + 20000;
					wait = true;
					return true;
				case 11:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 14:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				}
			}

			wait_timeout = 0;
			wait = false;

			if (current_point >= points.length - 1)
			{
				current_point = -1;
			}

			current_point++;

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
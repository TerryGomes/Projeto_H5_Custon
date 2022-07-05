package ai.suspiciousmerchant;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class SuspiciousMerchantMonastic extends DefaultAI
{
	static final Location[] points =
	{
		new Location(69553, -91746, -1488),
		new Location(70941, -89751, -2256),
		new Location(71104, -89094, -2368),
		new Location(73471, -91462, -2024),
		new Location(74532, -92202, -1776),
		new Location(74908, -93152, -1536),
		new Location(74532, -92202, -1776),
		new Location(73471, -91462, -2024),
		new Location(71104, -89094, -2368),
		new Location(70941, -89751, -2256),
		new Location(69553, -91746, -1488)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantMonastic(NpcInstance actor)
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
					wait_timeout = System.currentTimeMillis() + 60000;
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 30000;
					wait = true;
					return true;
				case 10:
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
package ai.suspiciousmerchant;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SuspiciousMerchantAaru extends DefaultAI
{
	static final Location[] points =
	{
		new Location(71692, 188004, -2616),
		new Location(69326, 187042, -3008),
		new Location(68627, 185540, -2984),
		new Location(69077, 184566, -2976),
		new Location(70642, 182573, -2992),
		new Location(73647, 181706, -3160),
		new Location(74283, 181756, -3152),
		new Location(73655, 182960, -2736),
		new Location(74283, 181756, -3152),
		new Location(73647, 181706, -3160),
		new Location(70642, 182573, -2992),
		new Location(69077, 184566, -2976),
		new Location(68627, 185540, -2984),
		new Location(69326, 187042, -3008),
		new Location(71692, 188004, -2616)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public SuspiciousMerchantAaru(NpcInstance actor)
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
					wait_timeout = System.currentTimeMillis() + 60000;
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
package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class Kreed extends DefaultAI
{
	static final Location[] points =
	{
		new Location(23436, 11164, -3728),
		new Location(20256, 11104, -3728),
		new Location(17330, 13579, -3720),
		new Location(17415, 13044, -3736),
		new Location(20153, 12880, -3728),
		new Location(21621, 13349, -3648),
		new Location(20686, 10432, -3720),
		new Location(22426, 10260, -3648),
		new Location(23436, 11164, -3728)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Kreed(NpcInstance actor)
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

		if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if (!wait)
			{
				switch (current_point)
				{
				case 3:
					wait_timeout = System.currentTimeMillis() + 15000;
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 60000;
					Functions.npcSay(actor, NpcString.THE_MASS_OF_DARKNESS_WILL_START_IN_A_COUPLE_OF_DAYS);
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

			addTaskMove(points[current_point], true);
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
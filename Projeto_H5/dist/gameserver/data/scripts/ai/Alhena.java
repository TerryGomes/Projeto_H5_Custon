package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Location;

public class Alhena extends DefaultAI
{
	static final Location[] points =
	{
		new Location(10968, 14620, -4248),
		new Location(11308, 15847, -4584),
		new Location(12119, 16441, -4584),
		new Location(15104, 15661, -4376),
		new Location(15265, 16288, -4376),
		new Location(12292, 16934, -4584),
		new Location(11777, 17669, -4584),
		new Location(11229, 17650, -4576),
		new Location(10641, 17282, -4584),
		new Location(7683, 18034, -4376),
		new Location(10551, 16775, -4584),
		new Location(11004, 15942, -4584),
		new Location(10827, 14757, -4248),
		new Location(10968, 14620, -4248)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Alhena(NpcInstance actor)
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
				case 4:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.YOURE_A_HARD_WORKER_RAYLA);
					wait = true;
					return true;
				case 9:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.YOURE_A_HARD_WORKER);
					wait = true;
					return true;
				case 12:
					wait_timeout = System.currentTimeMillis() + 60000;
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
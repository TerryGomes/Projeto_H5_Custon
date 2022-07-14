package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class Remy extends DefaultAI
{
	static final Location[] points =
	{
		new Location(-81926, 243894, -3712),
		new Location(-82134, 243600, -3728),
		new Location(-83165, 243987, -3728),
		new Location(-84501, 243245, -3728),
		new Location(-85100, 243285, -3728),
		new Location(-86152, 242898, -3728),
		new Location(-86288, 242962, -3720),
		new Location(-86348, 243223, -3720),
		new Location(-86522, 242762, -3720),
		new Location(-86500, 242615, -3728),
		new Location(-86123, 241606, -3728),
		new Location(-85167, 240589, -3728),
		new Location(-84323, 241245, -3728),
		new Location(-83215, 241170, -3728),
		new Location(-82364, 242944, -3728),
		new Location(-81674, 243391, -3712),
		new Location(-81926, 243894, -3712)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Remy(NpcInstance actor)
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
				case 0:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.A_DELIVERY_FOR_MR);
					wait = true;
					return true;
				case 3:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.I_NEED_A_BREAK);
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.HELLO_MR);
					wait = true;
					return true;
				case 12:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.LULU);
					wait = true;
					return true;
				case 15:
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

			// Remy всегда бегает
			actor.setRunning();

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
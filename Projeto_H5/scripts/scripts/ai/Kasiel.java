package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class Kasiel extends DefaultAI
{
	static final Location[] points =
	{
		new Location(43932, 51096, -2992),
		new Location(43304, 50364, -2992),
		new Location(43041, 49312, -2992),
		new Location(43612, 48322, -2992),
		new Location(44009, 47645, -2992),
		new Location(45309, 47341, -2992),
		new Location(46726, 47762, -2992),
		new Location(47509, 49004, -2992),
		new Location(47443, 50456, -2992),
		new Location(47013, 51287, -2992),
		new Location(46380, 51254, -2900),
		new Location(46389, 51584, -2800),
		new Location(46009, 51593, -2800),
		new Location(46027, 52156, -2800),
		new Location(44692, 52141, -2800),
		new Location(44692, 51595, -2800),
		new Location(44346, 51564, -2850),
		new Location(44357, 51259, -2900),
		new Location(44111, 51252, -2992)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Kasiel(NpcInstance actor)
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
				case 5:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.THE_MOTHER_TREE_IS_ALWAYS_SO_GORGEOUS);
					wait = true;
					return true;
				case 9:
					wait_timeout = System.currentTimeMillis() + 60000;
					Functions.npcSay(actor, NpcString.LADY_MIRABEL_MAY_THE_PEACE_OF_THE_LAKE_BE_WITH_YOU);
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
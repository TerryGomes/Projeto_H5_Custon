package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Location;

public class Tate extends DefaultAI
{
	static final Location[] points =
	{
		new Location(115824, -181564, -1352),
		new Location(116048, -181575, -1352),
		new Location(116521, -181476, -1400),
		new Location(116632, -180022, -1168),
		new Location(115355, -178617, -928),
		new Location(115763, -177585, -896),
		new Location(115795, -177361, -880),
		new Location(115877, -177338, -880),
		new Location(115783, -177493, -880),
		new Location(115112, -179836, -880),
		new Location(115102, -180026, -872),
		new Location(114876, -180045, -872),
		new Location(114840, -179694, -872),
		new Location(116322, -179602, -1096),
		new Location(116792, -180386, -1240),
		new Location(116319, -181573, -1376),
		new Location(115824, -181564, -1352)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Tate(NpcInstance actor)
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
					wait_timeout = System.currentTimeMillis() + 20000;
					Functions.npcSay(actor, NpcString.CARE_TO_GO_A_ROUND);
					wait = true;
					return true;
				case 7:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, NpcString.HAVE_A_NICE_DAY_MR);
					wait = true;
					return true;
				case 11:
					wait_timeout = System.currentTimeMillis() + 30000;
					Functions.npcSay(actor, NpcString.MR);
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
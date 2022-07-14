package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class Yakand extends DefaultAI
{
	static final Location[] points =
	{
		new Location(-48820, -113748, -232),
		new Location(-47365, -113618, -232),
		new Location(-45678, -113635, -256),
		new Location(-45168, -114038, -256),
		new Location(-44671, -114185, -256),
		new Location(-44199, -113763, -256),
		new Location(-44312, -113201, -256),
		new Location(-44844, -112958, -256),
		new Location(-45717, -113564, -256),
		new Location(-47370, -113588, -232),
		new Location(-48821, -113496, -232),
		new Location(-48820, -113748, -232)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Yakand(NpcInstance actor)
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
				case 10:
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
package ai.hellbound;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class GreaterEvil extends Fighter
{
	static final Location[] path =
	{
		new Location(28448, 243816, -3696),
		new Location(27624, 245256, -3696),
		new Location(27528, 246808, -3656),
		new Location(28296, 247912, -3248),
		new Location(25880, 246184, -3176)
	};

	private int current_point = 0;

	public GreaterEvil(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = 6000;
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

		if (current_point >= path.length - 1)
		{
			actor.doDie(null);
			current_point = 0;
			return true;
		}
		actor.setRunning();
		addTaskMove(path[current_point], false);
		doTask();
		return false;
	}

	@Override
	protected void onEvtArrived()
	{
		current_point++;
		super.onEvtArrived();
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}
}
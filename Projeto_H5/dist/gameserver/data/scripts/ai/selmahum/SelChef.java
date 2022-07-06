package ai.selmahum;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class SelChef extends Fighter
{
	private Location targetLoc;
	private long wait_timeout = 0;

	public SelChef(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().getMinionList().spawnMinions();
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
		if (System.currentTimeMillis() > wait_timeout)
		{
			wait_timeout = System.currentTimeMillis() + 2000;
			actor.setWalking();
			targetLoc = findFirePlace(actor);
			addTaskMove(targetLoc, true);
			doTask();
			return true;
		}
		return false;
	}

	private Location findFirePlace(NpcInstance actor)
	{
		Location loc = new Location();
		List<NpcInstance> list = new ArrayList<NpcInstance>();
		for (NpcInstance npc : actor.getAroundNpc(3000, 600))
		{
			if (npc.getNpcId() == 18927 && GeoEngine.canSeeTarget(actor, npc, false))
			{
				list.add(npc);
			}
		}

		if (!list.isEmpty())
		{
			loc = list.get(Rnd.get(list.size())).getLoc();
		}
		else
		{
			loc = Location.findPointToStay(actor, 1000, 1500);
		}
		return loc;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
package ai.hellbound;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class FloatingGhost extends Fighter
{
	public FloatingGhost(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isMoving)
		{
			return false;
		}

		randomWalk();
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		NpcInstance actor = getActor();
		Location sloc = actor.getSpawnedLoc();
		Location pos = Location.findPointToStay(actor, sloc, 50, 300);
		if (GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
		{
			actor.setRunning();
			addTaskMove(pos, false);
		}

		return true;
	}
}
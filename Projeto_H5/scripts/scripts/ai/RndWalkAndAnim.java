package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class RndWalkAndAnim extends DefaultAI
{
	protected static final int PET_WALK_RANGE = 100;

	public RndWalkAndAnim(NpcInstance actor)
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

		int val = Rnd.get(100);

		if (val < 10)
		{
			randomWalk();
		}
		else if (val < 20)
		{
			actor.onRandomAnimation();
		}

		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return false;
		}

		Location sloc = actor.getSpawnedLoc();

		int x = sloc.x + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
		int y = sloc.y + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());

		actor.setRunning();
		actor.moveToLocation(x, y, z, 0, true);

		return true;
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
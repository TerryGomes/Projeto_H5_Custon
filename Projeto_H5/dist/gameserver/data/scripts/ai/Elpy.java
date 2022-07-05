package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Elpy extends Fighter
{
	public Elpy(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker != null && Rnd.chance(50))
		{
			Location pos = Location.findPointToStay(actor, 150, 200);
			if (GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
			{
				actor.setRunning();
				addTaskMove(pos, false);
			}
		}
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		return false;
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{

	}
}
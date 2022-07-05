package ai;

import java.util.Collection;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

/**
 * Contaminated Mucrokian (22655).
 * Tries to attack the protective devices within sight.
 * When attacking defenders, ignoring the attack and escapes.
 */
public class AwakenedMucrokian extends Fighter
{

	private NpcInstance mob = null;

	public AwakenedMucrokian(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
		{
			return true;
		}
		if (mob == null)
		{
			Collection<NpcInstance> around = getActor().getAroundNpc(300, 300);
			if (around != null && !around.isEmpty())
			{
				for (NpcInstance npc : around)
				{
					if (npc.getNpcId() == 18805 || npc.getNpcId() == 18806)
					{
						if (mob == null || getActor().getDistance3D(npc) < getActor().getDistance3D(mob))
						{
							mob = npc;
						}
					}
				}
			}

		}
		if (mob != null)
		{
			actor.stopMove();
			actor.setRunning();
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, mob, 1);
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor != null && !actor.isDead())
		{
			if (attacker != null)
			{
				if (attacker.getNpcId() >= 22656 && attacker.getNpcId() <= 22659)
				{
					if (Rnd.chance(100))
					{
						actor.abortAttack(true, false);
						actor.getAggroList().clear();
						Location pos = Location.findPointToStay(actor, 450, 600);
						if (GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
						{
							actor.setRunning();
							addTaskMove(pos, false);
						}
					}
				}
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
}

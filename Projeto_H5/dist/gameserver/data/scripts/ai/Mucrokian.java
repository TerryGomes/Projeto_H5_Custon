package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Location;

/**
 * Mucrokian (22650, 22651, 22652, 22653).
 * Кричат в чат перед атакой. Игнорируют атаку стражей и убегают.
 */
public class Mucrokian extends Fighter
{

	public static final NpcString[] MsgText =
	{
		NpcString.PEUNGLUI_MUGLANEP_NAIA_WAGANAGEL_PEUTAGUN,
		NpcString.PEUNGLUI_MUGLANEP
	};

	public Mucrokian(NpcInstance actor)
	{
		super(actor);
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
					if (Rnd.chance(10))
					{
						Functions.npcSay(actor, Rnd.get(MsgText), ChatType.NPC_ALL, 5000);
					}
				}
			}
			super.onEvtAttacked(attacker, damage);
		}
	}
}
package ai.residences.fortress.siege;

import ai.residences.SiegeGuardMystic;
import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.SkillTable;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 16:41/17.04.2011
 */
public class SupportUnitCaption extends SiegeGuardMystic
{
	public SupportUnitCaption(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtAttacked(Creature attacker, int dam)
	{
		super.onEvtAttacked(attacker, dam);
		SiegeGuardInstance actor = getActor();

		if (Rnd.chance(1))
		{
			Functions.npcShout(actor, NpcString.SPIRIT_OF_FIRE_UNLEASH_YOUR_POWER_BURN_THE_ENEMY);
		}
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();
		SiegeGuardInstance actor = getActor();

		FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
		if (siegeEvent == null)
		{
			return;
		}

		if (siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0)
		{
			actor.doCast(SkillTable.getInstance().getInfo(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF)), actor, false);
		}

		siegeEvent.barrackAction(2, false);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		SiegeGuardInstance actor = getActor();
		FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
		if (siegeEvent == null)
		{
			return;
		}

		siegeEvent.barrackAction(2, true);

		siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

		Functions.npcShout(actor, NpcString.AT_LAST_THE_MAGIC_FIELD_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK);

		super.onEvtDead(killer);

		siegeEvent.checkBarracks();
	}
}

package ai.residences.fortress.siege;

import ai.residences.SiegeGuardFighter;
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
 * @date 16:43/17.04.2011
 */
public class GuardCaption extends SiegeGuardFighter
{
	public GuardCaption(NpcInstance actor)
	{
		super(actor);
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

		siegeEvent.barrackAction(1, false);
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

		siegeEvent.barrackAction(1, true);

		siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

		Functions.npcShout(actor, NpcString.AIIEEEE_COMMAND_CENTER_THIS_IS_GUARD_UNIT_WE_NEED_BACKUP_RIGHT_AWAY);

		super.onEvtDead(killer);

		siegeEvent.checkBarracks();
	}
}

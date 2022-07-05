package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2f.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2f.gameserver.model.entity.residence.Dominion;
import l2f.gameserver.model.instances.residences.SiegeFlagInstance;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.StatsSet;

public class TakeFlag extends Skill
{
	public TakeFlag(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!super.checkCondition(activeChar, target, forceUse, dontMove, first) || activeChar == null || !activeChar.isPlayer())
		{
			return false;
		}

		Player player = (Player) activeChar;

		if (player.getClan() == null)
		{
			return false;
		}

		DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
		if (siegeEvent1 == null)
		{
			return false;
		}

		if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject) || player.isMounted())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || target.getClan() != player.getClan())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
		if (siegeEvent2 == null || siegeEvent1 != siegeEvent2)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		return true;
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				Player player = (Player) activeChar;
				DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
				if ((siegeEvent1 == null) || !(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || target.getClan() != player.getClan())
				{
					continue;
				}
				if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject))
				{
					continue;
				}
				DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
				if (siegeEvent2 == null || siegeEvent1 != siegeEvent2)
				{
					continue;
				}

				// текущая територия, к которой пойдет Вард
				Dominion dominion = siegeEvent1.getResidence();
				// вард с вражеской територии
				TerritoryWardObject wardObject = (TerritoryWardObject) player.getActiveWeaponFlagAttachment();
				// територия с которой уйдет Вард
				DominionSiegeEvent siegeEvent3 = wardObject.getEvent();
				Dominion dominion3 = siegeEvent3.getResidence();
				// айди територии к которой относится Вард
				int wardDominionId = wardObject.getDominionId();

				// удаляем с инвентарями вард, и освободжаем ресурсы
				wardObject.despawnObject(siegeEvent3);
				// удаляем Вард
				dominion3.removeFlag(wardDominionId);
				// добавляем Вард
				dominion.addFlag(wardDominionId);
				// позиции вардов с текущей територии
				// спавним Варда, уже в новой територии
				siegeEvent1.spawnAction("ward_" + wardDominionId, true);

				DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
				runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD).addString(dominion.getOwner().getName()).addResidenceName(wardDominionId));
			}
		}
	}
}
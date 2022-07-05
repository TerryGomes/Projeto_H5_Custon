package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.entity.events.impl.fightclub.FightForThroneEvent;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.StatsSet;

public class TakeCastle extends Skill
{
	public TakeCastle(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Zone siegeZone = target.getZone(ZoneType.SIEGE);

		if (!super.checkCondition(activeChar, target, forceUse, dontMove, first) || activeChar == null || !activeChar.isPlayer())
		{
			return false;
		}

		final Player player = (Player) activeChar;
		if (!isFightClubSiegeEvent(player) && (player.getClan() == null || !player.isClanLeader()))
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		final CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
		if (!isFightClubSiegeEvent(player) && siegeEvent == null)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		if (siegeEvent != null && siegeZone != null)
		{
			if (siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) == null || siegeEvent.getResidence().getId() != siegeZone.getParams().getInteger("residence", 0))
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
		}
		else
		{
			for (GlobalEvent e : player.getEvents())
			{
				if (!e.canUseSkill(player, target, this))
				{
					player.sendMessage("The skill you are trying to cast is not allowed in your current state");
					return false;
				}
			}
		}

		if (player.isMounted())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		if (!player.isInRangeZ(target, 185))
		{
			player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		if (first && !isFightClubSiegeEvent(player))
		{
			siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, "defenders");
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
				if (!target.isArtefact())
				{
					continue;
				}

				final Player player = (Player) activeChar;
				final CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
				if (siegeEvent != null)
				{
					IStaticPacket lostPacket = siegeEvent.getResidence().getOwner() != null ? new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, siegeEvent.getResidence().getName() + " Castle", "Clan " + siegeEvent.getResidence().getOwner().getName() + " has lost " + siegeEvent.getResidence().getName() + " Castle") : null;
					IStaticPacket winPacket = new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, siegeEvent.getResidence().getName() + " Castle", "Clan " + player.getClan().getName() + " has taken " + siegeEvent.getResidence().getName() + " Castle");
					for (Player playerToSeeMsg : GameObjectsStorage.getAllPlayersForIterate())
					{
						if (lostPacket != null)
						{
							playerToSeeMsg.sendPacket(lostPacket);
						}
						playerToSeeMsg.sendPacket(winPacket);
					}
					siegeEvent.processStep(player.getClan());
				}
				else if (isFightClubSiegeEvent(player))
				{
					player.getFightClubEvent().onFinishedSkill(activeChar, target, this);
				}
			}
		}
	}

	private static boolean isFightClubSiegeEvent(Player player)
	{
		return player.getEvent(FightForThroneEvent.class) != null;
	}
}
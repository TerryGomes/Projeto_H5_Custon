package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.objects.SiegeClanObject;
import l2mv.gameserver.model.entity.events.objects.ZoneObject;
import l2mv.gameserver.model.instances.residences.SiegeFlagInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncMul;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.Util;

public class SummonSiegeFlag extends Skill
{
	public static enum FlagType
	{
		DESTROY, NORMAL, ADVANCED, OUTPOST
	}

	private final FlagType _flagType;
	private final double _advancedMult;

	public SummonSiegeFlag(StatsSet set)
	{
		super(set);
		_flagType = set.getEnum("flagType", FlagType.class);
		_advancedMult = set.getDouble("advancedMultiplier", 1.);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!activeChar.isPlayer() || !super.checkCondition(activeChar, target, forceUse, dontMove, first))
		{
			return false;
		}

		Player player = (Player) activeChar;
		if (player.getClan() == null || !player.isClanLeader())
		{
			return false;
		}

		switch (_flagType)
		{
		case DESTROY:
		{
			SiegeEvent siegeEvent = activeChar.getEvent(SiegeEvent.class);
			if (siegeEvent == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, player.getClan());
			if (siegeClan == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			if (siegeClan.getFlag() == null)
			{
				player.sendPacket(SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			if (Util.calculateDistance(activeChar, siegeClan.getFlag(), true) > 800)
			{
				player.sendMessage("You are too far from the base to destroy it");
				player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			break;
		}
		case OUTPOST:
		case NORMAL:
		case ADVANCED:
			if (player.isInZone(Zone.ZoneType.RESIDENCE))
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			SiegeEvent siegeEvent = activeChar.getEvent(SiegeEvent.class);
			if (siegeEvent == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			boolean inZone = false;
			List<ZoneObject> zones = siegeEvent.getObjects(SiegeEvent.FLAG_ZONES);
			for (ZoneObject zone : zones)
			{
				if (player.isInZone(zone.getZone()))
				{
					inZone = true;
				}
			}

			if (!inZone)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, player.getClan());
			if (siegeClan == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			if (siegeClan.getFlag() != null)
			{
				player.sendPacket(SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		Player player = (Player) activeChar;

		Clan clan = player.getClan();
		if (clan == null || !player.isClanLeader())
		{
			return;
		}

		SiegeEvent siegeEvent = activeChar.getEvent(SiegeEvent.class);
		if (siegeEvent == null)
		{
			return;
		}

		SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, clan);
		if (siegeClan == null)
		{
			return;
		}

		switch (_flagType)
		{
		case DESTROY:
			siegeClan.deleteFlag();
			break;
		default:
			if (siegeClan.getFlag() != null)
			{
				return;
			}

			// 35062/36590
			SiegeFlagInstance flag = (SiegeFlagInstance) NpcHolder.getInstance().getTemplate(_flagType == FlagType.OUTPOST ? 36590 : 35062).getNewInstance();
			flag.setClan(siegeClan);
			flag.addEvent(siegeEvent);

			if (_flagType == FlagType.ADVANCED)
			{
				flag.addStatFunc(new FuncMul(Stats.MAX_HP, 0x50, flag, _advancedMult));
			}

			flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp(), true);
			flag.setHeading(player.getHeading());

			// Ставим флаг перед чаром
			int x = (int) (player.getX() + 100 * Math.cos(player.headingToRadians(player.getHeading() - 32768)));
			int y = (int) (player.getY() + 100 * Math.sin(player.headingToRadians(player.getHeading() - 32768)));
			flag.spawnMe(GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), x, y, player.getGeoIndex()));

			siegeClan.setFlag(flag);
			break;
		}
	}
}
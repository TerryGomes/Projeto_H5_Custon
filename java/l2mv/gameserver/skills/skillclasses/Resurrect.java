package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.base.BaseStats;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.StatsSet;

public class Resurrect extends Skill
{
	private final boolean _canPet;

	public Resurrect(StatsSet set)
	{
		super(set);
		_canPet = set.getBool("canPet", false);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!activeChar.isPlayer())
		{
			return false;
		}
		if ((target == null) || ((target != activeChar) && (!target.isDead())))
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		Player player = (Player) activeChar;
		Player pcTarget = target.getPlayer();

		if (pcTarget == null)
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		if (((pcTarget.getTeam() != TeamType.NONE) && (player.getTeam() == TeamType.NONE)) || ((player.getTeam() != TeamType.NONE) && (pcTarget.getTeam() == TeamType.NONE)))
		{
			return false;
		}
		if ((player.getTeam() != TeamType.NONE) && (pcTarget.getTeam() != TeamType.NONE) && (player.getTeam() != pcTarget.getTeam()))
		{
			return false;
		}
		if ((player.isInOlympiadMode()) || (pcTarget.isInOlympiadMode()))
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		for (GlobalEvent e : player.getEvents())
		{
			if (!e.canRessurect(player, target, forceUse))
			{
				player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
		}

		if (!player.getPermissions().canResurrect(pcTarget, forceUse, false, true))
		{
			player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		boolean playerInSiegeZone = player.isInZone(Zone.ZoneType.SIEGE);
		boolean targetInSiegeZone = target.isInZone(Zone.ZoneType.SIEGE);
		boolean playerClan = player.getClan() != null;
		boolean targetClan = target.getClan() != null;

		if ((playerInSiegeZone) || (targetInSiegeZone))
		{
			if ((!targetClan) || (!playerClan))
			{
				player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
			if (targetClan)
			{
				SiegeEvent event = target.getEvent(SiegeEvent.class);
				if (event == null)
				{
					target.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
			}
			else if (playerClan)
			{
				SiegeEvent event = player.getEvent(SiegeEvent.class);
				if (event == null)
				{
					player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
			}
		}

		if (oneTarget())
		{
			if (target.isPet())
			{
				Pair ask = pcTarget.getAskListener(false);
				ReviveAnswerListener reviveAsk = (ask != null) && ((ask.getValue() instanceof ReviveAnswerListener)) ? (ReviveAnswerListener) ask.getValue() : null;
				if (reviveAsk != null)
				{
					if (reviveAsk.isForPet())
					{
						activeChar.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
					}
					else
					{
						activeChar.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
					}
					return false;
				}
				if ((!_canPet) && (_targetType != Skill.SkillTargetType.TARGET_PET))
				{
					player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
					return false;
				}
			}
			else if (target.isPlayer())
			{
				Pair ask = pcTarget.getAskListener(false);
				ReviveAnswerListener reviveAsk = (ask != null) && ((ask.getValue() instanceof ReviveAnswerListener)) ? (ReviveAnswerListener) ask.getValue() : null;

				if (reviveAsk != null)
				{
					if (reviveAsk.isForPet())
					{
						activeChar.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
					}
					else
					{
						activeChar.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
					}
					return false;
				}
				if (_targetType == Skill.SkillTargetType.TARGET_PET)
				{
					player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
					return false;
				}

				if (pcTarget.isFestivalParticipant())
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.skills.skillclasses.Resurrect", player, new Object[0]));
					return false;
				}
			}
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		double percent = _power;

		if (percent < 100 && !isHandler())
		{
			double wit_bonus = _power * (BaseStats.WIT.calcBonus(activeChar) - 1);
			percent += wit_bonus > 20 ? 20 : wit_bonus;
			if (percent > 90)
			{
				percent = 90;
			}
		}

		final Player pcActor = activeChar.getPlayer();
		for (Creature target : targets)
		{
			Loop:
			if (target != null)
			{
				if (target.getPlayer() == null)
				{
					continue;
				}

				for (GlobalEvent e : target.getEvents())
				{
					if (!e.canRessurect(pcActor, target, true))
					{
						break Loop;
					}
				}

				if (!pcActor.getPermissions().canResurrect(target, true, false, true))
				{
					continue;
				}

				if (target.isPet() && _canPet)
				{
					if (target.getPlayer() == activeChar)
					{
						((PetInstance) target).doRevive(percent);
					}
					else
					{
						target.getPlayer().reviveRequest((Player) activeChar, percent, true);
					}
				}
				else if (target.isPlayer())
				{
					if (_targetType == SkillTargetType.TARGET_PET)
					{
						continue;
					}

					Player targetPlayer = (Player) target;

					Pair<Integer, OnAnswerListener> ask = targetPlayer.getAskListener(false);
					ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue() : null;
					if ((reviveAsk != null) || targetPlayer.isFestivalParticipant())
					{
						continue;
					}

					targetPlayer.reviveRequest((Player) activeChar, percent, false);
				}
				else
				{
					continue;
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}
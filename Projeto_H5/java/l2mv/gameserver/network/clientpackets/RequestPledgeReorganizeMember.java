package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestPledgeReorganizeMember extends L2GameClientPacket
{
	// format: (ch)dSdS
	int _replace;
	String _subjectName;
	int _targetUnit;
	String _replaceName;

	@Override
	protected void readImpl()
	{
		this._replace = this.readD();
		this._subjectName = this.readS(16);
		this._targetUnit = this.readD();
		if (this._replace > 0)
		{
			this._replaceName = this.readS();
		}
	}

	@Override
	protected void runImpl()
	{
		// _log.warn("Received RequestPledgeReorganizeMember("+_arg1+","+_arg2+","+_arg3+","+_arg4+") from player "+getClient().getActiveChar().getName());

		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (!activeChar.isClanLeader())
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.ChangeAffiliations", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		UnitMember subject = clan.getAnyMember(this._subjectName);
		if (subject == null)
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.NotInYourClan", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (subject.getPledgeType() == this._targetUnit)
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.AlreadyInThatCombatUnit", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (this._targetUnit != 0 && clan.getSubUnit(this._targetUnit) == null)
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.NoSuchCombatUnit", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (clan.isAcademy(this._targetUnit))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.AcademyViaInvitation", activeChar));
			activeChar.sendActionFailed();
			return;
		}
		/*
		 * unsure for next check, but anyway as workaround before academy refactoring
		 * (needs LvlJoinedAcademy to be put on UnitMember if so, to be able relocate from academy correctly)
		 */
		if (clan.isAcademy(subject.getPledgeType()))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.CantMoveAcademyMember", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		UnitMember replacement = null;

		if (this._replace > 0)
		{
			replacement = clan.getAnyMember(this._replaceName);
			if (replacement == null)
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongClan", activeChar));
				activeChar.sendActionFailed();
				return;
			}
			if (replacement.getPledgeType() != this._targetUnit)
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongCombatUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}
			if (replacement.isSubLeader() != 0)
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterLeaderAnotherCombatUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}
		}
		else
		{
			if (clan.getUnitMembersSize(this._targetUnit) >= clan.getSubPledgeLimit(this._targetUnit))
			{
				if (this._targetUnit == Clan.SUBUNIT_MAIN_CLAN)
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
				}
				else
				{
					activeChar.sendPacket(SystemMsg.THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
				}
				activeChar.sendActionFailed();
				return;
			}

			if (subject.isSubLeader() != 0)
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeReorganizeMember.MemberLeaderAnotherUnit", activeChar));
				activeChar.sendActionFailed();
				return;
			}

		}

		SubUnit oldUnit = null;

		if (replacement != null)
		{
			oldUnit = replacement.getSubUnit();

			oldUnit.replace(replacement.getObjectId(), subject.getPledgeType());

			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(replacement));

			if (replacement.isOnline())
			{
				replacement.getPlayer().updatePledgeClass();
				replacement.getPlayer().broadcastCharInfo();
			}
		}

		oldUnit = subject.getSubUnit();

		oldUnit.replace(subject.getObjectId(), this._targetUnit);

		clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subject));

		if (subject.isOnline())
		{
			subject.getPlayer().updatePledgeClass();
			subject.getPlayer().broadcastCharInfo();
		}
	}
}
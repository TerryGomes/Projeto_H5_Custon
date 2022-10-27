package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;

public class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	// format: (ch)Sd
	private int _powerGrade;
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
		this._powerGrade = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || this._powerGrade < Clan.RANK_FIRST || this._powerGrade > Clan.RANK_LAST)
		{
			return;
		}

		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}

		if ((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS)
		{
			UnitMember member = activeChar.getClan().getAnyMember(this._name);
			if (member != null)
			{
				if (Clan.isAcademy(member.getPledgeType()))
				{
					activeChar.sendMessage("You cannot change academy member grade.");
					return;
				}
				if (this._powerGrade > 5)
				{
					member.setPowerGrade(clan.getAffiliationRank(member.getPledgeType()));
				}
				else
				{
					member.setPowerGrade(this._powerGrade);
				}
				if (member.isOnline())
				{
					member.getPlayer().sendUserInfo();
				}
			}
			else
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.NotBelongClan", activeChar));
			}
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.HaveNotAuthority", activeChar));
		}
	}
}
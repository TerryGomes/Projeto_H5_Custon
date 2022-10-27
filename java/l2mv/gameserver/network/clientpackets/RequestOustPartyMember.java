package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.DimensionalRift;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;

public class RequestOustPartyMember extends L2GameClientPacket
{
	// Format: cS
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Party party = activeChar.getParty();
		if (party == null || !activeChar.getParty().isLeader(activeChar))
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestOustPartyMember.CantOutOfGroup", activeChar));
			return;
		}

		Player member = party.getPlayerByName(this._name);

		if ((member == activeChar) || (member == null))
		{
			activeChar.sendActionFailed();
			return;
		}

		Reflection r = party.getReflection();

		if (r != null && r instanceof DimensionalRift && member.getReflection().equals(r))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", activeChar));
		}
		else if (r != null && !(r instanceof DimensionalRift))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", activeChar));
		}
		else
		{
			party.removePartyMember(member, true, false);
		}
	}
}
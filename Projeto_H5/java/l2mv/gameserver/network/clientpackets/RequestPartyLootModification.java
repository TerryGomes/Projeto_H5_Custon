package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;

public class RequestPartyLootModification extends L2GameClientPacket
{
	private byte _mode;

	@Override
	protected void readImpl()
	{
		_mode = (byte) readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || _mode < 0 || _mode > Party.ITEM_ORDER_SPOIL)
		{
			return;
		}

		Party party = activeChar.getParty();
		if (party == null || _mode == party.getLootDistribution() || party.getLeader() != activeChar)
		{
			return;
		}

		party.requestLootChange(_mode);
	}
}

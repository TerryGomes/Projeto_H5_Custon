package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

public class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if (activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
		{
			return;
		}

		for (Party party : activeChar.getParty().getCommandChannel().getParties())
		{
			Player leader = party.getLeader();
			if (leader != null && leader.getObjectId() == _objectId)
			{
				activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(party));
				break;
			}
		}
	}
}
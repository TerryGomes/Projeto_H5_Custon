package l2mv.gameserver.handler.usercommands.impl;

import l2mv.gameserver.handler.usercommands.IUserCommandHandler;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Support for /partyinfo command
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}

		Party playerParty = activeChar.getParty();
		if (!activeChar.isInParty())
		{
			return false;
		}

		Player partyLeader = playerParty.getLeader();
		if (partyLeader == null)
		{
			return false;
		}

		int memberCount = playerParty.size();
		int lootDistribution = playerParty.getLootDistribution();

		activeChar.sendPacket(SystemMsg.PARTY_INFORMATION);

		switch (lootDistribution)
		{
		case Party.ITEM_LOOTER:
			activeChar.sendPacket(SystemMsg.LOOTING_METHOD_FINDERS_KEEPERS);
			break;
		case Party.ITEM_ORDER:
			activeChar.sendPacket(SystemMsg.LOOTING_METHOD_BY_TURN);
			break;
		case Party.ITEM_ORDER_SPOIL:
			activeChar.sendPacket(SystemMsg.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
			break;
		case Party.ITEM_RANDOM:
			activeChar.sendPacket(SystemMsg.LOOTING_METHOD_RANDOM);
			break;
		case Party.ITEM_RANDOM_SPOIL:
			activeChar.sendPacket(SystemMsg.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
			break;
		}

		activeChar.sendPacket(new SystemMessage2(SystemMsg.PARTY_LEADER_C1).addString(partyLeader.getName()));
		activeChar.sendMessage(new CustomMessage("usercommandhandlers.PartyInfo.Members", activeChar).addNumber(memberCount));
		activeChar.sendPacket(SystemMsg.__DASHES__);
		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
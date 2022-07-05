package l2f.gameserver.network.clientpackets;

import l2f.gameserver.network.serverpackets.ExSendManorList;

/**
 * Format: ch
 * c (id) 0xD0
 * h (subid) 0x01
 *
 */
public class RequestManorList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		sendPacket(new ExSendManorList());
	}
}
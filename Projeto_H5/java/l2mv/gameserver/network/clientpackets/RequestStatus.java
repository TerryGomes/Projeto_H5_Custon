package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.network.serverpackets.SendStatus;

public final class RequestStatus extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		this.getClient().close(new SendStatus());
	}
}
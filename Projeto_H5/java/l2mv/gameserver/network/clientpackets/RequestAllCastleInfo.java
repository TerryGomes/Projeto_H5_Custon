package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.network.serverpackets.ExShowCastleInfo;

public class RequestAllCastleInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		getClient().getActiveChar().sendPacket(new ExShowCastleInfo());
	}
}
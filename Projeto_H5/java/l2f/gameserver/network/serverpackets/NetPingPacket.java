package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

/**
 * @author claww
 */
public class NetPingPacket extends L2GameServerPacket
{
	private final int _clientId;

	public NetPingPacket(Player cha)
	{
		_clientId = cha.getObjectId();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xD9);
		writeD(_clientId);
	}
}
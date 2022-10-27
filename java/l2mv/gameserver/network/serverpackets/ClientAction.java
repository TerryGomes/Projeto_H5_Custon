package l2mv.gameserver.network.serverpackets;

public class ClientAction extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeC(0x8F);
		// TODO d
	}
}
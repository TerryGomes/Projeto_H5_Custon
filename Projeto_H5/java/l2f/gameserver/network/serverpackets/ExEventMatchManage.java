package l2f.gameserver.network.serverpackets;

public class ExEventMatchManage extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x30);
		// TODO dccScScd[ccdSdd]
	}
}
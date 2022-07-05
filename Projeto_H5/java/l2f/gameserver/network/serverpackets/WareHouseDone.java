package l2f.gameserver.network.serverpackets;

public class WareHouseDone extends L2GameServerPacket
{

	@Override
	protected void writeImpl()
	{
		writeC(0x43);
		writeD(0); // ?
	}
}
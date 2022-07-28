package l2mv.gameserver.network.serverpackets;

public class WareHouseDone extends L2GameServerPacket
{

	@Override
	protected void writeImpl()
	{
		this.writeC(0x43);
		this.writeD(0); // ?
	}
}
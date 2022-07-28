package l2mv.gameserver.network.serverpackets;

public class ExPeriodicItemList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x87);
		this.writeD(0); // count of dd
	}
}
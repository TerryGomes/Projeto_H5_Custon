package l2mv.gameserver.network.serverpackets;

public class GMHide extends L2GameServerPacket
{
	private final int obj_id;

	public GMHide(int id)
	{
		obj_id = id; // TODO хз чей id должен посылатся, нужно эксперементировать
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x93);
		writeD(obj_id);
	}
}
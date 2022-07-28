package l2mv.gameserver.network.serverpackets;

public class GMHide extends L2GameServerPacket
{
	private final int obj_id;

	public GMHide(int id)
	{
		this.obj_id = id; // TODO хз чей id должен посылатся, нужно эксперементировать
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x93);
		this.writeD(this.obj_id);
	}
}
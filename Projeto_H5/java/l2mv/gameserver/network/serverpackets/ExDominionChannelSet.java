package l2mv.gameserver.network.serverpackets;

public class ExDominionChannelSet extends L2GameServerPacket
{
	public static final L2GameServerPacket ACTIVE = new ExDominionChannelSet(1);
	public static final L2GameServerPacket DEACTIVE = new ExDominionChannelSet(0);

	private int _active;

	public ExDominionChannelSet(int active)
	{
		_active = active;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x96);
		writeD(_active);
	}
}
package l2mv.gameserver.network.serverpackets;

public class ExNavitAdventEffect extends L2GameServerPacket
{
	private int _time;

	public ExNavitAdventEffect(int time)
	{
		_time = time;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0xE0);
		writeD(_time);
	}
}
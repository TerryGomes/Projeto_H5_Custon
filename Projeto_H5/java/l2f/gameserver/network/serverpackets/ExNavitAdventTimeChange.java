package l2f.gameserver.network.serverpackets;

public class ExNavitAdventTimeChange extends L2GameServerPacket
{
	private int _active;
	private int _time;

	public ExNavitAdventTimeChange(boolean active, int time)
	{
		_active = active ? 1 : 0;
		_time = 14400 - time;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0xE1);
		writeC(_active);
		writeD(_time); // in minutes
	}
}

package l2f.gameserver.network.serverpackets;

public class ExNavitAdventPointInfo extends L2GameServerPacket
{
	private int _points;

	public ExNavitAdventPointInfo(int points)
	{
		_points = points;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0xDF);
		writeD(_points);
	}
}
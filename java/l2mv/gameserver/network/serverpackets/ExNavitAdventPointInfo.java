package l2mv.gameserver.network.serverpackets;

public class ExNavitAdventPointInfo extends L2GameServerPacket
{
	private int _points;

	public ExNavitAdventPointInfo(int points)
	{
		this._points = points;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0xDF);
		this.writeD(this._points);
	}
}
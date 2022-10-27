package l2mv.gameserver.network.serverpackets;

public class ExNavitAdventTimeChange extends L2GameServerPacket
{
	private int _active;
	private int _time;

	public ExNavitAdventTimeChange(boolean active, int time)
	{
		this._active = active ? 1 : 0;
		this._time = 14400 - time;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0xE1);
		this.writeC(this._active);
		this.writeD(this._time); // in minutes
	}
}

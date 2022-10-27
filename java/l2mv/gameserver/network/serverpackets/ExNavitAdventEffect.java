package l2mv.gameserver.network.serverpackets;

public class ExNavitAdventEffect extends L2GameServerPacket
{
	private int _time;

	public ExNavitAdventEffect(int time)
	{
		this._time = time;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0xE0);
		this.writeD(this._time);
	}
}
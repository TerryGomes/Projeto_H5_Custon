package l2mv.gameserver.network.serverpackets;

public class ExRedSky extends L2GameServerPacket
{
	private int _duration;

	public ExRedSky(int duration)
	{
		this._duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x41); // sub id
		this.writeD(this._duration);
	}
}
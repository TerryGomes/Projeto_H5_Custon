package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

/**
 * format   dddddd
 */
public class Earthquake extends L2GameServerPacket
{
	private Location _loc;
	private int _intensity;
	private int _duration;

	public Earthquake(Location loc, int intensity, int duration)
	{
		this._loc = loc;
		this._intensity = intensity;
		this._duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xd3);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._intensity);
		this.writeD(this._duration);
		this.writeD(0x00); // Unknown
	}
}
package l2f.gameserver.network.serverpackets;

import l2f.gameserver.utils.Location;

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
		_loc = loc;
		_intensity = intensity;
		_duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xd3);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_intensity);
		writeD(_duration);
		writeD(0x00); // Unknown
	}
}
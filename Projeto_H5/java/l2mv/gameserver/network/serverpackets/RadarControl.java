package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

/**
 * Примеры пакетов:
 *
 * Ставит флажок на карте и показывает стрелку на компасе:
 * EB 00 00 00 00 01 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 * Убирает флажок и стрелку
 * EB 02 00 00 00 02 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 */
public class RadarControl extends L2GameServerPacket
{
	private int _x, _y, _z, _type, _showRadar;

	public RadarControl(int showRadar, int type, Location loc)
	{
		this(showRadar, type, loc.x, loc.y, loc.z);
	}

	public RadarControl(int showRadar, int type, int x, int y, int z)
	{
		_showRadar = showRadar; // showRadar?? 0 = showRadar; 1 = delete radar;
		_type = type; // 1 - только стрелка над головой, 2 - флажок на карте
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf1);
		writeD(_showRadar);
		writeD(_type); // maybe type
		writeD(_x); // x
		writeD(_y); // y
		writeD(_z); // z
	}
}
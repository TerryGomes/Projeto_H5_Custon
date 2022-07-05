package l2f.gameserver.network.serverpackets;

import java.util.List;

import l2f.gameserver.utils.Location;

/**
 * Format: (ch) d[ddddd]
 * Живой пример с оффа:
 * FE 46 00 01 00 00 00 FE 1F 00 00 01 00 00 00 03 A9 FF FF E7 5C FF FF 60 D5 FF FF
 */
public class ExCursedWeaponLocation extends L2GameServerPacket
{
	private List<CursedWeaponInfo> _cursedWeaponInfo;

	public ExCursedWeaponLocation(List<CursedWeaponInfo> cursedWeaponInfo)
	{
		_cursedWeaponInfo = cursedWeaponInfo;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x47);

		if (_cursedWeaponInfo.isEmpty())
		{
			writeD(0);
		}
		else
		{
			writeD(_cursedWeaponInfo.size());
			for (CursedWeaponInfo w : _cursedWeaponInfo)
			{
				writeD(w._id);
				writeD(w._status);

				writeD(w._pos.x);
				writeD(w._pos.y);
				writeD(w._pos.z);
			}
		}
	}

	public static class CursedWeaponInfo
	{
		public Location _pos;
		public int _id;
		public int _status;

		public CursedWeaponInfo(Location p, int ID, int status)
		{
			_pos = p;
			_id = ID;
			_status = status;
		}
	}
}
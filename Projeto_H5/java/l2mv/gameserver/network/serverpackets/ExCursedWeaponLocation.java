package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.utils.Location;

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
		this._cursedWeaponInfo = cursedWeaponInfo;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x47);

		if (this._cursedWeaponInfo.isEmpty())
		{
			this.writeD(0);
		}
		else
		{
			this.writeD(this._cursedWeaponInfo.size());
			for (CursedWeaponInfo w : this._cursedWeaponInfo)
			{
				this.writeD(w._id);
				this.writeD(w._status);

				this.writeD(w._pos.x);
				this.writeD(w._pos.y);
				this.writeD(w._pos.z);
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
			this._pos = p;
			this._id = ID;
			this._status = status;
		}
	}
}
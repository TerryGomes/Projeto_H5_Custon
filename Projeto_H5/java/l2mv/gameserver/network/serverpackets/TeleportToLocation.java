package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.utils.Location;

/**
 * format  dddd
 *
 * sample
 * 0000: 3a  69 08 10 48  02 c1 00 00  f7 56 00 00  89 ea ff    :i..H.....V.....
 * 0010: ff  0c b2 d8 61                                     ....a
 */
public class TeleportToLocation extends L2GameServerPacket
{
	private int _targetId;
	private Location _loc;

	public TeleportToLocation(GameObject cha, Location loc)
	{
		this._targetId = cha.getObjectId();
		this._loc = loc;
	}

	public TeleportToLocation(GameObject cha, int x, int y, int z)
	{
		this._targetId = cha.getObjectId();
		this._loc = new Location(x, y, z, cha.getHeading());
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x22);
		this.writeD(this._targetId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
		this.writeD(0x00); // IsValidation
		this.writeD(this._loc.h);
	}
}
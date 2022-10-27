package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.utils.Location;

/**
 * format   dddddd		(player id, target id, distance, startx, starty, startz)<p>
 */
public class ValidateLocation extends L2GameServerPacket
{
	private int _chaObjId;
	private Location _loc;

	public ValidateLocation(Creature cha)
	{
		this._chaObjId = cha.getObjectId();
		this._loc = cha.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x79);

		this.writeD(this._chaObjId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
	}
}
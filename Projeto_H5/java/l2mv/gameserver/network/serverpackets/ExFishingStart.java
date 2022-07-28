package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.utils.Location;

/**
 * Format (ch)ddddd
 */
public class ExFishingStart extends L2GameServerPacket
{
	private int _charObjId;
	private Location _loc;
	private int _fishType;
	private boolean _isNightLure;

	public ExFishingStart(Creature character, int fishType, Location loc, boolean isNightLure)
	{
		this._charObjId = character.getObjectId();
		this._fishType = fishType;
		this._loc = loc;
		this._isNightLure = isNightLure;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x1e);
		this.writeD(this._charObjId);
		this.writeD(this._fishType); // fish type
		this.writeD(this._loc.x); // x poisson
		this.writeD(this._loc.y); // y poisson
		this.writeD(this._loc.z); // z poisson
		this.writeC(this._isNightLure ? 0x01 : 0x00); // 0 = day lure 1 = night lure
		this.writeC(0x01); // result Button
	}
}
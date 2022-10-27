package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.Log;

public class CharMoveToLocation extends L2GameServerPacket
{
	private int _objectId, _client_z_shift;
	private Location _current;
	private Location _destination;

	public CharMoveToLocation(Creature cha)
	{
		this._objectId = cha.getObjectId();
		this._current = cha.getLoc();
		this._destination = cha.getDestination();
		if (!cha.isFlying())
		{
			this._client_z_shift = Config.CLIENT_Z_SHIFT;
		}
		if (cha.isInWater())
		{
			this._client_z_shift += Config.CLIENT_Z_SHIFT;
		}

		if (this._destination == null)
		{
			Log.debug("CharMoveToLocation: desc is null, but moving. L2Character: " + cha.getObjectId() + ":" + cha.getName() + "; Loc: " + this._current);
			this._destination = this._current;
		}
	}

	public CharMoveToLocation(int objectId, Location from, Location to)
	{
		this._objectId = objectId;
		this._current = from;
		this._destination = to;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x2f);

		this.writeD(this._objectId);

		this.writeD(this._destination.x);
		this.writeD(this._destination.y);
		this.writeD(this._destination.z + this._client_z_shift);

		this.writeD(this._current.x);
		this.writeD(this._current.y);
		this.writeD(this._current.z + this._client_z_shift);
	}
}
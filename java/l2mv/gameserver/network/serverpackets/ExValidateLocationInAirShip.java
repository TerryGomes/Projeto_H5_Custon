package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class ExValidateLocationInAirShip extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public ExValidateLocationInAirShip(Player cha)
	{
		this._playerObjectId = cha.getObjectId();
		this._boatObjectId = cha.getBoat().getObjectId();
		this._loc = cha.getInBoatPosition();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x6F);

		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
	}
}
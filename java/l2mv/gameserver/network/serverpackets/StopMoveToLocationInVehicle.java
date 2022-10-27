package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class StopMoveToLocationInVehicle extends L2GameServerPacket
{
	private int _boatObjectId, _playerObjectId, _heading;
	private Location _loc;

	public StopMoveToLocationInVehicle(Player player)
	{
		this._boatObjectId = player.getBoat().getObjectId();
		this._playerObjectId = player.getObjectId();
		this._loc = player.getInBoatPosition();
		this._heading = player.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x7f);
		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._heading);
	}
}
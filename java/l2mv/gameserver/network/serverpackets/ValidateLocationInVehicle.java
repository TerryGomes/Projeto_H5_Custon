package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class ValidateLocationInVehicle extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public ValidateLocationInVehicle(Player player)
	{
		this._playerObjectId = player.getObjectId();
		this._boatObjectId = player.getBoat().getObjectId();
		this._loc = player.getInBoatPosition();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x80);
		this.writeD(this._playerObjectId);
		this.writeD(this._boatObjectId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class StopMoveToLocationInVehicle extends L2GameServerPacket
{
	private int _boatObjectId, _playerObjectId, _heading;
	private Location _loc;

	public StopMoveToLocationInVehicle(Player player)
	{
		_boatObjectId = player.getBoat().getObjectId();
		_playerObjectId = player.getObjectId();
		_loc = player.getInBoatPosition();
		_heading = player.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x7f);
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_heading);
	}
}
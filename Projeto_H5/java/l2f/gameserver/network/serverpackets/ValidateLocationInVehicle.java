package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Location;

public class ValidateLocationInVehicle extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public ValidateLocationInVehicle(Player player)
	{
		_playerObjectId = player.getObjectId();
		_boatObjectId = player.getBoat().getObjectId();
		_loc = player.getInBoatPosition();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x80);
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}
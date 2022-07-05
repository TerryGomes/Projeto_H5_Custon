package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.boat.Boat;
import l2f.gameserver.utils.Location;

public class GetOnVehicle extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public GetOnVehicle(Player activeChar, Boat boat, Location loc)
	{
		_loc = loc;
		_playerObjectId = activeChar.getObjectId();
		_boatObjectId = boat.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6e);
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}
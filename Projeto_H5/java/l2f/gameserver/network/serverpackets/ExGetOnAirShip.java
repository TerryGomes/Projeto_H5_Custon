package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.boat.Boat;
import l2f.gameserver.utils.Location;

public class ExGetOnAirShip extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public ExGetOnAirShip(Player cha, Boat boat, Location loc)
	{
		_playerObjectId = cha.getObjectId();
		_boatObjectId = boat.getObjectId();
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x63);
		writeD(_playerObjectId);
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}
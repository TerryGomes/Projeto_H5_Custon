package l2f.gameserver.model.entity.boat;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExAirShipInfo;
import l2f.gameserver.network.serverpackets.ExGetOffAirShip;
import l2f.gameserver.network.serverpackets.ExGetOnAirShip;
import l2f.gameserver.network.serverpackets.ExMoveToLocationAirShip;
import l2f.gameserver.network.serverpackets.ExMoveToLocationInAirShip;
import l2f.gameserver.network.serverpackets.ExStopMoveAirShip;
import l2f.gameserver.network.serverpackets.ExStopMoveInAirShip;
import l2f.gameserver.network.serverpackets.ExValidateLocationInAirShip;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.templates.CharTemplate;
import l2f.gameserver.utils.Location;

public class AirShip extends Boat
{
	public AirShip(int objectId, CharTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public L2GameServerPacket infoPacket()
	{
		return new ExAirShipInfo(this);
	}

	@Override
	public L2GameServerPacket movePacket()
	{
		return new ExMoveToLocationAirShip(this);
	}

	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new ExMoveToLocationInAirShip(player, this, src, desc);
	}

	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new ExStopMoveAirShip(this);
	}

	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new ExStopMoveInAirShip(player);
	}

	@Override
	public L2GameServerPacket startPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ExValidateLocationInAirShip(player);
	}

	@Override
	public L2GameServerPacket getOnPacket(Player player, Location location)
	{
		return new ExGetOnAirShip(player, this, location);
	}

	@Override
	public L2GameServerPacket getOffPacket(Player player, Location location)
	{
		return new ExGetOffAirShip(player, this, location);
	}

	@Override
	public boolean isAirShip()
	{
		return true;
	}

	@Override
	public void oustPlayers()
	{
		for (Player player : _players)
		{
			oustPlayer(player, getReturnLoc(), true);
		}
	}
}

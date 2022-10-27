package l2mv.gameserver.model.entity.boat;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.GetOffVehicle;
import l2mv.gameserver.network.serverpackets.GetOnVehicle;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.MoveToLocationInVehicle;
import l2mv.gameserver.network.serverpackets.StopMove;
import l2mv.gameserver.network.serverpackets.StopMoveToLocationInVehicle;
import l2mv.gameserver.network.serverpackets.ValidateLocationInVehicle;
import l2mv.gameserver.network.serverpackets.VehicleCheckLocation;
import l2mv.gameserver.network.serverpackets.VehicleDeparture;
import l2mv.gameserver.network.serverpackets.VehicleInfo;
import l2mv.gameserver.network.serverpackets.VehicleStart;
import l2mv.gameserver.templates.CharTemplate;
import l2mv.gameserver.utils.Location;

public class Vehicle extends Boat
{
	public Vehicle(int objectId, CharTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public L2GameServerPacket startPacket()
	{
		return new VehicleStart(this);
	}

	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ValidateLocationInVehicle(player);
	}

	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return new VehicleCheckLocation(this);
	}

	@Override
	public L2GameServerPacket infoPacket()
	{
		return new VehicleInfo(this);
	}

	@Override
	public L2GameServerPacket movePacket()
	{
		return new VehicleDeparture(this);
	}

	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new MoveToLocationInVehicle(player, this, src, desc);
	}

	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new StopMove(this);
	}

	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new StopMoveToLocationInVehicle(player);
	}

	@Override
	public L2GameServerPacket getOnPacket(Player player, Location location)
	{
		return new GetOnVehicle(player, this, location);
	}

	@Override
	public L2GameServerPacket getOffPacket(Player player, Location location)
	{
		return new GetOffVehicle(player, this, location);
	}

	@Override
	public void oustPlayers()
	{
		//
	}

	@Override
	public boolean isVehicle()
	{
		return true;
	}
}

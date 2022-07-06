/*	*/
package l2mv.gameserver.network.clientpackets;

/*	*/
/*	*/ import l2mv.gameserver.model.Player;
/*	*/ import l2mv.gameserver.model.entity.boat.Boat;
/*	*/ import l2mv.gameserver.network.GameClient;
/*	*/ import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
/*	*/ import l2mv.gameserver.utils.Location;

/*	*/
/*	*/ public class CannotMoveAnymoreInVehicle extends L2GameClientPacket
/*	*/ {
	/*	*/ private Location _loc;
	/*	*/ private int _boatid;

	/*	*/
	/*	*/ public CannotMoveAnymoreInVehicle()
	/*	*/ {
		/* 10 */ _loc = new Location();
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void readImpl()
	/*	*/ {
		/* 16 */ _boatid = readD();
		/* 17 */ _loc.x = readD();
		/* 18 */ _loc.y = readD();
		/* 19 */ _loc.z = readD();
		/* 20 */ _loc.h = readD();
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void runImpl()
	/*	*/ {
		/* 26 */ Player player = ((GameClient) getClient()).getActiveChar();
		/* 27 */ if (player == null)
		{
			/* 28 */ return;
			/*	*/ }
		/* 30 */ Boat boat = player.getBoat();
		/* 31 */ if ((boat == null) || (boat.getObjectId() != _boatid))
		{
			/*	*/ return;
		}
		/* 33 */ player.setInBoatPosition(_loc);
		/* 34 */ player.setHeading(_loc.h);
		/* 35 */ player.broadcastPacket(new L2GameServerPacket[]
		{
			boat.inStopMovePacket(player)
		});
		/*	*/ }
	/*	*/ }
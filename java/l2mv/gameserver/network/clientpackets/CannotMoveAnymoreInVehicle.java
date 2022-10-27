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
		/* 10 */ this._loc = new Location();
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void readImpl()
	/*	*/ {
		/* 16 */ this._boatid = this.readD();
		/* 17 */ this._loc.x = this.readD();
		/* 18 */ this._loc.y = this.readD();
		/* 19 */ this._loc.z = this.readD();
		/* 20 */ this._loc.h = this.readD();
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void runImpl()
	/*	*/ {
		/* 26 */ Player player = ((GameClient) this.getClient()).getActiveChar();
		/* 27 */ if (player == null)
		{
			/* 28 */ return;
			/*	*/ }
		/* 30 */ Boat boat = player.getBoat();
		/* 31 */ if ((boat == null) || (boat.getObjectId() != this._boatid))
		{
			/*	*/ return;
		}
		/* 33 */ player.setInBoatPosition(this._loc);
		/* 34 */ player.setHeading(this._loc.h);
		/* 35 */ player.broadcastPacket(new L2GameServerPacket[]
		{
			boat.inStopMovePacket(player)
		});
		/*	*/ }
	/*	*/ }
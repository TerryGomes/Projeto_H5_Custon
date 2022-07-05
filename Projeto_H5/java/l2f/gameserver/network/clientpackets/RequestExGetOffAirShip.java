package l2f.gameserver.network.clientpackets;

//@Deprecated
public class RequestExGetOffAirShip extends L2GameClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _id;

	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_id = readD();
	}

	@Override
	protected void runImpl()
	{
		/*
		 * L2Player activeChar = getClient().getActiveChar();
		 * if (activeChar == null)
		 * return;
		 * L2AirShip boat = (L2AirShip) L2VehicleManager.getInstance().getBoat(_id);
		 * if (boat == null || boat.isMoving) // Не даем слезть с лодки на ходу
		 * {
		 * activeChar.sendActionFailed();
		 * return;
		 * }
		 * activeChar.setBoat(null);
		 * double angle = Util.convertHeadingToDegree(activeChar.getHeading());
		 * double radian = Math.toRadians(angle - 90);
		 * int x = _x - (int) (100 * Math.sin(radian));
		 * int y = _y + (int) (100 * Math.cos(radian));
		 * int z = GeoEngine.getHeight(x, y, _z, activeChar.getReflectionId());
		 * activeChar.setXYZ(x, y, z);
		 * activeChar.broadcastPacket(new ExGetOffAirShip(activeChar, boat, new Location(x, y, z)));
		 */
	}
}
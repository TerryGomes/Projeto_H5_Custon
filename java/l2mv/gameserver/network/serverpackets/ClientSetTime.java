package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.GameTimeController;

public class ClientSetTime extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ClientSetTime();

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xf2);
		this.writeD(GameTimeController.getInstance().getGameTime()); // time in client minutes
		this.writeD(6); // constant to match the server time( this determines the speed of the client clock)
	}
}
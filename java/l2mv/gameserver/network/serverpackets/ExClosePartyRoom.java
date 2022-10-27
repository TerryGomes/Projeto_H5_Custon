package l2mv.gameserver.network.serverpackets;

public class ExClosePartyRoom extends L2GameServerPacket
{
	public static L2GameServerPacket STATIC = new ExClosePartyRoom();

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x09);
	}
}
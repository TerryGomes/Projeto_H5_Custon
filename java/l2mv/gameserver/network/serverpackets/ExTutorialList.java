package l2mv.gameserver.network.serverpackets;

public class ExTutorialList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x6B);
		// todo writeB(new byte[128]);
	}
}
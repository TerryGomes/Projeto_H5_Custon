package l2f.gameserver.network.serverpackets;

public class TutorialCloseHtml extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new TutorialCloseHtml();

	@Override
	protected final void writeImpl()
	{
		writeC(0xa9);
	}
}
package l2f.gameserver.network.serverpackets;

public class ExShowPetitionHtml extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0xB1);
		// TODO dx[dcS]
	}
}
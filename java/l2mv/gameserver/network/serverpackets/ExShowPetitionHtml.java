package l2mv.gameserver.network.serverpackets;

public class ExShowPetitionHtml extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB1);
		// TODO dx[dcS]
	}
}
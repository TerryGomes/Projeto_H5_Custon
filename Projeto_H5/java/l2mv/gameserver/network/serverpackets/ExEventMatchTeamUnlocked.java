package l2mv.gameserver.network.serverpackets;

public class ExEventMatchTeamUnlocked extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeEx(0x06);
		// TODO dc
	}
}
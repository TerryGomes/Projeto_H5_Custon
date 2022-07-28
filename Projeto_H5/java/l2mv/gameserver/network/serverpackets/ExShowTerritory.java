package l2mv.gameserver.network.serverpackets;

public class ExShowTerritory extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x89);
		// TODO ddd[dd]
	}
}
package l2f.gameserver.network.serverpackets;

public class AttackOutOfRange extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		writeC(0x02);
	}
}
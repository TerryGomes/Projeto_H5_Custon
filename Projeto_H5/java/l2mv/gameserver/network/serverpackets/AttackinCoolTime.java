package l2mv.gameserver.network.serverpackets;

public class AttackinCoolTime extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		writeC(0x03);
	}
}
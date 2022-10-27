package l2mv.gameserver.network.serverpackets;

public class AttackinCoolTime extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		// just trigger - без аргументов
		this.writeC(0x03);
	}
}
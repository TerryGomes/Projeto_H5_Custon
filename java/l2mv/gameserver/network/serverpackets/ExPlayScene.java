package l2mv.gameserver.network.serverpackets;

/**
 * Format: ch
 * Протокол 828: при отправке пакета клиенту ничего не происходит.
 */
public class ExPlayScene extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x5c);
		this.writeD(0x00); // Kamael
	}
}
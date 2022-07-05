package l2f.gameserver.network.serverpackets;

/**
 * Пример дампа:
 * 0000: 92 2c 05 10 58 77 bb 0f 00 00 00 00 00 00 00 00    .,..Xw..........
 * 0010: 54 ff ff b0 42 fe ff 14 ff ff ff 00 00 00 00 00    T...B...........
 * 0020: 00 00 00 00 00 f0 3f 00 00 00 00 00 00 f0 3f 00    ......?.......?.
 * 0030: 00 00 00 00 00 3e 40 00 00 00 00 00 00 3e 40 00    .....>@......>@.
 * 0040: 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00       ...............
 */
public class ServerObjectInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0x92);
		// TODO ddSdddddffffdddd ServerObjectInfo ID:%d, ClassID:%d, CanBeAttacked:%d, X:%d, Y:%d, Z:%d
	}
}
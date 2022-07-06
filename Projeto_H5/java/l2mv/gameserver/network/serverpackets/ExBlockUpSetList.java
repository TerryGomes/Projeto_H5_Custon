package l2mv.gameserver.network.serverpackets;

public class ExBlockUpSetList extends L2GameServerPacket
{
	private int BlockUpType = 0; // TODO

	@Override
	protected void writeImpl()
	{
		writeEx(0x97);
		writeD(BlockUpType);
		switch (BlockUpType)
		{
		case 0:
			// dd
			// d[dS]
			// d[dS]
			break;
		case 1:
			// dddS
			break;
		case 2:
			// ddd
			break;
		case 3:
			// d
			break;
		case 4: // nothing
			break;
		case 5:
			// ddd
			break;
		case -1: // nothing
			break;
		}
	}
}
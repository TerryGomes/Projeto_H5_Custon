package l2mv.gameserver.network.serverpackets;

public class ExBlockUpSetState extends L2GameServerPacket
{
	private int BlockUpStateType = 0; // TODO

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x98);
		this.writeD(this.BlockUpStateType);
		switch (this.BlockUpStateType)
		{
		case 0:
			// dddddd
			break;
		case 1:
			// dd
			break;
		case 2:
			// ddd
			break;
		}
	}
}
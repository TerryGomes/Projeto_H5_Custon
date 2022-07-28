package l2mv.gameserver.network.serverpackets;

public class ExEventMatchList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x0D);
		// TODO пока не реализован даже в коиенте
	}
}
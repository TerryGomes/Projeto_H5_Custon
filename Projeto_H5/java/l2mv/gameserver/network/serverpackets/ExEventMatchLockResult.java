package l2mv.gameserver.network.serverpackets;

public class ExEventMatchLockResult extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x0B);
		// TODO пока не реализован даже в клиенте
	}
}
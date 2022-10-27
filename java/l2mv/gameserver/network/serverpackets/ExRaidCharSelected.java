package l2mv.gameserver.network.serverpackets;

public class ExRaidCharSelected extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB5);
		// just a trigger
	}
}
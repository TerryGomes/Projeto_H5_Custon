package l2f.gameserver.network.serverpackets;

/**
 * @author VISTALL
 * @date 11:33/03.07.2011
 */
public class ExGoodsInventoryChangedNotify extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExGoodsInventoryChangedNotify();

	@Override
	protected void writeImpl()
	{
		writeEx(0xE2);
	}
}

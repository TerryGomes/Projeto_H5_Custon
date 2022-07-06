package l2mv.gameserver.network.serverpackets;

/**
 * @author VISTALL
 * @date 12:11/05.03.2011
 */
public class ExDominionWarEnd extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExDominionWarEnd();

	@Override
	public void writeImpl()
	{
		writeEx(0xA4);
	}
}

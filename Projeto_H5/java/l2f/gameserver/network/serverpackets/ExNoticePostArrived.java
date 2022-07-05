package l2f.gameserver.network.serverpackets;

import l2f.gameserver.network.clientpackets.RequestExRequestReceivedPostList;

/**
 * Уведомление о получении почты. При нажатии на него клиент отправляет {@link RequestExRequestReceivedPostList}.
 */
public class ExNoticePostArrived extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC_TRUE = new ExNoticePostArrived(1);
	public static final L2GameServerPacket STATIC_FALSE = new ExNoticePostArrived(0);

	private int _anim;

	public ExNoticePostArrived(int useAnim)
	{
		_anim = useAnim;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xA9);

		writeD(_anim); // 0 - просто показать уведомление, 1 - с красивой анимацией
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.network.clientpackets.RequestExRequestReceivedPostList;

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
		this._anim = useAnim;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA9);

		this.writeD(this._anim); // 0 - просто показать уведомление, 1 - с красивой анимацией
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.clientpackets.RequestExCancelSentPost;
import l2mv.gameserver.network.clientpackets.RequestExRequestSentPost;

/**
 * Просмотр собственного отправленного письма. Шлется в ответ на {@link RequestExRequestSentPost}.
 * При нажатии на кнопку Cancel клиент шлет {@link RequestExCancelSentPost}.
 * @see ExReplyReceivedPost
 */
public class ExReplySentPost extends L2GameServerPacket
{
	private final Mail mail;

	public ExReplySentPost(Mail mail)
	{
		this.mail = mail;
	}

	// ddSSS dx[hddQdddhhhhhhhhhh] Qd
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAD);

		this.writeD(this.mail.getMessageId()); // id письма
		this.writeD(this.mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		this.writeS(this.mail.getReceiverName()); // кому
		this.writeS(this.mail.getTopic()); // топик
		this.writeS(this.mail.getBody()); // тело

		this.writeD(this.mail.getAttachments().size()); // количество приложенных вещей
		for (ItemInstance item : this.mail.getAttachments())
		{
			this.writeItemInfo(item);
			this.writeD(item.getObjectId());
		}

		this.writeQ(this.mail.getPrice()); // для писем с оплатой - цена
		this.writeD(0); // ?
	}
}
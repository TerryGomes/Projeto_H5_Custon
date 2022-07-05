package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.clientpackets.RequestExCancelSentPost;
import l2f.gameserver.network.clientpackets.RequestExRequestSentPost;

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
		writeEx(0xAD);

		writeD(mail.getMessageId()); // id письма
		writeD(mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		writeS(mail.getReceiverName()); // кому
		writeS(mail.getTopic()); // топик
		writeS(mail.getBody()); // тело

		writeD(mail.getAttachments().size()); // количество приложенных вещей
		for (ItemInstance item : mail.getAttachments())
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
		}

		writeQ(mail.getPrice()); // для писем с оплатой - цена
		writeD(0); // ?
	}
}
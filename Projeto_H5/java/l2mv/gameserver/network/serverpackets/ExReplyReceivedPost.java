package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.clientpackets.RequestExReceivePost;
import l2mv.gameserver.network.clientpackets.RequestExRejectPost;
import l2mv.gameserver.network.clientpackets.RequestExRequestReceivedPost;

/**
 * Просмотр полученного письма. Шлется в ответ на {@link RequestExRequestReceivedPost}.
 * При попытке забрать приложенные вещи клиент шлет {@link RequestExReceivePost}.
 * При возврате письма клиент шлет {@link RequestExRejectPost}.
 * @see ExReplySentPost
 */
public class ExReplyReceivedPost extends L2GameServerPacket
{
	private final Mail mail;

	public ExReplyReceivedPost(Mail mail)
	{
		this.mail = mail;
	}

	// dddSSS dx[hddQdddhhhhhhhhhh] Qdd
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAB);

		this.writeD(this.mail.getMessageId()); // id письма
		this.writeD(this.mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо
		this.writeD(this.mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable

		this.writeS(this.mail.getSenderName()); // от кого
		this.writeS(this.mail.getTopic()); // топик
		this.writeS(this.mail.getBody()); // тело

		this.writeD(this.mail.getAttachments().size()); // количество приложенных вещей
		for (ItemInstance item : this.mail.getAttachments())
		{
			this.writeItemInfo(item);
			this.writeD(item.getObjectId());
		}

		this.writeQ(this.mail.getPrice()); // для писем с оплатой - цена
		this.writeD(this.mail.getAttachments().size() > 0 ? 1 : 0); // 1 - письмо можно вернуть
		this.writeD(this.mail.getType().ordinal()); // 1 - на письмо нельзя отвечать, его нельзя вернуть, в отправителе значится news informer (или "****" если установлен флаг в начале
													// пакета)
	}
}
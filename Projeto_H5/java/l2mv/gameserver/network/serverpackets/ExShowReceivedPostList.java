package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.commons.collections.CollectionUtils;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.clientpackets.RequestExDeleteReceivedPost;
import l2mv.gameserver.network.clientpackets.RequestExPostItemList;
import l2mv.gameserver.network.clientpackets.RequestExRequestReceivedPost;
import l2mv.gameserver.network.clientpackets.RequestExRequestReceivedPostList;

/**
 * Появляется при нажатии на кнопку "почта" или "received mail", входящие письма
 * <br> Ответ на {@link RequestExRequestReceivedPostList}.
 * <br> При нажатии на письмо в списке шлется {@link RequestExRequestReceivedPost} а в ответ {@link ExReplyReceivedPost}.
 * <br> При попытке удалить письмо шлется {@link RequestExDeleteReceivedPost}.
 * <br> При нажатии кнопки send mail шлется {@link RequestExPostItemList}.
 * @see ExShowSentPostList аналогичный список отправленной почты
 */
public class ExShowReceivedPostList extends L2GameServerPacket
{
	private final List<Mail> mails;

	public ExShowReceivedPostList(Player cha)
	{
		this.mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
		CollectionUtils.eqSort(this.mails);
	}

	// d dx[dSSddddddd]
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAA);
		this.writeD((int) (System.currentTimeMillis() / 1000L));
		this.writeD(this.mails.size()); // number of letters
		for (Mail mail : this.mails)
		{
			this.writeD(mail.getMessageId()); // a unique id letters
			this.writeS(mail.getTopic()); // the topic
			this.writeS(mail.getSenderName()); // sender
			this.writeD(mail.isPayOnDelivery() ? 1 : 0); // 1 if there is a letter requires payment
			this.writeD(mail.getExpireTime()); // time actually writing
			this.writeD(mail.isUnread() ? 1 : 0); // the letter was not read - it can not be removed and it is highlighted in bright color
			this.writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable
			this.writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - letter of application, 0 - a letter
			// TODO [VISTALL] returned
			this.writeD(0x00); // if 1 and then the next option 1, then the sender will "****", if then 2 then the next parameter is ignored
			this.writeD(mail.getType().ordinal()); // 1 - the sender is listed "**News Informer**"
			this.writeD(0x00);
		}
	}
}
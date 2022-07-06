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
		mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
		CollectionUtils.eqSort(mails);
	}

	// d dx[dSSddddddd]
	@Override
	protected void writeImpl()
	{
		writeEx(0xAA);
		writeD((int) (System.currentTimeMillis() / 1000L));
		writeD(mails.size()); // number of letters
		for (Mail mail : mails)
		{
			writeD(mail.getMessageId()); // a unique id letters
			writeS(mail.getTopic()); // the topic
			writeS(mail.getSenderName()); // sender
			writeD(mail.isPayOnDelivery() ? 1 : 0); // 1 if there is a letter requires payment
			writeD(mail.getExpireTime()); // time actually writing
			writeD(mail.isUnread() ? 1 : 0); // the letter was not read - it can not be removed and it is highlighted in bright color
			writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable
			writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - letter of application, 0 - a letter
			// TODO [VISTALL] returned
			writeD(0x00); // if 1 and then the next option 1, then the sender will "****", if then 2 then the next parameter is ignored
			writeD(mail.getType().ordinal()); // 1 - the sender is listed "**News Informer**"
			writeD(0x00);
		}
	}
}
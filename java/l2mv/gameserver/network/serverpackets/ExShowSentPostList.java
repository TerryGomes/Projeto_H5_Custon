package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.commons.collections.CollectionUtils;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.clientpackets.RequestExDeleteSentPost;
import l2mv.gameserver.network.clientpackets.RequestExRequestSentPost;
import l2mv.gameserver.network.clientpackets.RequestExRequestSentPostList;

/**
 * Появляется при нажатии на кнопку "sent mail", исходящие письма
 * Ответ на {@link RequestExRequestSentPostList}
 * При нажатии на письмо в списке шлется {@link RequestExRequestSentPost}, а в ответ {@link ExReplySentPost}.
 * При нажатии на "delete" шлется {@link RequestExDeleteSentPost}.
 * @see ExShowReceivedPostList аналогичный список принятой почты
 */
public class ExShowSentPostList extends L2GameServerPacket
{
	private final List<Mail> mails;

	public ExShowSentPostList(Player cha)
	{
		this.mails = MailDAO.getInstance().getSentMailByOwnerId(cha.getObjectId());
		CollectionUtils.eqSort(this.mails);
	}

	// d dx[dSSddddd]
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xAC);
		this.writeD((int) (System.currentTimeMillis() / 1000L));
		this.writeD(this.mails.size()); // количество писем
		for (Mail mail : this.mails)
		{
			this.writeD(mail.getMessageId()); // уникальный id письма
			this.writeS(mail.getTopic()); // топик
			this.writeS(mail.getReceiverName()); // получатель
			this.writeD(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
			this.writeD(mail.getExpireTime()); // время действительности письма
			this.writeD(mail.isUnread() ? 1 : 0); // ?
			this.writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable
			this.writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
		}
	}
}
package l2mv.gameserver.network.clientpackets;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Запрос на удаление полученных сообщений. Удалить можно только письмо без вложения. Отсылается при нажатии на "delete" в списке полученных писем.
 * @see ExShowReceivedPostList
 * @see RequestExDeleteSentPost
 */
public class RequestExDeleteReceivedPost extends L2GameClientPacket
{
	private int _count;
	private int[] _list;

	/**
	 * format: dx[d]
	 */
	@Override
	protected void readImpl()
	{
		this._count = this.readD();
		if (this._count * 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._list = new int[this._count]; // количество элементов для удаления
		for (int i = 0; i < this._count; i++)
		{
			this._list[i] = this.readD(); // уникальный номер письма
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._count == 0)
		{
			return;
		}

		List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId());
		if (!mails.isEmpty())
		{
			for (Mail mail : mails)
			{
				if (ArrayUtils.contains(this._list, mail.getMessageId()))
				{
					if (mail.getAttachments().isEmpty())
					{
						MailDAO.getInstance().deleteReceivedMailByMailId(activeChar.getObjectId(), mail.getMessageId());
					}
				}
			}
		}

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}
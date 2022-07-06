package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExReplySentPost;
import l2mv.gameserver.network.serverpackets.ExShowSentPostList;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}.
 * В ответ шлется {@link ExReplySentPost}.
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD(); // id письма
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
		if (mail != null)
		{
			activeChar.sendPacket(new ExReplySentPost(mail));
			return;
		}

		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}
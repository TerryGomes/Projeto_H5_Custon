package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExNoticePostArrived;
import l2mv.gameserver.network.serverpackets.ExReplyReceivedPost;
import l2mv.gameserver.network.serverpackets.ExShowReceivedPostList;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Шлется клиентом как запрос на отказ принять письмо из {@link ExReplyReceivedPost}. Если к письму приложены вещи то их надо вернуть отправителю.
 */
public class RequestExRejectPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
			return;
		}

		if (activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		if (!activeChar.isInPeaceZone() && activeChar.getAccessLevel() <= 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_IN_A_NONPEACE_ZONE_LOCATION);
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if (activeChar.isJailed())
		{
			activeChar.sendMessage("You cannot do that while in jail");
			return;
		}

		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
		if (mail != null)
		{
			if (mail.getType() != Mail.SenderType.NORMAL || mail.getAttachments().isEmpty())
			{
				activeChar.sendActionFailed();
				return;
			}

			int expireTime = 360 * 3600 + (int) (System.currentTimeMillis() / 1000L); // TODO [G1ta0] хардкод времени актуальности почты

			Mail reject = mail.reject();
			mail.delete();
			reject.setExpireTime(expireTime);
			reject.save();

			Player sender = World.getPlayer(reject.getReceiverId());
			if (sender != null)
			{
				sender.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			}
		}

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}
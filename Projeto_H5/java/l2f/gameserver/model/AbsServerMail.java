package l2f.gameserver.model;

import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.ExNoticePostArrived;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.ItemFunctions;

public abstract class AbsServerMail
{
	protected final Mail _mail;

	public AbsServerMail(Player player, int itemId, int toGive)
	{
		_mail = new Mail();
		prepare();
		_mail.setSenderId(1);
		_mail.setSenderName("System");
		_mail.setReceiverId(player.getObjectId());
		_mail.setReceiverName(player.getName());
		_mail.setType(Mail.SenderType.NEWS_INFORMER);
		_mail.setUnread(true);
		_mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
		if (toGive > 0)
		{
			final ItemInstance item = ItemFunctions.createItem(itemId);
			item.setCount(toGive);
			item.save();
			_mail.addAttachment(item);
		}
		_mail.save();
		player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		player.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
	}

	protected abstract void prepare();
}
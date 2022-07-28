package l2mv.gameserver.network.clientpackets;

import java.util.Set;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExReplySentPost;
import l2mv.gameserver.network.serverpackets.ExShowSentPostList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Запрос на удаление письма с приложениями. Возвращает приложения отправителю на личный склад и удаляет письмо. Ответ на кнопку Cancel в {@link ExReplySentPost}.
 */
public class RequestExCancelSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		this.postId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
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

		if (!activeChar.isInPeaceZone())
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

		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), this.postId);
		if (mail != null)
		{
			if (mail.getAttachments().isEmpty())
			{
				activeChar.sendActionFailed();
				return;
			}
			activeChar.getInventory().writeLock();
			try
			{
				int slots = 0;
				long weight = 0;
				for (ItemInstance item : mail.getAttachments())
				{
					weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), item.getTemplate().getWeight()));
					if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
					{
						slots++;
					}
				}

				if (!activeChar.getInventory().validateWeight(weight) || !activeChar.getInventory().validateCapacity(slots))
				{
					activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
					return;
				}

				ItemInstance[] items;
				Set<ItemInstance> attachments = mail.getAttachments();

				synchronized (attachments)
				{
					items = mail.getAttachments().toArray(new ItemInstance[attachments.size()]);
					attachments.clear();
				}

				for (ItemInstance item : items)
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getItemId()).addInteger((int) item.getCount()));
					activeChar.getInventory().addItem(item, "Post Cancel");
				}

				mail.delete();

				activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_CANCELLED);
			}
			catch (ArithmeticException ae)
			{
				// TODO audit
			}
			finally
			{
				activeChar.getInventory().writeUnlock();
			}
		}
		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}
package l2mv.gameserver.network.clientpackets;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.math.SafeMath;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExShowReceivedPostList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.ItemStateLog;
import l2mv.gameserver.utils.Log;

public class RequestExReceivePost extends L2GameClientPacket
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
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
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

		if (activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), this.postId);
		if (mail != null)
		{
			activeChar.getInventory().writeLock();
			try
			{
				Set<ItemInstance> attachments = mail.getAttachments();
				ItemInstance[] items;

				if (attachments.size() > 0 && !activeChar.isInPeaceZone() && activeChar.getAccessLevel() <= 0)
				{
					activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_IN_A_NONPEACE_ZONE_LOCATION);
					return;
				}
				synchronized (attachments)
				{
					if (mail.getAttachments().isEmpty())
					{
						return;
					}

					items = mail.getAttachments().toArray(new ItemInstance[attachments.size()]);

					int slots = 0;
					long weight = 0;
					for (ItemInstance item : items)
					{
						weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), item.getTemplate().getWeight()));
						if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
						{
							slots++;
						}
					}

					if (!activeChar.getInventory().validateWeight(weight) || !activeChar.getInventory().validateCapacity(slots))
					{
						activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
						return;
					}

					if (mail.getPrice() > 0)
					{
						if (activeChar.getInventory().getCountOf(ItemTemplate.ITEM_ID_ADENA) < mail.getPrice())
						{
							activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
							return;
						}

						if (!activeChar.reduceAdena(mail.getPrice(), true, "Paid Payment Request"))
						{
							return;
						}

						Player sender = World.getPlayer(mail.getSenderId());
						if (sender != null)
						{
							sender.addAdena(mail.getPrice(), true, "Received Payment Request");
							sender.sendPacket(new SystemMessage2(SystemMsg.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL).addName(activeChar));
						}
						else
						{
							int expireTime = 360 * 3600 + (int) (System.currentTimeMillis() / 1000L);
							Mail reply = mail.reply();
							reply.setExpireTime(expireTime);

							ItemInstance item = ItemFunctions.createItem(ItemTemplate.ITEM_ID_ADENA);
							item.setOwnerId(reply.getReceiverId());
							item.setCount(mail.getPrice());
							item.setLocation(ItemLocation.MAIL);
							item.save();

							Log.logItemActions(new ItemActionLog(ItemStateLog.ADD, "ReceivedPaymentRequest(offline)", mail.getSenderName() + '[' + mail.getSenderName() + ']', item, mail.getPrice()));

							reply.addAttachment(item);
							reply.save();
						}
					}

					attachments.clear();
				}

				mail.setJdbcState(JdbcEntityState.UPDATED);
				if (StringUtils.isEmpty(mail.getBody()))
				{
					mail.delete();
				}
				else
				{
					mail.update();
				}

				for (ItemInstance item : items)
				{
					activeChar.sendMessage("You have acquired " + item.getCount() + " " + item.getTemplate().getName() + '!');
					activeChar.getInventory().addItem(item, "PostReceive");
				}

				activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_RECEIVED);
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

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}
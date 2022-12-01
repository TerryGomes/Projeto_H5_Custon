package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.database.mysql;
import l2mv.gameserver.multverso.managers.MailManager;
import l2mv.gameserver.handler.admincommands.impl.AdminMail;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExNoticePostArrived;
import l2mv.gameserver.network.serverpackets.ExReplyWritePost;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

/**
 * Request for sending a new message. In response, sends {@link ExReplyWritePost}.
 * @see RequestExPostItemList
 * @see RequestExRequestReceivedPostList
 */
public class RequestExSendPost extends L2GameClientPacket
{
	public static String NEW_LINE_SEPARATOR = "\r\n";

	private static final Logger _log = LoggerFactory.getLogger(RequestExSendPost.class);

	private int _messageType;
	private String _recieverName, _topic, _body;
	private int _count;
	private int[] _items;
	private long[] _itemQ;
	private long _price;

	/**
	 * format: SdSS dx[dQ] Q
	 */
	@Override
	protected void readImpl()
	{
		this._recieverName = this.readS(35); // the recipient's name
		this._messageType = this.readD(); // Type the letters 0 1 simple request payment
		this._topic = this.readS(Byte.MAX_VALUE); // topic
		this._body = this.readS(Short.MAX_VALUE); // body

		this._count = this.readD(); // the number of attached items
		if (this._count * 12 + 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) // TODO [G1ta0] audit
		{
			this._count = 0;
			return;
		}

		this._items = new int[this._count];
		this._itemQ = new long[this._count];

		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD(); // objectId
			this._itemQ[i] = this.readQ(); // the amount
			if (this._itemQ[i] < 1 || ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				return;
			}
		}

		this._price = this.readQ(); // price for letters requesting payment

		if (this._price < 0)
		{
			this._count = 0;
			this._price = 0;
		}
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

		// Custom
		if (activeChar.isGM() && this._recieverName.equalsIgnoreCase(AdminMail.MAIL_ALL_TEXT))
		{
			Map<Integer, Long> map = new HashMap<Integer, Long>();
			if (this._items != null && this._items.length > 0)
			{
				for (int i = 0; i < this._items.length; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(this._items[i]);
					map.put(item.getItemId(), this._itemQ[i]);
				}
			}

			for (Player p : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (p != null && p.isOnline())
				{
					Functions.sendSystemMail(p, this._topic, this._body, map);
				}
			}

			activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
			activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
			return;
		}
		else if (activeChar.isGM() && this._recieverName.equalsIgnoreCase(AdminMail.MAIL_LIST))
		{
			Map<Integer, Long> map = new HashMap<>();
			if (this._items != null && this._items.length > 0)
			{
				for (int i = 0; i < this._items.length; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(this._items[i]);
					map.put(item.getItemId(), this._itemQ[i]);
				}
			}

			int count = 0;
			for (String name : AdminMail.getMailNicks(activeChar.getObjectId()))
			{
				boolean success = Functions.sendSystemMail(name, this._topic, this._body, map);
				if (!success)
				{
					activeChar.sendMessage("Mail couldn't be sent to " + name);
				}
				else
				{
					count++;
				}
			}

			activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
			activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
			AdminMail.clearNicks(activeChar.getObjectId());
			activeChar.sendMessage("Mail was sent to " + count + " players!");
			return;
		}

		if (!Config.ALLOW_MAIL)
		{
			activeChar.sendMessage(new CustomMessage("mail.Disabled", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
			return;
		}

		if (activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		if (activeChar.getName().equalsIgnoreCase(this._recieverName))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
			return;
		}

		if (this._count > 0 && !activeChar.isInPeaceZone() && activeChar.getAccessLevel() <= 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_IN_A_NONPEACE_ZONE_LOCATION);
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

		if (Config.containsAbuseWord(this._body) || Config.containsAbuseWord(this._topic))
		{
			activeChar.sendMessage("Your mail containts prohibited words. Correct it and try again.");
			return;
		}

		if (activeChar.getLevel() < Config.ALT_MAIL_MIN_LVL)
		{
			activeChar.sendMessage(new StringBuilder().append("Mail is allowed only for characters with level higher than ").append(Config.ALT_MAIL_MIN_LVL).append(" to avoid spam!").toString());
			activeChar.sendActionFailed();
			return;
		}

		if (!activeChar.antiFlood.canMail())
		{
			activeChar.sendPacket(SystemMsg.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
			return;
		}

		// Prims - Penalty for mail system
		if (!MailManager.getInstance().canPlayerSendMail(activeChar))
		{
			return;
		}

		if (this._price > 0)
		{
			if (!activeChar.getPlayerAccess().UseTrade)
			{
				activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
				activeChar.sendActionFailed();
				return;
			}

			String tradeBan = activeChar.getVar("tradeBan");
			if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			{
				if (tradeBan.equals("-1"))
				{
					activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently", activeChar));
				}
				else
				{
					activeChar.sendMessage(new CustomMessage("common.TradeBanned", activeChar).addString(Util.formatTime((int) (Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
				}
				return;
			}
		}

		// looking for a goal and check bloklisty
		if (activeChar.isInBlockList(this._recieverName)) // those who do not blokliste helmet
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_BLOCKED_C1).addString(this._recieverName));
			return;
		}

		int recieverId;
		Player target = World.getPlayer(this._recieverName);
		if (target != null)
		{
			recieverId = target.getObjectId();
			this._recieverName = target.getName();
			if (target.isInBlockList(activeChar)) // goal blocked senders
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BLOCKED_YOU).addString(this._recieverName));
				return;
			}
		}
		else
		{
			recieverId = CharacterDAO.getInstance().getObjectIdByName(this._recieverName);
			if (recieverId > 0)
			{
				// TODO [G1ta0] adjust _recieverName
				if (mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) > 0) // goal blocked senders
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BLOCKED_YOU).addString(this._recieverName));
					return;
				}
			}
		}

		if (recieverId == 0) // did not find the goal?
		{
			activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
			return;
		}

		int expireTime = (this._messageType == 1 ? 12 : 72) * 3600 + (int) (System.currentTimeMillis() / 1000L); // TODO [G1ta0] hardcoding time urgency mail

		if (this._count > 8) // the client does not send more than 8 items
		{
			activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		long serviceCost = 100 + this._count * 1000; // TODO [G1ta0] hardcoding price for mail

		List<ItemInstance> attachments = new ArrayList<ItemInstance>();

		activeChar.getInventory().writeLock();
		try
		{
			if (activeChar.getAdena() < serviceCost)
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
				return;
			}

			// prepare attachement
			if (this._count > 0)
			{
				for (int i = 0; i < this._count; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(this._items[i]);

					if (item == null || item.getCount() < this._itemQ[i] || (item.getItemId() == ItemTemplate.ITEM_ID_ADENA && item.getCount() < this._itemQ[i] + serviceCost) || !item.canBeTraded(activeChar))
					{
						activeChar.sendPacket(SystemMsg.THE_ITEM_THAT_YOURE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISNT_PROPER);
						return;
					}

					if (!activeChar.getPermissions().canLoseItem(item, true))
					{
						return;
					}
				}
			}

			if (!activeChar.reduceAdena(serviceCost, true, "SendPost"))
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
				return;
			}

			if (this._count > 0)
			{
				for (int i = 0; i < this._count; i++)
				{
					ItemInstance item = activeChar.getInventory().removeItemByObjectId(this._items[i], this._itemQ[i], "SendPost");

					item.setOwnerId(activeChar.getObjectId());
					item.setLocation(ItemLocation.MAIL);
					if (item.getJdbcState().isSavable())
					{
						item.save();
					}
					else
					{
						item.setJdbcState(JdbcEntityState.UPDATED);
						item.update();
					}

					attachments.add(item);

					// Synerge - If someone sends more than 2kkk adena in a mail, log it in the console
					if (item.getItemId() == ItemTemplate.ITEM_ID_ADENA && item.getCount() >= 1000000000)
					{
						_log.warn("=============================================================");
						_log.warn("The player " + activeChar.getName() + " sent to " + this._recieverName + " in a mail Adena (" + String.format(Locale.US, "%,d", item.getCount()).replace(',', '.') + "). Possible adena botter!!!");
						_log.warn("=============================================================");
					}
				}
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		Mail mail = new Mail();
		mail.setSenderId(activeChar.getObjectId());
		mail.setSenderName(activeChar.getName());
		mail.setReceiverId(recieverId);
		mail.setReceiverName(this._recieverName);
		mail.setTopic(this._topic);
		mail.setBody(this._body);
		mail.setPrice(this._messageType > 0 ? this._price : 0);
		mail.setUnread(true);
		mail.setType(Mail.SenderType.NORMAL);
		mail.setExpireTime(expireTime);
		for (ItemInstance item : attachments)
		{
			mail.addAttachment(item);
		}
		mail.save();

		activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
		activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);

		if (target != null)
		{
			target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}

		Log.logMail(mail, activeChar.getAccessLevel() > 0);

		// Prims - Add the new mail sent to the manager
		MailManager.getInstance().addNewMailSent(activeChar);

		ItemLogHandler.getInstance().addLog(activeChar, attachments, this._recieverName, ItemActionType.MAIL);
	}
}
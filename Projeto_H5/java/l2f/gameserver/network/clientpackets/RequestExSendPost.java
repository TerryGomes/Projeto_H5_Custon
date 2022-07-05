package l2f.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.fandc.managers.MailManager;
import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.Config;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.database.mysql;
import l2f.gameserver.handler.admincommands.impl.AdminMail;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.ExNoticePostArrived;
import l2f.gameserver.network.serverpackets.ExReplyWritePost;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.Util;

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
		_recieverName = readS(35); // the recipient's name
		_messageType = readD(); // Type the letters 0 1 simple request payment
		_topic = readS(Byte.MAX_VALUE); // topic
		_body = readS(Short.MAX_VALUE); // body

		_count = readD(); // the number of attached items
		if (_count * 12 + 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) // TODO [G1ta0] audit
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];

		for (int i = 0; i < _count; i++)
		{
			_items[i] = readD(); // objectId
			_itemQ[i] = readQ(); // the amount
			if (_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				return;
			}
		}

		_price = readQ(); // price for letters requesting payment

		if (_price < 0)
		{
			_count = 0;
			_price = 0;
		}
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

		// Custom
		if (activeChar.isGM() && _recieverName.equalsIgnoreCase(AdminMail.MAIL_ALL_TEXT))
		{
			Map<Integer, Long> map = new HashMap<Integer, Long>();
			if (_items != null && _items.length > 0)
			{
				for (int i = 0; i < _items.length; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
					map.put(item.getItemId(), _itemQ[i]);
				}
			}

			for (Player p : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (p != null && p.isOnline())
				{
					Functions.sendSystemMail(p, _topic, _body, map);
				}
			}

			activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
			activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
			return;
		}
		else if (activeChar.isGM() && _recieverName.equalsIgnoreCase(AdminMail.MAIL_LIST))
		{
			Map<Integer, Long> map = new HashMap<>();
			if (_items != null && _items.length > 0)
			{
				for (int i = 0; i < _items.length; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
					map.put(item.getItemId(), _itemQ[i]);
				}
			}

			int count = 0;
			for (String name : AdminMail.getMailNicks(activeChar.getObjectId()))
			{
				boolean success = Functions.sendSystemMail(name, _topic, _body, map);
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

		if (activeChar.getName().equalsIgnoreCase(_recieverName))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
			return;
		}

		if (_count > 0 && !activeChar.isInPeaceZone() && activeChar.getAccessLevel() <= 0)
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

		if (Config.containsAbuseWord(_body) || Config.containsAbuseWord(_topic))
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

		if (_price > 0)
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
		if (activeChar.isInBlockList(_recieverName)) // those who do not blokliste helmet
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_BLOCKED_C1).addString(_recieverName));
			return;
		}

		int recieverId;
		Player target = World.getPlayer(_recieverName);
		if (target != null)
		{
			recieverId = target.getObjectId();
			_recieverName = target.getName();
			if (target.isInBlockList(activeChar)) // goal blocked senders
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BLOCKED_YOU).addString(_recieverName));
				return;
			}
		}
		else
		{
			recieverId = CharacterDAO.getInstance().getObjectIdByName(_recieverName);
			if (recieverId > 0)
			{
				// TODO [G1ta0] adjust _recieverName
				if (mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) > 0) // goal blocked senders
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BLOCKED_YOU).addString(_recieverName));
					return;
				}
			}
		}

		if (recieverId == 0) // did not find the goal?
		{
			activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
			return;
		}

		int expireTime = (_messageType == 1 ? 12 : 72) * 3600 + (int) (System.currentTimeMillis() / 1000L); // TODO [G1ta0] hardcoding time urgency mail

		if (_count > 8) // the client does not send more than 8 items
		{
			activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		long serviceCost = 100 + _count * 1000; // TODO [G1ta0] hardcoding price for mail

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
			if (_count > 0)
			{
				for (int i = 0; i < _count; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);

					if (item == null || item.getCount() < _itemQ[i] || (item.getItemId() == ItemTemplate.ITEM_ID_ADENA && item.getCount() < _itemQ[i] + serviceCost) || !item.canBeTraded(activeChar))
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

			if (_count > 0)
			{
				for (int i = 0; i < _count; i++)
				{
					ItemInstance item = activeChar.getInventory().removeItemByObjectId(_items[i], _itemQ[i], "SendPost");

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
						_log.warn("The player " + activeChar.getName() + " sent to " + _recieverName + " in a mail Adena (" + String.format(Locale.US, "%,d", item.getCount()).replace(',', '.') + "). Possible adena botter!!!");
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
		mail.setReceiverName(_recieverName);
		mail.setTopic(_topic);
		mail.setBody(_body);
		mail.setPrice(_messageType > 0 ? _price : 0);
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

		ItemLogHandler.getInstance().addLog(activeChar, attachments, _recieverName, ItemActionType.MAIL);
	}
}
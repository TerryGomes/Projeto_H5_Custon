package l2mv.gameserver.utils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.text.PrintfFormat;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.clientpackets.RequestExSendPost;
import l2mv.gameserver.network.serverpackets.components.ChatType;

public class Log
{
	public static final PrintfFormat LOG_BOSS_KILLED = new PrintfFormat("%s: %s[%d] killed by %s at Loc(%d %d %d) in %s");
	public static final PrintfFormat LOG_BOSS_RESPAWN = new PrintfFormat("%s: %s[%d] scheduled for respawn in %s at %s");

	private static final Logger LOG_ENTER_WORLD = LoggerFactory.getLogger("enterWorld");
	private static final Logger LOG_CHAT = LoggerFactory.getLogger("chat");
	private static final Logger LOG_CHAT_PM = LoggerFactory.getLogger("chatPM");
	private static final Logger LOG_CHAT_SHOUT = LoggerFactory.getLogger("chatShout");
	private static final Logger LOG_CHAT_TRADE = LoggerFactory.getLogger("chatTrade");
	private static final Logger LOG_MAILS = LoggerFactory.getLogger("mails");
	private static final Logger LOG_GM_MAILS = LoggerFactory.getLogger("gmmails");
	private static final Logger LOG_STORE_MESSAGES = LoggerFactory.getLogger("privateStoreMessages");
	private static final Logger LOG_EVENTS = LoggerFactory.getLogger("events");
	private static final Logger LOG_GM = LoggerFactory.getLogger("gmactions");
	private static final Logger LOG_HIDDEN_GMS = LoggerFactory.getLogger("hiddenGMActions");
	private static final Logger LOG_ITEMS = LoggerFactory.getLogger("item");
	private static final Logger LOG_GM_ITEMS = LoggerFactory.getLogger("gmItem");
	private static final Logger LOG_DONATION_COINS = LoggerFactory.getLogger("itemDonationCoins");
	private static final Logger LOG_GAME = LoggerFactory.getLogger("game");
	private static final Logger LOG_SERVICE = LoggerFactory.getLogger("service");
	private static final Logger LOG_DEBUG = LoggerFactory.getLogger("debug");
	private static final Logger LOG_VOTES = LoggerFactory.getLogger("votes");
	private static final Logger LOG_STREAM = LoggerFactory.getLogger("stream");
	private static final Logger LOG_FACEBOOK = LoggerFactory.getLogger("facebook");
	private static final Logger LOG_ILLEGAL_ACTIVITY = LoggerFactory.getLogger("illegalActivity");
	private static final Logger LOG_BOTS = LoggerFactory.getLogger("bot");

	private static final String[] SERVER_RESTART_LOG_TEXTS = new String[]
	{
		"",
		"==========================================================",
		"                     Server Shutdown!",
		"==========================================================",
		""
	};

	public static final String Create = "Create";
	public static final String Delete = "Delete";
	public static final String Drop = "Drop";
	public static final String PvPDrop = "PvPDrop";
	public static final String Crystalize = "Crystalize";
	public static final String EnchantFail = "EnchantFail";
	public static final String Pickup = "Pickup";
	public static final String PartyPickup = "PartyPickup";
	public static final String PrivateStoreBuy = "PrivateStoreBuy";
	public static final String PrivateStoreSell = "PrivateStoreSell";
	public static final String TradeBuy = "TradeBuy";
	public static final String TradeSell = "TradeSell";
	public static final String PostRecieve = "PostRecieve";
	public static final String PostSend = "PostSend";
	public static final String PostCancel = "PostCancel";
	public static final String PostExpire = "PostExpire";
	public static final String RefundSell = "RefundSell";
	public static final String RefundReturn = "RefundReturn";
	public static final String WarehouseDeposit = "WarehouseDeposit";
	public static final String WarehouseWithdraw = "WarehouseWithdraw";
	public static final String FreightWithdraw = "FreightWithdraw";
	public static final String FreightDeposit = "FreightDeposit";
	public static final String ClanWarehouseDeposit = "ClanWarehouseDeposit";
	public static final String ClanWarehouseWithdraw = "ClanWarehouseWithdraw";

	public static void logServerShutdown()
	{
		for (String text : SERVER_RESTART_LOG_TEXTS)
		{
			LOG_ENTER_WORLD.info(text);
		}
	}

	public static void logToConsole(String msg)
	{
		LOG_GAME.info(msg);
	}

	public static void add(PrintfFormat fmt, Object[] o, String cat)
	{
		add(fmt.sprintf(o), cat);
	}

	public static void add(String fmt, Object[] o, String cat)
	{
		add(new PrintfFormat(fmt).sprintf(o), cat);
	}

	public static void add(String text, String cat, Player player)
	{
		final StringBuilder output = new StringBuilder();
		output.append(cat);
		if (player != null)
		{
			output.append(' ');
			output.append(player);
		}
		output.append(' ');
		output.append(text);

		LOG_GAME.info(output.toString());
	}

	public static void add(String text, String cat)
	{
		add(text, cat, null);
	}

	public static void debug(String text)
	{
		LOG_DEBUG.debug(text);
	}

	public static void debug(String text, Throwable t)
	{
		LOG_DEBUG.debug(text, t);
	}

	public static void reachedProtocolVersion(GameClient client, String hwid)
	{
		LOG_ENTER_WORLD.info(client.toString() + " has been connected to Game Server. Gained HWID: " + hwid);
	}

	public static void logEnterWorld(Player player)
	{
		LOG_ENTER_WORLD.info(player.toString() + " Entered Game. Ip: " + player.getIP() + " HWID: " + player.getHWID() + " System Version: " + player.getNetConnection().getPatchVersion());
	}

	public static void logLeftGame(Player player, String leaveType)
	{
		final long minutesInGame = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - player.getOnlineBeginTime());
		LOG_ENTER_WORLD.info(player.toString() + " Left Game(" + leaveType + "). Ip: " + player.getIP() + " HWID: " + player.getHWID() + " Played for: " + TimeUtils.minutesToFullString(minutesInGame));
	}

	public static void logClientProtection(String text)
	{
		LOG_ENTER_WORLD.info(text);
	}

	public static void logIllegalActivity(String text)
	{
		LOG_ILLEGAL_ACTIVITY.warn(text);
	}

	public static void bots(String text)
	{
		LOG_BOTS.info(text);
	}

	public static void logChat(ChatType type, String player, String target, String text)
	{
		if (!Config.LOG_CHAT)
		{
			return;
		}
		final StringBuilder output = new StringBuilder();
		output.append(type);
		output.append(' ');
		output.append('[');
		output.append(player);
		if (target != null)
		{
			output.append(" -> ");
			output.append(target);
		}
		output.append(']');
		output.append("\t\t\t");
		output.append(text);
		final String msg = output.toString();
		if (type == ChatType.TELL)
		{
			LOG_CHAT_PM.info(msg);
		}
		else if (type == ChatType.SHOUT)
		{
			LOG_CHAT_SHOUT.info(msg);
		}
		else if (type == ChatType.TRADE)
		{
			LOG_CHAT_TRADE.info(msg);
		}
		LOG_CHAT.info(msg);
	}

	public static void logMail(Mail mail, boolean isGm)
	{
		final StringBuilder output = new StringBuilder();
		output.append(mail.getSenderName()).append("[").append(mail.getSenderId()).append("]");
		output.append(" --> ");
		output.append(mail.getReceiverName()).append("[").append(mail.getReceiverId()).append("]");
		output.append(" Topic: \"").append(mail.getTopic()).append("\"");
		output.append(" Body: \"").append(mail.getBody().replace(RequestExSendPost.NEW_LINE_SEPARATOR, "<br>")).append("\"");

		if (isGm)
		{
			LOG_GM_MAILS.info(output.toString());
		}
		else
		{
			LOG_MAILS.info(output.toString());
		}
	}

	public static void logPrivateStoreMessage(Player player, String message)
	{
		LOG_STORE_MESSAGES.info("Player " + player.toString() + " setting Store with msg:\t\t\t" + StringUtils.defaultString(message));
	}

	public static void LogEvents(String name, String action, String player, String target, String text)
	{
		final StringBuilder output = new StringBuilder();
		output.append(name);
		output.append(": ");
		output.append(action);
		output.append(' ');
		output.append('[');
		output.append(player);
		if (target != null)
		{
			output.append(" -> ");
			output.append(target);
		}
		output.append(']');
		output.append(' ');
		output.append(text);

		LOG_EVENTS.info(output.toString());
	}

	public static void logRuVotes(String msg)
	{
		LOG_VOTES.info("Russian Voting Engine: " + msg);
	}

	public static void logStream(String msg)
	{
		if (ConfigHolder.getBool("AllowStreamLogs"))
		{
			LOG_STREAM.info(msg);
		}
	}

	public static void logFacebook(String msg)
	{
		LOG_FACEBOOK.info(msg);
	}

	public static void LogCommand(Player player, GameObject target, String command, boolean success)
	{
		final StringBuilder output = new StringBuilder();
		if (success)
		{
			output.append("SUCCESS");
		}
		else
		{
			output.append("FAIL   ");
		}

		output.append(' ');
		output.append(player);
		if (target != null)
		{
			output.append(" -> ");
			output.append(target);
		}
		output.append(' ');
		output.append(command);

		LOG_GM.info(output.toString());
	}

	public static void logHiddenGMActions(String text)
	{
		LOG_HIDDEN_GMS.warn(text);
	}

	public static void logItemActions(String actionTitle, Collection<ItemActionLog> actions)
	{
		final ItemActionLog[] actionsArray = actions.toArray(new ItemActionLog[actions.size()]);
		logItemActions(actionTitle, actionsArray);
	}

	public static void logItemActions(Collection<ItemActionLog> actions)
	{
		final ItemActionLog[] actionsArray = actions.toArray(new ItemActionLog[actions.size()]);
		logItemActions("", actionsArray);
	}

	public static void logItemActions(ItemActionLog... actions)
	{
		logItemActions("", actions);
	}

	public static void logItemActions(String actionTitle, ItemActionLog... actions)
	{
		if (!Config.ALLOW_ITEMS_LOGGING || actions.length == 0)
		{
			return;
		}

		if (containsItemId(actions, 37000))
		{
			if (!actionTitle.isEmpty())
			{
				LOG_DONATION_COINS.info(actionTitle);
			}
			for (ItemActionLog itemLog : actions)
			{
				LOG_DONATION_COINS.info(itemLog.toString());
			}
		}

		// Separate gm items from normal player items
		if (actions[0].isGm())
		{
			if (!actionTitle.isEmpty())
			{
				LOG_GM_ITEMS.info(actionTitle);
			}
			for (ItemActionLog itemLog : actions)
			{
				LOG_GM_ITEMS.info(itemLog.toString());
			}
		}
		else
		{
			if (!actionTitle.isEmpty())
			{
				LOG_ITEMS.info(actionTitle);
			}
			for (ItemActionLog itemLog : actions)
			{
				LOG_ITEMS.info(itemLog.toString());
			}
		}
	}

	private static boolean containsItemId(ItemActionLog[] actions, int itemId)
	{
		for (ItemActionLog action : actions)
		{
			if (action.getItem().startsWith(itemId + " "))
			{
				return true;
			}
		}
		return false;
	}

	public static void LogPetition(Player fromChar, Integer Petition_type, String Petition_text)
	{
	}

	public static void LogAudit(Player player, String type, String msg)
	{
	}

	public static void service(String text, String cat)
	{
		final StringBuilder output = new StringBuilder();
		output.append(cat);
		output.append(": ");
		output.append(text);
		LOG_SERVICE.info(output.toString());
	}
}
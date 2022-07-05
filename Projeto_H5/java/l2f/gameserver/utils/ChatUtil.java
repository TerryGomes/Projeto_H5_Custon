package l2f.gameserver.utils;

import java.util.EnumMap;
import java.util.Map;

import l2f.gameserver.data.StringHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;

public class ChatUtil
{
	public static void sendGlobalEventString(AbstractFightClub event, String stringName, Object... replacements)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			final String msg = StringHolder.getNotNull(language, stringName, replacements);
			packetPerLanguage.put(language, new Say2(0, ChatType.CRITICAL_ANNOUNCE, event.getName(), msg));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static void sendGlobalEventString(String senderName, String stringName, Object... replacements)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			final String msg = StringHolder.getNotNull(language, stringName, replacements);
			packetPerLanguage.put(language, new Say2(0, ChatType.CRITICAL_ANNOUNCE, senderName, msg));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static void sendGlobalEventString(Map<Language, String> senderName, String stringName, Object... replacements)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			final String msg = StringHolder.getNotNull(language, stringName, replacements);
			packetPerLanguage.put(language, new Say2(0, ChatType.CRITICAL_ANNOUNCE, senderName.get(language), msg));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static void sendGlobalEventString(AbstractFightClub event, Map<Language, String> msg)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			packetPerLanguage.put(language, new Say2(0, ChatType.CRITICAL_ANNOUNCE, event.getName(), msg.get(language)));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static void sendGlobalEventString(Map<Language, String> senderName, Map<Language, String> msg)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			packetPerLanguage.put(language, new Say2(0, ChatType.CRITICAL_ANNOUNCE, senderName.get(language), msg.get(language)));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static void sendStringToAll(Map<Language, String> senderName, Map<Language, String> msg, ChatType chatType)
	{
		final Map<Language, IStaticPacket> packetPerLanguage = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language language : Language.values())
		{
			packetPerLanguage.put(language, new Say2(0, chatType, senderName.get(language), msg.get(language)));
		}
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(packetPerLanguage.get(player.getLanguage()));
		}
	}

	public static Map<Language, String> getMessagePerLang(String address, Object... replacements)
	{
		final Map<Language, String> messagePerLang = new EnumMap<Language, String>(Language.class);
		for (Language lang : Language.values())
		{
			messagePerLang.put(lang, StringHolder.getNotNull(lang, address, replacements));
		}
		return messagePerLang;
	}

	public static void broadcastToWorldSystemMsg(Map<Language, String> messages)
	{
		final Map<Language, SystemMessage> msgPerLang = new EnumMap<Language, SystemMessage>(Language.class);
		for (Map.Entry<Language, String> entry : messages.entrySet())
		{
			msgPerLang.put(entry.getKey(), new SystemMessage(entry.getValue()));
		}
		for (Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			player.sendPacket(msgPerLang.get(player.getLanguage()));
		}
	}
}

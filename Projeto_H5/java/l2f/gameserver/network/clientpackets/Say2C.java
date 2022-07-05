package l2f.gameserver.network.clientpackets;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graphbuilder.math.Expression;
import com.graphbuilder.math.ExpressionParseException;
import com.graphbuilder.math.ExpressionTree;
import com.graphbuilder.math.VarMap;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.cache.ItemInfoCache;
import l2f.gameserver.dao.EmotionsTable;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.instancemanager.PetitionManager;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.forum.ForumMemberGroup;
import l2f.gameserver.model.entity.forum.ForumMembersHolder;
import l2f.gameserver.model.entity.forum.ShoutboxHandler;
import l2f.gameserver.model.entity.olympiad.OlympiadGame;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.matching.MatchingRoom;
import l2f.gameserver.network.serverpackets.ActionFail;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.FakePlayersTable;
import l2f.gameserver.utils.AutoHuntingPunish;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.MapUtils;
import l2f.gameserver.utils.Strings;
import l2f.gameserver.utils.Util;

public class Say2C extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(Say2C.class);

	/** RegExp для кэширования ссылок на предметы, пример ссылки: \b\tType=1 \tID=268484598 \tColor=0 \tUnderline=0 \tTitle=\u001BAdena\u001B\b */
	public static final Pattern EX_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\u001B(.[^\u001B]*)[^\b]");
	private static final Pattern SKIP_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");

	private String _text;
	private ChatType _type;
	private String _target;

	@Override
	protected void readImpl()
	{
		_text = readS(Config.CHAT_MESSAGE_MAX_LEN);
		_type = l2f.commons.lang.ArrayUtils.valid(ChatType.VALUES, readD());
		_target = _type == ChatType.TELL ? readS(Config.CNAME_MAXLEN) : null;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isBeingPunished() && activeChar.getBotPunishType() == AutoHuntingPunish.Punish.CHATBAN)
		{
			if (activeChar.getPlayerPunish().canTalk() && activeChar.getBotPunishType() == AutoHuntingPunish.Punish.CHATBAN)
			{
				activeChar.endPunishment();
			}
			else if (activeChar.getBotPunishType() == AutoHuntingPunish.Punish.CHATBAN)
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_CHATTING_WILL_BE_BLOCKED_FOR_10_MINUTES);
				return;
			}
		}

		activeChar.isntAfk();

		// Prims - Players that are blocked cannot use the chat or commands
		if (_type == null || _text == null || _text.length() == 0 || activeChar.isBlocked())
		{
			activeChar.sendActionFailed();
			return;
		}

		/**
		_text = _text.replaceAll("\\\\n", "\n");

		if (_text.contains("\n"))
		{
			String[] lines = _text.split("\n");
			_text = StringUtils.EMPTY;
			for (int i = 0; i < lines.length; i++)
			{
				lines[i] = lines[i].trim();
				if (lines[i].length() == 0)
					continue;
				if (_text.length() > 0)
					_text += "\n  >";
				_text += lines[i];
			}
		}
		 */
		_text = _text.replaceAll("\\n", "").replaceAll("\n", "");

		if (_text.length() == 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		// Synerge - Trivia event
		if (Functions.isEventStarted("events.Trivia.TriviaEvent"))
		{
			if (activeChar.getVar("trivia") == "on")
			{
				final String answer = _text.trim();
				if (answer.length() > 0)
				{
					final Object[] objects =
					{
						answer,
						activeChar
					};
					Functions.callScripts("events.Trivia.TriviaEvent", "checkAnswer", objects);
				}
			}
		}

		Player receiver = World.getPlayer(_target);
		if (activeChar.getLevel() <= Config.PM_REQUIRED_LEVEL && activeChar.getSubClasses().size() <= 1 && (_type == ChatType.TELL && (receiver == null || !receiver.isGM())))
		{
			activeChar.sendMessage("This PM Chat is allowed only for characters with level higher than " + Config.PM_REQUIRED_LEVEL + " to avoid spam!");
			activeChar.sendActionFailed();
			return;
		}
		if (activeChar.getLevel() <= Config.SHOUT_REQUIRED_LEVEL && activeChar.getSubClasses().size() <= 1 && ((_type == ChatType.SHOUT && (receiver == null || !receiver.isGM())) || _type == ChatType.TRADE))
		{
			activeChar.sendMessage("This Shouting Chat is allowed only for characters with level higher than " + Config.SHOUT_REQUIRED_LEVEL + " to avoid spam!");
			activeChar.sendActionFailed();
			return;
		}
		if (activeChar.getLevel() <= Config.CHATS_REQUIRED_LEVEL && activeChar.getSubClasses().size() <= 1 && (!_text.startsWith(".") || Config.NOT_USE_USER_VOICED)
					&& (_type == ChatType.ALL && (receiver == null || !receiver.isGM())))
		{
			activeChar.sendMessage("This Chat is allowed only for characters with level higher than " + Config.CHATS_REQUIRED_LEVEL + " to avoid spam!");
			activeChar.sendActionFailed();
			return;
		}

		if (Functions.isEventStarted("events.Viktorina.Viktorina"))
		{
			String answer = _text.trim();
			if (answer.length() > 0)
			{
				Object[] objects =
				{
					answer,
					activeChar
				};
				Functions.callScripts("events.Viktorina.Viktorina", "checkAnswer", objects);
			}
		}

		if (_text.startsWith(".") && !Config.NOT_USE_USER_VOICED) // If available Voice Commands for configuration, process them
		{
			String fullcmd = _text.substring(1).trim();
			String command = fullcmd.split("\\s+")[0];
			String args = fullcmd.substring(command.length()).trim();

			if (command.length() > 0)
			{
				// then check for VoicedCommands
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
				if (vch != null)
				{
					vch.useVoicedCommand(command, activeChar, args);
					return;
				}
			}
			// activeChar.sendMessage(new CustomMessage("common.command404", activeChar));
			// return;

			// Check again for min lvl if there is no voiced with that text
			if (activeChar.getLevel() <= Config.CHATS_REQUIRED_LEVEL && activeChar.getSubClasses().size() <= 1 && (_type == ChatType.ALL && (receiver == null || !receiver.isGM())))
			{
				return;
			}

			// Synerge - Dont show in the chat the command .donate or it would cause problems
			if (_text.contains(".donate"))
			{
				return;
			}
		}

		/*
		 * else if (_text.startsWith(".") && _text.endsWith("hellbound") && Config.NOT_USE_USER_VOICED) // If Voice commands are not available, can only handle .hellbound
		 * {
		 * String fullcmd = _text.substring(1).trim();
		 * String command = fullcmd.split("\\s+")[0];
		 * String args = fullcmd.substring(command.length()).trim();
		 * if (command.length() > 0)
		 * {
		 * // then check for VoicedCommands
		 * IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
		 * if (vch != null)
		 * {
		 * vch.useVoicedCommand(command, activeChar, args);
		 * return;
		 * }
		 * }
		 * activeChar.sendMessage(new CustomMessage("common.command404", activeChar));
		 * return;
		 * }
		 */

		else if (_text.startsWith(".") && _text.endsWith("offline") && Config.SERVICES_OFFLINE_TRADE_ALLOW && Config.NOT_USE_USER_VOICED && !activeChar.isBlocked()) // Если войс
																																										// комманды не
																																										// доступны, но
																																										// включен
																																										// оффлайн трей,
																																										// обрабатываем
																																										// его
		{
			String fullcmd = _text.substring(1).trim();
			String command = fullcmd.split("\\s+")[0];
			String args = fullcmd.substring(command.length()).trim();

			if (command.length() > 0)
			{
				// then check for VoicedCommands
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
				if (vch != null)
				{
					vch.useVoicedCommand(command, activeChar, args);
					return;
				}
			}
			// activeChar.sendMessage(new CustomMessage("common.command404", activeChar));
			// return;
		}

		else if (_text.startsWith("==") && !activeChar.isBlocked())
		{
			String expression = _text.substring(2);
			Expression expr = null;

			if (!expression.isEmpty())
			{
				try
				{
					expr = ExpressionTree.parse(expression);
				}
				catch (ExpressionParseException epe)
				{

				}

				if (expr != null)
				{
					double result;

					try
					{
						VarMap vm = new VarMap();
						vm.setValue("adena", activeChar.getAdena());
						result = expr.eval(vm, null);
						activeChar.sendMessage(expression);
						activeChar.sendMessage("=" + Util.formatDouble(result, "NaN", false));
					}
					catch (RuntimeException e)
					{

					}
				}
			}

			return;
		}

		if (Config.CHATFILTER_MIN_LEVEL > 0 && ArrayUtils.contains(Config.CHATFILTER_CHANNELS, _type.ordinal()) && activeChar.getLevel() < Config.CHATFILTER_MIN_LEVEL)
		{
			if (Config.CHATFILTER_WORK_TYPE == 1)
			{
				_type = ChatType.ALL;
			}
			else if (Config.CHATFILTER_WORK_TYPE == 2)
			{
				activeChar.sendMessage(new CustomMessage("chat.NotHavePermission", activeChar).addNumber(Config.CHATFILTER_MIN_LEVEL));
				return;
			}
		}

		boolean globalchat = _type != ChatType.ALLIANCE && _type != ChatType.CLAN && _type != ChatType.PARTY;

		if (Config.TRADE_CHATS_REPLACE && globalchat)
		{
			for (String s : Config.TRADE_WORDS)
			{
				if (_text.contains(s))
				{
					_type = ChatType.TRADE;
					break;
				}
			}
		}

		if ((globalchat || ArrayUtils.contains(Config.BAN_CHANNEL_LIST, _type.ordinal())) && activeChar.getNoChannel() != 0)
		{
			if (activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
			{
				if (activeChar.getNoChannel() > 0)
				{
					int timeRemained = Math.round(activeChar.getNoChannelRemained() / 60000);
					activeChar.sendMessage(new CustomMessage("common.ChatBanned", activeChar).addNumber(timeRemained));
				}
				else
				{
					activeChar.sendMessage(new CustomMessage("common.ChatBannedPermanently", activeChar));
				}
				activeChar.sendActionFailed();
				return;
			}
			activeChar.updateNoChannel(0);
		}

		if (globalchat && !activeChar.isGM())
		{
			if (Config.ABUSEWORD_REPLACE && Config.containsAbuseWord(_text))
			{
				for (Pattern regex : Config.ABUSEWORD_LIST)
				{
					_text = regex.matcher(_text).replaceAll(Config.ABUSEWORD_REPLACE_STRING);
				}

				activeChar.sendActionFailed();
			}
			else if (Config.ABUSEWORD_BANCHAT && Config.containsAbuseWord(_text))
			{
				activeChar.sendMessage(new CustomMessage("common.ChatBanned", activeChar).addNumber(Config.ABUSEWORD_BANTIME * 60));
				Log.add(activeChar + ": " + _text, "abuse");
				activeChar.updateNoChannel(Config.ABUSEWORD_BANTIME * 60000);
				activeChar.sendActionFailed();
				return;
			}
		}

		// Caching objects links
		Matcher m = EX_ITEM_LINK_PATTERN.matcher(_text);
		ItemInstance item;
		int objectId;

		while (m.find())
		{
			objectId = Integer.parseInt(m.group(1));
			item = activeChar.getInventory().getItemByObjectId(objectId);

			if (item == null)
			{
				activeChar.sendActionFailed();
				break;
			}

			ItemInfoCache.getInstance().put(item);
		}

		String translit = activeChar.getVar("translit");
		if (translit != null)
		{
			// Rule of transliteration references to objects
			m = SKIP_ITEM_LINK_PATTERN.matcher(_text);
			StringBuilder sb = new StringBuilder();
			int end = 0;
			while (m.find())
			{
				sb.append(Strings.fromTranslit(_text.substring(end, end = m.start()), translit.equals("tl") ? 1 : 2));
				sb.append(_text.substring(end, end = m.end()));
			}

			_text = sb.append(Strings.fromTranslit(_text.substring(end, _text.length()), translit.equals("tl") ? 1 : 2)).toString();
		}

		if (ConfigHolder.getBool("AllowSboutboxFromGame") && ConfigHolder.getBool("AllowForum") && ForumMembersHolder.getInstance().getMembersCount(ForumMemberGroup.NORMAL) > 0)
		{
			if (activeChar.getForumMember() == null)
			{
				if (org.apache.commons.lang3.ArrayUtils.contains(ConfigHolder.getArray("ShoutboxNoAccountChatTypes", ChatType.class), _type))
				{
					if (!ConfigHolder.getString("ShoutboxUsageNoAccountMsg").isEmpty())
					{
						activeChar.sendMessage(ConfigHolder.getString("ShoutboxUsageNoAccountMsg"));
					}

					if (ConfigHolder.getBool("ShoutboxAccountRequired"))
					{
						return;
					}
				}
			}
			else if (org.apache.commons.lang3.ArrayUtils.contains(ConfigHolder.getArray("ShoutboxFromGameChatTypes", ChatType.class), _type))
			{
				ShoutboxHandler.getInstance().newInGameShout(activeChar, _text);
			}
		}

		// Synerge - Emotions system
		int emotion = EmotionsTable.containsEmotion(_text);
		if (emotion != -1 && !World.getAroundPlayers(activeChar, 300, 300).isEmpty() && !activeChar.isInCombat() && !activeChar.isDead() && !activeChar.isCastingNow() && !activeChar.isSitting()
					&& !activeChar.isNotShowEmotions())
		{
			activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), emotion));
		}

		Log.logChat(_type, activeChar.getName(), _target, _text);

		Say2 cs;
		if (activeChar.isInFightClub() && activeChar.getFightClubEvent().isHidePersonality())
		{
			cs = new Say2(0, _type, "Player", _text);
		}
		else
		{
			cs = new Say2(activeChar.getObjectId(), _type, activeChar.getName(), _text);
		}

		switch (_type)
		{
		case TELL:
			if ((receiver == null) && (Config.ALLOW_FAKE_PLAYERS) && (FakePlayersTable.getActiveFakePlayers().contains(_target.toLowerCase())))
			{
				cs = new Say2(activeChar.getObjectId(), _type, new StringBuilder().append("->").append(_target).toString(), _text);
				activeChar.sendPacket(cs);
				return;
			}

			// Synerge - Support for fake players created on real time
			if (receiver == null && FakePlayersTable.isRealTimeFakePlayerExist(_target))
			{
				final String realName = FakePlayersTable.getRealTimeFakePlayerRealName(_target);
				cs = new Say2(activeChar.getObjectId(), _type, new StringBuilder().append("->").append(realName).toString(), _text);
				activeChar.sendPacket(cs);
				return;
			}

			if (receiver != null && receiver.isInOfflineMode())
			{
				activeChar.sendMessage("The person is in offline trade mode.");
				activeChar.sendActionFailed();
			}
			else if (receiver != null && !receiver.isInBlockList(activeChar) && !receiver.isBlockAll())
			{
				if (!receiver.getMessageRefusal())
				{
					if (activeChar.antiFlood.canTell(receiver.getObjectId(), _text))
					{
						receiver.sendPacket(cs);
					}

					checkAutoRecall(activeChar, receiver);
					checkMessageCatcher(activeChar, receiver, _text);

					cs = new Say2(activeChar.getObjectId(), _type, "->" + receiver.getName(), _text);
					activeChar.sendPacket(cs);
				}
				else
				{
					activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
				}
			}
			else if (receiver == null)
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_NOT_CURRENTLY_LOGGED_IN).addString(_target), ActionFail.STATIC);
			}
			else
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT, ActionFail.STATIC);
			}
			break;
		case SHOUT:
			if (activeChar.isCursedWeaponEquipped())
			{
				activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
				return;
			}
			if (activeChar.isInObserverMode())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
				return;
			}

			if (!activeChar.isGM() && !activeChar.antiFlood.canShout(_text))
			{
				activeChar.sendMessage("Shout chat is allowed once per 5 seconds.");
				return;
			}

			if (Config.GLOBAL_SHOUT)
			{
				announce(activeChar, cs);
			}
			else
			{
				shout(activeChar, cs);
			}

			activeChar.sendPacket(cs);
			break;
		case TRADE:
			if (activeChar.isCursedWeaponEquipped())
			{
				activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
				return;
			}
			if (activeChar.isInObserverMode())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
				return;
			}

			if (!activeChar.isGM() && !activeChar.antiFlood.canTrade(_text))
			{
				activeChar.sendMessage("Trade chat is allowed once per 5 seconds.");
				return;
			}

			if (Config.GLOBAL_TRADE_CHAT)
			{
				announce(activeChar, cs);
			}
			else
			{
				shout(activeChar, cs);
			}

			activeChar.sendPacket(cs);
			break;
		case ALL:
			if (activeChar.isCursedWeaponEquipped())
			{
				cs = new Say2(activeChar.getObjectId(), _type, activeChar.getTransformationName(), _text);
			}

			List<Player> list = null;

			if (activeChar.isInObserverMode() && activeChar.getObserverRegion() != null && activeChar.getOlympiadObserveGame() != null)
			{
				OlympiadGame game = activeChar.getOlympiadObserveGame();
				if (game != null)
				{
					list = game.getAllPlayers();
				}
			}
			else if (activeChar.isInOlympiadMode())
			{
				OlympiadGame game = activeChar.getOlympiadGame();
				if (game != null)
				{
					list = game.getAllPlayers();
				}
			}
			else if (activeChar.isInFightClub())
			{
				list = activeChar.getFightClubEvent().getAllFightingPlayers();
			}
			else
			{
				list = World.getAroundPlayers(activeChar);
			}

			if (list != null)
			{
				final boolean isGmInvis = activeChar.isInvisible() && activeChar.getAccessLevel() > 0;
				for (Player player : list)
				{
					if (player == activeChar || player.getReflection() != activeChar.getReflection() || player.isBlockAll() || player.isInBlockList(activeChar))
					{
						continue;
					}

					// Prims - If a gm talks in all when he is invisible, only other gms will be able to read him
					if (isGmInvis && player.getAccessLevel() < 1)
					{
						continue;
					}

					player.sendPacket(cs);
				}
			}

			activeChar.sendPacket(cs);
			break;
		case CLAN:
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().broadcastToOnlineMembers(cs);
			}
			break;
		case ALLIANCE:
			if (activeChar.getClan() != null && activeChar.getClan().getAlliance() != null)
			{
				activeChar.getClan().getAlliance().broadcastToOnlineMembers(cs);
			}
			break;
		case PARTY:
			if (activeChar.isInParty())
			{
				activeChar.getParty().sendPacket(cs);
			}
			break;
		case PARTY_ROOM:
			MatchingRoom r = activeChar.getMatchingRoom();
			if (r != null && r.getType() == MatchingRoom.PARTY_MATCHING)
			{
				r.sendPacket(cs);
			}
			break;
		case COMMANDCHANNEL_ALL:
			if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
				return;
			}
			if (activeChar.getParty().getCommandChannel().getLeader() == activeChar)
			{
				activeChar.getParty().getCommandChannel().sendPacket(cs);
			}
			else
			{
				activeChar.sendPacket(SystemMsg.ONLY_THE_COMMAND_CHANNEL_CREATOR_CAN_USE_THE_RAID_LEADER_TEXT);
			}
			break;
		case COMMANDCHANNEL_COMMANDER:
			if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
				return;
			}
			if (activeChar.getParty().isLeader(activeChar))
			{
				activeChar.getParty().getCommandChannel().broadcastToChannelPartyLeaders(cs);
			}
			else
			{
				activeChar.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
			}
			break;
		case HERO_VOICE:
			if (activeChar.isHero() || activeChar.FakeHeroChat() || activeChar.getPlayerAccess().CanAnnounce)
			{
				// The only limitation for the characters, um, let us say.
				if (!activeChar.getPlayerAccess().CanAnnounce)
				{
					if (!activeChar.antiFlood.canHero(_text))
					{
						activeChar.sendMessage("Hero chat is allowed once per 10 seconds.");
						return;
					}
				}
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					if (!player.isInBlockList(activeChar) && !player.isBlockAll())
					{
						player.sendPacket(cs);
					}
				}
			}
			break;
		case PETITION_PLAYER:
		case PETITION_GM:
			if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar))
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT));
				return;
			}

			PetitionManager.getInstance().sendActivePetitionMessage(activeChar, _text);
			break;
		case BATTLEFIELD:
			if (activeChar.isInFightClub())
			{
				list = activeChar.getFightClubEvent().getMyTeamFightingPlayers(activeChar);
				for (Player player : list)
				{
					player.sendPacket(cs);
				}
				return;
			}
			if (activeChar.getBattlefieldChatId() == 0)
			{
				return;
			}

			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (!player.isInBlockList(activeChar) && !player.isBlockAll() && player.getBattlefieldChatId() == activeChar.getBattlefieldChatId())
				{
					player.sendPacket(cs);
				}
			}
			break;
		case MPCC_ROOM:
			MatchingRoom r2 = activeChar.getMatchingRoom();
			if (r2 != null && r2.getType() == MatchingRoom.CC_MATCHING)
			{
				r2.sendPacket(cs);
			}
			break;
		default:
			_log.warn("Character " + activeChar.getName() + " used unknown chat type: " + _type.ordinal() + ".");
		}

		// Synerge - Part of the proxies system
		activeChar.increaseWroteMessages(_type);
	}

	private static void checkAutoRecall(Player sender, Player receiver)
	{
		if (receiver.isGM() && receiver.getQuickVarB("autoRecall", false))
		{
			if (receiver.getDistance(sender) < 500 || sender.isTeleporting() || sender.isInOlympiadMode() || sender.getReflection() != ReflectionManager.DEFAULT)
			{
				return;
			}

			sender.teleToLocation(Location.findAroundPosition(receiver, 100));
			receiver.sendMessage("Recalled " + sender.getName() + ". Use \"//autorecall false\" to disable it!");
		}
	}

	private static void checkMessageCatcher(Player sender, Player receiver, String text)
	{
		if (sender.isGM() || receiver.isGM() || Say2C.EX_ITEM_LINK_PATTERN.matcher(text).find())
		{
			return;
		}

		final Say2 packet = new Say2(sender.getObjectId(), ChatType.TELL, sender.getName(), "-> " + receiver.getName() + ": " + text);
		for (Player gm : GameObjectsStorage.getAllPlayersForIterate())
		{
			final int minLength = gm.getVarInt("catchMessagesGM", -1);
			if (minLength > 0 && minLength <= text.length() && !sender.equals(gm) && !receiver.equals(gm))
			{
				gm.sendPacket(packet);
			}
		}
	}

	private static void shout(Player activeChar, Say2 cs)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);
		int offset = Config.SHOUT_OFFSET;

		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player == activeChar || activeChar.getReflection() != player.getReflection() || player.isBlockAll() || player.isInBlockList(activeChar))
			{
				continue;
			}

			int tx = MapUtils.regionX(player);
			int ty = MapUtils.regionY(player);

			if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(player, Config.CHAT_RANGE))
			{
				player.sendPacket(cs);
			}
		}
	}

	private static void announce(Player activeChar, Say2 cs)
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player == activeChar || activeChar.getReflection() != player.getReflection() || player.isBlockAll() || player.isInBlockList(activeChar))
			{
				continue;
			}

			player.sendPacket(cs);
		}
	}
}
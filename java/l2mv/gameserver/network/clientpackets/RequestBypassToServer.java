package l2mv.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.MultiSellHolder;
import l2mv.gameserver.fandc.managers.OfflineBufferManager;
import l2mv.gameserver.handler.admincommands.AdminCommandHandler;
import l2mv.gameserver.handler.bypass.BypassHandler;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2mv.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2mv.gameserver.instancemanager.OlympiadHistoryManager;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.masteriopack.rankpvpsystem.RPSBypass;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.OlympiadManagerInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Scripts;

public class RequestBypassToServer extends L2GameClientPacket
{
	// Format: cS
	private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);
	private DecodedBypass bp = null;

	@Override
	protected void readImpl()
	{
		String bypass = this.readS();
		if (!bypass.isEmpty())
		{
			this.bp = this.getClient().getActiveChar().decodeBypass(bypass);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this.bp == null || activeChar.isJailed() || (activeChar.isBlocked() && !activeChar.isInObserverMode() && (this.bp.bypass == null || !(this.bp.bypass.contains("secondaryPassS") || this.bp.bypass.contains("ProposePass") || this.bp.bypass.contains("TryPass") || this.bp.bypass.contains("user_report")))))
		{
			return;
		}
		if ((this.bp.handler == null && !Config.ALLOW_TALK_TO_NPCS) || (this.bp.handler != null && !Config.COMMUNITYBOARD_ENABLED))
		{
			return;
		}
		if (this.bp.handler != null && activeChar.isCursedWeaponEquipped())
		{
			return;
		}

		// Synerge - Bypass debug
		if (activeChar.isDebug())
		{
			activeChar.sendMessage("Bypass: " + this.bp.bypass + " - Handler: " + (this.bp.handler != null));
		}

		try
		{
			NpcInstance npc = activeChar.getLastNpc();
			GameObject target = activeChar.getTarget();
			if (npc == null && target != null && target.isNpc())
			{
				npc = (NpcInstance) target;
			}

			if (this.bp.bypass.startsWith("admin_"))
			{
				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, this.bp.bypass);
			}
			else if (this.bp.bypass.startsWith("openURL "))
			{
				openURL(activeChar, this.bp.bypass.substring("openURL ".length()));
			}
			else if (this.bp.bypass.equals("come_here") && activeChar.isGM())
			{
				comeHere(this.getClient());
			}
			else if (this.bp.bypass.startsWith("player_help "))
			{
				playerHelp(activeChar, this.bp.bypass.substring(12));
			}
			else if (this.bp.bypass.startsWith("RPS.")) // Rank PvP System by Masterio
			{
				RPSBypass.executeCommand(activeChar, this.bp.bypass);
			}
			else if (this.bp.bypass.startsWith("scripts_"))
			{
				String command = this.bp.bypass.substring(8).trim();
				String[] word = command.split("\\s+");
				String[] args = command.substring(word[0].length()).trim().split("\\s+");
				String[] path = word[0].split(":");
				if (path.length != 2)
				{
					_log.warn("Bad Script bypass!");
					return;
				}

				Map<String, Object> variables = null;
				if (npc != null)
				{
					variables = new HashMap<String, Object>(1);
					variables.put("npc", npc.getRef());
				}

				if (word.length == 1)
				{
					Scripts.getInstance().callScripts(activeChar, path[0], path[1], variables);
				}
				else
				{
					Scripts.getInstance().callScripts(activeChar, path[0], path[1], new Object[]
					{
						args
					}, variables);
				}
			}
			else if (this.bp.bypass.startsWith("user_"))
			{
				String command = this.bp.bypass.substring(5).trim();
				String word = command.split("\\s+")[0];
				String args = command.substring(word.length()).trim();
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);

				if (vch != null)
				{
					vch.useVoicedCommand(word, activeChar, args);
				}
				else
				{
					_log.warn("Unknow voiced command '" + word + "'");
				}
			}
			else if (this.bp.bypass.startsWith("npc_"))
			{
				int endOfId = this.bp.bypass.indexOf('_', 5);
				String id;
				if (endOfId > 0)
				{
					id = this.bp.bypass.substring(4, endOfId);
				}
				else
				{
					id = this.bp.bypass.substring(4);
				}
				GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
				if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRange(object.getLoc(), Creature.INTERACTION_DISTANCE))
				{
					activeChar.setLastNpc((NpcInstance) object);
					((NpcInstance) object).onBypassFeedback(activeChar, this.bp.bypass.substring(endOfId + 1));
				}
			}
			else if (this.bp.bypass.startsWith("twitch"))
			{
				StringTokenizer st = new StringTokenizer(this.bp.bypass, " ");
				st.nextToken();

				String token = st.nextToken();

				if (token.equals("submit"))
				{
					if (!st.hasMoreTokens())
					{
						activeChar.sendMessage("Please submit your twitch channel name only.");
					}
					else
					{
						token = st.nextToken();
						activeChar.setTwitch(token);
						activeChar.sendMessage("Twitch channel changed to http://twitch.tv/ " + token + "/");
					}
				}
				else
				{
					activeChar.setTwitch(null);
					activeChar.sendMessage("Your twitch channel has been erased.");
				}
			}
			else if (this.bp.bypass.startsWith("_olympiad?"))
			{
				String[] ar = this.bp.bypass.replace("_olympiad?", "").split("&");
				String firstVal = ar[0].split("=")[1];
				String secondVal = ar[1].split("=")[1];

				if (firstVal.equalsIgnoreCase("move_op_field"))
				{
					if (!Config.ENABLE_OLYMPIAD_SPECTATING)
					{
						return;
					}

					// Transition in view of Olympiad is allowed only from the manager or from the arena.
					if ((activeChar.getLastNpc() instanceof OlympiadManagerInstance && activeChar.getLastNpc().isInRange(activeChar, Creature.INTERACTION_DISTANCE)) || activeChar.getOlympiadObserveGame() != null)
					{
						Olympiad.addSpectator(Integer.parseInt(secondVal) - 1, activeChar);
					}
				}
			}
			else if (this.bp.bypass.startsWith("_diary"))
			{
				String params = this.bp.bypass.substring(this.bp.bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
				{
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
				}
			}
			else if (this.bp.bypass.startsWith("_match"))
			{
				String params = this.bp.bypass.substring(this.bp.bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);

				OlympiadHistoryManager.getInstance().showHistory(activeChar, heroclass, heropage);
			}
			else if (this.bp.bypass.startsWith("manor_menu_select?")) // Navigate throught Manor windows
			{
				GameObject object = activeChar.getTarget();
				if (object != null && object.isNpc())
				{
					((NpcInstance) object).onBypassFeedback(activeChar, this.bp.bypass);
				}
			}
			else if (this.bp.bypass.startsWith("partyMatchingInvite"))
			{
				try
				{
					String targetName = this.bp.bypass.substring(20);
					Player receiver = World.getPlayer(targetName);
					SystemMessage sm;

					if (receiver == null)
					{
						activeChar.sendMessage("First select a user to invite to your party.");
						return;
					}

					if ((receiver.isOnline() == false))
					{
						activeChar.sendMessage("Player is in offline mode.");
						return;
					}

					if (!activeChar.isGM() && receiver.isInvisible() || !activeChar.isInvisible() && receiver.isInvisible() && !activeChar.isGM())
					{
						activeChar.sendMessage("Incorrect target.");
						return;
					}

					if (receiver.isInParty())
					{
						activeChar.sendMessage("Player " + receiver.getName() + " is already in a party.");
						return;
					}

					if (activeChar.getBlockList().contains(receiver))
					{
						activeChar.sendMessage("Player " + receiver.getName() + " is in your ignore list.");
						return;
					}

					if (receiver == activeChar)
					{
						activeChar.sendMessage("Wrong target.");
						return;
					}

					if (receiver.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped())
					{
						receiver.sendMessage("You cannot invite this person to join in your party right now.");
						return;
					}

					if (receiver.isInJail() || activeChar.isInJail())
					{
						activeChar.sendMessage("You cannot invite a player while is in Jail.");
						return;
					}

					if (receiver.isInOlympiadMode() || activeChar.isInOlympiadMode())
					{
						if ((receiver.isInOlympiadMode() != activeChar.isInOlympiadMode()) || (receiver.getOlympiadGame().getId() != activeChar.getOlympiadGame().getId()) || (receiver.getOlympiadSide() != activeChar.getOlympiadSide()))
						{
							activeChar.sendMessage("You cannot invite this player to join your party right now.");
							return;
						}
					}

					activeChar.sendMessage("You invited " + receiver.getName() + " to join your party.");

					if (!activeChar.isInParty())
					{
						Party newparty = new Party(activeChar, Party.ITEM_LOOTER);
						activeChar.setParty(newparty);
						receiver.joinParty(newparty);
					}
					else if (activeChar.getParty().isInDimensionalRift())
					{
						activeChar.sendMessage("You cannot invite a player when you are in the Dimensional Rift.");
					}
					else
					{
						Party plparty = activeChar.getParty();
						receiver.joinParty(plparty);
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			else if (this.bp.bypass.startsWith("multisell "))
			{
				MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(this.bp.bypass.substring(10)), activeChar, 0);
			}
			else if (this.bp.bypass.startsWith("Quest "))
			{
				String p = this.bp.bypass.substring(6).trim();
				int idx = p.indexOf(' ');
				if (idx < 0)
				{
					activeChar.processQuestEvent(p, "", npc);
				}
				else
				{
					activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
				}
			}
			// Synerge - Bypass for Buff Store
			else if (this.bp.bypass.startsWith("BuffStore"))
			{
				try
				{
					OfflineBufferManager.getInstance().processBypass(activeChar, this.bp.bypass);
				}
				catch (Exception ex)
				{
				}
			}
			else if (this.bp.handler != null)
			{
				if (!Config.COMMUNITYBOARD_ENABLED)
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
				}
				else
				{
					this.bp.handler.onBypassCommand(activeChar, this.bp.bypass);
				}
			}
			// Synerge - Support for secondary password on cb
			else if (this.bp.bypass.startsWith("ProposePass") || this.bp.bypass.startsWith("TryPass"))
			{
				Quest tutorial = QuestManager.getQuest(255);
				if (tutorial != null)
				{
					activeChar.processQuestEvent(tutorial.getName(), this.bp.bypass, null);
				}
			}
			// Synerge - Bypass handler
			else
			{
				BypassHandler.getInstance().useBypassCommandHandler(activeChar, this.bp.bypass);
			}
		}
		catch (Exception e)
		{
			String st = "Char '" + activeChar.getName() + "' sent Bad RequestBypassToServer: " + this.bp.bypass;
			GameObject target = activeChar.getTarget();
			if (target != null && target.isNpc())
			{
				st = st + " via NPC #" + ((NpcInstance) target).getNpcId();
			}
			_log.error(st, e);
		}
	}

	private static void comeHere(GameClient client)
	{
		GameObject obj = client.getActiveChar().getTarget();
		if (obj != null && obj.isNpc())
		{
			NpcInstance temp = (NpcInstance) obj;
			Player activeChar = client.getActiveChar();
			temp.setTarget(activeChar);
			temp.moveToLocation(activeChar.getLoc(), 0, true);
		}
	}

	private static void openURL(Player player, String url)
	{
		// TODO: Give support with smartguard
		// player.sendPacket(new OpenURLPacket(url));
	}

	private static void playerHelp(Player activeChar, String path)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(5);
		html.setFile(path);
		activeChar.sendPacket(html);
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}

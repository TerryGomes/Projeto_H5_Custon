package l2f.gameserver.handler.admincommands.impl;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.lang.StatsUtils;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.instancemanager.PetitionManager;
import l2f.gameserver.instancemanager.SoDManager;
import l2f.gameserver.instancemanager.SoIManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.EventTrigger;
import l2f.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import l2f.gameserver.network.serverpackets.ExSendUIEvent;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.utils.GameStats;

public class AdminAdmin implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminAdmin.class);

	private static enum Commands
	{
		admin_admin, admin_play_sounds, admin_play_sound, admin_silence, admin_tradeoff, admin_cfg, admin_config, admin_show_html, admin_setnpcstate, admin_setareanpcstate, admin_showmovie, admin_setzoneinfo, admin_eventtrigger, admin_debug, admin_debugdata, admin_uievent, admin_opensod, admin_closesod, admin_setsoistage, admin_soinotify, admin_forcenpcinfo, admin_loc, admin_ready, admin_checkadena, admin_locdump, admin_pointdump, admin_coordsdump, admin_undying, admin_garbage_collector,
		admin_show_memory, admin_trivia,
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		StringTokenizer st;

		if (activeChar.getPlayerAccess().Menu)
		{
			switch (command)
			{
			case admin_admin:
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/admin.htm"));
				break;
			case admin_play_sounds:
				if (wordList.length == 1)
				{
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/songs/songs.htm"));
				}
				else
				{
					try
					{
						activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/songs/songs" + wordList[1] + ".htm"));
					}
					catch (StringIndexOutOfBoundsException e)
					{
					}
				}
				break;
			case admin_play_sound:
				try
				{
					playAdminSound(activeChar, wordList[1]);
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}
				break;
			case admin_silence:
				if (activeChar.getMessageRefusal()) // already in message refusal
				// mode
				{
					activeChar.unsetVar("gm_silence");
					activeChar.setMessageRefusal(false);
					activeChar.sendPacket(SystemMsg.MESSAGE_ACCEPTANCE_MODE);
					activeChar.sendEtcStatusUpdate();
				}
				else
				{
					if (Config.SAVE_GM_EFFECTS)
					{
						activeChar.setVar("gm_silence", "true", -1);
					}
					activeChar.setMessageRefusal(true);
					activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
					activeChar.sendEtcStatusUpdate();
				}
				break;
			case admin_tradeoff:
				try
				{
					if (wordList[1].equalsIgnoreCase("on"))
					{
						activeChar.setTradeRefusal(true);
						Functions.sendDebugMessage(activeChar, "tradeoff enabled");
					}
					else if (wordList[1].equalsIgnoreCase("off"))
					{
						activeChar.setTradeRefusal(false);
						Functions.sendDebugMessage(activeChar, "tradeoff disabled");
					}
				}
				catch (Exception ex)
				{
					if (activeChar.getTradeRefusal())
					{
						Functions.sendDebugMessage(activeChar, "tradeoff currently enabled");
					}
					else
					{
						Functions.sendDebugMessage(activeChar, "tradeoff currently disabled");
					}
				}
				break;
			case admin_checkadena:
				activeChar.sendMessage("Adena on the server is: " + GameStats.getAdena());
				break;
			case admin_show_html:
			{
				String html = wordList[1];
				try
				{
					if (html != null)
					{
						activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + html));
					}
					else
					{
						Functions.sendDebugMessage(activeChar, "Html page not found");
					}
				}
				catch (Exception npe)
				{
					Functions.sendDebugMessage(activeChar, "Html page not found");
				}
				break;
			}
			case admin_setnpcstate:
				if (wordList.length < 2)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //setnpcstate state");
					return false;
				}
				int state;
				GameObject target = activeChar.getTarget();
				try
				{
					state = Integer.parseInt(wordList[1]);
				}
				catch (NumberFormatException e)
				{
					Functions.sendDebugMessage(activeChar, "You must specify state");
					return false;
				}
				if (!target.isNpc())
				{
					Functions.sendDebugMessage(activeChar, "You must target an NPC");
					return false;
				}
				NpcInstance npc = (NpcInstance) target;
				npc.setNpcState(state);
				break;
			case admin_setareanpcstate:
				try
				{
					final String val = fullString.substring(15).trim();

					String[] vals = val.split(" ");
					int range = NumberUtils.toInt(vals[0], 0);
					int astate = vals.length > 1 ? NumberUtils.toInt(vals[1], 0) : 0;

					for (NpcInstance n : activeChar.getAroundNpc(range, 200))
					{
						n.setNpcState(astate);
					}
				}
				catch (Exception e)
				{
					Functions.sendDebugMessage(activeChar, "Usage: //setareanpcstate [range] [state]");
				}
				break;
			case admin_showmovie:
				if (wordList.length < 2)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //showmovie id");
					return false;
				}
				int id;
				try
				{
					id = Integer.parseInt(wordList[1]);
				}
				catch (NumberFormatException e)
				{
					Functions.sendDebugMessage(activeChar, "You must specify id");
					return false;
				}
				activeChar.showQuestMovie(id);
				break;
			case admin_setzoneinfo:
				if (wordList.length < 2)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //setzoneinfo id");
					return false;
				}
				int stateid;
				try
				{
					stateid = Integer.parseInt(wordList[1]);
				}
				catch (NumberFormatException e)
				{
					Functions.sendDebugMessage(activeChar, "You must specify id");
					return false;
				}
				activeChar.broadcastPacket(new ExChangeClientEffectInfo(stateid));
				break;
			case admin_ready:
				try
				{
					String[] message =
					{
						activeChar.getName() + " is now available via Petition System."
					};

					Announcements.getInstance().announceToAll(message, ChatType.SCREEN_ANNOUNCE);

					for (Player player : GameObjectsStorage.getAllPlayers())
					{
						if (player == null || player.isInOfflineMode())
						{
							continue;
						}
						player.sendChatMessage(player.getObjectId(), ChatType.TELL.ordinal(), "System", activeChar.getName() + " is now online. If you have problem/issue please contact him via petition.");

					}
					PetitionManager.getInstance().sendPendingPetitionList(activeChar);
				}
				catch (Exception e)
				{
					activeChar.sendMessage(new CustomMessage("Something went wrong...", activeChar));
					return false;
				}
				activeChar.sendMessage(new CustomMessage("You have sent the messages, now open petitions and work", activeChar));
				break;
			case admin_eventtrigger:
				if (wordList.length < 2)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //eventtrigger id");
					return false;
				}
				int triggerid;
				try
				{
					triggerid = Integer.parseInt(wordList[1]);
				}
				catch (NumberFormatException e)
				{
					Functions.sendDebugMessage(activeChar, "You must specify id");
					return false;
				}
				activeChar.broadcastPacket(new EventTrigger(triggerid, true));
				break;
			case admin_debug:
				GameObject ob = activeChar.getTarget();
				if (!ob.isPlayer())
				{
					Functions.sendDebugMessage(activeChar, "Only player target is allowed");
					return false;
				}
				Player pl = ob.getPlayer();
				List<String> _s = new ArrayList<String>();
				_s.add("==========TARGET STATS:");
				_s.add("==Magic Resist: " + pl.calcStat(Stats.MAGIC_RESIST, null, null));
				_s.add("==Magic Power: " + pl.calcStat(Stats.MAGIC_POWER, 1, null, null));
				_s.add("==Skill Power: " + pl.calcStat(Stats.SKILL_POWER, 1, null, null));
				_s.add("==Cast Break Rate: " + pl.calcStat(Stats.CAST_INTERRUPT, 1, null, null));

				_s.add("==========Powers:");
				_s.add("==Bleed: " + pl.calcStat(Stats.BLEED_POWER, 1, null, null));
				_s.add("==Poison: " + pl.calcStat(Stats.POISON_POWER, 1, null, null));
				_s.add("==Stun: " + pl.calcStat(Stats.STUN_POWER, 1, null, null));
				_s.add("==Root: " + pl.calcStat(Stats.ROOT_POWER, 1, null, null));
				_s.add("==Mental: " + pl.calcStat(Stats.MENTAL_POWER, 1, null, null));
				_s.add("==Sleep: " + pl.calcStat(Stats.SLEEP_POWER, 1, null, null));
				_s.add("==Paralyze: " + pl.calcStat(Stats.PARALYZE_POWER, 1, null, null));
				_s.add("==Cancel: " + pl.calcStat(Stats.CANCEL_POWER, 1, null, null));
				_s.add("==Debuff: " + pl.calcStat(Stats.DEBUFF_POWER, 1, null, null));

				_s.add("==========PvP Stats:");
				_s.add("==Phys Attack Dmg: " + pl.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null));
				_s.add("==Phys Skill Dmg: " + pl.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null));
				_s.add("==Magic Skill Dmg: " + pl.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null));
				_s.add("==Phys Attack Def: " + pl.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1, null, null));
				_s.add("==Phys Skill Def: " + pl.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1, null, null));
				_s.add("==Magic Skill Def: " + pl.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null));

				_s.add("==========Reflects:");
				_s.add("==Phys Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, null, null));
				_s.add("==Phys Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, null, null));
				_s.add("==Magic Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, null, null));
				_s.add("==Counterattack: Phys Dmg Chance: " + pl.calcStat(Stats.REFLECT_DAMAGE_PERCENT, null, null));
				_s.add("==Counterattack: Phys Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, null, null));
				_s.add("==Counterattack: Magic Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, null, null));

				_s.add("==========MP Consume Rate:");
				_s.add("==Magic Skills: " + pl.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, 1, null, null));
				_s.add("==Phys Skills: " + pl.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, 1, null, null));
				_s.add("==Music: " + pl.calcStat(Stats.MP_DANCE_SKILL_CONSUME, 1, null, null));

				_s.add("==========Shield:");
				_s.add("==Shield Defence: " + pl.calcStat(Stats.SHIELD_DEFENCE, null, null));
				_s.add("==Shield Defence Rate: " + pl.calcStat(Stats.SHIELD_RATE, null, null));
				_s.add("==Shield Defence Angle: " + pl.calcStat(Stats.SHIELD_ANGLE, null, null));

				_s.add("==========Etc:");
				_s.add("==Fatal Blow Rate: " + pl.calcStat(Stats.FATALBLOW_RATE, null, null));
				_s.add("==Phys Skill Evasion Rate: " + pl.calcStat(Stats.PSKILL_EVASION, null, null));
				_s.add("==Counterattack Rate: " + pl.calcStat(Stats.COUNTER_ATTACK, null, null));
				_s.add("==Pole Attack Angle: " + pl.calcStat(Stats.POLE_ATTACK_ANGLE, null, null));
				_s.add("==Pole Target Count: " + pl.calcStat(Stats.POLE_TARGET_COUNT, 1, null, null));
				_s.add("==========DONE.");

				for (String s : _s)
				{
					Functions.sendDebugMessage(activeChar, s);
				}

				// Synerge - Enable and disable debug
				if (pl.isDebug())
				{
					pl.setDebug(false);
					activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled", activeChar));
				}
				else
				{
					pl.setDebug(true);
					activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled", activeChar));
				}
				break;
			case admin_uievent:
				if (wordList.length < 5)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //uievent isHide doIncrease startTime endTime Text");
					return false;
				}
				boolean hide;
				boolean increase;
				int startTime;
				int endTime;
				String text;
				try
				{
					hide = Boolean.parseBoolean(wordList[1]);
					increase = Boolean.parseBoolean(wordList[2]);
					startTime = Integer.parseInt(wordList[3]);
					endTime = Integer.parseInt(wordList[4]);
					text = wordList[5];
				}
				catch (NumberFormatException e)
				{
					Functions.sendDebugMessage(activeChar, "Invalid format");
					return false;
				}
				activeChar.broadcastPacket(new ExSendUIEvent(activeChar, hide, increase, startTime, endTime, text));
				break;
			case admin_opensod:
				if (wordList.length < 1)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //opensod minutes");
					return false;
				}
				SoDManager.openSeed(Integer.parseInt(wordList[1]) * 60 * 1000L);
				break;
			case admin_closesod:
				SoDManager.closeSeed();
				break;
			case admin_setsoistage:
				if (wordList.length < 1)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //setsoistage stage[1-5]");
					return false;
				}
				SoIManager.setCurrentStage(Integer.parseInt(wordList[1]));
				break;
			case admin_soinotify:
				if (wordList.length < 1)
				{
					Functions.sendDebugMessage(activeChar, "USAGE: //soinotify [1-3]");
					return false;
				}
				switch (Integer.parseInt(wordList[1]))
				{
				case 1:
					SoIManager.notifyCohemenesKill();
					break;
				case 2:
					SoIManager.notifyEkimusKill();
					break;
				case 3:
					SoIManager.notifyHoEDefSuccess();
					break;
				}
				break;
			case admin_forcenpcinfo:
				GameObject obj2 = activeChar.getTarget();
				if (!obj2.isNpc())
				{
					Functions.sendDebugMessage(activeChar, "Only NPC target is allowed");
					return false;
				}
				((NpcInstance) obj2).broadcastCharInfo();
				break;
			case admin_loc:
				Functions.sendDebugMessage(activeChar, "Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
				break;
			case admin_locdump:
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					try
					{
						new File("dumps").mkdir();
						File f = new File("dumps/locdump.txt");
						if (!f.exists())
						{
							f.createNewFile();
						}
						Functions.sendDebugMessage(activeChar, "Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
						FileWriter writer = new FileWriter(f, true);
						writer.write("Loc: " + activeChar.getLoc().x + ", " + activeChar.getLoc().y + ", " + activeChar.getLoc().z + "\n");
						writer.close();
					}
					catch (Exception e)
					{

					}
				}
				catch (Exception e)
				{
					// Case of wrong monster data
				}
				break;
			case admin_pointdump:
			{
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					try
					{
						new File("dumps").mkdir();
						final File f = new File("dumps/locdump.txt");
						if (!f.exists())
						{
							f.createNewFile();
						}
						Functions.sendDebugMessage(activeChar, "Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
						final FileWriter writer = new FileWriter(f, true);
						writer.write("\t\t\t<point x=\"" + activeChar.getX() + "\" y=\"" + activeChar.getY() + "\" z=\"" + activeChar.getZ() + "\"/>\n");
						writer.close();
					}
					catch (Exception ex6)
					{
					}
				}
				catch (Exception ex7)
				{
				}
				break;
			}
			case admin_coordsdump:
			{
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					try
					{
						new File("dumps").mkdir();
						final File f = new File("dumps/locdump.txt");
						if (!f.exists())
						{
							f.createNewFile();
						}
						Functions.sendDebugMessage(activeChar, "Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
						final FileWriter writer = new FileWriter(f, true);
						writer.write("\t\t\t<coords loc=\"" + activeChar.getX() + ' ' + activeChar.getY() + "\"/>\n");
						writer.close();
					}
					catch (Exception ex8)
					{
					}
				}
				catch (Exception ex9)
				{
				}
				break;
			}
			case admin_undying:
				if (activeChar.isUndying())
				{
					activeChar.setUndying(false);
					Functions.sendDebugMessage(activeChar, "Undying state has been disabled.");
				}
				else
				{
					activeChar.setUndying(true);
					Functions.sendDebugMessage(activeChar, "Undying state has been enabled.");
				}
				break;
			case admin_garbage_collector:
				System.gc();
				break;
			case admin_show_memory:
				_log.info("=================================================");
				String memUsage = new StringBuilder().append(StatsUtils.getMemUsage()).toString();
				for (String line : memUsage.split("\n"))
				{
					_log.info(line);
				}
				_log.info("=================================================");
				break;
			case admin_trivia:
			{
				final String customHtm = HtmCache.getInstance().getNotNull("admin/events/trivia.htm", activeChar);
				NpcHtmlMessage html1 = new NpcHtmlMessage(0);
				html1.setHtml(customHtm);
				activeChar.sendPacket(html1);
				break;
			}
			// Synerge - A special command that will show the value of every variable of a target, to know what is on and off, specially usefull to debug and find problems source
			case admin_debugdata:
			{
				ob = activeChar.getTarget();
				if (ob == null || !ob.isPlayer())
				{
					Functions.sendDebugMessage(activeChar, "Only player target is allowed");
					return false;
				}
				pl = ob.getPlayer();

				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				final StringBuilder sb = new StringBuilder();
				sb.append("<html><title>Debug Status Data</title>");
				sb.append("<body>");
				sb.append("<center>");
				sb.append("<font color=f1f3fb>" + pl.getName() + "</font><br>");

				// Make the dynamic list of all methods that are related to the player and return a boolean value for setting variables and statuses
				try
				{
					final List<String> addedMethodNames = new ArrayList<>();
					final List<Method> methods = new ArrayList<>();

					// Get all public boolean methods from Player class
					for (Method method : Player.class.getDeclaredMethods())
					{
						// We need public methods, that return boolean, that start with is and dont have any parameter
						if (method.getName().startsWith("is") && method.getParameterCount() == 0 && (method.getModifiers() & Modifier.PUBLIC) != 0 && method.getReturnType().equals(boolean.class))
						{
							methods.add(method);
							addedMethodNames.add(method.getName());
						}
					}

					// Get all public boolean methods from Creature class
					for (Method method : Creature.class.getDeclaredMethods())
					{
						// Dont add the same methods as in player
						if (addedMethodNames.contains(method.getName()))
						{
							continue;
						}

						// We need public methods, that return boolean, that start with is and dont have any parameter
						if (method.getName().startsWith("is") && method.getParameterCount() == 0 && (method.getModifiers() & Modifier.PUBLIC) != 0 && method.getReturnType().equals(boolean.class))
						{
							methods.add(method);
						}
					}

					// Sort the methods by name
					Collections.sort(methods, new Comparator<Method>()
					{
						@Override
						public int compare(Method left, Method right)
						{
							return left.getName().compareTo(right.getName());
						}
					});

					// Add to the html all the methods and the returns but after the methods were sort by name
					for (Method method : methods)
					{
						final boolean ret = (boolean) method.invoke(pl);
						sb.append(method.getName() + ": <font color=" + (ret ? "99ff33" : "ff3300") + ">" + ret + "</font><br1>");
					}
				}
				catch (Exception e)
				{
				}

				sb.append("</center>");
				sb.append("</body>");
				sb.append("</html>");
				html.setHtml(sb.toString());
				activeChar.sendPacket(html);
				break;
			}
			}
			return true;
		}

		if (activeChar.getPlayerAccess().CanTeleport)
		{
			switch (command)
			{
			case admin_show_html:
				String html = wordList[1];
				try
				{
					if (html != null)
					{
						if (html.startsWith("tele"))
						{
							activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + html));
						}
						else
						{
							activeChar.sendMessage("Access denied");
						}
					}
					else
					{
						activeChar.sendMessage("Html page not found");
					}
				}
				catch (Exception npe)
				{
					activeChar.sendMessage("Html page not found");
				}
				break;
			}
			return true;
		}

		return false;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void playAdminSound(Player activeChar, String sound)
	{
		activeChar.broadcastPacket(new PlaySound(sound));
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/admin.htm"));
		activeChar.sendMessage("Playing " + sound + ".");
	}
}
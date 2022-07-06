package l2mv.gameserver.handler.admincommands.impl;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.PhantomPlayerAI;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.PhantomPlayers;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import l2mv.gameserver.network.serverpackets.PledgeSkillList;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.utils.Location;

public class AdminPhantoms implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_phantoms, admin_phantoms_ai, admin_phantoms_enable, admin_phantoms_disable, admin_phantoms_terminate, admin_phantoms_spawner, admin_phantoms_spawner_here, admin_phantoms_stop_spawner, admin_phantoms_stop_spawners, admin_phantoms_stop_spawning, admin_phantoms_spawn, admin_phantoms_spawnnew, admin_phantoms_reset, admin_phantoms_spawn_new, admin_phantoms_spawn_level_gear, admin_phantoms_spawn_withclan, admin_getphantomcount
	}

	String charName = "";
	int level = -1;
	int classId = -1;
	int gearScore = -1;
	boolean isMage = false;
	boolean isMale = false;
	Race race = Race.human;
	boolean hasAi = false;
	String hasClan = "";
	boolean farming = false;
	boolean disableRespawn = false;
	int despawn = 0;
	String spawnedPhantomName = "";
	String title = "";

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		Location loc = null;

		StringBuilder sb = new StringBuilder();

		switch (command)
		{
		case admin_phantoms:
			String html = HtmCache.getInstance().getNotNull("admin/phantoms.htm", activeChar);

			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

			adminReply.setHtml(html);
			if (charName.equals(""))
			{
				sb.append("FALSE;");

				for (Integer clans : Config.PHANTOM_CLANS)
				{
					Clan clan = ClanTable.getInstance().getClan(clans);
					if (clan != null)
					{
						sb.append(clan.getName() + ";");
					}
				}

				adminReply.replace("%charname%", "<edit var=name width=75>");
				adminReply.replace("%level%", "<edit var=level width=25>");
				adminReply.replace("%classId%", "<edit var=classid width=30>");
				adminReply.replace("%gearScore%", "<combobox width=45 var=gear list=N;D;C;B;A;S;S80;S84;>");
				adminReply.replace("%type%", "<combobox width=65 var=type list=FIGHTER;MAGE;>");
				adminReply.replace("%gender%", "<combobox width=70 var=gender list=MALE;FEMALE;>");
				adminReply.replace("%race%", "<combobox width=80 var=race list=RANDOM;HUMAN;ELF;DARKELF;ORC;DWARF;KAMAEL;>");
				adminReply.replace("%hasAi%", "<combobox width=60 var=hasAi list=TRUE;FALSE;>");
				adminReply.replace("%hasClan%", "<combobox width=100 var=hasClan list=" + sb.toString() + ">");
				adminReply.replace("%farm%", "<combobox width=60 var=farm list=FALSE;TRUE;>");
				adminReply.replace("%dspwn%", "<combobox width=70 var=\"despawn\" list=\"DEFAULT;5;15;30;60;120;360;720;\">");
				adminReply.replace("%rspwn%", "<combobox width=60 var=respawn list=FALSE;TRUE;>");

				adminReply.replace("%spawnedPhantom%", "&nbsp;");
				adminReply.replace("%title%", "<edit var=title width=60>");

				adminReply.replace("%buttonSpawn%", "<button value=\"Save & Spawn\" action=\"bypass -h admin_phantoms_spawn $name $level $classid $gear $type $gender $race $hasAi $hasClan $farm $despawn $title $respawn\" width=120 height=25 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
				adminReply.replace("%buttonReset%", "&nbsp;");
			}
			else
			{
				String gear = "None";
				switch (gearScore)
				{
				case 0:
					gear = "N";
					break;
				case 1:
					gear = "D";
					break;
				case 2:
					gear = "C";
					break;
				case 3:
					gear = "B";
					break;
				case 4:
					gear = "A";
					break;
				case 5:
					gear = "S";
					break;
				case 6:
					gear = "S80";
					break;
				case 7:
					gear = "S84";
					break;
				}

				String type = isMage ? "MAGE" : "FIGHTER";
				String gender = isMale ? "MALE" : "FEMALE";
				String haveAi = hasAi ? "TRUE" : "FALSE";
				String farm = farming ? "TRUE" : "FALSE";
				String rspwn = disableRespawn ? "TRUE" : "FALSE";

				adminReply.replace("%charname%", "<br><font color=57D760>" + charName + "</font>");
				adminReply.replace("%level%", "<br><font color=fff312>" + level + "</font>");
				adminReply.replace("%classId%", "<br><font color=57D760>" + classId + "</font>");
				adminReply.replace("%gearScore%", "<br><font color=8B80F3>" + gear + "</font>");
				adminReply.replace("%type%", "<br><font color=F56642>" + type + "</font>");
				adminReply.replace("%gender%", "<br><font color=80F2EA>" + gender + "</font>");
				adminReply.replace("%race%", "<br><font color=80F2EA>" + race.name().toUpperCase() + "</font>");
				adminReply.replace("%hasAi%", "<br><font color=FABA76>" + haveAi + "</font>");
				adminReply.replace("%hasClan%", "<br><font color=FABA76>" + hasClan + "</font>");
				adminReply.replace("%farm%", "<br><font color=993288>" + farm + "</font>");
				adminReply.replace("%dspwn%", "<br><font color=FABA76>" + (despawn == 0 ? "Default" : despawn) + "</font>");
				adminReply.replace("%title%", "<br><font color=FABA76>" + title + "</font>");
				adminReply.replace("%rspwn%", "<br><font color=FABA76>" + rspwn + "</font>");

				adminReply.replace("%buttonSpawn%", "<button value=\"Save & Spawn\" action=\"bypass -h admin_phantoms_spawn " + charName + " " + level + " " + classId + " " + gear + " " + type + " " + gender + " " + race.name().toUpperCase() + " " + haveAi + " " + hasClan + " " + farm + " " + despawn + " " + title + " " + rspwn + "\" width=120 height=25 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
				adminReply.replace("%buttonReset%", "<button value=\"Reset\" action=\"bypass -h admin_phantoms_reset\" width=120 height=25 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
			}

			adminReply.replace("%phantomEnabled%", String.valueOf(Config.PHANTOM_PLAYERS_ENABLED));
			adminReply.replace("%spawnedPhantom%", "Latest Spawned Phantom: " + spawnedPhantomName);
			adminReply.replace("%phantomcount%", "" + PhantomPlayers.getSpawnedPhantomsCount());

			activeChar.sendPacket(adminReply);
			break;
		case admin_phantoms_reset:
			html = HtmCache.getInstance().getNotNull("admin/phantoms.htm", activeChar);

			adminReply = new NpcHtmlMessage(5);

			charName = "";
			level = -1;
			classId = -1;
			gearScore = -1;
			isMage = false;
			isMale = false;
			race = Race.human;
			hasAi = false;
			hasClan = "";
			title = "";
			disableRespawn = false;

			adminReply.setHtml(html);
			adminReply.replace("%phantomcount%", "" + PhantomPlayers.getSpawnedPhantomsCount());

			sb.append("FALSE;");

			for (Integer clans : Config.PHANTOM_CLANS)
			{
				Clan clan = ClanTable.getInstance().getClan(clans);
				if (clan != null)
				{
					sb.append(clan.getName() + ";");
				}
			}

			adminReply.replace("%charname%", "<edit var=\"name\" width=75>");
			adminReply.replace("%level%", "<edit var=\"level\" width=25>");
			adminReply.replace("%classId%", "<edit var=\"classid\" width=30>");
			adminReply.replace("%gearScore%", "<combobox width=45 var=\"gear\" list=\"N;D;C;B;A;S;S80;S84;\">");
			adminReply.replace("%type%", "<combobox width=65 var=\"type\" list=\"FIGHTER;MAGE;\">");
			adminReply.replace("%gender%", "<combobox width=70 var=\"gender\" list=\"MALE;FEMALE;\">");
			adminReply.replace("%race%", "<combobox width=80 var=\"race\" list=\"RANDOM;HUMAN;ELF;DARKELF;ORC;DWARF;KAMAEL;\">");
			adminReply.replace("%hasAi%", "<combobox width=60 var=\"hasAi\" list=\"TRUE;FALSE;\">");
			adminReply.replace("%hasClan%", "<combobox width=100 var=\"hasClan\" list=" + sb.toString() + ">");
			adminReply.replace("%farm%", "<combobox width=60 var=\"farm\" list=\"FALSE;TRUE;\">");
			adminReply.replace("%dspwn%", "<combobox width=70 var=\"despawn\" list=\"DEFAULT;5;15;30;60;120;360;720;\">");
			adminReply.replace("%spawnedPhantom%", "&nbsp;");
			adminReply.replace("%title%", "<edit var=\"title\" width=75>");
			adminReply.replace("%rspwn%", "<combobox width=60 var=\"respawn\" list=\"FALSE;TRUE;\">");

			adminReply.replace("%phantomEnabled%", String.valueOf(Config.PHANTOM_PLAYERS_ENABLED));
			adminReply.replace("%buttonSpawn%", "<button value=\"Save & Spawn\" action=\"bypass -h admin_phantoms_spawn $name $level $classid $gear $type $gender $race $hasAi $hasClan $farm $despawn $title $respawn\" width=120 height=25 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
			adminReply.replace("%buttonReset%", "&nbsp;");

			activeChar.sendPacket(adminReply);
			break;
		case admin_phantoms_enable:
			Config.PHANTOM_PLAYERS_ENABLED = true;
			PhantomPlayers.init();
			// Original Message: Phantom Players enabled.
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message1", activeChar));
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/phantoms.htm").replace("%phantomcount%", "" + PhantomPlayers.getSpawnedPhantomsCount()));
			break;
		case admin_phantoms_disable:
			Config.PHANTOM_PLAYERS_ENABLED = false;
			PhantomPlayers.terminatePhantoms(true, true);
			// Original Message: Phantom Players disabled and terminated.
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message2", activeChar));
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/phantoms.htm").replace("%phantomcount%", "" + PhantomPlayers.getSpawnedPhantomsCount()));
			break;
		case admin_phantoms_terminate:
			if (wordList.length > 1)
			{
				if (wordList[1].equalsIgnoreCase("all"))
				{
					if (wordList.length > 2 && (wordList[2].equalsIgnoreCase("true") || wordList[2].equalsIgnoreCase("force")))
					{
						PhantomPlayers.terminatePhantoms(true, true);
					}
					else
					{
						PhantomPlayers.terminatePhantoms(false, false);
					}
				}
				else
				{
					Player phantom = World.getPlayer(wordList[1]);
					if (phantom == null)
					{
						// Original Message: Phantom player with name " + wordList[1] + " not found.
						activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message3", activeChar).addString(wordList[1]));
					}
					else if (wordList.length > 2 && (wordList[2].equalsIgnoreCase("true") || wordList[2].equalsIgnoreCase("force") || wordList[2].equalsIgnoreCase("perma")))
					{
						PhantomPlayers.terminatePhantom(phantom.getObjectId(), true, true);
					}
					else
					{
						PhantomPlayers.terminatePhantom(phantom.getObjectId(), false, false);
					}
				}
			}
			else if (activeChar.getTargetId() > 0)
			{
				PhantomPlayers.terminatePhantom(activeChar.getTargetId(), true, true);
			}
			else
			{
				activeChar.sendMessage("//phantoms_terminate [target | <charname> | <all true/false>] ");
			}
			break;
		case admin_phantoms_spawner_here:
			loc = activeChar.getLoc();
		case admin_phantoms_spawner:
			try
			{
				if (wordList.length > 3)
				{
					PhantomPlayers.spawnPhantoms(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), Boolean.parseBoolean(wordList[3]), loc);
				}
				else if (wordList.length > 2)
				{
					PhantomPlayers.spawnPhantoms(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), false, loc);
				}
				else if (wordList.length > 1)
				{
					PhantomPlayers.spawnPhantoms(Integer.parseInt(wordList[1]), Config.PHANTOM_SPAWN_DELAY, false, loc);
				}

				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message4", activeChar));

			}
			catch (Exception e)
			{
				// Original Message: Usage: //phantoms_spawner numSpawns [delayInMilis] [true for generateNew]
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message5", activeChar));
			}
			break;
		case admin_phantoms_stop_spawner:
		case admin_phantoms_stop_spawners:
		case admin_phantoms_stop_spawning:
			PhantomPlayers.stopSpawners();
			// Original Message: All phantom player spawners stopped.
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message6", activeChar));
			break;
		case admin_phantoms_spawn:
			try
			{
				if (wordList.length != 14)
				{
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message10", activeChar));
					return false;
				}

				charName = wordList[1];
				level = Integer.parseInt(wordList[2]);
				classId = Integer.parseInt(wordList[3]);

				String gear = wordList[4];
				switch (gear)
				{
				case "N":
					gearScore = 0;
					break;
				case "D":
					gearScore = 1;
					break;
				case "C":
					gearScore = 2;
					break;
				case "B":
					gearScore = 3;
					break;
				case "A":
					gearScore = 4;
					break;
				case "S":
					gearScore = 5;
					break;
				case "S80":
					gearScore = 6;
					break;
				case "S84":
					gearScore = 7;
					break;
				}
				String type = wordList[5];
				switch (type)
				{
				case "MAGE":
					isMage = true;
					break;
				case "FIGHTER":
					isMage = false;
					break;
				}
				String gender = wordList[6];
				switch (gender)
				{
				case "MALE":
					isMale = true;
					break;
				case "FEMALE":
					isMale = false;
					break;
				}
				String race1 = wordList[7];
				switch (race1)
				{
				case "RANDOM":
					race = Rnd.get(Race.values());
				case "HUMAN":
					race = Race.human;
					break;
				case "ELF":
					race = Race.elf;
					break;
				case "DARKELF":
					race = Race.darkelf;
					break;
				case "ORC":
					race = Race.orc;
					break;
				case "DWARF":
					race = Race.dwarf;
					break;
				case "KAMAEL":
					race = Race.kamael;
					break;
				}

				String ai = wordList[8];
				switch (ai)
				{
				case "TRUE":
					hasAi = true;
					break;
				case "FALSE":
					hasAi = false;
					break;
				}
				String clan = wordList[9];
				switch (clan)
				{
				case "FALSE":
					hasClan = "";
					break;
				case "":
					hasClan = "";
					break;
				default:
					hasClan = clan;
					break;
				}
				String farm = wordList[10];
				switch (farm)
				{
				case "TRUE":
					farming = true;
					break;
				case "FALSE":
					farming = false;
					break;
				}
				String dspwn = wordList[11];
				switch (dspwn)
				{
				case "DEFAULT":
					despawn = 0;
					break;
				case "5":
					despawn = 5;
					break;
				case "15":
					despawn = 15;
					break;
				case "30":
					despawn = 30;
					break;
				case "60":
					despawn = 60;
					break;
				case "120":
					despawn = 120;
					break;
				case "360":
					despawn = 360;
					break;
				case "720":
					despawn = 720;
					break;
				}

				title = wordList[12];

				String rspwn = wordList[13];
				switch (rspwn)
				{
				case "TRUE":
					disableRespawn = true;
					break;
				case "FALSE":
					disableRespawn = false;
					break;
				}

				final Player phantom;

				if (hasClan != "")
				{
					Player clanPhantom = null;

					// get randomPhatnom already in database
					int rndbotwithclan = PhantomPlayers.getRandomPhantomWithClan();

					// if there is no phantoms with clan, create new phantom in selected clan
					if (rndbotwithclan == -1)
					{
						clanPhantom = PhantomPlayers.createNewPhantom(charName, level, classId, isMage, isMale, race, title);

						if (clanPhantom == null)
						{
							activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message8", activeChar));
							return false;
						}

						Clan clan2 = ClanTable.getInstance().getClanByName(hasClan);
						if (clan2 != null)
						{
							int pledgeType = 0; // main clan
							SubUnit subUnit = clan2.getSubUnit(pledgeType);
							if (subUnit == null)
							{
								return false;
							}

							UnitMember member = new UnitMember(clan2, clanPhantom.getName(), clanPhantom.getTitle(), clanPhantom.getLevel(), clanPhantom.getClassId().getId(), clanPhantom.getObjectId(), pledgeType, clanPhantom.getPowerGrade(), clanPhantom.getApprentice(), clanPhantom.getSex(), Clan.SUBUNIT_NONE);
							subUnit.addUnitMember(member);

							clanPhantom.setPledgeType(pledgeType);
							clanPhantom.setClan(clan2);

							member.setPlayerInstance(clanPhantom, false);

							if (pledgeType == Clan.SUBUNIT_ACADEMY)
							{
								clanPhantom.setLvlJoinedAcademy(clanPhantom.getLevel());
							}

							member.setPowerGrade(clan2.getAffiliationRank(clanPhantom.getPledgeType()));

							clan2.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), clanPhantom);
							clan2.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(clanPhantom.getName()), new PledgeShowInfoUpdate(clan2));

							// this activates the clan tab on the new member
							clanPhantom.sendPacket(SystemMsg.ENTERED_THE_CLAN);
							clanPhantom.sendPacket(clanPhantom.getClan().listAll());
							clanPhantom.setLeaveClanTime(0);
							clanPhantom.updatePledgeClass();

							clan2.addSkillsQuietly(clanPhantom);
							clanPhantom.sendPacket(new PledgeSkillList(clan2));
							clanPhantom.sendPacket(new SkillList(clanPhantom));

							EventHolder.getInstance().findEvent(clanPhantom);
							if (clan2.getWarDominion() > 0)
							{
								DominionSiegeEvent siegeEvent = clanPhantom.getEvent(DominionSiegeEvent.class);

								siegeEvent.updatePlayer(clanPhantom, true);
							}
							else
							{
								clanPhantom.broadcastCharInfo();
							}

							clanPhantom.store(false);
						}

						rndbotwithclan = clanPhantom.getObjectId();
					}

					String name = CharacterDAO.getNameByObjectId(rndbotwithclan);
					ThreadPoolManager.getInstance().execute(new PhantomPlayers.PhantomSpawn(rndbotwithclan, hasAi, gearScore, farming, despawn, disableRespawn).setLocation(activeChar.getLoc().setH(activeChar.getHeading())));
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message9", activeChar).addString(name));
					spawnedPhantomName = name;
					useAdminCommand(Commands.admin_phantoms, null, "", activeChar);
					return true;
				}

				phantom = PhantomPlayers.createNewPhantom(charName, level, classId, isMage, isMale, race, title);

				if (phantom != null)
				{
					ThreadPoolManager.getInstance().execute(new PhantomPlayers.PhantomSpawn(phantom.getObjectId(), hasAi, gearScore, farming, despawn, disableRespawn).setLocation(activeChar.getLoc().setH(activeChar.getHeading())));
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message9", activeChar).addString(phantom.getName()));
					spawnedPhantomName = phantom.getName();
					useAdminCommand(Commands.admin_phantoms, null, "", activeChar);
				}
				else
				{
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message8", activeChar));
				}
			}
			catch (Exception e)
			{
				// Original Message: Usage: //phantoms_spawn_new [name] [name (human|elf|darkelf|orc|dwarf|kamael) (true for mage)] [name classId (true for female) hairStyle hairColor
				// face]
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message10", activeChar));
			}
			break;
		case admin_phantoms_spawn_level_gear:
			try
			{
				final Player phantom;
				boolean hasAi = true;
				if (wordList.length == 2)
				{
					phantom = PhantomPlayers.createNewPhantomWithLevel(Integer.parseInt(wordList[1]));
				}
				else
				{
					phantom = PhantomPlayers.createNewPhantom();
				}

				if (wordList.length == 3)
				{
					hasAi = Boolean.parseBoolean(wordList[3]);
				}

				if (phantom == null)
				{
					// Original Message: Failed to create phantom.
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message8", activeChar));
				}
				else
				{
					ThreadPoolManager.getInstance().execute(new PhantomPlayers.PhantomSpawn(phantom.getObjectId(), hasAi, Integer.parseInt(wordList[2]), false, 0, false).setLocation(activeChar.getLoc()));
					// Original Message: Spawned phantom with name: " + phantom.getName()
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message9", activeChar).addString(phantom.getName()));
				}
			}
			catch (Exception e)
			{
				// Original Message: Usage: //phantoms_spawn_new [name] [name (human|elf|darkelf|orc|dwarf|kamael) (true for mage)] [name classId (true for female) hairStyle hairColor
				// face]
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message10", activeChar));
			}
			break;
		case admin_phantoms_spawn_withclan:
			try
			{
				boolean hasAi = true;
				if (wordList.length > 1)
				{
					hasAi = Boolean.parseBoolean(wordList[1]);
				}

				int rndbotwithclan = PhantomPlayers.getRandomPhantomWithClan();
				String name = CharacterDAO.getNameByObjectId(rndbotwithclan);
				ThreadPoolManager.getInstance().execute(new PhantomPlayers.PhantomSpawn(rndbotwithclan, hasAi, -1, false, 0, false).setLocation(activeChar.getLoc()));
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message9", activeChar).addString(name));
			}
			catch (Exception e)
			{
				// Original Message: Usage: //phantoms_spawn_new [name] [name (human|elf|darkelf|orc|dwarf|kamael) (true for mage)] [name classId (true for female) hairStyle hairColor
				// face]
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message10", activeChar));
			}
			break;
		case admin_phantoms_ai:
			if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				// Original Message: Please target a player.
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message11", activeChar));
			}
			else if (!activeChar.getTarget().getPlayer().getAI().isPhantomPlayerAI())
			{
				// Original Message: Target is not phantom.
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message12", activeChar));
			}
			else
			{
				PhantomPlayerAI ai = (PhantomPlayerAI) activeChar.getTarget().getPlayer().getAI();
				if (wordList.length == 1)
				{
					activeChar.sendMessage(System.identityHashCode(ai));
					// Original Message: Intention: " + ai.getIntention()
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message13", activeChar).addString(ai.getIntention().toString()));
					// Original Message: Mood: " + ai.getMood()
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message14", activeChar).addString(ai.getMood()));
					// Original Message: AttackTarget: " + ai.getAttackTarget()
					activeChar.sendMessage(new CustomMessage("l2mv.gameserver.handler.admincommands.impl.AdminPhantoms.message15", activeChar).addString(ai.getAttackTarget() != null ? ai.getAttackTarget().toString() : "none"));
				}
				else if ("stopRoaming".equalsIgnoreCase(wordList[1]))
				{
					ai.stopRoamingTask();
				}
				else if ("startRoaming".equalsIgnoreCase(wordList[1]))
				{
					ai.startRoamingInTown();
				}
			}
			break;
		case admin_getphantomcount:
			activeChar.sendMessage(PhantomPlayers.getSpawnedPhantomsCount());
			break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
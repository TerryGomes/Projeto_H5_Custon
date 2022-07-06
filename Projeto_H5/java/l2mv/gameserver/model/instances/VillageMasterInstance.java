package l2mv.gameserver.model.instances;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SubClass;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.ClassType;
import l2mv.gameserver.model.base.PlayerClass;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2mv.gameserver.network.serverpackets.PledgeStatusChanged;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.CertificationFunctions;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.SiegeUtils;
import l2mv.gameserver.utils.Util;

public final class VillageMasterInstance extends NpcInstance
{
	private static final Logger LOG = LoggerFactory.getLogger(VillageMasterInstance.class);

	public VillageMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.startsWith("create_clan") && command.length() > 12)
		{
			String val = command.substring(12);
			createClan(player, val);
		}
		else if (command.startsWith("create_academy") && command.length() > 15)
		{
			String sub = command.substring(15, command.length());
			createSubPledge(player, sub, Clan.SUBUNIT_ACADEMY, 5, "");
		}
		else if (command.startsWith("create_royal") && command.length() > 15)
		{
			String[] sub = command.substring(13, command.length()).split(" ", 2);
			if (sub.length == 2)
			{
				createSubPledge(player, sub[1], Clan.SUBUNIT_ROYAL1, 6, sub[0]);
			}
		}
		else if (command.startsWith("create_knight") && command.length() > 16)
		{
			String[] sub = command.substring(14, command.length()).split(" ", 2);
			if (sub.length == 2)
			{
				createSubPledge(player, sub[1], Clan.SUBUNIT_KNIGHT1, 7, sub[0]);
			}
		}
		else if (command.startsWith("assign_subpl_leader") && command.length() > 22)
		{
			String[] sub = command.substring(20, command.length()).split(" ", 2);
			if (sub.length == 2)
			{
				assignSubPledgeLeader(player, sub[1], sub[0]);
			}
		}
		else if (command.startsWith("assign_new_clan_leader") && command.length() > 23)
		{
			String val = command.substring(23);
			setLeader(player, val);
		}
		if (command.startsWith("create_ally") && command.length() > 12)
		{
			String val = command.substring(12);
			createAlly(player, val);
		}
		else if (command.startsWith("dissolve_ally"))
		{
			dissolveAlly(player);
		}
		else if (command.startsWith("dissolve_clan"))
		{
			dissolveClan(player);
		}
		else if (command.startsWith("restore_clan"))
		{
			recoverClan(player);
		}
		else if (command.startsWith("increase_clan_level"))
		{
			levelUpClan(player);
		}
		else if (command.startsWith("learn_clan_skills"))
		{
			showClanSkillList(player);
		}
		else if (command.startsWith("ShowCouponExchange"))
		{
			if (Functions.getItemCount(player, 8869) > 0 || Functions.getItemCount(player, 8870) > 0)
			{
				command = "Multisell 800";
			}
			else
			{
				command = "Link villagemaster/reflect_weapon_master_noticket.htm";
			}
			super.onBypassFeedback(player, command);
		}
		else if (command.equalsIgnoreCase("CertificationList"))
		{
			CertificationFunctions.showCertificationList(this, player);
		}
		else if (command.equalsIgnoreCase("GetCertification65"))
		{
			CertificationFunctions.getCertification65(this, player);
		}
		else if (command.equalsIgnoreCase("GetCertification70"))
		{
			CertificationFunctions.getCertification70(this, player);
		}
		else if (command.equalsIgnoreCase("GetCertification80"))
		{
			CertificationFunctions.getCertification80(this, player);
		}
		else if (command.equalsIgnoreCase("GetCertification75List"))
		{
			CertificationFunctions.getCertification75List(this, player);
		}
		else if (command.equalsIgnoreCase("GetCertification75C"))
		{
			CertificationFunctions.getCertification75(this, player, true);
		}
		else if (command.equalsIgnoreCase("GetCertification75M"))
		{
			CertificationFunctions.getCertification75(this, player, false);
		}
		else if (command.startsWith("Subclass"))
		{
			if (player.getPet() != null)
			{
				player.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
				return;
			}

			if ((player.isInDuel()) || (player.getTeam() != TeamType.NONE))
			{
				player.sendMessage("You cannot change your subclass while in duel	");
				return;
			}

			// Sub class can not be obtained or changed while using the skill or character is in transformation
			if (player.isActionsDisabled() || player.getTransformation() != 0)
			{
				player.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
				return;
			}

			if (player.getWeightPenalty() >= 3)
			{
				player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
				return;
			}

			if (player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
			{
				player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
				return;
			}

			StringBuilder content = new StringBuilder("<html><body>");
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);

			Map<Integer, SubClass> playerClassList = player.getSubClasses();
			Set<PlayerClass> subsAvailable;

			if (player.getLevel() < 40)
			{
				content.append("You must be level 40 or more to operate with your sub-classes.");
				content.append("</body></html>");
				html.setHtml(content.toString());
				player.sendPacket(html);
				return;
			}

			int classId = 0;
			int newClassId = 0;
			int intVal = 0;

			try
			{
				for (String id : command.substring(9, command.length()).split(" "))
				{
					if (intVal == 0)
					{
						intVal = Integer.parseInt(id);
						continue;
					}
					if (classId > 0)
					{
						newClassId = Integer.parseInt(id);
						continue;
					}
					classId = Integer.parseInt(id);
				}
			}
			catch (NumberFormatException e)
			{
				LOG.error("Error while creating Subclass page", e);
			}

			switch (intVal)
			{
			case 1: // Returns a list of subs that can be taken (see case 4)
				subsAvailable = getAvailableSubsByNpc(player, true);

				if (subsAvailable != null && !subsAvailable.isEmpty())
				{
					content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

					for (PlayerClass subClass : subsAvailable)
					{
						content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 4 ").append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal())).append("</a><br>");
					}
				}
				else
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", player));
					return;
				}
				break;
			case 2: // Установка уже взятого саба (см case 5)
				content.append("Change Subclass:<br>");

				final int baseClassId = player.getBaseClassId();

				if (playerClassList.size() < 2)
				{
					content.append("You can't change subclasses when you don't have a subclass to begin with.<br><a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 1\">Add subclass.</a>");
				}
				else
				{
					content.append("Which class would you like to switch to?<br>");

					if (baseClassId == player.getActiveClassId())
					{
						content.append(HtmlUtils.htmlClassName(baseClassId)).append(" <font color=\"LEVEL\">(Base Class)</font><br><br>");
					}
					else
					{
						content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 5 ").append(baseClassId).append("\">").append(HtmlUtils.htmlClassName(baseClassId)).append("</a> " + "<font color=\"LEVEL\">(Base Class)</font><br><br>");
					}

					for (SubClass subClass : playerClassList.values())
					{
						if (subClass.isBase())
						{
							continue;
						}
						int subClassId = subClass.getClassId();

						if (subClassId == player.getActiveClassId())
						{
							content.append(HtmlUtils.htmlClassName(subClassId)).append("<br>");
						}
						else
						{
							content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 5 ").append(subClassId).append("\">").append(HtmlUtils.htmlClassName(subClassId)).append("</a><br>");
						}
					}
				}
				break;
			case 3: // Отмена сабкласса - список имеющихся (см case 6)
				content.append("Change Subclass:<br>Which of the following sub-classes would you like to change?<br>");

				for (SubClass sub : playerClassList.values())
				{
					content.append("<br>");
					if (!sub.isBase())
					{
						content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 6 ").append(sub.getClassId()).append("\">").append(HtmlUtils.htmlClassName(sub.getClassId())).append("</a><br>");
					}
				}

				content.append("<br>If you change a sub-class, you'll start at level 40 after the 2nd class transfer.");
				break;
			case 4: // Добавление сабкласса - обработка выбора из case 1
				boolean added = addNewSubclass(player, classId);
				if (added)
				{
					content.append("Add Subclass:<br>The subclass of <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(classId)).append("</font> has been added.");
					player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				}
				else
				{
					html.setFile("villagemaster/SubClass_Fail.htm");
				}
				break;
			case 5: // Changing to another of sub already taken - the treatment of choice of case 2
				/*
				 * If the character is less than level 75 on any of their
				 * previously chosen classes then disallow them to change to
				 * their most recently added sub-class choice.
				 */
				/*
				 * for(L2SubClass<?> sub : playerClassList.values())
				 * if (sub.isBase() && sub.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
				 * {
				 * player.sendMessage("You may not change to your subclass before you are level " + Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS,
				 * "Вы не можете добавить еще сабкласс пока у вас уровень " + Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS + " на Вашем предыдущем сабклассе.");
				 * return;
				 * }
				 */

				if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player))
				{
					player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
					return;
				}

				player.setActiveSubClass(classId, true);

				content.append("Change Subclass:<br>Your active subclass is now a <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(player.getActiveClassId())).append("</font>.");

				player.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
				// completed.
				break;
			case 6: // Cancel subclass - the treatment of choice of case 3
				content.append("Please choose a subclass to change to. If the one you are looking for is not here, " + //
							"please seek out the appropriate master for that class.<br>" + //
							"<font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>");

				subsAvailable = getAvailableSubsByNpc(player, false);

				if (!subsAvailable.isEmpty())
				{
					for (PlayerClass subClass : subsAvailable)
					{
						content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 7 ").append(classId).append(" ").append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal())).append("</a><br>");
					}
				}
				else
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", player));
					return;
				}
				break;
			case 7: // Отмена сабкласса - обработка выбора из case 6
				// player.sendPacket(Msg.YOUR_PREVIOUS_SUB_CLASS_WILL_BE_DELETED_AND_YOUR_NEW_SUB_CLASS_WILL_START_AT_LEVEL_40__DO_YOU_WISH_TO_PROCEED); // Change confirmation.

				if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player))
				{
					player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
					return;
				}

				if (player.modifySubClass(classId, newClassId))
				{
					if (classId == 97)
					{
						player.getInventory().destroyItemByItemId(15307, 1L, "holypomander");
					}

					if (classId == 105)
					{
						player.getInventory().destroyItemByItemId(15308, 1L, "holypomander");
					}

					if (classId == 112)
					{
						List<ItemInstance> HolyPomanders = player.getInventory().getItemsByItemId(15309);
						for (ItemInstance i : HolyPomanders)
						{
							player.getInventory().destroyItem(i, 1L, "holypomander");
						}
					}
					content.append("Change Subclass:<br>Your subclass has been changed to <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(newClassId)).append("</font>.");
					player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				}
				else
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player));
					return;
				}
				break;
			}
			content.append("</body></html>");

			// If the content is greater than for a basic blank page,
			// then assume no external HTML file was assigned.
			if (content.length() > 26)
			{
				html.setHtml(content.toString());
			}

			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "villagemaster/" + pom + ".htm";
	}

	// Private stuff
	public void createClan(Player player, String clanName)
	{
		if (player.getLevel() < 10)
		{
			player.sendPacket(Msg.YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN);
			return;
		}

		if (player.getClanId() != 0)
		{
			player.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
			return;
		}

		if (!player.canCreateClan())
		{
			// you can't create a new clan within 10 days
			player.sendPacket(Msg.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
			return;
		}
		if (clanName.length() > 16)
		{
			player.sendPacket(Msg.CLAN_NAMES_LENGTH_IS_INCORRECT);
			return;
		}
		if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE))
		{
			// clan name is not matching template
			player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
			return;
		}

		Clan clan = ClanTable.getInstance().createClan(player, clanName);
		if (clan == null)
		{
			// clan name is already taken
			player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		// should be update packet only
		player.sendPacket(clan.listAll());
		player.sendPacket(new PledgeShowInfoUpdate(clan), Msg.CLAN_HAS_BEEN_CREATED);
		player.updatePledgeClass();
		player.broadcastCharInfo();

		if (Config.ALT_CLAN_LEVEL_CREATE > 0)
		{
			increaseClanLevel(player, Config.ALT_CLAN_LEVEL_CREATE);
		}

	}

	public void setLeader(Player leader, String newLeader)
	{
		if (!leader.isClanLeader())
		{
			leader.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		if (leader.getEvent(SiegeEvent.class) != null)
		{
			leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", leader));
			return;
		}

		Clan clan = leader.getClan();
		SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		UnitMember member = mainUnit.getUnitMember(newLeader);

		if (member == null)
		{
			// FIX ME зачем 2-ве мессаги(VISTALL)
			// leader.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.S1IsNotMemberOfTheClan", leader).addString(newLeader));
			showChatWindow(leader, "villagemaster/clan-20.htm");
			return;
		}

		if (member.getLeaderOf() != Clan.SUBUNIT_NONE)
		{
			leader.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader", leader));
			return;
		}

		setLeader(leader, clan, mainUnit, member);
	}

	public static void setLeader(Player player, Clan clan, SubUnit unit, UnitMember newLeader)
	{
		player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.ClanLeaderWillBeChangedFromS1ToS2", player).addString(clan.getLeaderName()).addString(newLeader.getName()));
		// TODO: В данной редакции смена лидера производится сразу же.
		// Надо подумать над реализацией смены кланлидера в запланированный день недели.

		/*
		 * if (clan.getLevel() >= CastleSiegeManager.getSiegeClanMinLevel())
		 * {
		 * if (clan.getLeader() != null)
		 * {
		 * L2Player oldLeaderPlayer = clan.getLeader().getPlayer();
		 * if (oldLeaderPlayer != null)
		 * SiegeUtils.removeSiegeSkills(oldLeaderPlayer);
		 * }
		 * L2Player newLeaderPlayer = newLeader.getPlayer();
		 * if (newLeaderPlayer != null)
		 * SiegeUtils.addSiegeSkills(newLeaderPlayer);
		 * }
		 */
		unit.setLeader(newLeader, true);

		clan.broadcastClanStatus(true, true, false);
	}

	public void createSubPledge(Player player, String clanName, int pledgeType, int minClanLvl, String leaderName)
	{
		UnitMember subLeader = null;

		Clan clan = player.getClan();

		if (clan == null || !player.isClanLeader())
		{
			player.sendPacket(Msg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
			return;
		}

		if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
			return;
		}

		Collection<SubUnit> subPledge = clan.getAllSubUnits();
		for (SubUnit element : subPledge)
		{
			if (element.getName().equals(clanName))
			{
				player.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
				return;
			}
		}

		if (ClanTable.getInstance().getClanByName(clanName) != null)
		{
			player.sendPacket(Msg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
			return;
		}

		if (clan.getLevel() < minClanLvl)
		{
			player.sendPacket(Msg.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
			return;
		}

		SubUnit unit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);

		if (pledgeType != Clan.SUBUNIT_ACADEMY)
		{
			subLeader = unit.getUnitMember(leaderName);
			if (subLeader == null)
			{
				player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player));
				return;
			}
			else if (subLeader.getLeaderOf() != Clan.SUBUNIT_NONE)
			{
				player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player));
				return;
			}
		}

		pledgeType = clan.createSubPledge(player, pledgeType, subLeader, clanName);
		if (pledgeType == Clan.SUBUNIT_NONE)
		{
			return;
		}

		clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(clan.getSubUnit(pledgeType)));

		SystemMessage sm;
		if (pledgeType == Clan.SUBUNIT_ACADEMY)
		{
			sm = new SystemMessage(SystemMessage.CONGRATULATIONS_THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= Clan.SUBUNIT_KNIGHT1)
		{
			sm = new SystemMessage(SystemMessage.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= Clan.SUBUNIT_ROYAL1)
		{
			sm = new SystemMessage(SystemMessage.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else
		{
			sm = Msg.CLAN_HAS_BEEN_CREATED;
		}

		player.sendPacket(sm);

		if (subLeader != null)
		{
			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subLeader));
			if (subLeader.isOnline())
			{
				subLeader.getPlayer().updatePledgeClass();
				subLeader.getPlayer().broadcastCharInfo();
			}
		}
	}

	public void assignSubPledgeLeader(Player player, String clanName, String leaderName)
	{
		Clan clan = player.getClan();

		if (clan == null)
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.ClanDoesntExist", player));
			return;
		}

		if (!player.isClanLeader())
		{
			player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		SubUnit targetUnit = null;
		for (SubUnit unit : clan.getAllSubUnits())
		{
			if (unit.getType() == Clan.SUBUNIT_MAIN_CLAN || unit.getType() == Clan.SUBUNIT_ACADEMY)
			{
				continue;
			}
			if (unit.getName().equalsIgnoreCase(clanName))
			{
				targetUnit = unit;
			}

		}
		if (targetUnit == null)
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.SubUnitNotFound", player));
			return;
		}
		SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		UnitMember subLeader = mainUnit.getUnitMember(leaderName);
		if (subLeader == null)
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player));
			return;
		}

		if (subLeader.getLeaderOf() != Clan.SUBUNIT_NONE)
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player));
			return;
		}

		targetUnit.setLeader(subLeader, true);
		clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(targetUnit));

		clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subLeader));
		if (subLeader.isOnline())
		{
			subLeader.getPlayer().updatePledgeClass();
			subLeader.getPlayer().broadcastCharInfo();
		}

		player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.NewSubUnitLeaderHasBeenAssigned", player));
	}

	private void dissolveClan(Player player)
	{
		if (player == null || player.getClan() == null)
		{
			return;
		}
		Clan clan = player.getClan();

		if (!player.isClanLeader())
		{
			player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}
		if (clan.isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
			return;
		}
		if (!clan.canDisband())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION);
			return;
		}
		if (clan.getAllyId() != 0)
		{
			player.sendPacket(Msg.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
			return;
		}
		if (clan.isAtWar() > 0)
		{
			player.sendPacket(Msg.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
			return;
		}
		if (clan.getCastle() != 0 || clan.getHasHideout() != 0 || clan.getHasFortress() != 0)
		{
			player.sendPacket(Msg.UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS);
			return;
		}

		for (Residence r : ResidenceHolder.getInstance().getResidences())
		{
			if (r.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, clan) != null || r.getSiegeEvent().getSiegeClan(SiegeEvent.DEFENDERS, clan) != null || r.getSiegeEvent().getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, clan) != null)
			{
				player.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE);
				return;
			}
		}

		clan.setDissolvedClanTime(System.currentTimeMillis() + Clan.DISBAND_TIME);
		clan.updateClanInDB();
		ClanTable.getInstance().scheduleRemoveClan(clan.getClanId(), clan.getDissolvedClanTime());
		clan.broadcastClanStatus(true, true, false);
		player.sendMessage("Your clan has been scheduled for disband!");
	}

	public void recoverClan(Player player)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		final Clan clan = player.getClan();
		if (!clan.isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.THERE_ARE_NO_REQUESTS_TO_DISPERSE);
			return;
		}
		clan.unPlaceDisband();
		clan.broadcastClanStatus(true, true, false);
		player.sendMessage("Your clan has been recovered!");
	}

	public void levelUpClan(Player player)
	{
		Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		if (!player.isClanLeader())
		{
			player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}
		if (clan.isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED);
			return;
		}
		boolean increaseClanLevel = false;

		switch (clan.getLevel())
		{
		case 0:
			// Upgrade to 1
			if (player.getSp() >= 20000 && player.getAdena() >= 650000)
			{
				player.setSp(player.getSp() - 20000);
				player.reduceAdena(650000, true, "Clan Level");
				increaseClanLevel = true;
			}
			break;
		case 1:
			// Upgrade to 2
			if (player.getSp() >= 100000 && player.getAdena() >= 2500000)
			{
				player.setSp(player.getSp() - 100000);
				player.reduceAdena(2500000, true, "Clan Level");
				increaseClanLevel = true;
			}
			break;
		case 2:
			// Upgrade to 3
			// itemid 1419 == Blood Mark
			if (player.getSp() >= 350000 && player.getInventory().destroyItemByItemId(1419, 1, "Clan Level"))
			{
				player.setSp(player.getSp() - 350000);
				increaseClanLevel = true;
			}
			break;
		case 3:
			// Upgrade to 4
			// itemid 3874 == Alliance Manifesto
			if (player.getSp() >= 1000000 && player.getInventory().destroyItemByItemId(3874, 1, "Clan Level"))
			{
				player.setSp(player.getSp() - 1000000);
				increaseClanLevel = true;
			}
			break;
		case 4:
			// Upgrade to 5
			// itemid 3870 == Seal of Aspiration
			if (player.getSp() >= 2500000 && player.getInventory().destroyItemByItemId(3870, 1, "Clan Level"))
			{
				player.setSp(player.getSp() - 2500000);
				increaseClanLevel = true;
			}
			break;
		case 5:
			// Upgrade to 6
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_6_COST && clan.getAllSize() >= Config.CLAN_LEVEL_6_REQUIREMEN)
			{
				clan.incReputation(-Config.CLAN_LEVEL_6_COST, false, "LvlUpClan");
				increaseClanLevel = true;
			}
			break;
		case 6:
			// Upgrade to 7
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_7_COST && clan.getAllSize() >= Config.CLAN_LEVEL_7_REQUIREMEN)
			{
				clan.incReputation(-Config.CLAN_LEVEL_7_COST, false, "LvlUpClan");
				increaseClanLevel = true;
			}
			break;
		case 7:
			// Upgrade to 8
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_8_COST && clan.getAllSize() >= Config.CLAN_LEVEL_8_REQUIREMEN)
			{
				clan.incReputation(-Config.CLAN_LEVEL_8_COST, false, "LvlUpClan");
				increaseClanLevel = true;
			}
			break;
		case 8:
			// Upgrade to 9
			// itemId 9910 == Blood Oath
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_9_COST && clan.getAllSize() >= Config.CLAN_LEVEL_9_REQUIREMEN)
			{
				ItemInstance item = player.getInventory().getItemByItemId(9910);
				if (item != null && item.getCount() >= Config.BLOOD_OATHS)
				{
					clan.incReputation(-Config.CLAN_LEVEL_9_COST, false, "LvlUpClan");
					player.getInventory().destroyItemByItemId(9910, Config.BLOOD_OATHS, "Clan Level");
					increaseClanLevel = true;
				}
			}
			break;
		case 9:
			// Upgrade to 10
			// itemId 9911 == Blood Alliance
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_10_COST && clan.getAllSize() >= Config.CLAN_LEVEL_10_REQUIREMEN)
			{
				ItemInstance item = player.getInventory().getItemByItemId(9911);
				if (item != null && item.getCount() >= Config.BLOOD_PLEDGES)
				{
					clan.incReputation(-Config.CLAN_LEVEL_10_COST, false, "LvlUpClan");
					player.getInventory().destroyItemByItemId(9911, Config.BLOOD_PLEDGES, "Clan Level");
					increaseClanLevel = true;
				}
			}
			break;
		case 10:
			// Upgrade to 11
			if (clan.getReputationScore() >= Config.CLAN_LEVEL_11_COST && clan.getAllSize() >= Config.CLAN_LEVEL_11_REQUIREMEN)
			{
				Castle castle = ResidenceHolder.getInstance().getResidence(clan.getCastle());
				if (castle == null)
				{
					player.sendMessage("Your clan has no 1st of the castle!");
					increaseClanLevel = false;
					break;
				}

				boolean hasTerritory = false;
				for (Dominion dominion : ResidenceHolder.getInstance().getResidenceList(Dominion.class))
				{
					if (dominion.getOwnerId() == player.getClanId())
					{
						hasTerritory = true;
						break;
					}
				}

				if (hasTerritory)
				{
					clan.incReputation(-Config.CLAN_LEVEL_11_COST, false, "LvlUpClan");
					increaseClanLevel = true;
				}
				else
				{
					player.sendMessage("You must own a territory/dominion to lvl up the clan");
				}
			}
			break;
		}

		if (increaseClanLevel)
		{
			clan.setLevel(clan.getLevel() + 1);
			clan.updateClanInDB();

			player.broadcastCharInfo();

			doCast(SkillTable.getInstance().getInfo(5103, 1), player, true);

			if (clan.getLevel() >= 4)
			{
				SiegeUtils.addSiegeSkills(player);
			}

			if (clan.getLevel() == 5)
			{
				player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
			}

			// notify all the members about it
			PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
			PledgeStatusChanged ps = new PledgeStatusChanged(clan);
			for (UnitMember mbr : clan)
			{
				if (mbr.isOnline())
				{
					mbr.getPlayer().updatePledgeClass();
					mbr.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
					mbr.getPlayer().broadcastCharInfo();
				}
			}
		}
		else
		{
			player.sendPacket(Msg.CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL);
		}
	}

	public void increaseClanLevel(Player player, int levelClan)
	{
		Clan clan = player.getClan();
		clan.setLevel(levelClan);
		clan.updateClanInDB();

		player.broadcastCharInfo();

		doCast(SkillTable.getInstance().getInfo(5103, 1), player, true);

		if (clan.getLevel() >= 4)
		{
			SiegeUtils.addSiegeSkills(player);
		}

		if (clan.getLevel() >= 5)
		{
			player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
		}

		// notify all the members about it
		PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
		PledgeStatusChanged ps = new PledgeStatusChanged(clan);
		for (UnitMember mbr : clan)
		{
			if (mbr.isOnline())
			{
				mbr.getPlayer().updatePledgeClass();
				mbr.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
				mbr.getPlayer().broadcastCharInfo();
			}
		}
	}

	public void createAlly(Player player, String allyName)
	{
		// D5 You may not ally with clan you are battle with.
		// D6 Only the clan leader may apply for withdraw from alliance.
		// DD No response. Invitation to join an
		// D7 Alliance leaders cannot withdraw.
		// D9 Different Alliance
		// EB alliance information
		// Ec alliance name $s1
		// ee alliance leader: $s2 of $s1
		// ef affilated clans: total $s1 clan(s)
		// f6 you have already joined an alliance
		// f9 you cannot new alliance 10 days
		// fd cannot accept. clan ally is register as enemy during siege battle.
		// fe you have invited someone to your alliance.
		// 100 do you wish to withdraw from the alliance
		// 102 enter the name of the clan you wish to expel.
		// 202 do you realy wish to dissolve the alliance
		// 502 you have accepted alliance
		// 602 you have failed to invite a clan into the alliance
		// 702 you have withdraw

		if (!player.isClanLeader())
		{
			player.sendPacket(Msg.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
			return;
		}
		if (player.getClan().getAllyId() != 0)
		{
			player.sendPacket(Msg.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
			return;
		}
		if (player.getClan().isPlacedForDisband())
		{
			player.sendPacket(SystemMsg.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED);
			return;
		}
		if (allyName.length() > 16)
		{
			player.sendPacket(Msg.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
			return;
		}
		if (!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE))
		{
			player.sendPacket(Msg.INCORRECT_ALLIANCE_NAME);
			return;
		}
		if (player.getClan().getLevel() < 5)
		{
			player.sendPacket(Msg.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
			return;
		}
		if (ClanTable.getInstance().getAllyByName(allyName) != null)
		{
			player.sendPacket(Msg.THIS_ALLIANCE_NAME_ALREADY_EXISTS);
			return;
		}
		if (!player.getClan().canCreateAlly())
		{
			player.sendPacket(Msg.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_AFTER_DISSOLUTION);
			return;
		}

		Alliance alliance = ClanTable.getInstance().createAlliance(player, allyName);
		if (alliance == null)
		{
			return;
		}

		player.broadcastCharInfo();
		player.sendMessage("Alliance " + allyName + " has been created.");
	}

	private void dissolveAlly(Player player)
	{
		if (player == null || player.getAlliance() == null)
		{
			return;
		}

		if (!player.isAllyLeader())
		{
			player.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
			return;
		}

		if (player.getAlliance().getMembersCount() > 1)
		{
			player.sendPacket(Msg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
			return;
		}

		ClanTable.getInstance().dissolveAlly(player);
	}

	private Set<PlayerClass> getAvailableSubsByNpc(Player player, boolean isNew)
	{
		Set<PlayerClass> availSubs = getAllAvailableSubs(player, isNew);
		final Race npcRace = getVillageMasterRace();
		final ClassType npcTeachType = getVillageMasterTeachType();

		for (PlayerClass availSub : availSubs)
		{
			if (!availSub.isOfRace(Race.human) && !availSub.isOfRace(Race.elf))
			{
				if (!availSub.isOfRace(npcRace))
				{
					availSubs.remove(availSub);
				}
			}
			else if (!availSub.isOfType(npcTeachType))
			{
				availSubs.remove(availSub);
			}
		}
		return availSubs;
	}

	public static Set<PlayerClass> getAllAvailableSubs(Player player, boolean isNew)
	{
		final int charClassId = player.getBaseClassId();

		PlayerClass currClass = PlayerClass.values()[charClassId];

		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select
		 * each class as a subclass to the other class, and you may not select
		 * Overlord and Warsmith class as a subclass.
		 *
		 * You may not select a similar class as the subclass. The occupations
		 * classified as similar classes are as follows:
		 *
		 * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger
		 * and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien
		 * Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and
		 * Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
		 * Spellhowler
		 *
		 */
		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
		if (availSubs == null)
		{
			return Collections.emptySet();
		}

		// Remove from the list sub maine class player
		availSubs.remove(currClass);

		for (PlayerClass availSub : availSubs)
		{
			// Remove from the list of subs already made subs and their ancestors
			for (SubClass subClass : player.getSubClasses().values())
			{
				if (availSub.ordinal() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Remove possible sub their parents, if they have chara
				ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
				if (parent != null && parent.getId() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Remove possible sbb parents current subclass, or if you take a sub berserker
				// And bring to a third Profiles - doombringer, the player will be offered a berserker again (deja vu)
				ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
				if (subParent != null && subParent.getId() == availSub.ordinal())
				{
					availSubs.remove(availSub);
				}
			}

			// Особенности саб классов камаэль
			if (availSub.isOfRace(Race.kamael))
			{
				// Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
				if ((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker))
				{
					availSubs.remove(availSub);
				}

				// Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
				if (currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster)
				{
					if (player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker)
					{
						availSubs.remove(availSub);
					}
				}

				// Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс)
				if (availSub == PlayerClass.Inspector && player.getSubClasses().size() < (isNew ? 3 : 4))
				{
					availSubs.remove(availSub);
				}
			}
		}
		return availSubs;
	}

	public static boolean addNewSubclass(Player player, int classId)
	{
		Map<Integer, SubClass> playerClassList = player.getSubClasses();

		// Проверка хватает ли уровня
		if (player.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player).addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
			return false;
		}

		if (!playerClassList.isEmpty())
		{
			for (SubClass subClass : playerClassList.values())
			{
				if (subClass.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", player).addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
					return false;
				}
			}
		}

		if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player))
		{
			player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			return false;
		}

		/*
		 * If you want to quest - continuity check Mimir's Elixir (Path to Subclass)
		 * For Kamael quest 236_SeedsOfChaos
		 * If the sub first, then check nachil subject, if not the first, then give the subclass.
		 * If you do not have subs, then check for a subject.
		 */
		if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !playerClassList.isEmpty() && playerClassList.size() < 2 + Config.ALT_GAME_SUB_ADD)
		{
			if (player.isQuestCompleted("_234_FatesWhisper"))
			{
				if (player.getRace() == Race.kamael)
				{
					if (!player.isQuestCompleted("_236_SeedsOfChaos"))
					{
						player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.QuestSeedsOfChaos", player));
						return false;
					}
				}
				else if (!player.isQuestCompleted("_235_MimirsElixir"))
				{
					player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir", player));
					return false;
				}
			}
			else
			{
				player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper", player));
				return false;
			}
		}

		if (!player.addSubClass(classId, true, 0))
		{
			player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", player));
			return false;
		}
		return true;
	}

	private Race getVillageMasterRace()
	{
		switch (getTemplate().getRace())
		{
		case 14:
			return Race.human;
		case 15:
			return Race.elf;
		case 16:
			return Race.darkelf;
		case 17:
			return Race.orc;
		case 18:
			return Race.dwarf;
		case 25:
			return Race.kamael;
		}
		return null;
	}

	private ClassType getVillageMasterTeachType()
	{
		switch (getNpcId())
		{
		case 30031:
		case 30037:
		case 30070:
		case 30120:
		case 30191:
		case 30289:
		case 30857:
		case 30905:
		case 32095:
		case 30141:
		case 30305:
		case 30358:
		case 30359:
		case 31336:
			return ClassType.Priest;

		case 30115:
		case 30174:
		case 30175:
		case 30176:
		case 30694:
		case 30854:
		case 31331:
		case 31755:
		case 31996:
		case 32098:
		case 32147:
		case 32160:
		case 30154:
		case 31285:
		case 31288:
		case 31326:
		case 31977:
		case 32150:
			return ClassType.Mystic;
		default:
		}

		return ClassType.Fighter;
	}
}
package services.community;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.cache.ImagesCache;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SubClass;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.PlayerClass;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.instances.SchemeBufferInstance;
import l2mv.gameserver.model.instances.VillageMasterInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.network.clientpackets.CharacterCreate;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.HideBoard;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

public class CommunityNpcs implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityNpcs.class);

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			_log.info("CommunityBoard: Npcs loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsgetfav",
			"_bbsnpcs",
			"_bbsgatekeeper",
			"_bbsbuffer",
			"_bbsbufferbypass",
			"_bbsNewSubPage",
			"_bbsAddNewSub",
			"_changeSubPage",
			"_bbsChangeSubTo",
			"_bbsCancelSubPage",
			"_bbsSelectCancelSub",
			"_bbsChooseCertificate",
			"_decreasePKPage",
			"_decreasePK",
			"_actionToAsk",
			"_changeNick",
			"_changeClanName"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String folder = "";
		String file = "";
		int subclassId = 0;
		if (!cmd.equals("bbsNewSubPage") && !cmd.equals("bbsAddNewSub"))
		{
			player.deleteQuickVar("SubToRemove");
		}

		if ("bbsgetfav".equals(cmd) || "bbsnpcs".equals(cmd) || "bbsnpcs".equals(cmd))
		{
			sendFileToPlayer(player, "bbs_npcs.htm", true);
			return;
		}

		if ("bbsgetfav".equals(cmd))
		{
			HideBoard p = new HideBoard();
			player.sendPacket(p);
			return;
		}

		if ("bbsgatekeeper".equals(cmd))
		{
			folder = "gatekeeper";
//		{
//			// Synerge - Only allow using the gatekeeper when close to the global gatekeeper npc
//			boolean found = player.isInFightClub() || player.getReflection().getInstancedZoneId() == ReflectionManager.TOURNAMENT_REFLECTION_ID;
//			if (!found)
//			{
//				for (Creature cha : World.getAroundCharacters(player, 1500, 200))
//				{
//					if (cha.isNpc() && cha.getNpcId() == 37001)
//					{
//						found = true;
//						break;
//					}
//				}
//			}
//			if (found)
//				folder = "gatekeeper";
//		}
		}

		if ("bbsbuffer".equals(cmd))
		{
			SchemeBufferInstance.showWindow(player);
			return;
		}

		if ("bbsbufferbypass".equals(cmd))
		{
			if (bypass.contains("_bbsbufferbypass_"))
			{
				SchemeBufferInstance.onBypass(player, bypass.substring("_bbsbufferbypass_".length()));
			}
			return;
		}

		if ("bbsNewSubPage".equals(cmd))
		{
			String race = st.nextToken();
			addNewSubPage(player, race);
			return;
		}

		if ("bbsAddNewSub".equals(cmd))
		{
			subclassId = Integer.parseInt(st.nextToken());
			addNewSub(player, subclassId);
			return;
		}

		if ("changeSubPage".equals(cmd))
		{
			changeSubPage(player);
			return;
		}

		if ("bbsChangeSubTo".equals(cmd))
		{
			subclassId = Integer.parseInt(st.nextToken());
			changeSub(player, subclassId);
			return;
		}

		if ("bbsCancelSubPage".equals(cmd))
		{
			cancelSubPage(player);
			return;
		}

		if ("bbsSelectCancelSub".equals(cmd))
		{
			subclassId = Integer.parseInt(st.nextToken());
			player.addQuickVar("SubToRemove", subclassId);
			sendFileToPlayer(player, "smallNpcs/subclassChanger_add.htm", true);
			return;
		}

		if ("bbsChooseCertificate".equals(cmd))
		{
			chooseCertificatePage(player);
			return;
		}

		if ("decreasePKPage".equals(cmd))
		{
			decreasePKPage(player);
			return;
		}

		if ("decreasePK".equals(cmd))
		{
			if (!player.isInPeaceZone())
			{
				player.sendMessage("You cannot do it right now!");
				return;
			}
			int pksToDecrease = Integer.parseInt(st.nextToken());
			if (player.getInventory().getCountOf(Config.SERVICES_WASH_PK_ITEM) >= Config.SERVICES_WASH_PK_PRICE)
			{
				player.getInventory().destroyItemByItemId(Config.SERVICES_WASH_PK_ITEM, Config.SERVICES_WASH_PK_PRICE, "Decreasing Pk");
				player.setPkKills(player.getPkKills() - pksToDecrease);
				player.broadcastCharInfo();
				player.sendMessage(pksToDecrease + " PKs were decreased!");
			}
			else
			{
				player.sendMessage("You don't have required items!");
			}
			onBypassCommand(player, "decreasePKPage");
			return;
		}

		if ("actionToAsk".equals(cmd))
		{
			int id = Integer.parseInt(st.nextToken());
			String message = null;
			if (id == 0)
			{
				message = "Would you like to increase Warehouse Slots by 1 for " + Config.SERVICES_EXPAND_INVENTORY_PRICE + " Coin?";
			}
			else if (id == 1)
			{
				message = "Would you like to increase Clan Warehouse Slots by 1 for " + Config.SERVICES_EXPAND_CWH_PRICE + " Coin?";
			}

			if (message != null)
			{
				player.ask(new ConfirmDlg(SystemMsg.S1, 60000).addString(message), new ActionAnswerListener(player, id));
			}
			sendFileToPlayer(player, "smallNpcs/exclusiveShop.htm", true);
			return;
		}

		if ("changeNick".equals(cmd))
		{
			if (st.hasMoreTokens())
			{
				String newName = st.nextToken().trim();
				changeNick(player, newName);
			}
			return;
		}

		if ("changeClanName".equals(cmd))
		{
			if (st.hasMoreTokens())
			{
				String newName = st.nextToken().trim();
				changeClanName(player, newName);
			}
			return;
		}

		if (!folder.isEmpty())
		{
			file = st.hasMoreTokens() ? st.nextToken() : "main.htm";
		}

		sendFileToPlayer(player, folder + '/' + file, false);
	}

	private static void changeNick(Player player, String newName)
	{
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_NICK_ENABLED2)
		{
			player.sendMessage("Service is disabled.");
			return;
		}
		if (player.isHero())
		{
			player.sendMessage("Not available for heroes.");
			return;
		}

		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", player));
			return;
		}

		if (!CharacterCreate.checkName(newName) && !Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL2)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.incorrectinput", player));
			return;
		}

		if (player.getInventory().getCountOf(Config.SERVICES_CHANGE_NICK_ITEM2) < Config.SERVICES_CHANGE_NICK_PRICE2)
		{
			if (Config.SERVICES_CHANGE_NICK_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			return;
		}

		if (CharacterDAO.getInstance().getObjectIdByName(newName) > 0)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.Thisnamealreadyexists", player));
			return;
		}

		player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_NICK_ITEM2, Config.SERVICES_CHANGE_NICK_PRICE2, "Nick Change");

		String oldName = player.getName();
		player.reName(newName, true);
		Log.add("Character " + oldName + " renamed to " + newName, "renames");
		player.sendMessage(new CustomMessage("scripts.services.Rename.changedname", player).addString(oldName).addString(newName));
		player.sendPacket(new HideBoard());
	}

	private static void changeClanName(Player player, String newName)
	{
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED2)
		{
			player.sendMessage("Service is disabled.");
			return;
		}

		if (player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", player));
			return;
		}

		if (!Util.isMatchingRegexp(newName, Config.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
			return;
		}

		if (ClanTable.getInstance().getClanByName(newName) != null)
		{
			player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		if (player.getInventory().getCountOf(Config.SERVICES_CHANGE_CLAN_NAME_ITEM2) < Config.SERVICES_CHANGE_CLAN_NAME_PRICE2)
		{
			player.sendMessage("Incorrect Coin Count. You need " + Config.SERVICES_CHANGE_CLAN_NAME_PRICE2 + ". You have " + player.getInventory().getCountOf(Config.SERVICES_CHANGE_CLAN_NAME_ITEM2));
			return;
		}

		player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_CLAN_NAME_ITEM2, Config.SERVICES_CHANGE_CLAN_NAME_PRICE2, "Clan Name Change");

		final String oldName = player.getClan().getName();
		Log.add("Clan " + oldName + " renamed to " + newName, "renames");
		player.sendMessage("You have changed your clan's name from " + oldName + " to " + newName);
		player.sendPacket(new HideBoard());

		SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		sub.setName(newName, true);
		player.getClan().broadcastClanStatus(true, true, false);
	}

	private static void sendFileToPlayer(Player player, String path, boolean sendImages, String... replacements)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + path, player);

		if (sendImages)
		{
			ImagesCache.getInstance().sendUsedImages(html, player);
		}

		for (int i = 0; i < replacements.length; i += 2)
		{
			String toReplace = replacements[i + 1];
			if (toReplace == null)
			{
				toReplace = "<br>";
			}
			html = html.replace(replacements[i], toReplace);
		}

		ShowBoard.separateAndSend(html, player);
	}

	private static final int FIELDS_IN_SUB_SELECT_PAGE = 11;

	private static void addNewSubPage(Player player, String raceName)
	{
		Race race = Race.valueOf(raceName);
		Set<PlayerClass> allSubs = VillageMasterInstance.getAllAvailableSubs(player, true);
		allSubs = getSubsByRace(allSubs, race);

		PlayerClass[] arraySubs = new PlayerClass[allSubs.size()];
		arraySubs = allSubs.toArray(arraySubs);

		String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
		for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++)
		{
			replacements[i * 2] = "%sub" + i + '%';
			if (arraySubs.length <= i)
			{
				replacements[i * 2 + 1] = "<br>";
			}
			else
			{
				PlayerClass playerClass = arraySubs[i];
				replacements[i * 2 + 1] = "<button value=\"Add " + playerClass.name() + "\" action=\"bypass _bbsAddNewSub_" + playerClass.ordinal() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">";
			}
		}
		sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
	}

	private static void changeSubPage(Player player)
	{
		Collection<SubClass> allSubs = player.getSubClasses().values();
		List<SubClass> availableSubs = new ArrayList<SubClass>();
		for (SubClass sub : allSubs)
		{
			if (sub.getClassId() != player.getActiveClassId())
			{
				availableSubs.add(sub);
			}
		}

		String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
		for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++)
		{
			replacements[i * 2] = "%sub" + i + '%';
			if (availableSubs.size() <= i)
			{
				replacements[i * 2 + 1] = "<br>";
			}
			else
			{
				SubClass playerClass = availableSubs.get(i);
				replacements[i * 2 + 1] = "<button value=\"Change To " + Util.getFullClassName(ClassId.values()[playerClass.getClassId()]) + "\" action=\"bypass _bbsChangeSubTo_" + playerClass.getClassId() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">";
			}
		}
		sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
	}

	private static void cancelSubPage(Player player)
	{
		List<SubClass> subToChoose = new ArrayList<SubClass>();
		for (SubClass sub : player.getSubClasses().values())
		{
			if (!sub.isBase())
			{
				subToChoose.add(sub);
			}
		}
		String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
		for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++)
		{
			replacements[i * 2] = "%sub" + i + '%';
			if (subToChoose.size() <= i)
			{
				replacements[i * 2 + 1] = "<br>";
			}
			else
			{
				SubClass playerClass = subToChoose.get(i);
				replacements[i * 2 + 1] = "<button value=\"Remove " + Util.getFullClassName(ClassId.values()[playerClass.getClassId()]) + "\" action=\"bypass _bbsSelectCancelSub_" + playerClass.getClassId() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">";
			}
		}
		sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
	}

	private static void changeSub(Player player, int subId)
	{
		if (!canChangeClass(player))
		{
			return;
		}

		player.setActiveSubClass(subId, true);

		player.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
		player.sendPacket(new HideBoard());
	}

	private static void addNewSub(Player player, int subclassId)
	{
		if (!canChangeClass(player))
		{
			return;
		}

		int subToRemove = player.getQuickVarI("SubToRemove");
		boolean added;
		if (subToRemove > 0)
		{
			added = player.modifySubClass(subToRemove, subclassId);
		}
		else
		{
			added = VillageMasterInstance.addNewSubclass(player, subclassId);
		}

		if (added)
		{
			player.sendMessage("Subclass was added!");
		}
		else
		{
			player.sendMessage("Subclass couldn't be added!");
		}
		player.sendPacket(new HideBoard());
	}

	private static Set<PlayerClass> getSubsByRace(Set<PlayerClass> allSubs, Race race)
	{
		for (PlayerClass sub : allSubs)
		{
			if (!sub.isOfRace(race))
			{
				allSubs.remove(sub);
			}
		}
		return allSubs;
	}

	private static void chooseCertificatePage(Player player)
	{
		if (!canChangeClass(player))
		{
			return;
		}

		if (player.getBaseClassId() == player.getClassId().getId())
		{
			sendFileToPlayer(player, "smallNpcs/subclassChanger_back.htm", true);
			return;
		}

		String[][] certifications =
		{
			{
				"Level 65 Emergent",
				"CommunityCert65"
			},
			{
				"Level 70 Emergent",
				"CommunityCert70"
			},
			{
				"Level 75 Class Specific",
				"CommunityCert75Class"
			},
			{
				"Level 75 Master",
				"CommunityCert75Master"
			},
			{
				"Level 80 Divine",
				"CommunityCert80"
			}
		};

		String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
		for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++)
		{
			replacements[i * 2] = "%sub" + i + '%';
			if (certifications.length <= i)
			{
				replacements[i * 2 + 1] = "<br>";
			}
			else
			{
				String[] button = certifications[i];
				replacements[i * 2 + 1] = "<button value=\"Add " + button[0] + "\" action=\"bypass _bbsscripts:Util:" + button[1] + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">";
			}
		}
		sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
	}

	private String[] fillReplacements(String[] currentReplacements, String stringInside, int count)
	{
		for (int i = 0; i <= count; i++)
		{
			currentReplacements[i * 2] = '%' + stringInside + i + '%';
		}
		return currentReplacements;
	}

	private static final int[] PKS =
	{
		1,
		5,
		10,
		50,
		100 // , 250, 500, 1000
	};

	private void decreasePKPage(Player player)
	{
		String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
		replacements = fillReplacements(replacements, "action", 10);

		int replaceIndex = 1;

		for (int pk : PKS)
		{
			if (player.getPkKills() <= pk)
			{
				break;
			}
			replacements[replaceIndex] = getDecreasePkButton(pk);
			replaceIndex += 2;
		}

		if (player.getPkKills() > 0)
		{
			replacements[replaceIndex] = getDecreasePkButton(player.getPkKills());
			replaceIndex += 2;
		}
		replacements[replaceIndex] = "<button value=\"Back\" action=\"bypass _bbsfile:smallNpcs/exclusiveShop\" width=100 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";

		sendFileToPlayer(player, "smallNpcs/exclusiveShop_select.htm", true, replacements);
	}

	private static String getDecreasePkButton(int pks)
	{
		return "<button value=\"" + pks + " PKs for " + (Config.SERVICES_WASH_PK_PRICE * pks) + " Coins\" action=\"bypass _decreasePK_" + pks + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">";
	}

	private static boolean canChangeClass(Player player)
	{
		if (player.getPet() != null)
		{
			player.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
			return false;
		}

		// Sub class can not be obtained or changed while using the skill or character is in transformation
		if (player.isActionsDisabled() || player.getTransformation() != 0)
		{
			player.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
			return false;
		}

		if (!player.isInPeaceZone())
		{
			player.sendMessage("You cannot change Subclass right now!");
			return false;
		}
		if (Olympiad.isRegisteredInComp(player))
		{
			player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			return false;
		}
		return true;
	}

	private static class ActionAnswerListener implements OnAnswerListener
	{
		private final Player player;
		private final int action;

		private ActionAnswerListener(Player player, int action)
		{
			this.player = player;
			this.action = action;
		}

		@Override
		public void sayYes()
		{
			if (action == 0)// Inventory
			{
				if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_WAREHOUSE_ITEM, Config.SERVICES_EXPAND_WAREHOUSE_PRICE, "Inventory Expand"))
				{
					player.setExpandWarehouse(player.getExpandWarehouse() + 1);
					player.setVar("ExpandWarehouse", String.valueOf(player.getExpandWarehouse()), -1);
					player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
				}
				else if (Config.SERVICES_EXPAND_WAREHOUSE_ITEM == 57)
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				}
				else
				{
					player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
				}
			}
			else if (action == 1)// CWH
			{
				if (player.getClan() == null)
				{
					player.sendMessage("You need to have Clan, to use that option!");
					return;
				}
				if (player.getClan().getWhBonus() >= 500)
				{
					player.sendMessage("500 Slots is Max for Clan Warehouse");
					return;
				}
				if (player.getClan() == null)
				{
					player.sendMessage("You must be in clan.");
					return;
				}

				if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_CWH_ITEM, Config.SERVICES_EXPAND_CWH_PRICE, "CWH Expand"))
				{
					player.getClan().setWhBonus(player.getClan().getWhBonus() + 1);
					player.sendMessage("Warehouse capacity is now " + (Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
				}
				else if (Config.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.ITEM_ID_ADENA)
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				}
				else
				{
					player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
				}
			}
		}

		@Override
		public void sayNo()
		{
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}

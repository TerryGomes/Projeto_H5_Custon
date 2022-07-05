package services.community;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.RewardListInfo;
import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.cache.ImagesCache;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.instancemanager.SpawnManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.reward.CalculateRewardChances;
import l2f.gameserver.network.serverpackets.RadarControl;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Util;

/**
 * Community Board page containing Drop Calculator
 */
public class CommunityDropCalculator implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityDropCalculator.class);

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED && !ConfigHolder.getBool("EnableMergeCommunity"))
		{
			_log.info("CommunityBoard: Drop Calculator service loaded.");
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
			"_dropCalc",
			"_dropItemsByName",
			"_dropMonstersByItem",
			"_dropMonstersByName",
			"_dropMonsterDetailsByItem",
			"_dropMonsterDetailsByName"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);

		if (!Config.ALLOW_DROP_CALCULATOR)
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropCalcOff.htm", player);
			ShowBoard.separateAndSend(html, player);
			return;
		}

		switch (cmd)
		{
		case "dropCalc":
		case "friendlist":
			showMainPage(player);
			break;
		case "dropItemsByName":
			if (!st.hasMoreTokens())
			{
				showMainPage(player);
				return;
			}
			String itemName = "";
			while (st.countTokens() > 1)
			{
				itemName += " " + st.nextToken();
			}

			int itemsPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showDropItemsByNamePage(player, itemName.trim(), itemsPage);
			break;
		case "dropMonstersByItem":
			int itemId = Integer.parseInt(st.nextToken());
			int monstersPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showDropMonstersByItem(player, itemId, monstersPage);
			break;
		case "dropMonsterDetailsByItem":
			int monsterId = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
			{
				manageButton(player, Integer.parseInt(st.nextToken()), monsterId);
			}
			showdropMonsterDetailsByItem(player, monsterId);
			break;
		case "dropMonstersByName":
			if (!st.hasMoreTokens())
			{
				showMainPage(player);
				return;
			}
			String monsterName = "";
			while (st.countTokens() > 1)
			{
				monsterName += " " + st.nextToken();
			}

			int monsterPage = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showDropMonstersByName(player, monsterName.trim(), monsterPage);
			break;
		case "dropMonsterDetailsByName":
			int chosenMobId = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
			{
				manageButton(player, Integer.parseInt(st.nextToken()), chosenMobId);
			}
			showDropMonsterDetailsByName(player, chosenMobId);
			break;
		default:
			break;
		}
	}

	private static void showMainPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropCalcMain.htm", player);
		ShowBoard.separateAndSend(html, player);
	}

	private static void showDropItemsByNamePage(Player player, String itemName, int page)
	{
		player.addQuickVar("DCItemName", itemName);
		player.addQuickVar("DCItemsPage", page);
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropItemsByName.htm", player);
		html = replaceItemsByNamePage(html, itemName, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceItemsByNamePage(String html, String itemName, int page)
	{
		String newHtml = html;

		List<ItemTemplate> itemsByName = ItemHolder.getInstance().getItemsByNameContainingString(itemName, true);
		itemsByName.sort(new ItemComparator(itemName));

		int itemIndex = 0;

		for (int i = 0; i < 8; i++)
		{
			itemIndex = i + (page - 1) * 8;
			ItemTemplate item = itemsByName.size() > itemIndex ? itemsByName.get(itemIndex) : null;

			newHtml = newHtml.replace("%itemIcon" + i + '%', item != null ? getItemIcon(item) : "<br>");
			newHtml = newHtml.replace("%itemName" + i + '%', item != null ? getName(item.getName()) : "<br>");
			newHtml = newHtml.replace("%itemGrade" + i + '%', item != null ? getItemGradeIcon(item) : "<br>");
			newHtml = newHtml.replace("%dropLists" + i + '%', item != null ? String.valueOf(CalculateRewardChances.getDroplistsCountByItemId(item.getItemId(), true)) : "<br>");
			newHtml = newHtml.replace("%spoilLists" + i + '%', item != null ? String.valueOf(CalculateRewardChances.getDroplistsCountByItemId(item.getItemId(), false)) : "<br>");
			newHtml = newHtml.replace("%showMonsters" + i + '%', item != null ? "<button value=\"Show Monsters\" action=\"bypass _dropMonstersByItem_%itemChosenId" + i + "%\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
			newHtml = newHtml.replace("%itemChosenId" + i + '%', item != null ? String.valueOf(item.getItemId()) : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"Previous\" action=\"bypass _dropItemsByName_" + itemName + "_" + (page - 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", itemsByName.size() > itemIndex + 1 ? "<button value=\"Next\" action=\"bypass _dropItemsByName_" + itemName + "_" + (page + 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");

		newHtml = newHtml.replace("%searchItem%", itemName);
		newHtml = newHtml.replace("%page%", String.valueOf(page));

		return newHtml;
	}

	private static void showDropMonstersByItem(Player player, int itemId, int page)
	{
		player.addQuickVar("DCItemId", itemId);
		player.addQuickVar("DCMonstersPage", page);
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropMonstersByItem.htm", player);
		html = replaceMonstersByItemPage(player, html, itemId, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonstersByItemPage(Player player, String html, int itemId, int page)
	{
		String newHtml = html;

		List<CalculateRewardChances.NpcTemplateDrops> templates = CalculateRewardChances.getNpcsByDropOrSpoil(itemId);
		templates.sort(new ItemChanceComparator(player, itemId));

		int npcIndex = 0;

		for (int i = 0; i < 10; i++)
		{
			npcIndex = i + (page - 1) * 10;
			CalculateRewardChances.NpcTemplateDrops drops = templates.size() > npcIndex ? templates.get(npcIndex) : null;
			NpcTemplate npc = templates.size() > npcIndex ? templates.get(npcIndex).template : null;

			newHtml = newHtml.replace("%monsterName" + i + '%', npc != null ? getName(npc.getName()) : "<br>");
			newHtml = newHtml.replace("%monsterLevel" + i + '%', npc != null ? String.valueOf(npc.level) : "<br>");
			newHtml = newHtml.replace("%monsterAggro" + i + '%', npc != null ? Util.boolToString(npc.aggroRange > 0) : "<br>");
			newHtml = newHtml.replace("%monsterType" + i + '%', npc != null ? drops.dropNoSpoil ? "Drop" : "Spoil" : "<br>");
			newHtml = newHtml.replace("%monsterCount" + i + '%', npc != null ? String.valueOf(getDropCount(player, npc, itemId, drops.dropNoSpoil)) : "<br>");
			newHtml = newHtml.replace("%monsterChance" + i + '%', npc != null ? String.valueOf(getDropChance(player, npc, itemId, drops.dropNoSpoil)) : "<br>");
			newHtml = newHtml.replace("%showDetails" + i + '%', npc != null ? "<button value=\"Show Details\" action=\"bypass _dropMonsterDetailsByItem_%monsterId" + i + "%\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
			newHtml = newHtml.replace("%monsterId" + i + '%', npc != null ? String.valueOf(npc.getNpcId()) : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"Previous\" action=\"bypass _dropMonstersByItem_%itemChosenId%_" + (page - 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", templates.size() > npcIndex + 1 ? "<button value=\"Next\" action=\"bypass _dropMonstersByItem_%itemChosenId%_" + (page + 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");

		newHtml = newHtml.replace("%searchItem%", player.getQuickVarS("DCItemName"));
		newHtml = newHtml.replace("%searchItemPage%", String.valueOf(player.getQuickVarI("DCItemsPage")));
		newHtml = newHtml.replace("%itemChosenId%", String.valueOf(itemId));
		newHtml = newHtml.replace("%monsterPage%", String.valueOf(page));
		return newHtml;
	}

	private static void showdropMonsterDetailsByItem(Player player, int monsterId)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropMonsterDetailsByItem.htm", player);
		html = replaceMonsterDetails(player, html, monsterId);

		// DO NOT ALLOW TO TELEPORT TO MOBS
//		if (!canTeleToMonster(player, monsterId, false))
//			html = html.replace("%goToNpc%", "<br>");
//		else
//			html = html.replace("%goToNpc%", "<button value=\"Go to Npc\" action=\"bypass _dropMonsterDetailsByItem_"+monsterId+"_3\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">");

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonsterDetails(Player player, String html, int monsterId)
	{
		String newHtml = html;

		int itemId = player.getQuickVarI("DCItemId");
		NpcTemplate template = NpcHolder.getInstance().getTemplate(monsterId);
		if (template == null)
		{
			return newHtml;
		}

		newHtml = newHtml.replace("%searchName%", String.valueOf(player.getQuickVarS("DCMonsterName")));
		newHtml = newHtml.replace("%itemChosenId%", String.valueOf(player.getQuickVarI("DCItemId")));
		newHtml = newHtml.replace("%monsterPage%", String.valueOf(player.getQuickVarI("DCMonstersPage")));
		newHtml = newHtml.replace("%monsterId%", String.valueOf(monsterId));
		newHtml = newHtml.replace("%monsterName%", getName(template.getName()));
		newHtml = newHtml.replace("%monsterLevel%", String.valueOf(template.level));
		newHtml = newHtml.replace("%monsterAggro%", Util.boolToString(template.aggroRange > 0));
		if (itemId > 0)
		{
			newHtml = newHtml.replace("%monsterDropSpecific%", String.valueOf(getDropChance(player, template, itemId, true)));
			newHtml = newHtml.replace("%monsterSpoilSpecific%", String.valueOf(getDropChance(player, template, itemId, false)));
		}
		newHtml = newHtml.replace("%monsterDropAll%", String.valueOf(CalculateRewardChances.getDrops(template, true, false).size()));
		newHtml = newHtml.replace("%monsterSpoilAll%", String.valueOf(CalculateRewardChances.getDrops(template, false, true).size()));
		newHtml = newHtml.replace("%spawnCount%", String.valueOf(SpawnManager.getInstance().getSpawnedCountByNpc(monsterId)));
		newHtml = newHtml.replace("%minions%", String.valueOf(template.getMinionData().size()));
		newHtml = newHtml.replace("%expReward%", String.valueOf(template.rewardExp));
		newHtml = newHtml.replace("%maxHp%", String.valueOf(template.baseHpMax));
		newHtml = newHtml.replace("%maxMP%", String.valueOf(template.baseMpMax));
		newHtml = newHtml.replace("%pAtk%", String.valueOf(template.basePAtk));
		newHtml = newHtml.replace("%mAtk%", String.valueOf(template.baseMAtk));
		newHtml = newHtml.replace("%pDef%", String.valueOf(template.basePDef));
		newHtml = newHtml.replace("%mDef%", String.valueOf(template.baseMDef));
		newHtml = newHtml.replace("%atkSpd%", String.valueOf(template.basePAtkSpd));
		newHtml = newHtml.replace("%castSpd%", String.valueOf(template.baseMAtkSpd));
		newHtml = newHtml.replace("%runSpd%", String.valueOf(template.baseRunSpd));

		return newHtml;
	}

	private static void showDropMonstersByName(Player player, String monsterName, int page)
	{
		player.addQuickVar("DCMonsterName", monsterName);
		player.addQuickVar("DCMonstersPage", page);
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropMonstersByName.htm", player);
		html = replaceMonstersByName(html, monsterName, page);
		ShowBoard.separateAndSend(html, player);
	}

	private static String replaceMonstersByName(String html, String monsterName, int page)
	{
		String newHtml = html;
		List<NpcTemplate> npcTemplates = CalculateRewardChances.getNpcsContainingString(monsterName);
		npcTemplates = sortMonsters(npcTemplates, monsterName);

		int npcIndex = 0;

		for (int i = 0; i < 10; i++)
		{
			npcIndex = i + (page - 1) * 10;
			NpcTemplate npc = npcTemplates.size() > npcIndex ? npcTemplates.get(npcIndex) : null;

			newHtml = newHtml.replace("%monsterName" + i + '%', npc != null ? getName(npc.getName()) : "<br>");
			newHtml = newHtml.replace("%monsterLevel" + i + '%', npc != null ? String.valueOf(npc.level) : "<br>");
			newHtml = newHtml.replace("%monsterAggro" + i + '%', npc != null ? Util.boolToString(npc.aggroRange > 0) : "<br>");
			newHtml = newHtml.replace("%monsterDrops" + i + '%', npc != null ? String.valueOf(CalculateRewardChances.getDrops(npc, true, false).size()) : "<br>");
			newHtml = newHtml.replace("%monsterSpoils" + i + '%', npc != null ? String.valueOf(CalculateRewardChances.getDrops(npc, false, true).size()) : "<br>");
			newHtml = newHtml.replace("%showDetails" + i + '%', npc != null ? "< button value =\"Show Details\" action=\"bypass _dropMonsterDetailsByName_" + npc.getNpcId() + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
		}

		newHtml = newHtml.replace("%previousButton%", page > 1 ? "<button value=\"Previous\" action=\"bypass _dropMonstersByName_%searchName%_" + (page - 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");
		newHtml = newHtml.replace("%nextButton%", npcTemplates.size() > npcIndex + 1 ? "<button value=\"Next\" action=\"bypass _dropMonstersByName_%searchName%_" + (page + 1) + "\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">" : "<br>");

		newHtml = newHtml.replace("%searchName%", monsterName);
		newHtml = newHtml.replace("%page%", String.valueOf(page));
		return newHtml;
	}

	private static void showDropMonsterDetailsByName(Player player, int monsterId)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_dropMonsterDetailsByName.htm", player);
		html = replaceMonsterDetails(player, html, monsterId);
//		if (!canTeleToMonster(player, monsterId, false))
//			html = html.replace("%goToNpc%", "<br>");
//		else
//			html = html.replace("%goToNpc%", "<button value=\"Go to Npc\" action=\"bypass _dropMonsterDetailsByName_" + monsterId + "_3\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1" + ".OlympiadWnd_DF_Fight1None\">");

		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private static void manageButton(Player player, int buttonId, int monsterId)
	{
		switch (buttonId)
		{
		case 1:// Show Monster on Map
			final List<Location> locs = SpawnManager.getInstance().getRandomSpawnsByNpc(monsterId);
			if (locs == null || locs.isEmpty())
			{
				return;
			}

			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new Say2(player.getObjectId(), ChatType.COMMANDCHANNEL_ALL, "", "Open Map to see Locations"));

			for (Location loc : locs)
			{
				player.sendPacket(new RadarControl(0, 1, loc));
			}
			break;
		case 2:// Show Drops
			RewardListInfo.showInfo(player, NpcHolder.getInstance().getTemplate(monsterId), false, false, 1.0);
			break;
//			case 3:// Teleport To Monster
//				if (!canTeleToMonster(player, monsterId, true))
//				{
//					return;
//				}
//				List<NpcInstance> aliveInstance = GameObjectsStorage.getAllByNpcId(monsterId, true);
//				if (!aliveInstance.isEmpty())
//					player.teleToLocation(aliveInstance.get(0).getLoc());
//				else
//					player.sendMessage("Monster isn't alive!");
//				break;
		default:
			break;
		}
	}

//	private static boolean canTeleToMonster(Player player, int monsterId, boolean sendMessage)
//	{
//		if (!player.isInZonePeace())
//		{
//			if (sendMessage)
//				player.sendMessage("You can do it only in safe zone!");
//			return false;
//		}
//
//		if (Olympiad.isRegistered(player) || player.isInOlympiadMode())
//		{
//			if (sendMessage)
//				player.sendMessage("You cannot do it while being registered in Olympiad Battle!");
//			return false;
//		}
//
//		if (Arrays.binarySearch(Config.DROP_CALCULATOR_DISABLED_TELEPORT, monsterId) >= 0)
//		{
//			if (sendMessage)
//				player.sendMessage("You cannot teleport to this Npc!");
//			return false;
//		}
//
//		return true;
//	}

	private static CharSequence getItemIcon(ItemTemplate template)
	{
		return "<img src=\"" + template.getIcon() + "\" width=32 height=32>";
	}

	private static CharSequence getItemGradeIcon(ItemTemplate template)
	{
		if (template.getCrystalType() == ItemTemplate.Grade.NONE)
		{
			return "";
		}
		return "<img src=\"L2UI_CT1.Icon_DF_ItemGrade_" + template.getCrystalType() + "\" width=16 height=16>";
	}

	private static CharSequence getName(String name)
	{
		if (name.length() > 24)
		{
			return "</font><font color=31B404>" + name;
		}
		return name;
	}

	private static String getDropCount(Player player, NpcTemplate monster, int itemId, boolean drop)
	{
		long[] counts = CalculateRewardChances.getDropCounts(player, monster, drop, itemId);
		String formattedCounts = "[" + counts[0] + "..." + counts[1] + ']';
		if (formattedCounts.length() > 20)
		{
			formattedCounts = "</font><font color=31B404>" + formattedCounts;
		}
		return formattedCounts;
	}

	private static String getDropChance(Player player, NpcTemplate monster, int itemId, boolean drop)
	{
		String chance = CalculateRewardChances.getDropChance(player, monster, drop, itemId);
		return formatDropChance(chance);
	}

	public static String formatDropChance(String chance)
	{
		String realChance = chance;
		if (realChance.length() - realChance.indexOf('.') > 6)
		{
			realChance = realChance.substring(0, realChance.indexOf('.') + 7);
		}

		if (realChance.endsWith(".0"))
		{
			realChance = realChance.substring(0, realChance.length() - 2);
		}

		return realChance + '%';
	}

	private static class ItemComparator implements Comparator<ItemTemplate>, Serializable
	{
		private static final long serialVersionUID = -6389059445439769861L;
		private final String search;

		private ItemComparator(String search)
		{
			this.search = search;
		}

		@Override
		public int compare(ItemTemplate o1, ItemTemplate o2)
		{
			if (o1.equals(o2))
			{
				return 0;
			}
			if (o1.getName().equalsIgnoreCase(search))
			{
				return -1;
			}
			if (o2.getName().equalsIgnoreCase(search))
			{
				return 1;
			}

			return Integer.compare(CalculateRewardChances.getDroplistsCountByItemId(o2.getItemId(), true), CalculateRewardChances.getDroplistsCountByItemId(o1.getItemId(), true));
		}
	}

	private static class ItemChanceComparator implements Comparator<CalculateRewardChances.NpcTemplateDrops>, Serializable
	{
		private static final long serialVersionUID = 6323413829869254438L;
		private final int itemId;
		private final Player player;

		private ItemChanceComparator(Player player, int itemId)
		{
			this.itemId = itemId;
			this.player = player;
		}

		@Override
		public int compare(CalculateRewardChances.NpcTemplateDrops o1, CalculateRewardChances.NpcTemplateDrops o2)
		{
			BigDecimal maxDrop1 = BigDecimal.valueOf(CalculateRewardChances.getDropCounts(player, o1.template, o1.dropNoSpoil, itemId)[1]);
			BigDecimal maxDrop2 = BigDecimal.valueOf(CalculateRewardChances.getDropCounts(player, o2.template, o2.dropNoSpoil, itemId)[1]);
			BigDecimal chance1 = new BigDecimal(CalculateRewardChances.getDropChance(player, o1.template, o1.dropNoSpoil, itemId));
			BigDecimal chance2 = new BigDecimal(CalculateRewardChances.getDropChance(player, o2.template, o2.dropNoSpoil, itemId));

			int compare = chance2.multiply(maxDrop2).compareTo(chance1.multiply(maxDrop1));
			if (compare == 0)
			{
				return o2.template.getName().compareTo(o1.template.getName());
			}
			return compare;
		}
	}

	private static List<NpcTemplate> sortMonsters(List<NpcTemplate> npcTemplates, String monsterName)
	{
		Collections.sort(npcTemplates, new MonsterComparator(monsterName));
		return npcTemplates;
	}

	private static class MonsterComparator implements Comparator<NpcTemplate>, Serializable
	{
		private static final long serialVersionUID = 2116090903265145828L;
		private final String search;

		private MonsterComparator(String search)
		{
			this.search = search;
		}

		@Override
		public int compare(NpcTemplate o1, NpcTemplate o2)
		{
			if (o1.equals(o2))
			{
				return 0;
			}
			if (o1.getName().equalsIgnoreCase(search))
			{
				return 1;
			}
			if (o2.getName().equalsIgnoreCase(search))
			{
				return -1;
			}

			return o2.getName().compareTo(o2.getName());
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
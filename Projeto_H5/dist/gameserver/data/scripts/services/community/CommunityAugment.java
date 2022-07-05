package services.community;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;
import l2f.commons.dao.JdbcEntityState;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.actor.instances.player.ShortCut;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.ShortCutRegister;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.item.ItemTemplate.Grade;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.ValueSortMap;
import l2f.gameserver.utils.XMLUtil;

/**
 * @author Infern0
 */
public class CommunityAugment implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityAugment.class);
	private static TIntObjectHashMap<SortBy> _playerSortBy = new TIntObjectHashMap<>();
	private static TIntObjectHashMap<String> _playerSearch = new TIntObjectHashMap<>();
	private static List<AugmentationData> _augdata = new ArrayList<AugmentationData>();

	@Override
	public void onLoad()
	{
		if (!Config.ALLOW_CB_AUGMENTATION)
		{
			return;
		}
		CommunityBoardManager.getInstance().registerHandler(this);
		load();
		_log.info("CommunityBoard: Augmentation Manager loaded with " + _augdata.size() + " lifestone skills.");
	}

	@Override
	public void onReload()
	{
		if (!Config.ALLOW_CB_AUGMENTATION)
		{
			return;
		}
		CommunityBoardManager.getInstance().removeHandler(this);
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
			"_bbsls",
			"_bbslsShowItems",
			"_bbslsInsertItem",
			"_bbslsSetStat",
			"_bbslsSearch",
			"_bbslsySort",
			"_bbslsyReset",
			"_bbslsAugment"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		final StringTokenizer str = new StringTokenizer(bypass, " ");
		final String cmd = str.nextToken();
		if (!checkConditions(player))
		{
			return;
		}
		if (cmd.equalsIgnoreCase("_bbsls"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			if (!st.hasMoreTokens())
			{
				player.setCommunityAugmentStat(0);
				player.setCommunityAugmentItem(null);
			}
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showLSPage(player, page);
		}
		else if (cmd.equalsIgnoreCase("_bbslsySort"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String sorname = st.hasMoreTokens() ? st.nextToken() : "ID";
			_playerSortBy.put(player.getObjectId(), SortBy.getEnum(sorname));
			showLSPage(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbslsSearch"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String search = st.hasMoreTokens() ? st.nextToken() : "";
			_playerSearch.put(player.getObjectId(), search);
			showLSPage(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbslsyReset"))
		{
			_playerSearch.remove(player.getObjectId());
			showLSPage(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbslsShowItems"))
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/augmentation/item_select.htm", player);
			final StringBuilder sb = new StringBuilder();
			int count = 0;
			for (final ItemInstance item : player.getInventory().getItems())
			{
				final ItemTemplate template = item.getTemplate();
				if (checkItemValiable(item))
				{
					count++;
					String name = template.getName();
					if (name.length() > 25)
					{
						name = name.substring(0, name.length() - (name.length() - 25));
						name += "...";
					}
					sb.append("<tr>");
					sb.append("<td width=34 height=34><img src=\"icon." + template.getIcon() + "\" width=32 height=32></td>");
					sb.append("<td width=220>" + name + " " + template.getAdditionalName() + " " + (item.getEnchantLevel() == 0 ? "" : "+" + item.getEnchantLevel()) + "</td>");
					sb.append("<td width=32><button value=\"\" action=\"bypass _bbslsInsertItem " + item.getObjectId()
								+ "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin1\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
					sb.append("</tr>");
				}
			}
			if (count == 0)
			{
				sb.append("<tr>");
				sb.append("<td width=120 height=50 align=center><font name=hs12 color=FF0040>No items has been found!</font></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td width=120 height=10 align=center>- Your items are already augmented.</td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td width=120 height=10 align=center>- Your inventory contains non-augmentable items.</td>");
				sb.append("</tr>");
			}
			html = html.replace("%data%", sb.toString());
			html = html.replaceAll("%count%", "" + count);
			final NpcHtmlMessage npcHtml = new NpcHtmlMessage(0);
			npcHtml.setHtml(html);
			player.sendPacket(npcHtml);
		}
		else if (cmd.equalsIgnoreCase("_bbslsSetStat"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final String type = st.nextToken();
			int stat = 0;
			if (type.equalsIgnoreCase("RANDOM"))
			{
				stat = 0;
			}
			else if (type.equalsIgnoreCase("STR"))
			{
				stat = 1;
			}
			else if (type.equalsIgnoreCase("CON"))
			{
				stat = 2;
			}
			else if (type.equalsIgnoreCase("MEN"))
			{
				stat = 3;
			}
			else if (type.equalsIgnoreCase("INT"))
			{
				stat = 4;
			}
			player.setCommunityAugmentStat(stat);
			showLSPage(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbslsInsertItem"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final int weaponObjId = Integer.parseInt(st.nextToken());
			final ItemInstance iteminstance = player.getInventory().getItemByObjectId(weaponObjId);
			if (iteminstance == null || !checkItemValiable(iteminstance))
			{
				_log.warn("LifeStoneCommunity: invalid item id: Character objID: " + player.getObjectId());
				return;
			}
			player.setCommunityAugmentItem(iteminstance);
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/augmentation/selected_Item.htm", player);
			html = html.replaceAll("%itemname%", iteminstance.getName());
			html = html.replaceAll("%icon%", "" + iteminstance.getTemplate().getIcon());
			html = html.replaceAll("%enchantLevel%", "" + iteminstance.getEnchantLevel());
			final NpcHtmlMessage npcHtml = new NpcHtmlMessage(0);
			npcHtml.setHtml(html);
			player.sendPacket(npcHtml);
			showLSPage(player, 1);
		}
		else if (cmd.equalsIgnoreCase("_bbslsAugment"))
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			st.nextToken();
			final int id = Integer.parseInt(st.nextToken());
			if ((player == null) || !checkConditions(player))
			{
				return;
			}
			final AugmentationData aug = getAugById(id);
			if (aug == null)
			{
				_log.error("LifeStoneCommunity: _bbslsAugment invalid id: " + id);
				return;
			}
			final int augmentSkillid = aug.getSkillId();
			final ItemInstance iteminstance = player.getCommnityAugmentItem();
			if (iteminstance == null)
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Please select item that you wish to be augmented.");
				showLSPage(player, 1);
				return;
			}
			if (iteminstance.isNotAugmented())
			{
				return;
			}
			if (augmentSkillid == 0)
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Please select Augmentation Skill to continue...");
				showLSPage(player, 1);
				return;
			}
			if (!checkItemValiable(iteminstance))
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Item does not meet the conditions to be augmented.");
				showLSPage(player, 1);
				return;
			}
			if (!iteminstance.canBeAugmented())
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "This item cannot be augmented!");
				showLSPage(player, 1);
				return;
			}
			if (!player.getInventory().destroyItemByItemId(aug.getItemId(), aug.getCount(), "CB Augment"))
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation",
							"Not enought items... Augmentation cost: " + aug.getCount() + " " + ItemHolder.getInstance().getTemplateName(aug.getItemId()));
				showLSPage(player, 1);
				return;
			}
			if (player.getLevel() < Config.COMMUNITY_AUGMENTATION_MIN_LEVEL)
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Your level is too low. Min level that can use this service: " + Config.COMMUNITY_AUGMENTATION_MIN_LEVEL);
				showLSPage(player, 1);
				return;
			}
			if (player.isActionsDisabled())
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Action denied, please come back later.");
				showLSPage(player, 1);
				return;
			}
			if (player.isInStoreMode())
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Cannot augment item while in store mode.");
				showLSPage(player, 1);
				return;
			}
			if (player.isInTrade())
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Cannot augment this item while you are in trade mode.");
				showLSPage(player, 1);
				return;
			}
			if (player.isProcessingRequest())
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "Please try again later...");
				showLSPage(player, 1);
				return;
			}
			final ItemTemplate item = iteminstance.getTemplate();
			final int stat = player.getCommnityAugmentStat();
			if (item.getType2() == ItemTemplate.TYPE2_WEAPON && (aug.getType().equalsIgnoreCase("chance") || aug.getType().equalsIgnoreCase("passive") || aug.getType().equalsIgnoreCase("active")))
			{
				setAugmentation(iteminstance, player, aug.getAugId(), stat);
			}
			else if (item.getType1() == ItemTemplate.TYPE1_WEAPON_RING_EARRING_NECKLACE && aug.getType().equalsIgnoreCase("jewelry") && !item.isWeapon() && Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY)
			{
				setAugmentation(iteminstance, player, aug.getAugId(), 0);
			}
			else
			{
				player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "You cannot put jewelry skill on a weapon... gtfo!");
			}
		}
	}

	public AugmentationData getAugById(int id)
	{
		for (final AugmentationData aug : _augdata)
		{
			if (aug.getAugId() == id)
			{
				return aug;
			}
		}
		return null;
	}

	public void reload()
	{
		load();
		_log.info("CommunityLS: Reloading...");
	}

	private void load()
	{
		final File localFile = new File("config/CommunityPvP/life_stone_community.xml");
		if (!localFile.exists())
		{
			_log.error("CommunityLS : File life_stone_community.xml not found!");
			return;
		}
		Document localDocument = null;
		try
		{
			final DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			localDocumentBuilderFactory.setValidating(false);
			localDocumentBuilderFactory.setIgnoringComments(true);
			localDocument = localDocumentBuilderFactory.newDocumentBuilder().parse(localFile);
		}
		catch (final Exception e1)
		{
			e1.printStackTrace();
		}
		try
		{
			parseFile(localDocument);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private void parseFile(final Document doc)
	{
		_augdata.clear();
		try
		{
			for (Node il = doc.getFirstChild(); il != null; il = il.getNextSibling())
			{
				if ("list".equalsIgnoreCase(il.getNodeName()))
				{
					for (Node area = il.getFirstChild(); area != null; area = area.getNextSibling())
					{
						if ("aug".equalsIgnoreCase(area.getNodeName()))
						{
							final int id = XMLUtil.getAttributeIntValue(area, "id", 0);
							final int statsId = XMLUtil.getAttributeIntValue(area, "statsId", 0);
							final int skillId = XMLUtil.getAttributeIntValue(area, "skillId", 0);
							final int skillLvl = XMLUtil.getAttributeIntValue(area, "skillLvl", 0);
							final String type = XMLUtil.getAttributeValue(area, "type");
							final int itemId = XMLUtil.getAttributeIntValue(area, "itemId", 0);
							final int itemCount = XMLUtil.getAttributeIntValue(area, "itemCount", 0);
							final String desc = XMLUtil.getAttributeValue(area, "desc");
							if (!Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY && type.equalsIgnoreCase("jewelry"))
							{
								continue;
							}
							final AugmentationData data = new AugmentationData();
							data.setAugId(id);
							data.setStats(statsId);
							data.setSkill(skillId);
							data.setLevel(skillLvl);
							data.setType(type);
							data.setItem(itemId);
							data.setItemCount(itemCount);
							data.setDescription(desc);
							_augdata.add(data);
							if (SkillTable.getInstance().getBaseLevel(skillId) < skillLvl)
							{
								_log.warn("CommunityLS: skillId: " + skillId + " maxlevel = " + SkillTable.getInstance().getBaseLevel(skillId) + " current = " + skillLvl);
							}
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean checkItemValiable(ItemInstance im)
	{
		final ItemTemplate item = im.getTemplate();
		boolean ok = false;
		if ((item.getType1() == ItemTemplate.TYPE1_WEAPON_RING_EARRING_NECKLACE && Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY || item.getType2() == ItemTemplate.TYPE2_WEAPON) && !im.isAugmented())
		{
			final Grade grade = item.getItemGrade();
			if (grade != Grade.NONE && grade != Grade.D)
			{
				final int id = item.getItemId();
				// boss jewels.
				if (id != 6659 && id != 6656 && id != 6657 && id != 8191 && id != 6658 && id != 6662 && id != 6661 && id != 6660)
				{
					ok = true;
				}
			}
		}
		return ok;
	}

	private void setAugmentation(ItemInstance iteminstance, Player player, int augId, int stat)
	{
		if (iteminstance == null || player == null)
		{
			return;
		}
		final AugmentationData aug = getAugById(augId);
		if (aug == null)
		{
			_log.error("LifeStoneCommunity: setAugmentation invalid id: " + augId);
			return;
		}
		final int augskillId = aug.getStats();
		final int offset = 9 * 91 + Rnd.get(0, 1) * 3640 + (3 + 3) / 2 * 10 * 91 + 1;
		int stat12 = Rnd.get(offset, offset + 91 - 1);
		switch (stat)
		{
		case 1:
			stat12 = 16341;
			break;
		case 2:
			stat12 = 16342;
			break;
		case 3:
			stat12 = 16344;
			break;
		case 4:
			stat12 = 16343;
			break;
		}
		boolean equipped = false;
		if (equipped = iteminstance.isEquipped())
		{
			player.getInventory().unEquipItem(iteminstance);
		}
		iteminstance.setAugmentationId((augskillId << 16) + stat12);
		iteminstance.setJdbcState(JdbcEntityState.UPDATED);
		iteminstance.update();
		if (equipped)
		{
			player.getInventory().equipItem(iteminstance);
		}
		player.sendPacket(new InventoryUpdate().addModifiedItem(iteminstance));
		for (final ShortCut sc : player.getAllShortCuts())
		{
			if (sc.getId() == iteminstance.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM)
			{
				player.sendPacket(new ShortCutRegister(player, sc));
			}
		}
		player.sendChanges();
		player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", "All Done. You have purchased " + SkillTable.getInstance().getInfo(aug.getSkillId(), aug.getLevel()).getName());
		Log.add(player.getName() + " has augmented " + iteminstance.getName() + "[" + iteminstance.getObjectId() + "] . Skill: " + SkillTable.getInstance().getInfo(aug.getSkillId(), aug.getLevel()).getName()
					+ "[" + aug.getSkillId() + "]", "CommunityAugmentation");
		Log.add(player.getName() + " has augmented " + iteminstance.getName() + "[" + iteminstance.getObjectId() + "] . Augmentation cost: " + aug.getItemId() + " (" + aug.getCount() + ") Stat: "
					+ (stat == 0 ? "Random" : stat), "CommunityAugmentation");
		player.setCommunityAugmentItem(null);
		player.setCommunityAugmentStat(0);
		showLSPage(player, 1);
	}

	private void showLSPage(Player player, int page)
	{
		String htmltosend = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/augmentation/ls.htm", player);
		final List<AugmentationData> _augmentationData = getFilteredAugData(_playerSearch.get(player.getObjectId()));
		String searchfor = _playerSearch.get(player.getObjectId());
		if (searchfor == null)
		{
			searchfor = "";
		}
		final StringBuilder sb = new StringBuilder();
		SortBy sortBy = _playerSortBy.get(player.getObjectId());
		if (sortBy == null)
		{
			sortBy = SortBy.ALL;
		}
		final String nameOfCurSortBy = sortBy.toString() + ";";
		sb.append(nameOfCurSortBy);
		for (final SortBy s : SortBy.values())
		{
			final String str = s + ";";
			if (str.equalsIgnoreCase("jewelry;") && !Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY)
			{
				continue;
			}
			if (!str.toString().equalsIgnoreCase(nameOfCurSortBy))
			{
				sb.append(str);
			}
		}
		htmltosend = htmltosend.replaceAll("%sortbylist%", sb.toString());
		int all = 0;
		int clansvisual = 0;
		final boolean pagereached = false;
		final int totalpages = getSorttedAugmentation(_playerSortBy.get(player.getObjectId()), _augmentationData).size() / 10 + 1;
		if (page == 1)
		{
			if (totalpages == 1)
			{
				htmltosend = htmltosend.replaceAll("%more%", "&nbsp;");
			}
			else
			{
				htmltosend = htmltosend.replaceAll("%more%",
							"<button value=\"\" action=\"bypass _bbsls " + (page + 1) + " \" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
			}
			htmltosend = htmltosend.replaceAll("%back%", "&nbsp;");
		}
		else if (page > 1)
		{
			if (totalpages <= page)
			{
				htmltosend = htmltosend.replaceAll("%back%",
							"<button value=\"\" action=\"bypass _bbsls " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
				htmltosend = htmltosend.replaceAll("%more%", "&nbsp;");
			}
			else
			{
				htmltosend = htmltosend.replaceAll("%more%",
							"<button value=\"\" action=\"bypass _bbsls " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");
				htmltosend = htmltosend.replaceAll("%back%",
							"<button value=\"\" action=\"bypass _bbsls " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
			}
		}
		for (final AugmentationData aug : getSorttedAugmentation(_playerSortBy.get(player.getObjectId()), _augmentationData))
		{
			all++;
			if ((page == 1 && clansvisual > 10) || (!pagereached && all > page * 10))
			{
				continue;
			}
			if (!pagereached && all <= (page - 1) * 10)
			{
				continue;
			}
			clansvisual++;
			if (aug == null)
			{
				continue;
			}
			final Skill skill = SkillTable.getInstance().getInfo(aug.getSkillId(), aug.getLevel());
			String name = skill.getName();
			name = name.replaceAll("Item Skill: ", "");
			name = name.replaceAll("Augment Option - ", "");
			if (name.length() > 25)
			{
				name = name.substring(0, name.length() - (name.length() - 25));
				name += "...";
			}
			String desc = aug.getDesc();
			if (desc.length() > 95)
			{
				desc = desc.substring(0, desc.length() - (desc.length() - 95));
				desc += "...";
			}
			final String type = aug.getType();
			String typeColor = "FFFFFF";
			switch (type)
			{
			case "active":
				typeColor = "45C841";
				break;
			case "passive":
				typeColor = "D8EA37";
				break;
			case "chance":
				typeColor = "FC5050";
				break;
			case "jewelry":
				typeColor = "EE45D1";
				break;
			}
			htmltosend = htmltosend.replaceAll("%icon" + clansvisual + "%", "icon." + skill.getIcon());
			htmltosend = htmltosend.replaceAll("%skillName" + clansvisual + "%", "<font name=hs9 color=01A9DB>" + name + "</font>");
			htmltosend = htmltosend.replaceAll("%skillLevel" + clansvisual + "%", "(" + aug.getLevel() + ")");
			htmltosend = htmltosend.replaceAll("%type" + clansvisual + "%", "<font color=\"" + typeColor + "\">" + aug.getType().toUpperCase() + "</font>");
			htmltosend = htmltosend.replaceAll("%desc" + clansvisual + "%", "<font color=\"ad9d46\">[" + desc + "]</font>");
			htmltosend = htmltosend.replaceAll("%cost" + clansvisual + "%", "Cost: <font color=\"848484\">" + aug.getCount() + " " + ItemHolder.getInstance().getTemplateName(aug.getItemId()) + "</font>");
			htmltosend = htmltosend.replaceAll("%button" + clansvisual + "%",
						"<button value=\"\" action=\"bypass _bbslsAugment " + aug.getAugId() + "\" width=32 height=32 back=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\" fore=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\">");
			htmltosend = htmltosend.replaceAll("%width" + clansvisual + "%", "180");
		}
		if (clansvisual < 10)
		{
			for (int d = clansvisual + 1; d != 11; d++)
			{
				htmltosend = htmltosend.replaceAll("%icon" + d + "%", "L2UI_CT1.Inventory_DF_CloakSlot_Disable");
				htmltosend = htmltosend.replaceAll("%skillName" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%skillLevel" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%type" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%desc" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%cost" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%button" + d + "%", "&nbsp;");
				htmltosend = htmltosend.replaceAll("%width" + d + "%", "395");
			}
		}
		String icon = "<img src=L2UI_CH3.refineslot_item width=34 height=34 align=top />";
		if (player.getCommnityAugmentItem() != null)
		{
			final ItemTemplate tmp = ItemHolder.getInstance().getTemplate(player.getCommnityAugmentItem().getItemId());
			if (tmp != null)
			{
				icon = tmp.getIcon() == "" ? "<img src=icon.etc_question_mark_i00 width=32 height=32 align=top />" : "<img src=" + tmp.getIcon() + " width=32 height=32 align=top />";
			}
		}
		String stat = "";
		switch (player.getCommnityAugmentStat())
		{
		case 0:
			stat = "<font name=hs12 color=6E6E6E>Random Stats</font>";
			break;
		case 1:
			stat = "<font name=hs12 color=973BB3>+1 STR</font>";
			break;
		case 2:
			stat = "<font name=hs12 color=DF013A>+1 CON</font>";
			break;
		case 3:
			stat = "<font name=hs12 color=04B4AE>+1 MEN</font>";
			break;
		case 4:
			stat = "<font name=hs12 color=2E64FE>+1 INT</font>";
			break;
		}
		htmltosend = htmltosend.replaceAll("%selectedStat%", stat);
		htmltosend = htmltosend.replaceAll("%selectedWeapon%", icon);
		htmltosend = htmltosend.replaceAll("%searchfor%", searchfor == "" ? "&nbsp;" : "<font color=5b574c>Results:</font> <font color=LEVEL>" + searchfor + "</font>");
		htmltosend = htmltosend.replaceAll("%totalresults%", "" + all);
		ShowBoard.separateAndSend(htmltosend, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	public class AugmentationData
	{
		private int _augId = 0;
		private int _statsId = 0;
		private int _skillId = 0;
		private int _skillLevel = 0;
		private String _type = "";
		private int _itemId = 0;
		private int _itemCount = 0;
		private String _desc = "";

		public int getAugId()
		{
			return _augId;
		}

		public void setAugId(int id)
		{
			_augId = id;
		}

		public int getStats()
		{
			return _statsId;
		}

		public void setStats(int id)
		{
			_statsId = id;
		}

		public int getSkillId()
		{
			return _skillId;
		}

		public void setSkill(int id)
		{
			_skillId = id;
		}

		public int getLevel()
		{
			return _skillLevel;
		}

		public void setLevel(int level)
		{
			_skillLevel = level;
		}

		public String getType()
		{
			return _type;
		}

		public void setType(String type)
		{
			_type = type;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public void setItem(int id)
		{
			_itemId = id;
		}

		public int getCount()
		{
			return _itemCount;
		}

		public void setItemCount(int count)
		{
			_itemCount = count;
		}

		public String getDesc()
		{
			return _desc;
		}

		public void setDescription(String dsc)
		{
			_desc = dsc;
		}
	}

	private enum SortBy
	{
		ALL("ALL"), ACTIVE("ACTIVE"), PASSIVE("PASSIVE"), CHANCE("CHANCE"), JEWELRY("JEWELRY");

		private final String _sortName;

		private SortBy(String sortName)
		{
			_sortName = sortName;
		}

		@Override
		public String toString()
		{
			return _sortName;
		}

		public static SortBy getEnum(String sortName)
		{
			for (final SortBy sb : values())
			{
				if (sb.toString().equals(sortName))
				{
					return sb;
				}
			}
			return ALL;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<AugmentationData> getSorttedAugmentation(SortBy sort, List<AugmentationData> aug)
	{
		if (sort == null)
		{
			sort = SortBy.ALL;
		}
		final List<AugmentationData> sorted = new ArrayList<AugmentationData>();
		switch (sort)
		{
		default:
		case ALL:
			final Map<AugmentationData, Integer> id = new FastMap<>();
			for (final AugmentationData d : aug)
			{
				id.put(d, d.getAugId());
			}
			sorted.addAll(ValueSortMap.sortMapByValue(id, true).keySet());
			return sorted;
		case ACTIVE:
			final Map<AugmentationData, String> tmp = new FastMap<>();
			for (final AugmentationData d : aug)
			{
				if (d.getType().equalsIgnoreCase("active"))
				{
					tmp.put(d, d.getType());
				}
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp, true).keySet());
			return sorted;
		case PASSIVE:
			final Map<AugmentationData, String> tmp2 = new FastMap<>();
			for (final AugmentationData d : aug)
			{
				if (d.getType().equalsIgnoreCase("passive"))
				{
					tmp2.put(d, d.getType());
				}
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp2, true).keySet());
			return sorted;
		case CHANCE:
			final Map<AugmentationData, String> tmp3 = new FastMap<>();
			for (final AugmentationData d : aug)
			{
				if (d.getType().equalsIgnoreCase("chance"))
				{
					tmp3.put(d, d.getType());
				}
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp3, true).keySet());
			return sorted;
		case JEWELRY:
			Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY = true;
			final Map<AugmentationData, String> tmp4 = new FastMap<>();
			for (final AugmentationData d : aug)
			{
				if (d.getType().equalsIgnoreCase("jewelry"))
				{
					tmp4.put(d, d.getType());
				}
			}
			sorted.addAll(ValueSortMap.sortMapByValue(tmp4, true).keySet());
			return sorted;
		}
	}

	private static List<AugmentationData> getFilteredAugData(String filter)
	{
		if (filter == null)
		{
			filter = "";
		}
		final List<AugmentationData> filteredList = new ArrayList<AugmentationData>();
		for (final AugmentationData data : _augdata)
		{
			if ((data == null) || (!Config.COMMUNITY_AUGMENTATION_ALLOW_JEWELRY && data.getType().equalsIgnoreCase("jewelry")))
			{
				continue;
			}
			final Skill skill = SkillTable.getInstance().getInfo(data.getSkillId(), data.getLevel());
			if (skill == null)
			{
				continue;
			}
			if (skill.getName().toLowerCase().contains(filter.toLowerCase()) || data.getDesc().toLowerCase().contains(filter))
			{
				filteredList.add(data);
			}
		}
		return filteredList;
	}

	private boolean checkConditions(Player player)
	{
		if (player == null || player.isDead())
		{
			return false;
		}
		if (player.isCursedWeaponEquipped() || player.isInJail() || player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow()
					|| player.isInOlympiadMode() || player.isFlying() || player.isTerritoryFlagEquipped())
		{
			player.sendChatMessage(0, ChatType.BATTLEFIELD.ordinal(), "Augmentation", player.isLangRus() ? "Невозможно использовать в данный момент!" : "You can not use it at this moment!");
			return false;
		}
		if (player.getReflectionId() != 0 || player.isInZone(ZoneType.epic))
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Augmentation", player.isLangRus() ? "Невозможно использовать в данных зонах!" : "Can not be used in these areas!");
			return false;
		}
		if (player.isInZone(ZoneType.SIEGE))
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Augmentation", player.isLangRus() ? "Невозможно использовать во время осад!" : "Can not be used during the siege!");
			return false;
		}
		if (player.isTerritoryFlagEquipped())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return false;
		}
		return true;
	}

	public static final CommunityAugment getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final CommunityAugment _instance = new CommunityAugment();
	}
}

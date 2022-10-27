package l2mv.gameserver.handler.voicecommands.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.DressArmorHolder;
import l2mv.gameserver.data.xml.holder.DressCloakHolder;
import l2mv.gameserver.data.xml.holder.DressShieldHolder;
import l2mv.gameserver.data.xml.holder.DressWeaponHolder;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.DressArmorData;
import l2mv.gameserver.model.DressCloakData;
import l2mv.gameserver.model.DressShieldData;
import l2mv.gameserver.model.DressWeaponData;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.clientpackets.RequestPreviewItem.RemoveWearItemsTask;
import l2mv.gameserver.network.serverpackets.CharInfo;
import l2mv.gameserver.network.serverpackets.ShopPreviewInfo;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemType;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Util;

public class DressMe implements IVoicedCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(DressMe.class);

	private static Map<Integer, DressWeaponData> SWORD = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> BLUNT = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> DAGGER = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> BOW = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> POLE = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> FIST = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> DUAL = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> DUALFIST = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> BIGSWORD = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> ROD = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> BIGBLUNT = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> CROSSBOW = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> RAPIER = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> ANCIENTSWORD = new HashMap<Integer, DressWeaponData>();
	private static Map<Integer, DressWeaponData> DUALDAGGER = new HashMap<Integer, DressWeaponData>();

	private boolean _hasBeenInitialized = false;

	private final String[] _commandList = new String[]
	{
		"dressme",
		"dressme-armor",
		"dressme-cloak",
		"dressme-shield",
		"dressme-weapon",
		"dress-armor",
		"dress-cloak",
		"dress-shield",
		"dress-weapon",
		"dress-armorpage",
		"dress-cloakpage",
		"dress-shieldpage",
		"dress-weaponpage",
		"dress-tryarmor",
		"dress-trycloak",
		"dress-tryshield",
		"dress-tryweapon",
		"dressinfo",
		"undressme",
		"undressme-armor",
		"undressme-cloak",
		"undressme-shield",
		"undressme-weapon",
		"showdress",
		"hidedress"
	};

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (!Config.COMMAND_DRESSME_ENABLE)
		{
			return false;
		}

		if (!_hasBeenInitialized)
		{
			parseWeapon();
		}

		if (command.equals("dressme"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/index.htm", player);
			html = html.replace("<?show_hide?>", player.getVarObject("showVisualChange") == null ? "Show visual equip on other player!" : "Hide visual equip on other player!");
			html = html.replace("<?show_hide_b?>", player.getVarObject("showVisualChange") == null ? "showdress" : "hidedress");

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dressme-armor"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/index-armor.htm", player);
			String template = HtmCache.getInstance().getNotNull("command/dressme/template-armor.htm", player);
			String block = "";
			String list = "";

			if (args == null)
			{
				args = "1";
			}

			String[] param = args.split(" ");

			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;

			for (int i = (page - 1) * perpage; i < DressArmorHolder.getInstance().size(); i++)
			{
				DressArmorData dress = DressArmorHolder.getInstance().getArmor(i + 1);
				if (dress != null)
				{
					block = template;

					String dress_name = dress.getName();

					if (dress_name.length() > 29)
					{
						dress_name = dress_name.substring(0, 29) + "...";
					}

					block = block.replace("{bypass}", "bypass -h user_dress-armorpage " + (i + 1));
					block = block.replace("{name}", dress_name);
					block = block.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(dress.getChest()));
					list += block;
				}

				counter++;

				if (counter >= perpage)
				{
					break;
				}
			}

			double count = Math.ceil((double) DressArmorHolder.getInstance().size() / (double) perpage);
			int inline = 1;
			String navigation = "";

			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}

				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}

			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}

			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dressme-cloak"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/index-cloak.htm", player);
			String template = HtmCache.getInstance().getNotNull("command/dressme/template-cloak.htm", player);
			String block = "";
			String list = "";

			if (args == null)
			{
				args = "1";
			}

			String[] param = args.split(" ");

			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;

			for (int i = (page - 1) * perpage; i < DressCloakHolder.getInstance().size(); i++)
			{
				DressCloakData cloak = DressCloakHolder.getInstance().getCloak(i + 1);
				if (cloak != null)
				{
					block = template;

					String cloak_name = cloak.getName();

					if (cloak_name.length() > 29)
					{
						cloak_name = cloak_name.substring(0, 29) + "...";
					}

					block = block.replace("{bypass}", "bypass -h user_dress-cloakpage " + (i + 1));
					block = block.replace("{name}", cloak_name);
					block = block.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(cloak.getCloakId()));
					list += block;
				}

				counter++;

				if (counter >= perpage)
				{
					break;
				}
			}

			double count = Math.ceil((double) DressCloakHolder.getInstance().size() / (double) perpage);
			int inline = 1;
			String navigation = "";

			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}

				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}

			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}

			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dressme-shield"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/index-shield.htm", player);
			String template = HtmCache.getInstance().getNotNull("command/dressme/template-shield.htm", player);
			String block = "";
			String list = "";

			if (args == null)
			{
				args = "1";
			}

			String[] param = args.split(" ");

			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;

			for (int i = (page - 1) * perpage; i < DressShieldHolder.getInstance().size(); i++)
			{
				DressShieldData shield = DressShieldHolder.getInstance().getShield(i + 1);
				if (shield != null)
				{
					block = template;

					String shield_name = shield.getName();

					if (shield_name.length() > 29)
					{
						shield_name = shield_name.substring(0, 29) + "...";
					}

					block = block.replace("{bypass}", "bypass -h user_dress-shieldpage " + (i + 1));
					block = block.replace("{name}", shield_name);
					block = block.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(shield.getShieldId()));
					list += block;
				}

				counter++;

				if (counter >= perpage)
				{
					break;
				}
			}

			double count = Math.ceil((double) DressShieldHolder.getInstance().size() / (double) perpage);
			int inline = 1;
			String navigation = "";

			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}

				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}

			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}

			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dressme-weapon"))
		{
			ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if (slot == null)
			{
				player.sendMessage("Error: Weapon must be equiped!");
				return false;
			}

			ItemType type = slot.getItemType();

			String html = HtmCache.getInstance().getNotNull("command/dressme/index-weapon.htm", player);
			String template = HtmCache.getInstance().getNotNull("command/dressme/template-weapon.htm", player);
			String block = "";
			String list = "";

			if (args == null)
			{
				args = "1";
			}

			String[] param = args.split(" ");

			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			Map<Integer, DressWeaponData> map = new HashMap<Integer, DressWeaponData>();

			map = initMap(type.toString(), map);

			if (map == null)
			{
				_log.error("Dress me system: Weapon Map is null.");
				return false;
			}

			for (int i = (page - 1) * perpage; i < map.size(); i++)
			{
				DressWeaponData weapon = map.get(i + 1);
				if (weapon != null)
				{
					block = template;

					String cloak_name = weapon.getName();

					if (cloak_name.length() > 29)
					{
						cloak_name = cloak_name.substring(0, 29) + "...";
					}

					block = block.replace("{bypass}", "bypass -h user_dress-weaponpage " + weapon.getId());
					block = block.replace("{name}", cloak_name);
					block = block.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(weapon.getId()));
					list += block;
				}

				counter++;

				if (counter >= perpage)
				{
					break;
				}
			}

			double count = Math.ceil((double) map.size() / (double) perpage);
			int inline = 1;
			String navigation = "";

			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h user_dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h user_dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}

				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}

			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}

			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dress-armorpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressArmorData dress = DressArmorHolder.getInstance().getArmor(set);
			if (dress != null)
			{
				String html = HtmCache.getInstance().getNotNull("command/dressme/dress-armor.htm", player);

				Inventory inv = player.getInventory();

				ItemInstance my_chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
				html = html.replace("{my_chest_icon}", my_chest == null ? "icon.NOIMAGE" : my_chest.getTemplate().getIcon());
				ItemInstance my_legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
				html = html.replace("{my_legs_icon}", my_legs == null ? "icon.NOIMAGE" : my_legs.getTemplate().getIcon());
				ItemInstance my_gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
				html = html.replace("{my_gloves_icon}", my_gloves == null ? "icon.NOIMAGE" : my_gloves.getTemplate().getIcon());
				ItemInstance my_feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
				html = html.replace("{my_feet_icon}", my_feet == null ? "icon.NOIMAGE" : my_feet.getTemplate().getIcon());

				html = html.replace("{bypassBuy}", "bypass -h user_dress-armor " + set);
				html = html.replace("{bypassTry}", "bypass -h user_dress-tryarmor " + set);
				html = html.replace("{name}", dress.getName());
				html = html.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));

				ItemTemplate chest = ItemHolder.getInstance().getTemplate(dress.getChest());
				html = html.replace("{chest_icon}", chest.getIcon());
				html = html.replace("{chest_name}", chest.getName());
				html = html.replace("{chest_grade}", chest.getItemGrade().name());

				if (dress.getLegs() != -1)
				{
					ItemTemplate legs = ItemHolder.getInstance().getTemplate(dress.getLegs());
					html = html.replace("{legs_icon}", legs.getIcon());
					html = html.replace("{legs_name}", legs.getName());
					html = html.replace("{legs_grade}", legs.getItemGrade().name());
				}
				else
				{
					html = html.replace("{legs_icon}", "icon.NOIMAGE");
					html = html.replace("{legs_name}", "<font color=FF0000>...</font>");
					html = html.replace("{legs_grade}", "NO");
				}
				if (dress.getGloves() != -1)
				{
					ItemTemplate gloves = ItemHolder.getInstance().getTemplate(dress.getGloves());
					html = html.replace("{gloves_icon}", gloves.getIcon());
					html = html.replace("{gloves_name}", gloves.getName());
					html = html.replace("{gloves_grade}", gloves.getItemGrade().name());
				}
				else
				{
					html = html.replace("{gloves_icon}", "icon.NOIMAGE");
					html = html.replace("{gloves_name}", "<font color=FF0000>...</font>");
					html = html.replace("{gloves_grade}", "NO");
				}

				if (dress.getFeet() != -1)
				{
					ItemTemplate feet = ItemHolder.getInstance().getTemplate(dress.getFeet());
					html = html.replace("{feet_icon}", feet.getIcon());
					html = html.replace("{feet_name}", feet.getName());
					html = html.replace("{feet_grade}", feet.getItemGrade().name());
				}
				else
				{
					html = html.replace("{feet_icon}", "icon.NOIMAGE");
					html = html.replace("{feet_name}", "<font color=FF0000>...</font>");
					html = html.replace("{feet_grade}", "NO");
				}
//				ItemTemplate gloves = ItemHolder.getInstance().getTemplate(dress.getGloves());
//				html = html.replace("{gloves_icon}", gloves.getIcon());
//				html = html.replace("{gloves_name}", gloves.getName());
//				html = html.replace("{gloves_grade}", gloves.getItemGrade().name());

//				ItemTemplate feet = ItemHolder.getInstance().getTemplate(dress.getFeet());
//				html = html.replace("{feet_icon}", feet.getIcon());
//				html = html.replace("{feet_name}", feet.getName());
//				html = html.replace("{feet_grade}", feet.getItemGrade().name());

				Functions.show(html, player, null);
				return true;
			}
			else
			{
				return false;
			}

		}
		else if (command.equals("dress-cloakpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressCloakData cloak = DressCloakHolder.getInstance().getCloak(set);
			if (cloak != null)
			{
				String html = HtmCache.getInstance().getNotNull("command/dressme/dress-cloak.htm", player);

				Inventory inv = player.getInventory();

				ItemInstance my_cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);
				html = html.replace("{my_cloak_icon}", my_cloak == null ? "icon.NOIMAGE" : my_cloak.getTemplate().getIcon());

				html = html.replace("{bypassBuy}", "bypass -h user_dress-cloak " + cloak.getId());
				html = html.replace("{bypassTry}", "bypass -h user_dress-trycloak " + cloak.getId());
				html = html.replace("{name}", cloak.getName());
				html = html.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));

				ItemTemplate item = ItemHolder.getInstance().getTemplate(cloak.getCloakId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());

				Functions.show(html, player, null);
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (command.equals("dress-shieldpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressShieldData shield = DressShieldHolder.getInstance().getShield(set);
			if (shield != null)
			{
				String html = HtmCache.getInstance().getNotNull("command/dressme/dress-shield.htm", player);

				Inventory inv = player.getInventory();

				ItemInstance my_shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
				html = html.replace("{my_shield_icon}", my_shield == null ? "icon.NOIMAGE" : my_shield.getTemplate().getIcon());

				html = html.replace("{bypassBuy}", "bypass -h user_dress-shield " + shield.getId());
				html = html.replace("{bypassTry}", "bypass -h user_dress-tryshield " + shield.getId());
				html = html.replace("{name}", shield.getName());
				html = html.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));

				ItemTemplate item = ItemHolder.getInstance().getTemplate(shield.getShieldId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());

				Functions.show(html, player, null);
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (command.equals("dress-weaponpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressWeaponData weapon = DressWeaponHolder.getInstance().getWeapon(set);
			if (weapon != null)
			{
				String html = HtmCache.getInstance().getNotNull("command/dressme/dress-weapon.htm", player);

				Inventory inv = player.getInventory();

				ItemInstance my_weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

				html = html.replace("{my_weapon_icon}", my_weapon == null ? "icon.NOIMAGE" : my_weapon.getTemplate().getIcon());

				html = html.replace("{bypassBuy}", "bypass -h user_dress-weapon " + weapon.getId());
				html = html.replace("{bypassTry}", "bypass -h user_dress-tryweapon " + weapon.getId());
				html = html.replace("{name}", weapon.getName());
				html = html.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));

				ItemTemplate item = ItemHolder.getInstance().getTemplate(weapon.getId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());

				Functions.show(html, player, null);
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (command.equals("dressinfo"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/info.htm", player);
			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("dress-armor"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressArmorData dress = DressArmorHolder.getInstance().getArmor(set);
			Inventory inv = player.getInventory();

			ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);

			if (chest == null)
			{
				player.sendMessage("Error: Chest must be equiped.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}

			/*
			 * ItemTemplate visual = ItemHolder.getInstance().getTemplate(dress.getChest());
			 * if (chest.getTemplate().getBodyPart() != visual.getBodyPart())
			 * {
			 * player.sendMessage("Error: You can't change visual chest to full body and on the contrary!");
			 * useVoicedCommand("dress-armorpage", player, args);
			 * return false;
			 * }
			 */

			ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);

			if (legs == null && chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR)
			{
				player.sendMessage("Error: Legs must be equiped.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}

			ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);

			if (gloves == null)
			{
				player.sendMessage("Error: Gloves must be equiped.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}

			ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

			if (feet == null)
			{
				player.sendMessage("Error: Feet must be equiped.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}

			if (player.getInventory().getCountOf(dress.getPriceId()) >= dress.getPriceCount())
			{
				ItemFunctions.removeItem(player, dress.getPriceId(), dress.getPriceCount(), true, "VisualChange");

				visuality(player, chest, dress.getChest());
				if (legs != null)
				{
					visuality(player, legs, dress.getLegs());
				}
				visuality(player, gloves, dress.getGloves());
				visuality(player, feet, dress.getFeet());

				player.getInventory().unEquipItem(chest);
				player.getInventory().equipItem(chest);

				player.broadcastUserInfo(true);
				return true;
			}
			else
			{
				player.sendMessage("Error: You don't have items to pay.");
				return false;
			}
		}
		else if (command.equals("dress-tryarmor"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressArmorData dress = DressArmorHolder.getInstance().getArmor(set);
			if (dress == null)
			{
				return false;
			}

			final Map<Integer, Integer> itemList = new HashMap<>();
			itemList.put(Inventory.PAPERDOLL_CHEST, dress.getChest());
			itemList.put(Inventory.PAPERDOLL_LEGS, (dress.getLegs() > 0 ? dress.getLegs() : dress.getChest()));
			itemList.put(Inventory.PAPERDOLL_GLOVES, dress.getGloves());
			itemList.put(Inventory.PAPERDOLL_FEET, dress.getFeet());
			player.sendPacket(new ShopPreviewInfo(itemList));

			// Remove the try items in 6 seconds
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(player), 6 * 1000);

			useVoicedCommand("dress-armorpage", player, args);
			return false;
		}
		else if (command.equals("dress-cloak"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressCloakData cloak_data = DressCloakHolder.getInstance().getCloak(set);
			Inventory inv = player.getInventory();

			ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);

			if (cloak == null)
			{
				player.sendMessage("Error: Cloak must be equiped.");
				useVoicedCommand("dress-cloakpage", player, args);
				return false;
			}

			if (player.getInventory().getCountOf(cloak_data.getPriceId()) >= cloak_data.getPriceCount())
			{
				player.getInventory().destroyItemByItemId(cloak_data.getPriceId(), cloak_data.getPriceCount(), "VisualChange");
				visuality(player, cloak, cloak_data.getCloakId());

				player.sendUserInfo(true);
				player.broadcastUserInfo(true);
				return true;
			}
			else
			{
				player.sendMessage("Error: You don't have items to pay.");
				return false;
			}
		}
		else if (command.equals("dress-trycloak"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressCloakData cloak_data = DressCloakHolder.getInstance().getCloak(set);
			if (cloak_data == null)
			{
				return false;
			}

			final Map<Integer, Integer> itemList = new HashMap<>();
			itemList.put(Inventory.PAPERDOLL_BACK, cloak_data.getCloakId());
			player.sendPacket(new ShopPreviewInfo(itemList));

			// Remove the try items in 6 seconds
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(player), 6 * 1000);

			useVoicedCommand("dress-cloakpage", player, args);
			return false;
		}
		else if (command.equals("dress-shield"))
		{
			final int shield_id = Integer.parseInt(args.split(" ")[0]);

			DressShieldData shield_data = DressShieldHolder.getInstance().getShield(shield_id);
			Inventory inv = player.getInventory();

			ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);

			if (shield == null)
			{
				player.sendMessage("Error: Shield must be equiped.");
				useVoicedCommand("dress-shieldpage", player, args);
				return false;
			}

			if (player.getInventory().getCountOf(shield_data.getPriceId()) >= shield_data.getPriceCount())
			{
				player.getInventory().destroyItemByItemId(shield_data.getPriceId(), shield_data.getPriceCount(), "VisualChange");
				visuality(player, shield, shield_data.getShieldId());

				player.sendUserInfo(true);
				player.broadcastUserInfo(true);
				return true;
			}
			else
			{
				player.sendMessage("Error: You don't have items to pay.");
				return false;
			}
		}
		else if (command.equals("dress-tryshield"))
		{
			final int shield_id = Integer.parseInt(args.split(" ")[0]);

			DressShieldData shield_data = DressShieldHolder.getInstance().getShield(shield_id);
			if (shield_data == null)
			{
				return false;
			}

			final Map<Integer, Integer> itemList = new HashMap<>();
			itemList.put(Inventory.PAPERDOLL_LHAND, shield_data.getShieldId());
			player.sendPacket(new ShopPreviewInfo(itemList));

			// Remove the try items in 6 seconds
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(player), 6 * 1000);

			useVoicedCommand("dress-shieldpage", player, args);
			return false;
		}
		else if (command.equals("dress-weapon"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressWeaponData weapon_data = DressWeaponHolder.getInstance().getWeapon(set);
			Inventory inv = player.getInventory();

			ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			if (weapon == null)
			{
				player.sendMessage("Error: Weapon must be equiped.");
				useVoicedCommand("dress-weaponpage", player, args);
				return false;
			}

			if (!weapon.getItemType().toString().equals(weapon_data.getType()))
			{
				player.sendMessage("Error: Weapon must be equals type.");
				useVoicedCommand("dressme-weapon", player, null);
				return false;
			}

			if (player.getInventory().getCountOf(weapon_data.getPriceId()) >= weapon_data.getPriceCount())
			{
				player.getInventory().destroyItemByItemId(weapon_data.getPriceId(), weapon_data.getPriceCount(), "VisualChange");
				visuality(player, weapon, weapon_data.getId());

				player.sendUserInfo(true);
				player.broadcastUserInfo(true);
				return true;
			}
			else
			{
				player.sendMessage("Error: You don't have items to pay.");
				return false;
			}
		}
		else if (command.equals("dress-tryweapon"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);

			DressWeaponData weapon_data = DressWeaponHolder.getInstance().getWeapon(set);
			if (weapon_data == null)
			{
				return false;
			}

			final Map<Integer, Integer> itemList = new HashMap<>();
			itemList.put(Inventory.PAPERDOLL_RHAND, weapon_data.getId());
			player.sendPacket(new ShopPreviewInfo(itemList));

			// Remove the try items in 6 seconds
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(player), 6 * 1000);

			useVoicedCommand("dress-weaponpage", player, args);
			return false;
		}
		else if (command.equals("undressme"))
		{
			String html = HtmCache.getInstance().getNotNull("command/dressme/undressme.htm", player);
			html = html.replace("<?show_hide?>", player.getVarObject("showVisualChange") == null ? "Show visual equip on other player!" : "Hide visual equip on other player!");
			html = html.replace("<?show_hide_b?>", player.getVarObject("showVisualChange") == null ? "showdress" : "hidedress");

			Functions.show(html, player, null);
			return true;
		}
		else if (command.equals("undressme-armor"))
		{
			Inventory inv = player.getInventory();
			ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

			if (chest != null)
			{
				visuality(player, chest, 0);
			}
			if (legs != null)
			{
				visuality(player, legs, 0);
			}
			if (gloves != null)
			{
				visuality(player, gloves, 0);
			}
			if (feet != null)
			{
				visuality(player, feet, 0);
			}

			player.sendUserInfo(true);
			player.broadcastUserInfo(true);

			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-cloak"))
		{
			Inventory inv = player.getInventory();
			ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_BACK);

			if (cloak != null)
			{
				visuality(player, cloak, 0);
			}

			player.sendUserInfo(true);
			player.broadcastUserInfo(true);

			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-shield"))
		{
			Inventory inv = player.getInventory();
			ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);

			if (shield != null)
			{
				visuality(player, shield, 0);
			}

			player.sendUserInfo(true);
			player.broadcastUserInfo(true);

			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-weapon"))
		{
			Inventory inv = player.getInventory();
			ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			if (weapon != null)
			{
				visuality(player, weapon, 0);
			}

			player.sendUserInfo(true);
			player.broadcastUserInfo(true);

			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("showdress"))
		{
			if (player.getVarObject("showVisualChange") == null)
			{
				player.setVar("showVisualChange", "-1", -1);

				for (Player character : World.getAroundPlayers(player))
				{
					player.sendPacket(new CharInfo(character, player));
				}
			}

			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("hidedress"))
		{
			if (player.getVarObject("showVisualChange") != null)
			{
				player.unsetVar("showVisualChange");

				for (Player character : World.getAroundPlayers(player))
				{
					player.sendPacket(new CharInfo(character, player));
				}
			}

			useVoicedCommand("dressme", player, null);
			return true;
		}
		else
		{
			return false;
		}
	}

	private Map<Integer, DressWeaponData> initMap(String type, Map<Integer, DressWeaponData> map)
	{
		if (type.equals("Sword"))
		{
			return map = SWORD;
		}
		else if (type.equals("Blunt"))
		{
			return map = BLUNT;
		}
		else if (type.equals("Dagger"))
		{
			return map = DAGGER;
		}
		else if (type.equals("Bow"))
		{
			return map = BOW;
		}
		else if (type.equals("Pole"))
		{
			return map = POLE;
		}
		else if (type.equals("Fist"))
		{
			return map = FIST;
		}
		else if (type.equals("Dual Sword"))
		{
			return map = DUAL;
		}
		else if (type.equals("Dual Fist"))
		{
			return map = DUALFIST;
		}
		else if (type.equals("Big Sword"))
		{
			return map = BIGSWORD;
		}
		else if (type.equals("Rod"))
		{
			return map = ROD;
		}
		else if (type.equals("Big Blunt"))
		{
			return map = BIGBLUNT;
		}
		else if (type.equals("Crossbow"))
		{
			return map = CROSSBOW;
		}
		else if (type.equals("Rapier"))
		{
			return map = RAPIER;
		}
		else if (type.equals("Ancient Sword"))
		{
			return map = ANCIENTSWORD;
		}
		else if (type.equals("Dual Dagger"))
		{
			return map = DUALDAGGER;
		}
		else
		{
			_log.error("Dress me system: Unknown type: " + type);
			return null;
		}
	}

	private int parseWeapon()
	{
		_hasBeenInitialized = true;

		int Sword = 1, Blunt = 1, Dagger = 1, Bow = 1, Pole = 1, Fist = 1, DualSword = 1, DualFist = 1, BigSword = 1, Rod = 1, BigBlunt = 1, Crossbow = 1, Rapier = 1, AncientSword = 1, DualDagger = 1;

		for (DressWeaponData weapon : DressWeaponHolder.getInstance().getAllWeapons())
		{
			if (weapon.getType().equals("Sword"))
			{
				SWORD.put(Sword, weapon);
				Sword++;
			}
			else if (weapon.getType().equals("Blunt"))
			{
				BLUNT.put(Blunt, weapon);
				Blunt++;
			}
			else if (weapon.getType().equals("Dagger"))
			{
				DAGGER.put(Dagger, weapon);
				Dagger++;
			}
			else if (weapon.getType().equals("Bow"))
			{
				BOW.put(Bow, weapon);
				Bow++;
			}
			else if (weapon.getType().equals("Pole"))
			{
				POLE.put(Pole, weapon);
				Pole++;
			}
			else if (weapon.getType().equals("Fist"))
			{
				FIST.put(Fist, weapon);
				Fist++;
			}
			else if (weapon.getType().equals("Dual Sword"))
			{
				DUAL.put(DualSword, weapon);
				DualSword++;
			}
			else if (weapon.getType().equals("Dual Fist"))
			{
				DUALFIST.put(DualFist, weapon);
				DualFist++;
			}
			else if (weapon.getType().equals("Big Sword"))
			{
				BIGSWORD.put(BigSword, weapon);
				BigSword++;
			}
			else if (weapon.getType().equals("Rod"))
			{
				ROD.put(Rod, weapon);
				Rod++;
			}
			else if (weapon.getType().equals("Big Blunt"))
			{
				BIGBLUNT.put(BigBlunt, weapon);
				BigBlunt++;
			}
			else if (weapon.getType().equals("Crossbow"))
			{
				CROSSBOW.put(Crossbow, weapon);
				Crossbow++;
			}
			else if (weapon.getType().equals("Rapier"))
			{
				RAPIER.put(Rapier, weapon);
				Rapier++;
			}
			else if (weapon.getType().equals("Ancient Sword"))
			{
				ANCIENTSWORD.put(AncientSword, weapon);
				AncientSword++;
			}
			else if (weapon.getType().equals("Dual Dagger"))
			{
				DUALDAGGER.put(DualDagger, weapon);
				DualDagger++;
			}
			else
			{
				_log.error("Dress me system: Can't find type: " + weapon.getType());
			}
		}

		_log.info("Dress me system: Load " + Sword + " Sword(s).");
		_log.info("Dress me system: Load " + Blunt + " Blunt(s).");
		_log.info("Dress me system: Load " + Dagger + " Dagger(s).");
		_log.info("Dress me system: Load " + Bow + " Bow(s).");
		_log.info("Dress me system: Load " + Pole + " Pole(s).");
		_log.info("Dress me system: Load " + Fist + " Fist(s).");
		_log.info("Dress me system: Load " + DualSword + " Dual Sword(s).");
		_log.info("Dress me system: Load " + DualFist + " Dual Fist(s).");
		_log.info("Dress me system: Load " + BigSword + " Big Sword(s).");
		_log.info("Dress me system: Load " + Rod + " Rod(s).");
		_log.info("Dress me system: Load " + BigBlunt + " Big Blunt(s).");
		_log.info("Dress me system: Load " + Crossbow + " Crossbow(s).");
		_log.info("Dress me system: Load " + Rapier + " Rapier(s).");
		_log.info("Dress me system: Load " + AncientSword + " Ancient Sword(s).");
		_log.info("Dress me system: Load " + DualDagger + " Dual Dagger(s).");

		return 0;
	}

	private void visuality(Player player, ItemInstance item, int visual)
	{
		item.setVisualItemId(visual);
		item.setJdbcState(JdbcEntityState.UPDATED);
		item.update();

		if (visual > 0)
		{
			player.sendMessage(item.getName() + " has been visual change to " + Util.getItemName(visual));
		}
		else
		{
			player.sendMessage("Visual change from " + item.getName() + " has been remove.");
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}

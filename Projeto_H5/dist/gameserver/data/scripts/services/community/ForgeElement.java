package services.community;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.FoundationHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.templates.item.ItemTemplate;

public class ForgeElement
{
	protected static String[] generateAttribution(ItemInstance item, int slot, Player player, boolean hasBonus)
	{
		String[] data = new String[4];

		String noicon = "icon.NOIMAGE";
		String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
		String dot = "<font color=\"FF0000\">...</font>";
		String immposible = new CustomMessage("communityboard.forge.attribute.immposible", player).toString();
		String maxenchant = new CustomMessage("communityboard.forge.attribute.maxenchant", player).toString();
		String heronot = new CustomMessage("communityboard.forge.attribute.heronot", player).toString();
		String picenchant = "l2ui_ch3.multisell_plusicon";
		String pvp = "icon.pvp_tab";

		if (item != null)
		{
			data[0] = item.getTemplate().getIcon();
			data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
			if ((item.getTemplate().isAttributable() || item.getName().contains("{PvP}") || item.isAugmented()) && (itemCheckGrade(hasBonus, item)))
			{
				if (item.isHeroWeapon())
				{
					data[2] = heronot;
					data[3] = slotclose;
				}
				else if (((item.isArmor()) && (((item.getAttributes().getFire() | item.getAttributes().getWater()) & (item.getAttributes().getWind() | item.getAttributes().getEarth()) & (item.getAttributes().getHoly() | item.getAttributes().getUnholy())) >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX)) || ((item.isWeapon()) && (item.getAttributes().getValue() >= Config.BBS_FORGE_WEAPON_ATTRIBUTE_MAX)) || item.isAccessory() || item.getTemplate().isShield())
				{
					data[2] = maxenchant;
					data[3] = slotclose;
				}
				else
				{
					data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:item:").append(slot).append("\" value=\"").append(new CustomMessage("common.enchant.attribute", player).toString()).append("\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					 * if (item.getTemplate().isPvP())
					 * data[3] = pvp;
					 * else
					 */
					data[3] = picenchant;
				}
			}
			else
			{
				data[2] = immposible;
				data[3] = slotclose;
			}
		}
		else
		{
			data[0] = noicon;
			data[1] = new CustomMessage(new StringBuilder().append("common.item.not.clothed.").append(slot).append("").toString(), player).toString();
			data[2] = dot;
			data[3] = slotclose;
		}

		return data;
	}

	protected static String[] generateEnchant(ItemInstance item, int max, int slot, Player player)
	{
		String[] data = new String[4];

		String noicon = "icon.NOIMAGE";
		String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
		String dot = "<font color=\"FF0000\">...</font>";
		String maxenchant = new CustomMessage("communityboard.forge.enchant.max", player).toString();
		String picenchant = "l2ui_ch3.multisell_plusicon";
		String pvp = "icon.pvp_tab";

		if (item != null)
		{
			data[0] = item.getTemplate().getIcon();
			data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
			if (!item.getTemplate().isArrow())
			{
				if ((item.getEnchantLevel() >= max) || (!item.canBeEnchanted(true)))
				{
					data[2] = maxenchant;
					data[3] = slotclose;
				}
				else
				{
					data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:enchant:item:").append(slot).append("\" value=\"").append(new CustomMessage("common.enchant", player).toString()).append("\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					 * if (item.getTemplate().isPvP())
					 * data[3] = pvp;
					 * else
					 */
					data[3] = picenchant;
				}
			}
			else
			{
				data[2] = dot;
				data[3] = slotclose;
			}
		}
		else
		{
			data[0] = noicon;
			data[1] = new CustomMessage(new StringBuilder().append("common.item.not.clothed.").append(slot).append("").toString(), player).toString();
			data[2] = dot;
			data[3] = slotclose;
		}

		return data;
	}

	protected static String[] generateFoundation(ItemInstance item, int slot, Player player)
	{
		String[] data = new String[4];

		String noicon = "icon.NOIMAGE";
		String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
		String dot = "<font color=\"FF0000\">...</font>";
		String no = new CustomMessage("communityboard.forge.no.foundation", player).toString();
		String picenchant = "l2ui_ch3.multisell_plusicon";
		String pvp = "icon.pvp_tab";

		if (item != null)
		{
			data[0] = item.getTemplate().getIcon();
			data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
			if (!item.getTemplate().isArrow())
			{
				int found = FoundationHolder.getInstance().getFoundation(item.getItemId());
				if (found == -1)
				{
					data[2] = no;
					data[3] = slotclose;
				}
				else
				{
					data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:foundation:item:").append(slot).append("\" value=\"").append(new CustomMessage("common.exchange", player).toString()).append("\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					 * if (item.getTemplate().isPvP())
					 * data[3] = pvp;
					 * else
					 */
					data[3] = picenchant;
				}
			}
			else
			{
				data[2] = dot;
				data[3] = slotclose;
			}
		}
		else
		{
			data[0] = noicon;
			data[1] = new CustomMessage(new StringBuilder().append("common.item.not.clothed.").append(slot).append("").toString(), player).toString();
			data[2] = dot;
			data[3] = slotclose;
		}

		return data;
	}

	protected static String page(Player player)
	{
		return HtmCache.getInstance().getNotNull(new StringBuilder().append(Config.BBS_HOME_DIR).append("forge/page_template.htm").toString(), player);
	}

	protected static boolean itemCheckGrade(boolean hasBonus, ItemInstance item)
	{
		ItemTemplate.Grade grade = item.getCrystalType();

		switch (grade)
		{
//			case NONE:
//				return hasBonus;
//			case D:
//				return hasBonus;
//			case C:
//				return hasBonus;
//			case B:
//				return hasBonus;
//			case A:
//				return hasBonus;
		// The only S-Grade allowed are the dynasty weapons and armors. That were S80 and then changed to S
		case S:
			return item.getName().contains("Dynasty");
		case S80:
			return hasBonus;
		case S84:
			return hasBonus;
		}
		return false;
	}

	protected static boolean canEnchantArmorAttribute(int attr, ItemInstance item)
	{
		switch (attr)
		{
		case 0:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.FIRE), false) == 0)
			{
				break;
			}
			return false;
		case 1:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.WATER), false) == 0)
			{
				break;
			}
			return false;
		case 2:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.WIND), false) == 0)
			{
				break;
			}
			return false;
		case 3:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.EARTH), false) == 0)
			{
				break;
			}
			return false;
		case 4:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.HOLY), false) == 0)
			{
				break;
			}
			return false;
		case 5:
			if (item.getAttributeElementValue(Element.getReverseElement(Element.UNHOLY), false) == 0)
			{
				break;
			}
			return false;
		}

		return true;
	}
}

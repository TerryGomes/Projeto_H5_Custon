package l2mv.gameserver.handler.admincommands.impl;

import java.util.Map;

//import l2f.extensions.scripts.ScriptFile;
//import l2mv.gameserver.handler.AdminCommandHandler;

import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminAttribute implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_attribute
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		GameObject target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		if (!target.isPlayer())
		{
			activeChar.sendMessage("Wrong target type.");
			return false;
		}
		Player targetPlayer = target.getPlayer();

		int armorType = wordList.length > 1 ? Integer.parseInt(wordList[1]) : -1;

		if (armorType == -1 || wordList.length < 2 || targetPlayer.getInventory().getPaperdollItem(armorType) == null)
		{
			showMainPage(activeChar);
			return true;
		}

		int element = wordList.length > 2 ? Integer.parseInt(wordList[2]) : -2;
		int newValue = wordList.length > 3 ? Integer.parseInt(wordList[3]) : 0;

		if (element == -2)
		{
			showDetailsPage(activeChar, targetPlayer, armorType);
			return true;
		}

		try
		{
			setEnchant(activeChar, targetPlayer, newValue, element, armorType);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			activeChar.sendMessage("Please specify a new enchant value.");
		}
		catch (NumberFormatException e)
		{
			activeChar.sendMessage("Please specify a valid new enchant value.");
		}

		// show the enchant menu after an action
		showDetailsPage(activeChar, targetPlayer, armorType);
		return true;
	}

	private void setEnchant(Player activeChar, Player target, int value, int element, int armorType)
	{
		Element El = Element.NONE;
		switch (element)
		{
		case 0:
			El = Element.FIRE;
			break;
		case 1:
			El = Element.WATER;
			break;
		case 2:
			El = Element.WIND;
			break;
		case 3:
			El = Element.EARTH;
			break;
		case 4:
			El = Element.HOLY;
			break;
		case 5:
			El = Element.UNHOLY;
			break;
		}

		int curEnchant = 0;

		ItemInstance item = target.getInventory().getPaperdollItem(armorType);
		curEnchant = item.getEnchantLevel();
		if (item != null)
		{
			if (item.isWeapon())
			{
				item.setAttributeElement(El, value);
				target.getInventory().equipItem(item);
				target.sendPacket(new InventoryUpdate().addModifiedItem(item));
				target.broadcastUserInfo(true);
			}
			if (item.isArmor())
			{
				if (!canEnchantArmorAttribute(element, item))
				{
					target.sendMessage("Unable to insert an attribute in the armor, not the conditions.");
					return;
				}

				target.getInventory().unEquipItem(item);
				item.setAttributeElement(El, value);
				target.getInventory().equipItem(item);
				target.sendPacket(new InventoryUpdate().addModifiedItem(item));
				target.broadcastUserInfo(true);
			}
			String elementName = "";
			switch (element)
			{
			case 0:
				elementName = "Fire";
				break;
			case 1:
				elementName = "Water";
				break;
			case 2:
				elementName = "Wind";
				break;
			case 3:
				elementName = "Earth";
				break;
			case 4:
				elementName = "Holy";
				break;
			case 5:
				elementName = "Dark";
				break;
			}

			activeChar.sendMessage("You have changed attribute " + elementName + " on " + value + " in " + item.getName() + " +" + curEnchant + ".");
			target.sendMessage("Admin has changed the value of the attribute " + elementName + " on " + value + " in " + item.getName() + " +" + curEnchant + ".");
		}
	}

	private boolean canEnchantArmorAttribute(int attr, ItemInstance item)
	{
		switch (attr)
		{
		case 0:
			if (item.getDefenceWater() != 0)
			{
				return false;
			}
			break;
		case 1:
			if (item.getDefenceFire() != 0)
			{
				return false;
			}
			break;
		case 2:
			if (item.getDefenceEarth() != 0)
			{
				return false;
			}
			break;
		case 3:
			if (item.getDefenceWind() != 0)
			{
				return false;
			}
			break;
		case 4:
			if (item.getDefenceUnholy() != 0)
			{
				return false;
			}
			break;
		case 5:
			if (item.getDefenceHoly() != 0)
			{
				return false;
			}
			break;
		}
		return true;
	}

	private void showMainPage(Player activeChar)
	{
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/attribute.htm"));
	}

	private static void showDetailsPage(Player player, Player target, int paperdoll)
	{
		ItemInstance item = target.getInventory().getPaperdollItem(paperdoll);
		String html = HtmCache.getInstance().getNullable("admin/attributeDetails.htm", player);

		html = html.replace("%item%", item.getName());
		html = html.replace("%nick%", player.getTarget().getName());

		Map<Element, Integer> aboveZero = item.getAttributes().getElements();

		StringBuilder builder = new StringBuilder();

		if (!aboveZero.isEmpty())
		{
			builder.append("Current Attributes<br>");
			for (Map.Entry<Element, Integer> element : aboveZero.entrySet())
			{
				builder.append(getElementName(element.getKey())).append("<table><tr>");
				int value = element.getValue().intValue();
				builder.append(getAttributeButtons(element.getKey(), paperdoll, value, item.isWeapon()));
				builder.append("</tr></table><br1>");
			}
		}
		html = html.replace("%current%", builder.toString());

		builder = new StringBuilder();

		if (item.isWeapon() && aboveZero.isEmpty() || item.isArmor() && aboveZero.size() < 3)
		{
			builder.append("New Attributes<br>");
			Element[] mainAttributes =
			{
				Element.FIRE,
				Element.EARTH,
				Element.HOLY
			};

			for (Element attribute : mainAttributes)
			{
				Element reverse = Element.getReverseElement(attribute);
				if (!aboveZero.containsKey(attribute) && !aboveZero.containsKey(reverse))
				{
					builder.append("<table width=270><tr><td><center>").append(getElementName(attribute)).append("</center></td><td><center>").append(getElementName(reverse)).append("</center></td></tr>");
					builder.append("<tr><td><table border=0 cellpadding=0 cellspacing=0 width=135><tr>").append(getAttributeButtons(attribute, paperdoll, -1, item.isWeapon())).append("</table></td>");
					builder.append("<td><table border=0 cellpadding=0 cellspacing=0 width=135><tr>").append(getAttributeButtons(reverse, paperdoll, -1, item.isWeapon())).append("</table></td></tr></table>");
				}
			}
		}
		html = html.replace("%new%", builder.toString());

		player.sendPacket(new NpcHtmlMessage(5).setHtml(html));
	}

	private static String getAttributeButtons(Element element, int paperdoll, int currentValue, boolean weapon)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<td>").append(currentValue == 0 ? "0" : "<button value=\"0\" action=\"bypass -h admin_attribute " + paperdoll + ' ' + element.getId() + " 0\" width=40 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">").append("</td>");
		if (!weapon)
		{
			builder.append("<td>").append(currentValue == 0 ? "60" : "<button value=\"60\" action=\"bypass -h admin_attribute " + paperdoll + ' ' + element.getId() + " 60\" width=40 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">").append("</td>");
		}
		builder.append("<td>").append(currentValue == 0 ? "120" : "<button value=\"120\" action=\"bypass -h admin_attribute " + paperdoll + ' ' + element.getId() + " 120\" width=40 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">").append("</td>");
		if (weapon)
		{
			builder.append("<td>").append(currentValue == 0 ? "300" : "<button value=\"300\" action=\"bypass -h admin_attribute " + paperdoll + ' ' + element.getId() + " 300\" width=40 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">").append("</td>");
		}
		return builder.toString();
	}

	/**
	 * returning font color for each element
	 */
	private static String getElementName(Element element)
	{
		String color;
		switch (element)
		{
		case EARTH:
			color = "94775b";
			break;
		case FIRE:
			color = "b36464";
			break;
		case HOLY:
			color = "8c8787";
			break;
		case UNHOLY:
			color = "4c558f";
			break;
		case WATER:
			color = "528596";
			break;
		case WIND:
			color = "768f91";
			break;
		default:
			color = "768f91";
		}
		return "<font color=" + color + '>' + element.name() + "</font>";
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
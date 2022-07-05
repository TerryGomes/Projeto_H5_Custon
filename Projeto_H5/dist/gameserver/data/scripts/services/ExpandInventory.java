package services;

import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;

public class ExpandInventory extends Functions
{
	public void get()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}

		if (player.getInventoryLimit() >= Config.SERVICES_EXPAND_INVENTORY_MAX)
		{
			player.sendMessage("Already max count.");
			return;
		}

		if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_INVENTORY_ITEM, Config.SERVICES_EXPAND_INVENTORY_PRICE, "ExpandInventory$get"))
		{
			player.setExpandInventory(player.getExpandInventory() + 1);
			player.setVar("ExpandInventory", String.valueOf(player.getExpandInventory()), -1);
			player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
		}
		else if (Config.SERVICES_EXPAND_INVENTORY_ITEM == 57)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}

		show();
	}

	public void show()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}

		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_INVENTORY_ITEM);

		String out = "";

		out += "<html><body>Extension equipment";
		out += "<br><br><table>";
		out += "<tr><td>Current size:</td><td>" + player.getInventoryLimit() + "</td></tr>";
		out += "<tr><td>Maximum size:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_MAX + "</td></tr>";
		out += "<tr><td>Cost slots:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_PRICE + " " + item.getName() + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.ExpandInventory:get\" value=\"Expand\">";
		out += "</body></html>";

		show(out, player);
	}
}
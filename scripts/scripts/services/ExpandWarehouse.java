package services;

import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.item.ItemTemplate;

public class ExpandWarehouse extends Functions
{
	public void get()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}

		if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_WAREHOUSE_ITEM, Config.SERVICES_EXPAND_WAREHOUSE_PRICE, "ExpandWarehouse$get"))
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

		show();
	}

	public void show()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}

		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_WAREHOUSE_ITEM);

		String out = "";

		out += "<html><body>Expansion of warehouse";
		out += "<br><br><table>";
		out += "<tr><td>Current size:</td><td>" + player.getWarehouseLimit() + "</td></tr>";
		out += "<tr><td>Cost slots:</td><td>" + Config.SERVICES_EXPAND_WAREHOUSE_PRICE + " " + item.getName() + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.ExpandWarehouse:get\" value=\"Expand\">";
		out += "</body></html>";

		show(out, player);
	}
}
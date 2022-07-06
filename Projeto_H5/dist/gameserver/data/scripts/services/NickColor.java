package services;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Util;

/**
 *
 * @author FandC
 *
 */
public class NickColor extends Functions
{
	public void list(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_CHANGE_NICK_COLOR_ENABLED)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
		final int perpage = 6;
		int counter = 0;

		String html = HtmCache.getInstance().getNotNull("scripts/services/NickColor/index.htm", player);
		String template = HtmCache.getInstance().getNotNull("scripts/services/NickColor/template.htm", player);
		String block = "";
		String list = "";

		for (int i = (page - 1) * perpage; i < Config.SERVICES_CHANGE_NICK_COLOR_LIST.length; i++)
		{
			String color = Config.SERVICES_CHANGE_NICK_COLOR_LIST[i];
			block = template;
			block = block.replace("{bypass}", "bypass -h scripts_services.NickColor:change " + color);
			block = block.replace("{color}", (color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)));
			block = block.replace("{count}", Util.formatAdena(Config.SERVICES_CHANGE_NICK_COLOR_PRICE));
			block = block.replace("{item}", ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_NICK_COLOR_ITEM).getName());
			list += block;

			counter++;

			if (counter >= perpage)
			{
				break;
			}
		}

		double count = Math.ceil((double) Config.SERVICES_CHANGE_NICK_COLOR_LIST.length / (double) perpage); // Use rounding to get the last page with the rest!!
		int inline = 1;
		String navigation = "";
		for (int i = 1; i <= count; i++)
		{
			if (i == page)
			{
				navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h scripts_services.NickColor:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
			}
			else
			{
				navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.NickColor:list " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
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

		show(html, player, null);
	}

	public void change(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (param[0].equalsIgnoreCase("FFFFFF"))
		{
			player.setNameColor(Integer.decode("0xFFFFFF"));
			player.broadcastUserInfo(true);
			return;
		}

		if (player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_NICK_COLOR_ITEM, Config.SERVICES_CHANGE_NICK_COLOR_PRICE, "removedd pirce"))
		{
			player.setNameColor(Integer.decode("0x" + param[0]));
			player.broadcastUserInfo(true);
			player.sendMessage("Your name color was changed!");
		}

		else if (Config.SERVICES_CHANGE_NICK_COLOR_ITEM == 57)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendMessage("You don't have enought item(s)");
		}

		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			player.sendMessage("Incorrect item count!");
		}
	}
}
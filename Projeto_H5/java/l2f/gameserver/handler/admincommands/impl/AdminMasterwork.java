package l2f.gameserver.handler.admincommands.impl;

import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemAttributes;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.utils.ItemFunctions;

public class AdminMasterwork implements IAdminCommandHandler
{
	private static final int[] SLOTS_TO_MASTERWORK =
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_FEET
	};

	private static enum Commands
	{
		admin_masterwork, admin_create_masterwork
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		Player target;
		if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
		{
			target = activeChar.getTarget().getPlayer();
		}
		else
		{
			target = activeChar;
		}

		switch (command)
		{
		case admin_masterwork:
			showMainMasterwork(activeChar, target);
			break;
		case admin_create_masterwork:
			int slot = Integer.parseInt(wordList[1]);
			createMasterwork(activeChar, target, slot);
			showMainMasterwork(activeChar, target);
			break;
		}

		return true;
	}

	private static void showMainMasterwork(Player activeChar, Player target)
	{
		String html = HtmCache.getInstance().getNullable("admin/masterwork.htm", activeChar);

		StringBuilder main = new StringBuilder("<table width=250>");

		for (int slot : SLOTS_TO_MASTERWORK)
		{
			ItemInstance item = target.getInventory().getPaperdollItem(slot);
			if (item != null && item.getTemplate().getMasterworkConvert() > 0)
			{
				main.append("<tr><td width=250>");
				main.append("<center>").append(item.getName());
				main.append("<br1>");
				main.append("<button value=\"Make Masterwork\" action=\"bypass -h admin_create_masterwork ").append(slot).append("\" width=200 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></center>");
				main.append("</td></tr>");
			}
		}
		main.append("</table>");

		html = html.replace("%main%", main.toString());
		activeChar.sendPacket(new NpcHtmlMessage(0).setHtml(html));
	}

	private static void createMasterwork(Player activeChar, Player target, int slot)
	{
		ItemInstance item = target.getInventory().getPaperdollItem(slot);
		if (item != null && item.getTemplate().getMasterworkConvert() > 0)
		{
			convertToMasterwork(target, item);
			activeChar.sendMessage("Item was converted to Masterwork!");
		}
		else
		{
			activeChar.sendMessage("Item couldn't be converted!");
		}
	}

	private static void convertToMasterwork(Player target, ItemInstance item)
	{
		int enchant = item.getEnchantLevel();
		ItemAttributes attributes = item.getAttributes();
		int augmentation = item.getAugmentationId();

		ItemInstance newItem = ItemFunctions.createItem(item.getTemplate().getMasterworkConvert());
		newItem.setEnchantLevel(enchant);
		newItem.setAttributes(attributes);
		newItem.setAugmentationId(augmentation);

		target.getInventory().destroyItem(item, "Admin Masterwork Convert");
		target.getInventory().addItem(newItem, "Admin Masterwork Convert");
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}

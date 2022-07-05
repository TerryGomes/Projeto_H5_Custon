package services;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.Config;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.ItemFunctions;

/**
 * Service to transfer pvp and augmented weapons to another character
 *
 * @author Synerge
 */
public class TransferWeapon extends Functions
{
	public void list()
	{
		Player player = getSelf();
		if (!Config.SERVICES_TRANSFER_WEAPON_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		// Filter all the weapons of the character, we only want Augmented and PvP Weapons
		final List<ItemInstance> weapons = new ArrayList<>();
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (!isSuitableForTransfer(player, item))
			{
				continue;
			}

			weapons.add(item);
		}

		String html = HtmCache.getInstance().getNotNull("scripts/services/TransferWeapon/list.htm", player);

		final int ITEMS_PER_ROW = 4;
		final int MAX_ITEMS_ROWS = 5;

		// Weapons
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ITEMS_PER_ROW * MAX_ITEMS_ROWS; i++)
		{
			// Open row
			if (i == 0 || i % ITEMS_PER_ROW == 0)
			{
				sb.append("<tr>");
			}

			if (weapons.size() > i)
			{
				final ItemInstance weapon = weapons.get(i);

				sb.append("<td fixwidth=34>");
				sb.append("<table border=0 cellspacing=2 cellpadding=2 width=34 height=40>");
				sb.append("	<tr>");
				sb.append("		<td>");
				sb.append("			<table border=0 cellspacing=0 cellpadding=0 width=34 height=34 background=" + weapon.getTemplate().getIcon() + ">");
				sb.append("				<tr>");
				sb.append("					<td>");
				sb.append("						<table cellspacing=0 cellpadding=0 width=34 height=34 background=L2UI_CT1.ItemWindow_DF_Frame>");
				sb.append("							<tr>");
				sb.append("								<td>");
				sb.append("									<br>");
				sb.append("								</td>");
				sb.append("								<td height=34>");
				sb.append("									<button action=\"bypass -h scripts_services.TransferWeapon:chooseWeapon ").append(weapon.getObjectId())
							.append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\">");
				sb.append("								</td>");
				sb.append("							</tr>");
				sb.append("						</table>");
				sb.append("					</td>");
				sb.append("				</tr>");
				sb.append("			</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
				sb.append("</td>");
			}
			else
			{
				sb.append("<td fixwidth=230>");
				sb.append("<table cellspacing=2 cellpadding=2 width=230 height=40>");
				sb.append("<tr>");
				sb.append("<td>");
				sb.append("&nbsp;");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
			}

			// Close row
			if ((i + 1) % ITEMS_PER_ROW == 0 || i >= ITEMS_PER_ROW * MAX_ITEMS_ROWS)
			{
				sb.append("</tr>");
			}
		}

		html = html.replace("%weaponList%", sb.toString());
		show(html, player);
	}

	private static boolean isSuitableForTransfer(Player player, ItemInstance item)
	{
		// Only augmented and PvP weapons allowed
		if (!item.isWeapon() || !player.getPermissions().canLoseItem(item, false) || (!item.getName().contains("{PvP}") && !item.isAugmented()))
		{
			return false;
		}

		return true;
	}

	public void chooseWeapon(String[] param)
	{
		Player player = getSelf();
		if (!Config.SERVICES_TRANSFER_WEAPON_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		final int itemObjectId = Integer.parseInt(param[0]);
		final ItemInstance weapon = player.getInventory().getItemByObjectId(itemObjectId);
		if (weapon == null)
		{
			player.sendMessage("Wrong weapon selected");
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		String html = HtmCache.getInstance().getNotNull("scripts/services/TransferWeapon/transfer.htm", player);
		html = html.replace("%weaponName%", (weapon.getAugmentationId() > 0 ? "Augmented " : "") + weapon.getName() + (weapon.getEnchantLevel() > 0 ? " +" + weapon.getEnchantLevel() : "") + " "
					+ weapon.getTemplate().getAdditionalName());
		html = html.replace("%weaponId%", String.valueOf(weapon.getObjectId()));
		html = html.replace("%icon%", weapon.getTemplate().getIcon());
		show(html, player);
	}

	public void transferWeapon(String[] param)
	{
		Player player = getSelf();
		if (!Config.SERVICES_TRANSFER_WEAPON_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		final int itemObjectId = Integer.parseInt(param[0]);
		final ItemInstance weapon = player.getInventory().getItemByObjectId(itemObjectId);
		if (weapon == null || !isSuitableForTransfer(player, weapon))
		{
			player.sendMessage("Wrong weapon selected");
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		final String charName = param[1];
		final int charId = CharacterDAO.getInstance().getObjectIdByName(charName);
		if (charId < 1)
		{
			player.sendMessage("The input player does not exist");
			chooseWeapon(new String[]
			{
				param[0]
			});
			return;
		}

		if (ItemFunctions.removeItem(player, Config.SERVICES_TRANSFER_WEAPON_ITEM, Config.SERVICES_TRANSFER_WEAPON_PRICE, true, "TransferWeapon$transferWeapon") < 1)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT_);
		}
		else
		{
			// Remove the item from the player
			List<ItemInstance> attachments = new ArrayList<ItemInstance>();
			ItemInstance item = player.getInventory().removeItemByObjectId(itemObjectId, 1, "TransferWeapon");
			item.setOwnerId(player.getObjectId());
			item.setLocation(ItemLocation.MAIL);
			if (item.getJdbcState().isSavable())
			{
				item.save();
			}
			else
			{
				item.setJdbcState(JdbcEntityState.UPDATED);
				item.update();
			}
			attachments.add(item);

			// Create the mail and send the item to the other character
			Mail mail = new Mail();
			mail.setSenderId(player.getObjectId());
			mail.setSenderName(player.getName());
			mail.setReceiverId(charId);
			mail.setReceiverName(charName);
			mail.setTopic("Transfer Weapon");
			mail.setBody("You have recieved a weapon transfered using our special system.\nIt was sent by " + player.getName());
			mail.setPrice(0);
			mail.setUnread(true);
			mail.setType(Mail.SenderType.NORMAL);
			mail.setExpireTime(72 * 3600 + (int) (System.currentTimeMillis() / 1000L));
			for (ItemInstance items : attachments)
			{
				mail.addAttachment(items);
			}
			mail.save();

			ItemLogHandler.getInstance().addLog(player, attachments, charName, ItemActionType.MAIL);

			player.sendMessage("The selected weapon has been sent to the desired character succesfully!");
		}
	}
}
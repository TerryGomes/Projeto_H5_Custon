/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2f.gameserver.handler.admincommands.impl;

import java.util.Collection;
import java.util.StringTokenizer;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.data.xml.holder.OptionDataHolder;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Options.AugmentationFilter;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.actor.instances.player.ShortCut;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.ShortCutRegister;
import l2f.gameserver.tables.AugmentationData;
import l2f.gameserver.templates.OptionDataTemplate;

/**
 * To manage a dynamic html showing all available weapons Enlargements
 * You can assign any augmentation is chosen to target weapon
 *
 */
public class AdminAugmentation implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_augmentation
	}

	private static final int MAX_AUGMENTATIONS_PER_PAGE = 6;
	private static final int MAX_PAGES_PER_PAGE = 9;

	private static AugmentationFilter _filter = AugmentationFilter.NONE;
	private static int _page = 0;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		final StringTokenizer st = new StringTokenizer(fullString, " ");
		st.nextToken();

		if (!st.hasMoreTokens())
		{
			_page = 0;
			showMainMenu(activeChar);
			return true;
		}

		switch (st.nextToken())
		{
		case "menu":
			try
			{
				switch (Integer.parseInt(st.nextToken()))
				{
				case 1:
					_filter = AugmentationFilter.NONE;
					break;
				case 2:
					_filter = AugmentationFilter.ACTIVE_SKILL;
					break;
				case 3:
					_filter = AugmentationFilter.PASSIVE_SKILL;
					break;
				case 4:
					_filter = AugmentationFilter.CHANCE_SKILL;
					break;
				case 5:
					_filter = AugmentationFilter.STATS;
					break;
				}
				_page = 1;
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Error. Use: //augmentation menu [filter]");
			}
			break;
		case "main":
			_page = 0;
			break;
		case "page":
			try
			{
				_page = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Error. Use: //augmentation page [pageN]");
			}
			break;
		case "setaug":
			try
			{
				if (!(activeChar.getTarget() instanceof Player))
				{
					activeChar.sendMessage("Invalid Target");
					return false;
				}

				final Player target = activeChar.getTarget().getPlayer();

				// Chequeamos que el target este en las condiciones correctas por las dudas
				if (target.isInStoreMode() || target.isProcessingRequest() || target.isInTrade())
				{
					activeChar.sendMessage("You cannot edit target's augmentation because he is on store mode");
					return false;
				}

				final ItemInstance targetItem = target.getInventory().getItemByObjectId(target.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
				if (targetItem == null)
				{
					activeChar.sendMessage("The target doesn't have any weapon equipped");
					return false;
				}

				// Unequip item
				target.getInventory().unEquipItem(targetItem);

				// El augmentation principal viene como parametro seleccionado
				final int augId = Integer.parseInt(st.nextToken());

				// El segundo augmentation vamos a obtenerlo aleatoriamente
				final int secAugId = AugmentationData.getInstance().generateRandomSecondaryAugmentation();

				// Ahora augmentamos el item con el nuevo id pasado
				final int aug = ((augId << 16) + secAugId);

				targetItem.setAugmentationId(aug);
				targetItem.setJdbcState(JdbcEntityState.UPDATED);
				targetItem.update();

				// Equipamos el arma y enviamos los cambios
				target.getInventory().equipItem(targetItem);

				target.sendPacket(new InventoryUpdate().addModifiedItem(targetItem));

				for (ShortCut sc : target.getAllShortCuts())
				{
					if (sc.getId() == targetItem.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM)
					{
						target.sendPacket(new ShortCutRegister(target, sc));
					}
				}
				target.sendChanges();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Error. Use: //augmentation setaug [augId]");
			}
			break;
		default:
			return false;
		}

		showMainMenu(activeChar);
		return true;
	}

	protected void showMainMenu(Player player)
	{
		// Listas de augmentations para elegir
		if (_page < 1)
		{
			final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
			adminReply.setFile("admin/augmentations/auglists.htm");
			player.sendPacket(adminReply);
			return;
		}

		final Collection<OptionDataTemplate> augmentations = OptionDataHolder.getInstance().getUniqueOptions(_filter);
		if (augmentations.isEmpty())
		{
			_page = 0;
			showMainMenu(player);
			player.sendMessage("Augmentation list is empty. Try with another filter");
			return;
		}

		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setFile("admin/augmentations/augmentations.htm");

		final StringBuilder pagesHtm = new StringBuilder();
		final StringBuilder augmentationsHtm = new StringBuilder();
		final int maxPage = (int) Math.ceil(augmentations.size() / (double) MAX_AUGMENTATIONS_PER_PAGE);
		_page = Math.min(_page, maxPage);

		int page = 1;
		int count = 0;
		boolean lastColor = true;
		Skill skill;

		for (int i = Math.max((maxPage - _page < MAX_PAGES_PER_PAGE / 2 ? maxPage - MAX_PAGES_PER_PAGE : _page - MAX_PAGES_PER_PAGE / 2), 1); i <= maxPage; i++)
		{
			if (i == _page)
			{
				pagesHtm.append("<td background=L2UI_ct1.Button_DF_Calculator_Over><button action=\"\" value=\"" + i + "\" width=34 height=20 back=\"\" fore=\"\"></td>");
			}
			else
			{
				pagesHtm.append("<td><button action=\"bypass -h admin_augmentation page " + i + "\" value=\"" + i + "\" width=34 height=20 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df></td>");
			}

			count++;
			if (count >= MAX_PAGES_PER_PAGE)
			{
				break;
			}
		}

		count = 0;
		for (OptionDataTemplate augm : augmentations)
		{
			count++;
			if (count > MAX_AUGMENTATIONS_PER_PAGE)
			{
				count = 0;
				page++;
				if (page != _page)
				{
					continue;
				}
			}

			if (page > _page)
			{
				break;
			}

			if (page != _page)
			{
				continue;
			}

			skill = (!augm.getTriggerList().isEmpty() ? augm.getTriggerList().get(0).getSkill() : (!augm.getSkills().isEmpty() ? augm.getSkills().get(0) : null));

			augmentationsHtm.append("<table valign=top border=0 " + (lastColor ? "bgcolor=131210" : "") + ">");
			augmentationsHtm.append("<tr>");
			augmentationsHtm.append("<td valign=top align=center width=34 background=" + (skill != null ? skill.getIcon() : "icon.skill5041") + "><button action=\"bypass -h admin_augmentation setaug " + augm.getId() + "\" width=32 height=32 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"></td>");
			augmentationsHtm.append("<td width=8></td>");
			augmentationsHtm.append("<td width=220>");
			augmentationsHtm.append("<table cellspacing=-2 valign=top>");
			if (skill != null)
			{
				augmentationsHtm.append("<tr><td>" + (skill.getName().length() > 28 ? skill.getName().substring(0, 28) : skill.getName()) + " <font color=a3a3a3>Lv</font> <font color=ae9978>" + skill.getLevel() + "</font> <font color=616161>(" + skill.getId() + ")</font></td></tr>");
			}
			else
			{
				// Stats harcoded
				augmentationsHtm.append("<tr><td width=100><font color=ae9978>+1</font> ");

				switch (augm.getId())
				{
				case 16341: // +1 STR
					augmentationsHtm.append("STR");
					break;
				case 16342: // +1 CON
					augmentationsHtm.append("CON");
					break;
				case 16343: // +1 INT
					augmentationsHtm.append("INT");
					break;
				case 16344: // +1 MEN
					augmentationsHtm.append("MEN");
					break;
				default:
					augmentationsHtm.append(augm.getId());
					break;
				}

				augmentationsHtm.append("</td></tr>");
			}
			augmentationsHtm.append("</table>");
			augmentationsHtm.append("</td>");
			augmentationsHtm.append("</tr>");
			augmentationsHtm.append("</table>");

			lastColor = !lastColor;
		}

		adminReply.replace("%pages%", pagesHtm.toString());
		adminReply.replace("%augs%", augmentationsHtm.toString());
		player.sendPacket(adminReply);
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}

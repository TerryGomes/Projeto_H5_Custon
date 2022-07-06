package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.OptionDataHolder;
import l2mv.gameserver.model.Options;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.network.serverpackets.ExVariationCancelResult;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.ShortCutRegister;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.AugmentationData;
import l2mv.gameserver.templates.OptionDataTemplate;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.ItemStateLog;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

public class Augmentation extends Functions
{
	private static final int MAX_AUGMENTATIONS_PER_PAGE = 7;
	private static final int MAX_PAGES_PER_PAGE = 6;

	public void run(String[] arg)
	{
		int _page = 0;
		Options.AugmentationFilter _filter = Options.AugmentationFilter.NONE;
		Player player = getSelf();
		if (arg.length < 1)
		{
			showMainMenu(player, 0, _filter);
			return;
		}
		String command = arg[0];
		if (command.equals("menu"))
		{
			showMainMenu(player, 0, _filter);
			return;
		}
		if (command.equals("section"))
		{
			try
			{
				switch (Integer.parseInt(arg[1]))
				{
				case 1:
					_filter = Options.AugmentationFilter.NONE;
					break;
				case 2:
					_filter = Options.AugmentationFilter.ACTIVE_SKILL;
					break;
				case 3:
					_filter = Options.AugmentationFilter.PASSIVE_SKILL;
					break;
				case 4:
					_filter = Options.AugmentationFilter.CHANCE_SKILL;
					break;
				case 5:
					_filter = Options.AugmentationFilter.STATS;
				}

				_page = 1;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				player.sendMessage("Error.");
			}
		}
		else if (command.equals("page"))
		{
			try
			{
				switch (Integer.parseInt(arg[2]))
				{
				case 1:
					_filter = Options.AugmentationFilter.NONE;
					break;
				case 2:
					_filter = Options.AugmentationFilter.ACTIVE_SKILL;
					break;
				case 3:
					_filter = Options.AugmentationFilter.PASSIVE_SKILL;
					break;
				case 4:
					_filter = Options.AugmentationFilter.CHANCE_SKILL;
					break;
				case 5:
					_filter = Options.AugmentationFilter.STATS;
				}

				_page = Integer.parseInt(arg[1]);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				player.sendMessage("Error.");
			}
		}
		else if (command.equals("put"))
		{
			try
			{
				if ((player.isInStoreMode()) || (player.isProcessingRequest()) || (player.isInTrade()))
				{
					player.sendMessage("You cannot edit augmentation because you are on store mode");
					return;
				}
				PcInventory inv = player.getInventory();
				ItemInstance targetItem = inv.getItemByObjectId(inv.getPaperdollObjectId(7));
				if (targetItem == null)
				{
					player.sendMessage("You doesn't have any weapon equipped");
					return;
				}
				if (!check(targetItem))
				{
					return;
				}
				final ItemActionLog log = Util.getPay(player, Config.SERVICES_AUGMENTATION_ITEM, Config.SERVICES_AUGMENTATION_PRICE, "Augmentation_" + arg[1], true);
				if (log != null)
				{
					unAugment(targetItem);
					int augId = Integer.parseInt(arg[1]);
					int secAugId = AugmentationData.getInstance().generateRandomSecondaryAugmentation();
					int aug = (augId << 16) + secAugId;
					targetItem.setAugmentationId(aug);
					targetItem.setJdbcState(JdbcEntityState.UPDATED);
					targetItem.update();
					inv.equipItem(targetItem);
					player.sendPacket(new InventoryUpdate().addModifiedItem(targetItem));
					for (ShortCut sc : player.getAllShortCuts())
					{
						if ((sc.getId() == targetItem.getObjectId()) && (sc.getType() == 1))
						{
							player.sendPacket(new ShortCutRegister(player, sc));
						}
					}
					player.sendChanges();
					Log.logItemActions(log, new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "Augmentation_" + arg[1], player, targetItem, 1L));
				}
				switch (Integer.parseInt(arg[2]))
				{
				case 1:
					_filter = Options.AugmentationFilter.NONE;
					break;
				case 2:
					_filter = Options.AugmentationFilter.ACTIVE_SKILL;
					break;
				case 3:
					_filter = Options.AugmentationFilter.PASSIVE_SKILL;
					break;
				case 4:
					_filter = Options.AugmentationFilter.CHANCE_SKILL;
					break;
				case 5:
					_filter = Options.AugmentationFilter.STATS;
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				player.sendMessage("Error.");
			}
		}
		showMainMenu(player, _page, _filter);
	}

	private void unAugment(ItemInstance item)
	{
		if (item.getAugmentationId() == 0)
		{
			return;
		}

		Player player = getSelf();
		boolean equipped = item.isEquipped();
		if (equipped)
		{
			player.getInventory().unEquipItem(item);
		}
		item.setAugmentationId(0);
		item.setJdbcState(JdbcEntityState.UPDATED);
		item.update();
		if (equipped)
		{
			player.getInventory().equipItem(item);
		}
		InventoryUpdate iu = new InventoryUpdate().addModifiedItem(item);

		SystemMessage2 sm = new SystemMessage2(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
		sm.addItemName(item.getItemId());
		player.sendPacket(new IStaticPacket[]
		{
			new ExVariationCancelResult(1),
			iu,
			sm
		});
		for (ShortCut sc : player.getAllShortCuts())
		{
			if ((sc.getId() == item.getObjectId()) && (sc.getType() == 1))
			{
				player.sendPacket(new ShortCutRegister(player, sc));
			}
		}
		player.sendChanges();
	}

	private boolean check(ItemInstance item)
	{
		if (item.isHeroWeapon())
		{
			return false;
		}

		switch (item.getItemId())
		{
		case 13752:
		case 13753:
		case 13754:
		case 13755:
			return false;
		}

		switch (item.getCrystalType())
		{
		case NONE:
		case D:
		case C:
		case B:
		case A:
			return false;
		}
		return true;
	}

	private void showMainMenu(Player player, int _page, Options.AugmentationFilter _filter)
	{
		if (_page < 1)
		{
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile("scripts/services/Augmentations/index.htm");
			player.sendPacket(adminReply);
			return;
		}

		final List<OptionDataTemplate> augmentations = checkAugmentations(OptionDataHolder.getInstance().getUniqueOptions(_filter));
		if (augmentations.isEmpty())
		{
			showMainMenu(player, 0, Options.AugmentationFilter.NONE);
			player.sendMessage("Augmentation list is empty. Try with another filter");
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setFile("scripts/services/Augmentations/list.htm");
		String template = HtmCache.getInstance().getNotNull("scripts/services/Augmentations/template.htm", player);
		String block = "";
		String list = "";

		StringBuilder pagesHtm = new StringBuilder();
		final int maxPage = (int) Math.ceil(augmentations.size() / (double) MAX_AUGMENTATIONS_PER_PAGE);
		_page = Math.min(_page, maxPage);
		final int startingIndex = (_page - 1) * MAX_AUGMENTATIONS_PER_PAGE + 1;

		int count = 0;
		boolean lastColor = true;

		for (int i = Math.max((maxPage - _page < MAX_PAGES_PER_PAGE / 2 ? maxPage - MAX_PAGES_PER_PAGE : _page - MAX_PAGES_PER_PAGE / 2), 1); i <= maxPage; i++)
		{
			if (i == _page)
			{
				pagesHtm.append(new StringBuilder().append("<td background=L2UI_ct1.button_df><button action=\"\" value=\"").append(i).append("\" width=38 height=20 back=\"\" fore=\"\"></td>").toString());
			}
			else
			{
				pagesHtm.append(new StringBuilder().append("<td><button action=\"bypass -h scripts_services.Augmentation:run page ").append(i).append(" ").append(_filter.ordinal() + 1).append("\" value=\"").append(i).append("\" width=34 height=20 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df></td>").toString());
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

			if (count >= startingIndex + MAX_AUGMENTATIONS_PER_PAGE)
			{
				break;
			}

			if (count < startingIndex)
			{
				continue;
			}

			Skill skill = !augm.getSkills().isEmpty() ? (Skill) augm.getSkills().get(0) : !augm.getTriggerList().isEmpty() ? augm.getTriggerList().get(0).getSkill() : null;
			block = template;
			block = block.replace("{bypass}", new StringBuilder().append("bypass -h scripts_services.Augmentation:run put ").append(augm.getId()).append(" ").append(_filter.ordinal() + 1).toString());
			String name = "";
			if (skill != null)
			{
				name = skill.getName().length() > 28 ? skill.getName().substring(0, 28) : skill.getName();
			}
			else
			{
				name = "+1 ";
				switch (augm.getId())
				{
				case 16341:
					name = new StringBuilder().append(name).append("STR").toString();
					break;
				case 16342:
					name = new StringBuilder().append(name).append("CON").toString();
					break;
				case 16343:
					name = new StringBuilder().append(name).append("INT").toString();
					break;
				case 16344:
					name = new StringBuilder().append(name).append("MEN").toString();
					break;
				default:
					name = new StringBuilder().append(name).append("(Id:").append(augm.getId()).append(")").toString();
				}
			}

			block = block.replace("{name}", name);
			block = block.replace("{icon}", skill != null ? skill.getIcon() : "icon.skill5041");
			block = block.replace("{color}", lastColor ? "222222" : "333333");
			block = block.replace("{price}", new StringBuilder().append(Util.formatAdena(Config.SERVICES_AUGMENTATION_PRICE)).append(" ").append(Util.getItemName(Config.SERVICES_AUGMENTATION_ITEM)).toString());
			list = new StringBuilder().append(list).append(block).toString();
			lastColor = !lastColor;
		}
		adminReply.replace("%pages%", pagesHtm.toString());
		adminReply.replace("%augs%", list);
		player.sendPacket(adminReply);
	}

	private List<OptionDataTemplate> checkAugmentations(Collection<OptionDataTemplate> augmentations)
	{
		final List<OptionDataTemplate> checkedAugmentations = new ArrayList<OptionDataTemplate>(augmentations.size());
		for (OptionDataTemplate augmentation : augmentations)
		{
			if (checkId(augmentation.getId()))
			{
				checkedAugmentations.add(augmentation);
			}
		}
		return checkedAugmentations;
	}

	private boolean checkId(int id)
	{
		for (int skill : Config.SERVICES_AUGMENTATION_DISABLED_LIST)
		{
			if (skill == id)
			{
				return false;
			}
		}
		return true;
	}

}
package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExGMViewQuestItemList;
import l2mv.gameserver.network.serverpackets.GMHennaInfo;
import l2mv.gameserver.network.serverpackets.GMViewItemList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemFunctions;

public class RequestDestroyItem extends L2GameClientPacket
{
	private int _objectId;
	private long _count;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._count = this.readQ();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		long count = this._count;

		ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
		if (item == null) // Support for GMs deleting items from alt+g inventory.
		{
			for (Player player : GameObjectsStorage.getAllPlayersForIterate()) // There is no way to get item by objectId!!! Or im very stupid to not know such.
			{
				if ((item = player.getInventory().getItemByObjectId(this._objectId)) != null)
				{
					break;
				}
			}
		}

		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (count < 1)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
			return;
		}

		if (!activeChar.isGM() && item.isHeroWeapon())
		{
			activeChar.sendPacket(SystemMsg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
			return;
		}

		if (activeChar.getPet() != null && activeChar.getPet().getControlItemObjId() == item.getObjectId())
		{
			activeChar.sendPacket(SystemMsg.THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED);
			return;
		}

		if (!activeChar.isGM() && !item.canBeDestroyed(activeChar))
		{
			activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_DISCARDED);
			return;
		}

		if (this._count > item.getCount())
		{
			count = item.getCount();
		}

		boolean crystallize = item.canBeCrystallized(activeChar);

		int crystalId = item.getTemplate().getCrystalType().cry;
		int crystalAmount = item.getTemplate().getCrystalCount();
		if (crystallize)
		{
			int level = activeChar.getSkillLevel(Skill.SKILL_CRYSTALLIZE);
			if (level < 1 || crystalId - ItemTemplate.CRYSTAL_D + 1 > level)
			{
				crystallize = false;
			}
		}

		if (item.getOwnerId() == activeChar.getObjectId())
		{
			if (!activeChar.getInventory().destroyItemByObjectId(this._objectId, count, "Delete"))
			{
				activeChar.sendActionFailed();
				return;
			}
			else if (item.isAdena())
			{
				activeChar.getCounters().adenaDestroyed += count;
			}
		}
		else // Support for GM item deletion through Alt+G inventory.
		{
			Player owner = World.getPlayer(item.getOwnerId());
			if (owner != null)
			{
				// If item is successfully deleted, show updated target inventory.
				if (owner.getInventory().destroyItemByObjectId(this._objectId, count, "GMDelete"))
				{
					ItemInstance[] items = owner.getInventory().getItems();
					int questSize = 0;
					for (ItemInstance i : items)
					{
						if (i.getTemplate().isQuest())
						{
							questSize++;
						}
					}

					activeChar.sendPacket(new GMViewItemList(owner, items, items.length - questSize));
					activeChar.sendPacket(new ExGMViewQuestItemList(owner, items, questSize));
					activeChar.sendPacket(new GMHennaInfo(owner));
				}
				else
				{
					activeChar.sendActionFailed();
					return;
				}
			}
		}

		// При удалении ошейника, удалить пета
		if (PetDataTable.isPetControlItem(item))
		{
			PetDataTable.deletePet(item, activeChar);
		}

		if (crystallize)
		{
			activeChar.sendPacket(SystemMsg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
			ItemFunctions.addItem(activeChar, crystalId, crystalAmount, true, "Delete");
		}
		else
		{
			activeChar.sendPacket(SystemMessage2.removeItems(item.getItemId(), count));
		}

		activeChar.sendChanges();

		ItemLogHandler.getInstance().addLog(activeChar, item, count, ItemActionType.DESTROYED_ON_PURPOSE);
	}
}
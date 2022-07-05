package l2f.gameserver.network.clientpackets;

import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.instances.player.ShortCut;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExVariationCancelResult;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.ShortCutRegister;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.ItemTemplate;

public final class RequestRefineCancel extends L2GameClientPacket
{
	// format: (ch)d
	private int _targetItemObjId;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

//		if (!activeChar.checkLastAugmentNpc())
//		{
//			activeChar.sendPacket(new ExVariationCancelResult(0));
//			return;
//		}

		if (activeChar.isActionsDisabled() || activeChar.isInStoreMode() || activeChar.isInTrade())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0));
			return;
		}

		ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);

		// cannot remove augmentation from a not augmented item
		if (targetItem == null || !targetItem.isAugmented())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		// get the price
		int price = getRemovalPrice(targetItem.getTemplate());

		if (price < 0)
		{
			activeChar.sendPacket(new ExVariationCancelResult(0));
		}

		// try to reduce the players adena
		if (!activeChar.reduceAdena(price, true, "RefineCancel"))
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		boolean equipped = false;
		if (equipped = targetItem.isEquipped())
		{
			activeChar.getInventory().unEquipItem(targetItem);
		}

		// remove the augmentation
		targetItem.setAugmentationId(0);
		targetItem.setJdbcState(JdbcEntityState.UPDATED);
		targetItem.update();

		if (equipped)
		{
			activeChar.getInventory().equipItem(targetItem);
		}

		// send inventory update
		InventoryUpdate iu = new InventoryUpdate().addModifiedItem(targetItem);

		// send system message
		SystemMessage2 sm = new SystemMessage2(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
		sm.addItemName(targetItem.getItemId());
		activeChar.sendPacket(new ExVariationCancelResult(1), iu, sm);

		for (ShortCut sc : activeChar.getAllShortCuts())
		{
			if (sc.getId() == targetItem.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM)
			{
				activeChar.sendPacket(new ShortCutRegister(activeChar, sc));
			}
		}
		activeChar.sendChanges();
	}

	public static int getRemovalPrice(ItemTemplate item)
	{
		switch (item.getItemGrade().cry)
		{
		case ItemTemplate.CRYSTAL_C:
			if (item.getCrystalCount() < 1720)
			{
				return 95000;
			}
			else if (item.getCrystalCount() < 2452)
			{
				return 150000;
			}
			else
			{
				return 210000;
			}
		case ItemTemplate.CRYSTAL_B:
			if (item.getCrystalCount() < 1746)
			{
				return 240000;
			}
			else
			{
				return 270000;
			}
		case ItemTemplate.CRYSTAL_A:
			if (item.getCrystalCount() < 2160)
			{
				return 330000;
			}
			else if (item.getCrystalCount() < 2824)
			{
				return 390000;
			}
			else
			{
				return 420000;
			}
		case ItemTemplate.CRYSTAL_S:
			if (item.getCrystalCount() == 10394)
			{ // Icarus
				return 920000;
			}
			else if (item.getCrystalCount() == 7050)
			{ // Dynasty
				return 720000;
			}
			else if (item.getName().contains("Vesper"))
			{ // Vesper
				return 920000;
			}
			else
			{ // Vesper
				return 480000;
			}
			// any other item type is not augmentable
		default:
			return -1;
		}
	}
}
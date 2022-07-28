package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExPutItemResultForVariationMake;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestConfirmTargetItem extends L2GameClientPacket
{
	// format: (ch)d
	private int _itemObjId;

	@Override
	protected void readImpl()
	{
		this._itemObjId = this.readD(); // object_id шмотки
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		ItemInstance item = activeChar.getInventory().getItemByObjectId(this._itemObjId);

		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}
//		if (!activeChar.checkLastAugmentNpc())
//		{
//			return;
//		}

		if (activeChar.getLevel() < 46)
		{
			activeChar.sendMessage("You have to be level 46 in order to augment an item");
			return;
		}

		// check if the item is augmentable
		if (item.isAugmented())
		{
			activeChar.sendPacket(SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
			return;
		}

		else if (item.isNotAugmented() || !item.canBeAugmented(activeChar, item.isAccessory()))
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		// check if the player can augment
		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
			return;
		}
		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}
		if (activeChar.isDead())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
			return;
		}
		if (activeChar.isParalyzed())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
			return;
		}
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
			return;
		}
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
			return;
		}
		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.sendPacket(new ExPutItemResultForVariationMake(this._itemObjId), SystemMsg.SELECT_THE_CATALYST_FOR_AUGMENTATION);
	}
}
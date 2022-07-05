package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestConfirmCancelItem extends L2GameClientPacket
{
	// format: (ch)d
	int _itemId;

	@Override
	protected void readImpl()
	{
		_itemId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

//		if (!activeChar.checkLastAugmentNpc())
//		{
//			return;
//		}
//
		ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemId);

		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (!item.isAugmented())
		{
			activeChar.sendPacket(SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		activeChar.sendPacket(new ExPutItemResultForVariationCancel(item));
	}
}
package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExPutCommissionResultForVariationMake;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;
import l2mv.gameserver.utils.ItemFunctions;

public class RequestConfirmGemStone extends L2GameClientPacket
{
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	private static final int GEMSTONE_B = 2132;

	// format: (ch)dddd
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemstoneItemObjId;
	private long _gemstoneCount;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
		_gemstoneItemObjId = readD();
		_gemstoneCount = readQ();
	}

	@Override
	protected void runImpl()
	{
		if (_gemstoneCount <= 0)
		{
			return;
		}

		Player activeChar = getClient().getActiveChar();
		ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
		ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(_gemstoneItemObjId);

//		if (!activeChar.checkLastAugmentNpc())
//		{
//			return;
//		}

		if (targetItem == null || refinerItem == null || gemstoneItem == null)
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		int gemstoneItemId = gemstoneItem.getTemplate().getItemId();
		if (gemstoneItemId != GEMSTONE_D && gemstoneItemId != GEMSTONE_C && gemstoneItemId != GEMSTONE_B)
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		boolean isAccessoryLifeStone = ItemFunctions.isAccessoryLifeStone(refinerItem.getItemId());

		if (targetItem.isNotAugmented() || !targetItem.canBeAugmented(activeChar, isAccessoryLifeStone) || (!isAccessoryLifeStone && !ItemFunctions.isLifeStone(refinerItem.getItemId())))
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		Grade itemGrade = targetItem.getTemplate().getItemGrade();

		if (isAccessoryLifeStone)
		{
			switch (itemGrade)
			{
			case C:
				if (_gemstoneCount != 200 || gemstoneItemId != GEMSTONE_D)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case B:
				if (_gemstoneCount != 300 || gemstoneItemId != GEMSTONE_D)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case A:
				if (_gemstoneCount != 200 || gemstoneItemId != GEMSTONE_C)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S:
				if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_C)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S80:
				if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_B)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S84:
				if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_B)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			}
		}
		else
		{
			switch (itemGrade)
			{
			case C:
				if (_gemstoneCount != 20 || gemstoneItemId != GEMSTONE_D)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case B:
				if (_gemstoneCount != 30 || gemstoneItemId != GEMSTONE_D)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case A:
				if (_gemstoneCount != 20 || gemstoneItemId != GEMSTONE_C)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S:
				if (_gemstoneCount != 25 || gemstoneItemId != GEMSTONE_C)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S80:
				// Icarus
				if (targetItem.getTemplate().getCrystalCount() == 10394 && (_gemstoneCount != 36 || gemstoneItemId != GEMSTONE_B))
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				// Dynasty
				else if (_gemstoneCount != 28 || gemstoneItemId != GEMSTONE_B)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			case S84:
				if (_gemstoneCount != 36 || gemstoneItemId != GEMSTONE_B)
				{
					activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
					return;
				}
				break;
			}
		}

		activeChar.sendPacket(new ExPutCommissionResultForVariationMake(_gemstoneItemObjId, _gemstoneCount), SystemMsg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN);
	}
}
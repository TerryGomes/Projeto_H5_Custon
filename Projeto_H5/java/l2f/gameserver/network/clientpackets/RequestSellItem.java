package l2f.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExBuySellList;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * packet type id 0x37
 * format:		cddb, b - array if (ddd)
 */
public class RequestSellItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _items; // object id
	private long[] _itemQ; // count

	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();
		if (_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new int[_count];
		_itemQ = new long[_count];

		for (int i = 0; i < _count; i++)
		{
			_items[i] = readD(); // object id
			readD(); // item id
			_itemQ[i] = readQ(); // count
			if (_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || _count == 0)
		{
			return;
		}

		if (activeChar.isActionsDisabled() || activeChar.isBlocked())
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

		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			for (int i = 0; i < _count; i++)
			{
				int objectId = _items[i];
				long count = _itemQ[i];

				ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
				if (item == null || item.getCount() < count || !item.canBeSold(activeChar))
				{
					continue;
				}

				long price = SafeMath.mulAndCheck(item.getReferencePrice(), count) / 2; // nr.2 is the price you get on sell.
				if (Config.SELL_ALL_ITEMS_FREE)
				{
					price = 1;
				}

				ItemInstance refund = activeChar.getInventory().removeItemByObjectId(objectId, count, "Selling Item");

				activeChar.addAdena(price, "Selling Item");
				activeChar.getRefund().addItem(refund, null, null);
				if (activeChar.isBBSUse())
				{
					activeChar.setIsBBSUse(false);
				}

				ItemLogHandler.getInstance().addLog(activeChar, item, count, ItemActionType.SOLD_TO_NPC);
			}
		}
		catch (ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.sendPacket(new ExBuySellList.SellRefundList(activeChar, true));
		activeChar.sendChanges();
	}
}
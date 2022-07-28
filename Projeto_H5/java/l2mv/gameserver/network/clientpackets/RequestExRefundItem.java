package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExBuySellList;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestExRefundItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _items;

	@Override
	protected void readImpl()
	{
		this._listId = this.readD();
		this._count = this.readD();
		if (this._count * 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new int[this._count];
		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD();
			if (ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._count == 0)
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

		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			int slots = 0;
			long weight = 0;
			long totalPrice = 0;

			List<ItemInstance> refundList = new ArrayList<ItemInstance>();
			for (int objId : this._items)
			{
				ItemInstance item = activeChar.getRefund().getItemByObjectId(objId);
				if (item == null)
				{
					continue;
				}

				totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(item.getCount(), item.getReferencePrice()) / 2);
				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), item.getTemplate().getWeight()));

				if (!item.isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
				{
					slots++;
				}

				refundList.add(item);
			}

			if (refundList.isEmpty())
			{
				activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
				activeChar.sendActionFailed();
				return;
			}

			if (!activeChar.getInventory().validateWeight(weight))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				activeChar.sendActionFailed();
				return;
			}

			if (!activeChar.getInventory().validateCapacity(slots))
			{
				activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				activeChar.sendActionFailed();
				return;
			}

			if (!activeChar.reduceAdena(totalPrice, "RefundReturnItem"))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				activeChar.sendActionFailed();
				return;
			}

			for (ItemInstance item : refundList)
			{
				ItemInstance refund = activeChar.getRefund().removeItem(item, null, null);
				activeChar.getInventory().addItem(refund, "RefundReturnItem");
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
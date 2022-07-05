package l2f.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.PcInventory;
import l2f.gameserver.model.items.Warehouse;
import l2f.gameserver.model.items.Warehouse.WarehouseType;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Log;

public class SendWareHouseWithDrawList extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);

	private int _count;
	private int[] _items;
	private long[] _itemQ;

	@Override
	protected void readImpl()
	{
		_count = readD();
		if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new int[_count];
		_itemQ = new long[_count];
		for (int i = 0; i < _count; i++)
		{
			_items[i] = readD(); // item object id
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

		if (!activeChar.getPlayerAccess().UseWarehouse || activeChar.isActionsDisabled())
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

		Warehouse warehouse = null;
		String logType = null;

		if (activeChar.getUsingWarehouseType() == WarehouseType.PRIVATE)
		{
			if (activeChar.getWithdrawWarehouse() != null)
			{
				warehouse = activeChar.getWithdrawWarehouse();
			}
			else
			{
				warehouse = activeChar.getWarehouse();
			}
			logType = Log.WarehouseWithdraw;
		}
		else if (activeChar.getUsingWarehouseType() == WarehouseType.CLAN)
		{
			logType = Log.ClanWarehouseWithdraw;
			boolean canWithdrawCWH = false;
			if (activeChar.getClan() != null)
			{
				if ((activeChar.getClanPrivileges() & Clan.CP_CL_WAREHOUSE_SEARCH) == Clan.CP_CL_WAREHOUSE_SEARCH
							&& (Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || activeChar.isClanLeader() || activeChar.getVarB("canWhWithdraw")))
				{
					canWithdrawCWH = true;
				}
			}
			if (!canWithdrawCWH && activeChar.getWithdrawWarehouse() == null)
			{
				return;
			}

			if (activeChar.getWithdrawWarehouse() != null)
			{
				warehouse = activeChar.getWithdrawWarehouse();
			}
			else
			{
				warehouse = activeChar.getClan().getWarehouse();
			}
		}
		else if (activeChar.getUsingWarehouseType() == WarehouseType.FREIGHT)
		{
			if (activeChar.getWithdrawWarehouse() != null)
			{
				warehouse = activeChar.getWithdrawWarehouse();
			}
			else
			{
				warehouse = activeChar.getFreight();
			}
			logType = Log.FreightWithdraw;
		}
		else
		{
			_log.warn("Error retrieving a warehouse object for char " + activeChar.getName() + " - using warehouse type: " + activeChar.getUsingWarehouseType());
			return;
		}

		activeChar.setWithdrawWarehouse(null); // Reset the withdraw warehouse for GMs

		PcInventory inventory = activeChar.getInventory();

		inventory.writeLock();
		warehouse.writeLock();
		try
		{
			long weight = 0;
			int slots = 0;

			for (int i = 0; i < _count; i++)
			{
				ItemInstance item = warehouse.getItemByObjectId(_items[i]);
				if (item == null || item.getCount() < _itemQ[i])
				{
					activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
					return;
				}

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getTemplate().getWeight(), _itemQ[i]));
				if (!item.isStackable() || inventory.getItemByItemId(item.getItemId()) == null) // вещь требует слота
				{
					slots++;
				}
			}

			if (!activeChar.getInventory().validateCapacity(slots))
			{
				activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				return;
			}

			if (!activeChar.getInventory().validateWeight(weight))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				return;
			}

			for (int i = 0; i < _count; i++)
			{
				ItemInstance item = warehouse.removeItemByObjectId(_items[i], _itemQ[i], null, null);
				activeChar.getInventory().addItem(item, logType);
			}
		}
		catch (ArithmeticException ae)
		{
			// TODO audit
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			warehouse.writeUnlock();
			inventory.writeUnlock();
		}

		activeChar.sendChanges();
		activeChar.sendPacket(SystemMsg.THE_TRANSACTION_IS_COMPLETE);
	}
}
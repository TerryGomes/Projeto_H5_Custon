package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.PremiumItem;
import l2mv.gameserver.network.serverpackets.ExGetPremiumItemList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

//FIXME item-API
public final class RequestWithDrawPremiumItem extends L2GameClientPacket
{
	private int _itemNum;
	private int _charId;
	private long _itemcount;

	@Override
	protected void readImpl()
	{
		_itemNum = readD();
		_charId = readD();
		_itemcount = readQ();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();

		if ((activeChar == null) || (_itemcount <= 0) || (activeChar.getObjectId() != _charId) || activeChar.getPremiumItemList().isEmpty())
		{
			// audit
			return;
		}
		if ((activeChar.getWeightPenalty() >= 3) || ((activeChar.getInventoryLimit() * 0.8) <= activeChar.getInventory().getSize()))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT);
			return;
		}
		if (activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
			return;
		}

		PremiumItem _item = activeChar.getPremiumItemList().get(_itemNum);
		if (_item == null)
		{
			return;
		}
		boolean stackable = ItemHolder.getInstance().getTemplate(_item.getItemId()).isStackable();
		if (_item.getCount() < _itemcount)
		{
			return;
		}
		if (!stackable)
		{
			for (int i = 0; i < _itemcount; i++)
			{
				addItem(activeChar, _item.getItemId(), 1);
			}
		}
		else
		{
			addItem(activeChar, _item.getItemId(), _itemcount);
		}
		if (_itemcount < _item.getCount())
		{
			activeChar.getPremiumItemList().get(_itemNum).updateCount(_item.getCount() - _itemcount);
			activeChar.updatePremiumItem(_itemNum, _item.getCount() - _itemcount);
		}
		else
		{
			activeChar.getPremiumItemList().remove(_itemNum);
			activeChar.deletePremiumItem(_itemNum);
		}

		if (activeChar.getPremiumItemList().isEmpty())
		{
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
		}
		else
		{
			activeChar.sendPacket(new ExGetPremiumItemList(activeChar));
		}
	}

	private void addItem(Player player, int itemId, long count)
	{
		player.getInventory().addItem(itemId, count, "RequestWithDrawPremiumItem");
		player.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
	}
}
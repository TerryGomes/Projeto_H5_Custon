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
		this._itemNum = this.readD();
		this._charId = this.readD();
		this._itemcount = this.readQ();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = this.getClient().getActiveChar();

		if ((activeChar == null) || (this._itemcount <= 0) || (activeChar.getObjectId() != this._charId) || activeChar.getPremiumItemList().isEmpty())
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

		PremiumItem _item = activeChar.getPremiumItemList().get(this._itemNum);
		if (_item == null)
		{
			return;
		}
		boolean stackable = ItemHolder.getInstance().getTemplate(_item.getItemId()).isStackable();
		if (_item.getCount() < this._itemcount)
		{
			return;
		}
		if (!stackable)
		{
			for (int i = 0; i < this._itemcount; i++)
			{
				this.addItem(activeChar, _item.getItemId(), 1);
			}
		}
		else
		{
			this.addItem(activeChar, _item.getItemId(), this._itemcount);
		}
		if (this._itemcount < _item.getCount())
		{
			activeChar.getPremiumItemList().get(this._itemNum).updateCount(_item.getCount() - this._itemcount);
			activeChar.updatePremiumItem(this._itemNum, _item.getCount() - this._itemcount);
		}
		else
		{
			activeChar.getPremiumItemList().remove(this._itemNum);
			activeChar.deletePremiumItem(this._itemNum);
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
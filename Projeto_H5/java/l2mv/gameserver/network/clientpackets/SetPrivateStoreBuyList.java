package l2mv.gameserver.network.clientpackets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.TradeHelper;

public class SetPrivateStoreBuyList extends L2GameClientPacket
{
	private int _count;
	private int[] _items; // item id
	private long[] _itemQ; // count
	private long[] _itemP; // price

	@Override
	protected void readImpl()
	{
		this._count = this.readD();
		if (this._count * 40 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}

		this._items = new int[this._count];
		this._itemQ = new long[this._count];
		this._itemP = new long[this._count];

		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD();

			this.readH();
			this.readH();

			this._itemQ[i] = this.readQ();
			this._itemP[i] = this.readQ();

			if (this._itemQ[i] < 1 || this._itemP[i] < 1)
			{
				this._count = 0;
				break;
			}

			// TODO Gracia Final
			this.readC(); // FE
			this.readD(); // FF 00 00 00
			this.readD(); // 00 00 00 00

			this.readC(); // Unknown 7 bytes
			this.readC();
			this.readC();
			this.readC();
			this.readC();
			this.readC();
			this.readC();
		}
	}

	@Override
	protected void runImpl()
	{
		Player buyer = this.getClient().getActiveChar();
		if (buyer == null || this._count == 0)
		{
			return;
		}

		if (!TradeHelper.checksIfCanOpenStore(buyer, Player.STORE_PRIVATE_BUY) || !Config.ALLOW_PRIVATE_STORES)
		{
			buyer.sendActionFailed();
			return;
		}

		List<TradeItem> buyList = new CopyOnWriteArrayList<TradeItem>();
		long totalCost = 0;
		try
		{
			loop:
			for (int i = 0; i < this._count; i++)
			{
				int itemId = this._items[i];
				long count = this._itemQ[i];
				long price = this._itemP[i];

				ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);

				if (item == null || itemId == ItemTemplate.ITEM_ID_ADENA)
				{
					continue;
				}

				if (item.getReferencePrice() / 2 > price)
				{
					buyer.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.SetPrivateStoreBuyList.TooLowPrice", buyer).addItemName(item).addNumber(item.getReferencePrice() / 2));
					continue;
				}

				if (item.isStackable())
				{
					for (TradeItem bi : buyList)
					{
						if (bi.getItemId() == itemId)
						{
							bi.setOwnersPrice(price);
							bi.setCount(bi.getCount() + count);
							totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
							continue loop;
						}
					}
				}

				TradeItem bi = new TradeItem();
				bi.setItemId(itemId);
				bi.setCount(count);
				bi.setOwnersPrice(price);
				totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
				buyList.add(bi);
			}
		}
		catch (ArithmeticException ae)
		{
			buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		if (buyList.size() > buyer.getTradeLimit())
		{
			buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
			return;
		}

		if (totalCost > buyer.getAdena())
		{
			buyer.sendPacket(SystemMsg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
			buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
			return;
		}

		// Synerge - To be able to buy items, the player must have those items in the inventory. They can add the items, then remove from inventory and the system will get bugged
		for (TradeItem ti : buyList)
		{
			if (buyer.getInventory().getItemByItemId(ti.getItemId()) == null)
			{
				buyer.sendMessage("Your buy list contains some items that you no longer have in your inventory");
				buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
				return;
			}
		}

		if (!buyList.isEmpty())
		{
			buyer.setBuyList(buyList);
			buyer.saveTradeList();
			buyer.setPrivateStoreType(Player.STORE_PRIVATE_BUY);
			buyer.broadcastPacket(new PrivateStoreMsgBuy(buyer));
			buyer.sitDown(null);
			buyer.broadcastCharInfo();
			Log.logPrivateStoreMessage(buyer, buyer.getBuyStoreName());
		}

		buyer.sendActionFailed();
	}
}
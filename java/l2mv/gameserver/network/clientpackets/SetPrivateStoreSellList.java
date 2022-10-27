package l2mv.gameserver.network.clientpackets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.auction.Auction;
import l2mv.gameserver.model.entity.auction.AuctionManager;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.network.serverpackets.PrivateStoreManageListSell;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgSell;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.TradeHelper;

public class SetPrivateStoreSellList extends L2GameClientPacket
{
	private int _count;
	private boolean _package;
	private int[] _items; // objectId
	private long[] _itemQ; // count
	private long[] _itemP; // price

	@Override
	protected void readImpl()
	{
		this._package = this.readD() == 1;
		this._count = this.readD();
		if (this._count * 20 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
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
			this._itemQ[i] = this.readQ();
			this._itemP[i] = this.readQ();
			if (this._itemQ[i] < 1 || this._itemP[i] < 0 || ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player seller = this.getClient().getActiveChar();
		if (seller == null || this._count == 0)
		{
			return;
		}

		if (!TradeHelper.checksIfCanOpenStore(seller, this._package ? Player.STORE_PRIVATE_SELL_PACKAGE : Player.STORE_PRIVATE_SELL) || !Config.ALLOW_PRIVATE_STORES)
		{
			seller.sendActionFailed();
			return;
		}

		TradeItem temp;
		List<TradeItem> sellList = new CopyOnWriteArrayList<TradeItem>();

		seller.getInventory().writeLock();
		try
		{
			for (int i = 0; i < this._count; i++)
			{
				final int objectId = this._items[i];
				final long count = this._itemQ[i];
				final long price = this._itemP[i];
				final ItemInstance item = seller.getInventory().getItemByObjectId(objectId);

				if (item == null || item.getCount() < count || !item.canBeTraded(seller) || item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
				{
					continue;
				}

				temp = new TradeItem(item);
				temp.setCount(count);
				temp.setOwnersPrice(price);
				sellList.add(temp);
			}
		}
		finally
		{
			seller.getInventory().writeUnlock();
		}

		if (sellList.size() > seller.getTradeLimit())
		{
			seller.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			seller.sendPacket(new PrivateStoreManageListSell(seller, this._package));
			return;
		}

		if (!sellList.isEmpty())
		{
			seller.setSellList(this._package, sellList);
			seller.saveTradeList();
			seller.setPrivateStoreType(this._package ? Player.STORE_PRIVATE_SELL_PACKAGE : Player.STORE_PRIVATE_SELL);
			seller.broadcastPacket(new PrivateStoreMsgSell(seller));
			seller.sitDown(null);
			seller.broadcastCharInfo();
			Log.logPrivateStoreMessage(seller, seller.getSellStoreName());

			if (Config.AUCTION_PRIVATE_STORE_AUTO_ADDED && !this._package)
			{
				for (TradeItem ti : sellList)
				{
					ItemInstance item = seller.getInventory().getItemByObjectId(ti.getObjectId());
					// Synerge - Dont add potions to the auction house
					if ((item == null) || (item.getItemType() == EtcItemType.POTION))
					{
						continue;
					}

					Auction auc = AuctionManager.getInstance().addNewStore(seller, item, ti.getOwnersPrice(), ti.getCount());
					ti.setAuctionId(auc.getAuctionId());
				}
			}
		}

		seller.sendActionFailed();
	}
}
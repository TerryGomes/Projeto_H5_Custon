package l2mv.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.TradeHelper;

public class RequestPrivateStoreBuy extends L2GameClientPacket
{
	private int _sellerId;
	private int _count;
	private int[] _items; // object id
	private long[] _itemQ; // count
	private long[] _itemP; // price

	@Override
	protected void readImpl()
	{
		this._sellerId = this.readD();
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

			if (this._itemQ[i] < 1 || this._itemP[i] < 1 || ArrayUtils.indexOf(this._items, this._items[i]) < i)
			{
				this._count = 0;
				break;
			}
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

		if (buyer.isActionsDisabled() || buyer.isBlocked() || !Config.ALLOW_PRIVATE_STORES)
		{
			buyer.sendActionFailed();
			return;
		}

		if (buyer.isInStoreMode())
		{
			buyer.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (buyer.isInTrade())
		{
			buyer.sendActionFailed();
			return;
		}

		if (buyer.isFishing())
		{
			buyer.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if (!buyer.getPlayerAccess().UseTrade)
		{
			buyer.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return;
		}

		Player seller = (Player) buyer.getVisibleObject(this._sellerId);
		if (seller == null || seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE || !seller.isInRangeZ(buyer, Creature.INTERACTION_DISTANCE))
		{
			buyer.sendPacket(SystemMsg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
			buyer.sendActionFailed();
			return;
		}

		TradeHelper.buyFromStore(seller, buyer, this._count, this._items, this._itemQ, this._itemP);
	}
}
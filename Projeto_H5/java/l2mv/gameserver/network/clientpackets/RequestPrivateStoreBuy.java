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
		_sellerId = readD();
		_count = readD();
		if (_count * 20 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];
		_itemP = new long[_count];

		for (int i = 0; i < _count; i++)
		{
			_items[i] = readD();
			_itemQ[i] = readQ();
			_itemP[i] = readQ();

			if (_itemQ[i] < 1 || _itemP[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player buyer = getClient().getActiveChar();
		if (buyer == null || _count == 0)
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

		Player seller = (Player) buyer.getVisibleObject(_sellerId);
		if (seller == null || seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE || !seller.isInRangeZ(buyer, Creature.INTERACTION_DISTANCE))
		{
			buyer.sendPacket(SystemMsg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
			buyer.sendActionFailed();
			return;
		}

		TradeHelper.buyFromStore(seller, buyer, _count, _items, _itemQ, _itemP);
	}
}
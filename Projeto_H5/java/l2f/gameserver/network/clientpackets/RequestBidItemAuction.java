package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.itemauction.ItemAuction;
import l2f.gameserver.instancemanager.itemauction.ItemAuctionInstance;
import l2f.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;

/**
 * @author n0nam3
 */
public final class RequestBidItemAuction extends L2GameClientPacket
{
	private int _instanceId;
	private long _bid;

	@Override
	protected final void readImpl()
	{
		_instanceId = readD();
		_bid = readQ();
	}

	@Override
	protected final void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		final ItemInstance adena = activeChar.getInventory().getItemByItemId(57);
		if (adena == null || _bid < 0 || _bid > adena.getCount())
		{
			return;
		}

		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		final NpcInstance broker = activeChar.getLastNpc();
		if (broker == null || broker.getNpcId() != _instanceId || activeChar.getDistance(broker.getX(), broker.getY()) > Creature.INTERACTION_DISTANCE)
		{
			return;
		}

		if (instance != null)
		{
			final ItemAuction auction = instance.getCurrentAuction();
			if (auction != null)
			{
				auction.registerBid(activeChar, _bid);
			}
		}
	}
}
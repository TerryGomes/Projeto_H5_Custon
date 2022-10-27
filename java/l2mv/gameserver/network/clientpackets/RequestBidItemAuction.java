package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.itemauction.ItemAuction;
import l2mv.gameserver.instancemanager.itemauction.ItemAuctionInstance;
import l2mv.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;

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
		this._instanceId = this.readD();
		this._bid = this.readQ();
	}

	@Override
	protected final void runImpl()
	{
		final Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		final ItemInstance adena = activeChar.getInventory().getItemByItemId(57);
		if (adena == null || this._bid < 0 || this._bid > adena.getCount())
		{
			return;
		}

		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(this._instanceId);
		final NpcInstance broker = activeChar.getLastNpc();
		if (broker == null || broker.getNpcId() != this._instanceId || activeChar.getDistance(broker.getX(), broker.getY()) > Creature.INTERACTION_DISTANCE)
		{
			return;
		}

		if (instance != null)
		{
			final ItemAuction auction = instance.getCurrentAuction();
			if (auction != null)
			{
				auction.registerBid(activeChar, this._bid);
			}
		}
	}
}
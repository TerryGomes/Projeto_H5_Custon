package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.instancemanager.itemauction.ItemAuction;
import l2mv.gameserver.instancemanager.itemauction.ItemAuctionBid;
import l2mv.gameserver.instancemanager.itemauction.ItemAuctionState;

/**
 * @author n0nam3
 */
public class ExItemAuctionInfo extends L2GameServerPacket
{
	private boolean _refresh;
	private int _timeRemaining;
	private ItemAuction _currentAuction;
	private ItemAuction _nextAuction;

	public ExItemAuctionInfo(boolean refresh, ItemAuction currentAuction, ItemAuction nextAuction)
	{
		if (currentAuction == null)
		{
			throw new NullPointerException();
		}

		if (currentAuction.getAuctionState() != ItemAuctionState.STARTED)
		{
			this._timeRemaining = 0;
		}
		else
		{
			this._timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds
		}

		this._refresh = refresh;
		this._currentAuction = currentAuction;
		this._nextAuction = nextAuction;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x68);
		this.writeC(this._refresh ? 0x00 : 0x01);
		this.writeD(this._currentAuction.getInstanceId());

		ItemAuctionBid highestBid = this._currentAuction.getHighestBid();
		this.writeQ(highestBid != null ? highestBid.getLastBid() : this._currentAuction.getAuctionInitBid());

		this.writeD(this._timeRemaining);
		this.writeItemInfo(this._currentAuction.getAuctionItem());

		if (this._nextAuction != null)
		{
			this.writeQ(this._nextAuction.getAuctionInitBid());
			this.writeD((int) (this._nextAuction.getStartingTime() / 1000L)); // unix time in seconds
			this.writeItemInfo(this._nextAuction.getAuctionItem());
		}
	}
}
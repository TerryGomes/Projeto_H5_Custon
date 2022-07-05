package l2f.gameserver.instancemanager.itemauction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class ItemAuction
{
	private static Logger _log = LoggerFactory.getLogger(ItemAuction.class);

	private static long ENDING_TIME_EXTEND_5 = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
	private static long ENDING_TIME_EXTEND_8 = TimeUnit.MILLISECONDS.convert(8, TimeUnit.MINUTES);

	private int _auctionId;
	private int _instanceId;
	private long _startingTime;
	private long _endingTime;
	private AuctionItem _auctionItem;
	private TIntObjectHashMap<ItemAuctionBid> _auctionBids;

	private ItemAuctionState _auctionState;
	private int _scheduledAuctionEndingExtendState;
	private int _auctionEndingExtendState;

	private ItemAuctionBid _highestBid;
	private int _lastBidPlayerObjId;

	public ItemAuction(int auctionId, int instanceId, long startingTime, long endingTime, AuctionItem auctionItem, ItemAuctionState auctionState)
	{
		_auctionId = auctionId;
		_instanceId = instanceId;
		_startingTime = startingTime;
		_endingTime = endingTime;
		_auctionItem = auctionItem;
		_auctionBids = new TIntObjectHashMap<ItemAuctionBid>();
		_auctionState = auctionState;
	}

	void addBid(ItemAuctionBid bid)
	{
		_auctionBids.put(bid.getCharId(), bid);
		if (_highestBid == null || _highestBid.getLastBid() < bid.getLastBid())
		{
			_highestBid = bid;
		}
	}

	public ItemAuctionState getAuctionState()
	{
		return _auctionState;
	}

	public synchronized boolean setAuctionState(ItemAuctionState expected, ItemAuctionState wanted)
	{
		if (_auctionState != expected)
		{
			return false;
		}

		_auctionState = wanted;

		store();

		return true;
	}

	public int getAuctionId()
	{
		return _auctionId;
	}

	public int getInstanceId()
	{
		return _instanceId;
	}

	public AuctionItem getAuctionItem()
	{
		return _auctionItem;
	}

	public ItemInstance createNewItemInstance()
	{
		return _auctionItem.createNewItemInstance();
	}

	public long getAuctionInitBid()
	{
		return _auctionItem.getAuctionInitBid();
	}

	public ItemAuctionBid getHighestBid()
	{
		return _highestBid;
	}

	public int getAuctionEndingExtendState()
	{
		return _auctionEndingExtendState;
	}

	public int getScheduledAuctionEndingExtendState()
	{
		return _scheduledAuctionEndingExtendState;
	}

	public void setScheduledAuctionEndingExtendState(int state)
	{
		_scheduledAuctionEndingExtendState = state;
	}

	public long getStartingTime()
	{
		return _startingTime;
	}

	public long getEndingTime()
	{
		if (_auctionEndingExtendState == 0)
		{
			return _endingTime;
		}
		else if (_auctionEndingExtendState == 1)
		{
			return _endingTime + ENDING_TIME_EXTEND_5;
		}
		else
		{
			return _endingTime + ENDING_TIME_EXTEND_8;
		}
	}

	public long getStartingTimeRemaining()
	{
		return Math.max(getEndingTime() - System.currentTimeMillis(), 0L);
	}

	public long getFinishingTimeRemaining()
	{
		return Math.max(getEndingTime() - System.currentTimeMillis(), 0L);
	}

	public void store()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO item_auction (auctionId,instanceId,auctionItemId,startingTime,endingTime,auctionStateId) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE auctionStateId=?");
			statement.setInt(1, _auctionId);
			statement.setInt(2, _instanceId);
			statement.setInt(3, _auctionItem.getAuctionItemId());
			statement.setLong(4, _startingTime);
			statement.setLong(5, _endingTime);
			statement.setInt(6, _auctionState.ordinal());
			statement.setInt(7, _auctionState.ordinal());
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("Error while Storing Item Auction! ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public int getAndSetLastBidPlayerObjectId(int playerObjId)
	{
		int lastBid = _lastBidPlayerObjId;
		_lastBidPlayerObjId = playerObjId;
		return lastBid;
	}

	public void updatePlayerBid(ItemAuctionBid bid, boolean delete)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			if (delete)
			{
				statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=? AND playerObjId=?");
				statement.setInt(1, _auctionId);
				statement.setInt(2, bid.getCharId());
			}
			else
			{
				statement = con.prepareStatement("INSERT INTO item_auction_bid (auctionId,playerObjId,playerBid) VALUES (?,?,?) ON DUPLICATE KEY UPDATE playerBid=?");
				statement.setInt(1, _auctionId);
				statement.setInt(2, bid.getCharId());
				statement.setLong(3, bid.getLastBid());
				statement.setLong(4, bid.getLastBid());
			}

			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("Error while Updating Player Bid! ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void registerBid(Player player, long newBid)
	{
		if (player == null)
		{
			throw new NullPointerException();
		}

		if (newBid < getAuctionInitBid())
		{
			player.sendPacket(new SystemMessage2(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID));
			return;
		}

		if (newBid > Config.ALT_ITEM_AUCTION_MAX_BID)
		{
			if (Config.ALT_ITEM_AUCTION_MAX_BID == 100000000000L)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.BIDDING_IS_NOT_ALLOWED_BECAUSE_THE_MAXIMUM_BIDDING_PRICE_EXCEEDS_100_BILLION));
			}
			else
			{
				player.sendMessage("Your bid cannot exceed " + Config.ALT_ITEM_AUCTION_MAX_BID);
			}
			return;
		}

		if (getAuctionState() != ItemAuctionState.STARTED)
		{
			return;
		}

		int charId = player.getObjectId();

		synchronized (_auctionBids)
		{
			if (_highestBid != null && newBid < _highestBid.getLastBid())
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID));
				return;
			}

			ItemAuctionBid bid = getBidFor(charId);

			if (bid == null)
			{
				if (!reduceItemCount(player, newBid))
				{
					return;
				}

				bid = new ItemAuctionBid(charId, newBid);
				_auctionBids.put(charId, bid);

				onPlayerBid(player, bid);
				updatePlayerBid(bid, false);

				player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addInteger(newBid));
				return;
			}

			if (!Config.ALT_ITEM_AUCTION_CAN_REBID)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME));
				return;
			}

			if (bid.getLastBid() >= newBid)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID));
				return;
			}

			if (!reduceItemCount(player, newBid - bid.getLastBid()))
			{
				return;
			}

			bid.setLastBid(newBid);
			onPlayerBid(player, bid);
			updatePlayerBid(bid, false);

			player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1).addInteger(newBid));
		}
	}

	private void onPlayerBid(Player player, ItemAuctionBid bid)
	{
		if (_highestBid == null)
		{
			_highestBid = bid;
		}
		else if (_highestBid.getLastBid() < bid.getLastBid())
		{
			Player old = _highestBid.getPlayer();
			if (old != null)
			{
				old.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_BEEN_OUTBID));
			}

			_highestBid = bid;
		}

		if (getEndingTime() - System.currentTimeMillis() <= 1000 * 60 * 10)
		{
			if (_auctionEndingExtendState == 0)
			{
				_auctionEndingExtendState = 1;
				broadcastToAllBidders(new SystemMessage2(SystemMsg.BIDDER_EXISTS__THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES));
			}
			else if (_auctionEndingExtendState == 1 && getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId())
			{
				_auctionEndingExtendState = 2;
				broadcastToAllBidders(new SystemMessage2(SystemMsg.BIDDER_EXISTS__AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES));
			}
		}
	}

	public void broadcastToAllBidders(L2GameServerPacket packet)
	{
		TIntObjectIterator<ItemAuctionBid> itr = _auctionBids.iterator();
		ItemAuctionBid bid = null;
		while (itr.hasNext())
		{
			itr.advance();
			bid = itr.value();
			Player player = bid.getPlayer();
			if (player != null)
			{
				player.sendPacket(packet);
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void cancelBid(Player player)
	{
		if (player == null)
		{
			throw new NullPointerException();
		}

		switch (getAuctionState())
		{
		case CREATED:
			player.sendPacket(new SystemMessage2(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
			return;

		case FINISHED:
			if (_startingTime < System.currentTimeMillis() - Config.ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
				return;
			}
			else
			{
				break;
			}
		}

		int charId = player.getObjectId();

		synchronized (_auctionBids)
		{
			// _highestBid == null logical consequence is that no one bet yet
			if (_highestBid == null)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
				return;
			}

			ItemAuctionBid bid = getBidFor(charId);
			if (bid == null || bid.isCanceled())
			{
				player.sendPacket(new SystemMessage2(SystemMsg.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
				return;
			}

			if (bid.getCharId() == _highestBid.getCharId())
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID));
				return;
			}

			increaseItemCount(player, bid.getLastBid());
			player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID));

			if (Config.ALT_ITEM_AUCTION_CAN_REBID)
			{
				_auctionBids.remove(charId);
				updatePlayerBid(bid, true);
			}
			else
			{
				bid.cancelBid();
				updatePlayerBid(bid, false);
			}
		}
	}

	private boolean reduceItemCount(Player player, long count)
	{
		if (Config.ALT_ITEM_AUCTION_BID_ITEM_ID == 57 || !getAuctionItem().isEquipped())
		{
			if (!player.reduceAdena(count, true, "Retail Item Auction"))
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID));
				return false;
			}

			return true;
		}

		return player.getInventory().destroyItemByItemId(Config.ALT_ITEM_AUCTION_BID_ITEM_ID, count, "Retail Item Auction");
	}

	private void increaseItemCount(Player player, long count)
	{
		if (Config.ALT_ITEM_AUCTION_BID_ITEM_ID == 57 || !getAuctionItem().isEquipped())
		{
			player.addAdena(count, "Retail Item Auction");
		}
		else
		{
			player.getInventory().addItem(Config.ALT_ITEM_AUCTION_BID_ITEM_ID, count, "Retail Item Auction");
		}

		player.sendPacket(SystemMessage2.obtainItems(Config.ALT_ITEM_AUCTION_BID_ITEM_ID, count, 0));
	}

	/**
	 * Returns the last bid for the given player or -1 if he did not made one yet.
	 *
	 * @param player The player that made the bid
	 * @return The last bid the player made or -1
	 */
	public long getLastBid(Player player)
	{
		ItemAuctionBid bid = getBidFor(player.getObjectId());
		return bid != null ? bid.getLastBid() : -1L;
	}

	public ItemAuctionBid getBidFor(int charId)
	{
		return _auctionBids.get(charId);
	}
}
package l2f.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.dao.ClanDataDAO;
import l2f.gameserver.dao.ClanHallDAO;
import l2f.gameserver.data.xml.holder.DoorHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.PlayerMessageStack;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.DoorTemplate;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.item.ItemTemplate;

public class ClanHall extends Residence
{
	private static final Logger _log = LoggerFactory.getLogger(ClanHall.class);

	private static final int REWARD_CYCLE = 168; // 1 week - 7 days - 168 hours

	private int _auctionLength;
	private long _auctionMinBid;
	private String _auctionDescription = StringUtils.EMPTY;

	private final int _grade;
	private final long _rentalFee;
	private final long _minBid;
	private final long _deposit;

	public ClanHall(StatsSet set)
	{
		super(set);
		_grade = set.getInteger("grade", 0);
		_rentalFee = set.getInteger("rental_fee", 0);
		_minBid = set.getInteger("min_bid", 0);
		_deposit = set.getInteger("deposit", 0);
	}

	@Override
	public void init()
	{
		initZone();
		initEvent();

		// Synerge - Add a listener to get zone enter events
		// getZone().addListener(new ZoneListener());

		loadData();
		loadFunctions();
		rewardSkills();

		// если это Аукционный КХ, и есть овнер, и КХ, непродается
		if ((getSiegeEvent().getClass() == ClanHallAuctionEvent.class) && (_owner != null) && (getAuctionLength() == 0))
		{
			startCycleTask();
		}
	}

	@Override
	public void changeOwner(Clan clan)
	{
		final Clan oldOwner = getOwner();
		if (oldOwner != null && (clan == null || clan.getClanId() != oldOwner.getClanId()))
		{
			removeSkills();
			oldOwner.setHasHideout(0);

			cancelCycleTask();
		}

		updateOwnerInDB(clan);
		rewardSkills();

		update();

		if ((clan == null) && (getSiegeEvent().getClass() == ClanHallAuctionEvent.class))
		{
			getSiegeEvent().reCalcNextTime(false);
		}
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.ClanHall;
	}

	@Override
	protected void loadData()
	{
		_owner = ClanDataDAO.getInstance().getOwner(this);

		ClanHallDAO.getInstance().select(this);
	}

	private void updateOwnerInDB(Clan clan)
	{
		_owner = clan;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET hasHideout=0 WHERE hasHideout=?");
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE clan_data SET hasHideout=? WHERE clan_id=?");
			statement.setInt(1, getId());
			statement.setInt(2, getOwnerId());
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=?");
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);

			// Announce to clan memebers
			if (clan != null)
			{
				clan.setHasHideout(getId()); // Set has hideout flag for new owner
				clan.broadcastClanStatus(false, true, false);
			}
		}
		catch (Exception e)
		{
			_log.warn("Exception: updateOwnerInDB(L2Clan clan): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public int getGrade()
	{
		return _grade;
	}

	@Override
	public void update()
	{
		ClanHallDAO.getInstance().update(this);
	}

	public int getAuctionLength()
	{
		return _auctionLength;
	}

	public void setAuctionLength(int auctionLength)
	{
		_auctionLength = auctionLength;
	}

	public String getAuctionDescription()
	{
		return _auctionDescription;
	}

	public void setAuctionDescription(String auctionDescription)
	{
		_auctionDescription = auctionDescription == null ? StringUtils.EMPTY : auctionDescription;
	}

	public long getAuctionMinBid()
	{
		return _auctionMinBid;
	}

	public void setAuctionMinBid(long auctionMinBid)
	{
		_auctionMinBid = auctionMinBid;
	}

	public long getRentalFee()
	{
		return _rentalFee;
	}

	public long getBaseMinBid()
	{
		return _minBid;
	}

	public long getDeposit()
	{
		return _deposit;
	}

	@Override
	public void chanceCycle()
	{
		super.chanceCycle();

		setPaidCycle(getPaidCycle() + 1);
		if (getPaidCycle() >= REWARD_CYCLE)
		{
			if (_owner.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) > _rentalFee)
			{
				_owner.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, _rentalFee, "Clan Hall Cycle");
				setPaidCycle(0);
			}
			else
			{
				final UnitMember member = _owner.getLeader();
				if (member.isOnline())
				{
					member.getPlayer().sendPacket(SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
				}
				else
				{
					PlayerMessageStack.getInstance().mailto(member.getObjectId(), SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED.packet(null));
				}

				changeOwner(null);
			}
		}
	}

	/**
	 * Synerge
	 * @return Returns true if all clan hall doors are closed
	 */
	private final boolean isDoorsClosed()
	{
		for (DoorTemplate door : DoorHolder.getInstance().getDoors().values())
		{
			if (door != null && door.getAIParams().getInteger("residence_id", 0) == getId() && door.isOpened())
			{
				return false;
			}
		}

		return true;
	}

	// Synerge - Listener to control players that enter the clan hall, when doors are closed
	private class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			// No gms
			if (!actor.isPlayer() || (actor.getAccessLevel() > 0))
			{
				return;
			}

			Player player = actor.getPlayer();

			// Clan Hall owner
			// Doors opened
			if ((player.getClanId() == getOwnerId()) || !isDoorsClosed())
			{
				return;
			}

			player.teleToLocation(getBanishPoint());
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}
}
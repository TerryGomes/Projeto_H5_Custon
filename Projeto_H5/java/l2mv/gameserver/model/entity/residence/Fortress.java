package l2mv.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.dao.ClanDataDAO;
import l2mv.gameserver.dao.FortressDAO;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.ItemTemplate;

public class Fortress extends Residence
{
	private static final Logger _log = LoggerFactory.getLogger(Fortress.class);

	private static final long REMOVE_CYCLE = 7 * 24; // 7 Fort days may belong owneru
	private static final long REWARD_CYCLE = 6; // every 6 hours

	public static final long CASTLE_FEE = 25000;
	// type
	public static final int DOMAIN = 0;
	public static final int BOUNDARY = 1;
	// state
	public static final int NOT_DECIDED = 0;
	public static final int INDEPENDENT = 1;
	public static final int CONTRACT_WITH_CASTLE = 2;
	// facility
	public static final int REINFORCE = 0;
	public static final int GUARD_BUFF = 1;
	public static final int DOOR_UPGRADE = 2;
	public static final int DWARVENS = 3;
	public static final int SCOUT = 4;

	public static final int FACILITY_MAX = 5;
	private final int[] _facilities = new int[FACILITY_MAX];
	// envoy
	private int _state;
	private int _castleId;

	private int _supplyCount;
	private long _supplySpawn;

	private final List<Castle> _relatedCastles = new ArrayList<Castle>(5);

	public Fortress(StatsSet set)
	{
		super(set);
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.Fortress;
	}

	@Override
	public void changeOwner(Clan clan)
	{
		// If a clan is owned by a castle / fortress, we select it.
		if (clan != null)
		{
			if (clan.getHasFortress() != 0)
			{
				final Fortress oldFortress = ResidenceHolder.getInstance().getResidence(Fortress.class, clan.getHasFortress());
				if (oldFortress != null)
				{
					oldFortress.changeOwner(null);
				}
			}
			if (clan.getCastle() != 0)
			{
				final Castle oldCastle = ResidenceHolder.getInstance().getResidence(Castle.class, clan.getCastle());
				if (oldCastle != null)
				{
					oldCastle.changeOwner(null);
				}
			}
		}

		// If this fortress is someone captured, it takes away from the fortress
		if (getOwnerId() > 0 && (clan == null || clan.getClanId() != getOwnerId()))
		{
			// Remove Fortress skills with the old owner
			removeSkills();
			final Clan oldOwner = getOwner();
			if (oldOwner != null)
			{
				oldOwner.setHasFortress(0);
			}

			cancelCycleTask();
			clearFacility();
		}

		// We provide the new owner of the fortress
		if (clan != null)
		{
			clan.setHasFortress(getId());
		}

		// Save to base
		updateOwnerInDB(clan);

		// We provide Fortress skills to a new owner
		rewardSkills();

		setFortState(NOT_DECIDED, 0);
		setJdbcState(JdbcEntityState.UPDATED);

		update();

		if (clan != null)
		{
			clan.getAllMembers().stream().filter(plr -> plr.isOnline()).forEach(plr -> plr.getPlayer().getCounters().fortSiegesWon++);
		}
	}

	@Override
	protected void loadData()
	{
		_owner = ClanDataDAO.getInstance().getOwner(this);
		FortressDAO.getInstance().select(this);
	}

	private void updateOwnerInDB(Clan clan)
	{
		_owner = clan;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET hasFortress=0 WHERE hasFortress=? LIMIT 1");
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);

			if (clan != null)
			{
				statement = con.prepareStatement("UPDATE clan_data SET hasFortress=? WHERE clan_id=? LIMIT 1");
				statement.setInt(1, getId());
				statement.setInt(2, getOwnerId());
				statement.execute();

				clan.broadcastClanStatus(true, false, false);
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Fortress Owner in Database", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void setFortState(int state, int castleId)
	{
		_state = state;
		_castleId = castleId;
	}

	public int getCastleId()
	{
		return _castleId;
	}

	public int getContractState()
	{
		return _state;
	}

	@Override
	public void chanceCycle()
	{
		super.chanceCycle();
		if (getCycle() >= REMOVE_CYCLE)
		{
			getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
			changeOwner(null);
			return;
		}

		setPaidCycle(getPaidCycle() + 1);
		// if we add a multiple REWARD_CYCLE Revard
		if (getPaidCycle() % REWARD_CYCLE == 0)
		{
			setPaidCycle(0);
			setRewardCount(getRewardCount() + 1);

			if (getContractState() == CONTRACT_WITH_CASTLE)
			{
				final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _castleId);
				if (castle.getOwner() == null || castle.getOwner().getReputationScore() < 2 || _owner.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) > CASTLE_FEE)
				{
					setSupplyCount(0);
					setFortState(INDEPENDENT, 0);
					clearFacility();
				}
				else if (_supplyCount < 6)
				{
					castle.getOwner().incReputation(-2, false, "Fortress:chanceCycle():" + getId());
					_owner.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, CASTLE_FEE, "Fortress Cycle");
					_supplyCount++;
				}
			}
		}
	}

	@Override
	public void update()
	{
		FortressDAO.getInstance().update(this);
	}

	public int getSupplyCount()
	{
		return _supplyCount;
	}

	public void setSupplyCount(int c)
	{
		_supplyCount = c;
	}

	public int getFacilityLevel(int type)
	{
		return _facilities[type];
	}

	public void setFacilityLevel(int type, int val)
	{
		_facilities[type] = val;
	}

	public void clearFacility()
	{
		for (int i = 0; i < _facilities.length; i++)
		{
			_facilities[i] = 0;
		}
	}

	public int[] getFacilities()
	{
		return _facilities;
	}

	public void addRelatedCastle(Castle castle)
	{
		_relatedCastles.add(castle);
	}

	public List<Castle> getRelatedCastles()
	{
		return _relatedCastles;
	}

	public long getSupplySpawn()
	{
		return _supplySpawn;
	}

	public void setSupplySpawn(long val)
	{
		_supplySpawn = val;
	}
}

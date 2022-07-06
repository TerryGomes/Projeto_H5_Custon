package l2mv.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntity;
import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

@SuppressWarnings("rawtypes")
public abstract class Residence implements JdbcEntity
{
	public class ResidenceCycleTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			chanceCycle();
			update();
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Residence.class);
	public static final long CYCLE_TIME = 60 * 60 * 1000L; // 1 time

	protected final int _id;
	protected final String _name;

	protected Clan _owner;
	protected Zone _zone;

	protected List<ResidenceFunction> _functions = new ArrayList<ResidenceFunction>();
	protected List<Skill> _skills = new ArrayList<Skill>();

	protected SiegeEvent<?, ?> _siegeEvent;

	protected Calendar _siegeDate = Calendar.getInstance();
	protected Calendar _lastSiegeDate = Calendar.getInstance();
	protected Calendar _ownDate = Calendar.getInstance();

	// rewards
	protected ScheduledFuture<?> _cycleTask;
	private int _cycle;
	private int _rewardCount;
	private int _paidCycle;

	protected JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;

	// points
	protected List<Location> _banishPoints = new ArrayList<Location>();
	protected List<Location> _ownerRestartPoints = new ArrayList<Location>();
	protected List<Location> _otherRestartPoints = new ArrayList<Location>();
	protected List<Location> _chaosRestartPoints = new ArrayList<Location>();

	public Residence(StatsSet set)
	{
		_id = set.getInteger("id");
		_name = set.getString("name");
	}

	public abstract ResidenceType getType();

	public void init()
	{
		initZone();
		initEvent();

		loadData();
		loadFunctions();
		rewardSkills();
		startCycleTask();
	}

	protected void initZone()
	{
		_zone = ReflectionUtils.getZone("residence_" + _id);
		_zone.setParam("residence", this);
	}

	protected void initEvent()
	{
		_siegeEvent = EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, _id);
	}

	@SuppressWarnings("unchecked")
	public <E extends SiegeEvent> E getSiegeEvent()
	{
		return (E) _siegeEvent;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		if (getId() > 80 && getId() < 90)
		{
			return _name + " Dominion";
		}
		return _name;
	}

	public int getOwnerId()
	{
		return _owner == null ? 0 : _owner.getClanId();
	}

	public Clan getOwner()
	{
		return _owner;
	}

	public Zone getZone()
	{
		return _zone;
	}

	protected abstract void loadData();

	public abstract void changeOwner(Clan clan);

	public Calendar getOwnDate()
	{
		return _ownDate;
	}

	public Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	public Calendar getLastSiegeDate()
	{
		return _lastSiegeDate;
	}

	public void addSkill(Skill skill)
	{
		_skills.add(skill);
	}

	public void removeSkill(Skill skill)
	{
		_skills.remove(skill.getId());
	}

	public void addFunction(ResidenceFunction function)
	{
		_functions.add(function);
	}

	public boolean checkIfInZone(Location loc, Reflection ref)
	{
		return checkIfInZone(loc.x, loc.y, loc.z, ref);
	}

	public boolean checkIfInZone(int x, int y, int z, Reflection ref)
	{
		return getZone() != null && getZone().checkIfInZone(x, y, z, ref);
	}

	public void banishForeigner()
	{
		for (Player player : _zone.getInsidePlayers())
		{
			if (player.getClanId() == getOwnerId())
			{
				continue;
			}

			player.teleToLocation(getBanishPoint());
		}
	}

	/**
	 * Gets the clan that owns the residence skills
	 */
	public void rewardSkills()
	{
		final Clan owner = getOwner();
		if (owner != null)
		{
			for (Skill skill : _skills)
			{
				owner.addSkill(skill, false);
				owner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
			}
		}
	}

	/**
	 * Removes the clan that owns the residence skills
	 */
	public void removeSkills()
	{
		final Clan owner = getOwner();
		if (owner != null)
		{
			for (Skill skill : _skills)
			{
				owner.removeSkill(skill.getId());
			}
		}
	}

	protected void loadFunctions()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM residence_functions WHERE id = ?");
			statement.setInt(1, getId());
			rs = statement.executeQuery();
			while (rs.next())
			{
				final ResidenceFunction function = getFunction(rs.getInt("type"));
				function.setLvl(rs.getInt("lvl"));
				function.setEndTimeInMillis(rs.getInt("endTime") * 1000L);
				function.setInDebt(rs.getBoolean("inDebt"));
				function.setActive(true);
				startAutoTaskForFunction(function);
			}
		}
		catch (SQLException e)
		{
			_log.warn("Residence: loadFunctions()", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rs);
		}
	}

	public boolean isFunctionActive(int type)
	{
		final ResidenceFunction function = getFunction(type);
		if (function != null && function.isActive() && function.getLevel() > 0)
		{
			return true;
		}
		return false;
	}

	public ResidenceFunction getFunction(int type)
	{
		for (int i = 0; i < _functions.size(); i++)
		{
			if (_functions.get(i).getType() == type)
			{
				return _functions.get(i);
			}
		}
		return null;
	}

	public boolean updateFunctions(int type, int level)
	{
		final Clan clan = getOwner();
		if (clan == null)
		{
			return false;
		}

		long count = clan.getAdenaCount();

		ResidenceFunction function = getFunction(type);
		if (function == null)
		{
			return false;
		}

		if (function.isActive() && function.getLevel() == level)
		{
			return true;
		}

		int lease = level == 0 ? 0 : getFunction(type).getLease(level);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			if (!function.isActive())
			{
				if (count >= lease)
				{
					clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease, "Residence updateFunctions");
				}
				else
				{
					return false;
				}

				long time = Calendar.getInstance().getTimeInMillis() + 86400000;

				statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?, endTime=?");
				statement.setInt(1, getId());
				statement.setInt(2, type);
				statement.setInt(3, level);
				statement.setInt(4, (int) (time / 1000));
				statement.execute();

				function.setLvl(level);
				function.setEndTimeInMillis(time);
				function.setActive(true);
				startAutoTaskForFunction(function);
			}
			else
			{
				if (count >= lease - getFunction(type).getLease())
				{
					if (lease > getFunction(type).getLease())
					{
						clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease - getFunction(type).getLease(), "Residence updateFunctions");
					}
				}
				else
				{
					return false;
				}

				statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?");
				statement.setInt(1, getId());
				statement.setInt(2, type);
				statement.setInt(3, level);
				statement.execute();

				function.setLvl(level);
			}
		}
		catch (SQLException e)
		{
			_log.warn("Exception: SiegeUnit.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public void removeFunction(int type)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=? AND type=?");
			statement.setInt(1, getId());
			statement.setInt(2, type);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("Exception: removeFunctions(int type): ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private void startAutoTaskForFunction(ResidenceFunction function)
	{
		if (getOwnerId() == 0)
		{
			return;
		}

		Clan clan = getOwner();

		if (clan == null)
		{
			return;
		}

		if (function.getEndTimeInMillis() > System.currentTimeMillis())
		{
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		}
		else if (function.isInDebt() && clan.getAdenaCount() >= function.getLease()) // if player didn't pay before add extra fee
		{
			clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, function.getLease(), "Residence Functions Auto Task");
			function.updateRentTime(false);
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		}
		else if (!function.isInDebt())
		{
			function.setInDebt(true);
			function.updateRentTime(true);
			ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
		}
		else
		{
			function.setLvl(0);
			function.setActive(false);
			removeFunction(function.getType());
		}
	}

	private class AutoTaskForFunctions extends RunnableImpl
	{
		ResidenceFunction _function;

		public AutoTaskForFunctions(ResidenceFunction function)
		{
			_function = function;
		}

		@Override
		public void runImpl() throws Exception
		{
			startAutoTaskForFunction(_function);
		}
	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_jdbcEntityState = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _jdbcEntityState;
	}

	@Override
	public void save()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete()
	{
		throw new UnsupportedOperationException();
	}

	public void cancelCycleTask()
	{
		_cycle = 0;
		_paidCycle = 0;
		_rewardCount = 0;
		if (_cycleTask != null)
		{
			_cycleTask.cancel(false);
			_cycleTask = null;
		}

		setJdbcState(JdbcEntityState.UPDATED);
	}

	public void startCycleTask()
	{
		if (_owner == null)
		{
			return;
		}

		long ownedTime = getOwnDate().getTimeInMillis();
		if (ownedTime == 0)
		{
			return;
		}
		long diff = System.currentTimeMillis() - ownedTime;
		while (diff >= CYCLE_TIME)
		{
			diff -= CYCLE_TIME;
		}

		_cycleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ResidenceCycleTask(), diff, CYCLE_TIME);
	}

	public void chanceCycle()
	{
		setCycle(getCycle() + 1);

		setJdbcState(JdbcEntityState.UPDATED);
	}

	public List<Skill> getSkills()
	{
		return _skills;
	}

	public void addBanishPoint(Location loc)
	{
		_banishPoints.add(loc);
	}

	public void addOwnerRestartPoint(Location loc)
	{
		_ownerRestartPoints.add(loc);
	}

	public void addOtherRestartPoint(Location loc)
	{
		_otherRestartPoints.add(loc);
	}

	public void addChaosRestartPoint(Location loc)
	{
		_chaosRestartPoints.add(loc);
	}

	public Location getBanishPoint()
	{
		if (_banishPoints.isEmpty())
		{
			return null;
		}
		return _banishPoints.get(Rnd.get(_banishPoints.size()));
	}

	public Location getOwnerRestartPoint()
	{
		if (_ownerRestartPoints.isEmpty())
		{
			return null;
		}
		return _ownerRestartPoints.get(Rnd.get(_ownerRestartPoints.size()));
	}

	public Location getOtherRestartPoint()
	{
		if (_otherRestartPoints.isEmpty())
		{
			return null;
		}
		return _otherRestartPoints.get(Rnd.get(_otherRestartPoints.size()));
	}

	public Location getChaosRestartPoint()
	{
		if (_chaosRestartPoints.isEmpty())
		{
			return null;
		}
		return _chaosRestartPoints.get(Rnd.get(_chaosRestartPoints.size()));
	}

	public Location getNotOwnerRestartPoint(Player player)
	{
		return player.getKarma() > 0 ? getChaosRestartPoint() : getOtherRestartPoint();
	}

	public int getCycle()
	{
		return _cycle;
	}

	public long getCycleDelay()
	{
		if (_cycleTask == null)
		{
			return 0;
		}
		return _cycleTask.getDelay(TimeUnit.SECONDS);
	}

	public void setCycle(int cycle)
	{
		_cycle = cycle;
	}

	public int getPaidCycle()
	{
		return _paidCycle;
	}

	public void setPaidCycle(int paidCycle)
	{
		_paidCycle = paidCycle;
	}

	public int getRewardCount()
	{
		return _rewardCount;
	}

	public void setRewardCount(int rewardCount)
	{
		_rewardCount = rewardCount;
	}
}
package l2f.gameserver.model.pledge;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.collections.JoinedIterator;
import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.cache.CrestCache;
import l2f.gameserver.cache.Msg;
//import l2f.gameserver.data.ClanRequest;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.boat.ClanAirShip;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.entity.residence.ResidenceType;
import l2f.gameserver.model.items.ClanWarehouse;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.JoinPledge;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import l2f.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2f.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import l2f.gameserver.network.serverpackets.PledgeShowMemberListAll;
import l2f.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2f.gameserver.network.serverpackets.PledgeSkillList;
import l2f.gameserver.network.serverpackets.PledgeSkillListAdd;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Log;

public class Clan implements Iterable<UnitMember>
{
	private static final Logger _log = LoggerFactory.getLogger(Clan.class);

	private final int _clanId;

	private int _allyId;
	private int _level;

	private int _hasCastle;
	private int _hasFortress;
	private int _hasHideout;
	private int _warDominion;

	private int _crestId;
	private int _crestLargeId;

	private long _expelledMemberTime;
	private long _leavedAllyTime;
	private long _dissolvedAllyTime;
	private long _dissolvedClanTime;
	private long _disbandPenaltyTime;

	private ClanAirShip _airship;
	private boolean _airshipLicense;
	private int _airshipFuel;

	// all these in milliseconds
	public static long EXPELLED_MEMBER_PENALTY = Config.CLAN_LEAVE_PENALTY * 60 * 60 * 1000L;
	public static long LEAVED_ALLY_PENALTY = Config.ALLY_LEAVE_PENALTY * 60 * 60 * 1000L;
	public static long DISSOLVED_ALLY_PENALTY = Config.DISSOLVED_ALLY_PENALTY * 60 * 60 * 1000L;
	public static long DISBAND_TIME = Config.CLAN_DISBAND_TIME * 60 * 60 * 1000L;
	public static long DISBAND_PENALTY = Config.DISSOLVED_CLAN_PENALTY * 60 * 60 * 1000L;

	private ClanWarehouse _warehouse;
	private int _whBonus = -1;
	private String _notice = null;
	// private String _description = null;

	private final List<Clan> _atWarWith = new ArrayList<Clan>();
	private final List<Clan> _underAttackFrom = new ArrayList<Clan>();

	protected IntObjectMap<Skill> _skills = new CTreeIntObjectMap<Skill>();
	protected IntObjectMap<RankPrivs> _privs = new CTreeIntObjectMap<RankPrivs>();
	protected IntObjectMap<SubUnit> _subUnits = new CTreeIntObjectMap<SubUnit>();

	private int _reputation = 0;

	private int _siegeKills = 0;

	// Recruitment
	private boolean _recruting = false;
	private final ArrayList<Integer> _classesNeeded = new ArrayList<>();
	private String[] _questions = new String[8];
	private final ArrayList<SinglePetition> _petitions = new ArrayList<>();

	// Clan Privileges: system
	public static final int CP_NOTHING = 0;
	public static final int CP_CL_INVITE_CLAN = 2; // Join clan
	public static final int CP_CL_MANAGE_TITLES = 4; // Give a title
	public static final int CP_CL_WAREHOUSE_SEARCH = 8; // View warehouse content
	public static final int CP_CL_MANAGE_RANKS = 16; // manage clan ranks
	public static final int CP_CL_CLAN_WAR = 32;
	public static final int CP_CL_DISMISS = 64;
	public static final int CP_CL_EDIT_CREST = 128; // Edit clan crest
	public static final int CP_CL_APPRENTICE = 256;
	public static final int CP_CL_TROOPS_FAME = 512;
	public static final int CP_CL_SUMMON_AIRSHIP = 1024;

	// Clan Privileges: clan hall
	public static final int CP_CH_ENTRY_EXIT = 2048; // open a door
	public static final int CP_CH_USE_FUNCTIONS = 4096;
	public static final int CP_CH_AUCTION = 8192;
	public static final int CP_CH_DISMISS = 16384;
	public static final int CP_CH_SET_FUNCTIONS = 32768;

	// Clan Privileges: castle/fotress
	public static final int CP_CS_ENTRY_EXIT = 65536;
	public static final int CP_CS_MANOR_ADMIN = 131072;
	public static final int CP_CS_MANAGE_SIEGE = 262144;
	public static final int CP_CS_USE_FUNCTIONS = 524288;
	public static final int CP_CS_DISMISS = 1048576;
	public static final int CP_CS_TAXES = 2097152;
	public static final int CP_CS_MERCENARIES = 4194304;
	public static final int CP_CS_SET_FUNCTIONS = 8388606;
	public static final int CP_ALL = 16777214;

	public static final int RANK_FIRST = 1;
	public static final int RANK_LAST = 9;

	// Sub-unit types
	public static final int SUBUNIT_NONE = Byte.MIN_VALUE;
	public static final int SUBUNIT_ACADEMY = -1;
	public static final int SUBUNIT_MAIN_CLAN = 0;
	public static final int SUBUNIT_ROYAL1 = 100;
	public static final int SUBUNIT_ROYAL2 = 200;
	public static final int SUBUNIT_KNIGHT1 = 1001;
	public static final int SUBUNIT_KNIGHT2 = 1002;
	public static final int SUBUNIT_KNIGHT3 = 2001;
	public static final int SUBUNIT_KNIGHT4 = 2002;

	private final static ClanReputationComparator REPUTATION_COMPARATOR = new ClanReputationComparator();
	/** Number of places in the table of ranks clans */
	private final static int REPUTATION_PLACES = 100;

	/**
	 * The constructor is only used internally to restore a database
	 * @param clanId
	 */
	public Clan(int clanId)
	{
		_clanId = clanId;
		InitializePrivs();
		restoreCWH();
	}

	public void restoreCWH()
	{
		_warehouse = new ClanWarehouse(this);
		_warehouse.restore();
	}

	public int getClanId()
	{
		return _clanId;
	}

	public int getLeaderId()
	{
		return getLeaderId(SUBUNIT_MAIN_CLAN);
	}

	public UnitMember getLeader()
	{
		return getLeader(SUBUNIT_MAIN_CLAN);
	}

	public String getLeaderName()
	{
		return getLeaderName(SUBUNIT_MAIN_CLAN);
	}

	public String getName()
	{
		return getUnitName(SUBUNIT_MAIN_CLAN);
	}

	public void setExpelledMemberTime(long time)
	{
		_expelledMemberTime = time;
	}

	public long getExpelledMemberTime()
	{
		return _expelledMemberTime;
	}

	public void setExpelledMember()
	{
		_expelledMemberTime = System.currentTimeMillis();
		updateClanInDB();
	}

	public void setLeavedAllyTime(long time)
	{
		_leavedAllyTime = time;
	}

	public long getLeavedAllyTime()
	{
		return _leavedAllyTime;
	}

	public void setLeavedAlly()
	{
		_leavedAllyTime = System.currentTimeMillis();
		updateClanInDB();
	}

	public void setDissolvedAllyTime(long time)
	{
		_dissolvedAllyTime = time;
	}

	public long getDissolvedAllyTime()
	{
		return _dissolvedAllyTime;
	}

	public void setDissolvedAlly()
	{
		_dissolvedAllyTime = System.currentTimeMillis();
		updateClanInDB();
	}

	public UnitMember getAnyMember(int id)
	{
		for (SubUnit unit : getAllSubUnits())
		{
			UnitMember m = unit.getUnitMember(id);
			if (m != null)
			{
				return m;
			}
		}
		return null;
	}

	public UnitMember getAnyMember(String name)
	{
		for (SubUnit unit : getAllSubUnits())
		{
			UnitMember m = unit.getUnitMember(name);
			if (m != null)
			{
				return m;
			}
		}
		return null;
	}

	public int getAllSize()
	{
		int size = 0;

		for (SubUnit unit : getAllSubUnits())
		{
			size += unit.size();
		}

		return size;
	}

	public String getUnitName(int unitType)
	{
		if (unitType == SUBUNIT_NONE || !_subUnits.containsKey(unitType))
		{
			return StringUtils.EMPTY;
		}

		return getSubUnit(unitType).getName();
	}

	public String getLeaderName(int unitType)
	{
		if (unitType == SUBUNIT_NONE || !_subUnits.containsKey(unitType))
		{
			return StringUtils.EMPTY;
		}

		return getSubUnit(unitType).getLeaderName();
	}

	public int getLeaderId(int unitType)
	{
		if (unitType == SUBUNIT_NONE || !_subUnits.containsKey(unitType))
		{
			return 0;
		}

		return getSubUnit(unitType).getLeaderObjectId();
	}

	public UnitMember getLeader(int unitType)
	{
		if (unitType == SUBUNIT_NONE || !_subUnits.containsKey(unitType))
		{
			return null;
		}

		return getSubUnit(unitType).getLeader();
	}

	public void flush()
	{
		for (UnitMember member : this)
		{
			removeClanMember(member.getObjectId());
		}
		_warehouse.writeLock();
		try
		{
			for (ItemInstance item : _warehouse.getItems())
			{
				_warehouse.destroyItem(item, "Flush");
			}
		}
		finally
		{
			_warehouse.writeUnlock();
		}
		if (_hasCastle != 0)
		{
			ResidenceHolder.getInstance().getResidence(Castle.class, _hasCastle).changeOwner(null);
		}
		if (_hasFortress != 0)
		{
			ResidenceHolder.getInstance().getResidence(Fortress.class, _hasFortress).changeOwner(null);
		}
	}

	public void removeClanMember(int id)
	{
		if (id == getLeaderId(SUBUNIT_MAIN_CLAN))
		{
			return;
		}

		for (SubUnit unit : getAllSubUnits())
		{
			if (unit.isUnitMember(id))
			{
				removeClanMember(unit.getType(), id);
				break;
			}
		}
	}

	public void removeClanMember(int subUnitId, int objectId)
	{
		SubUnit subUnit = getSubUnit(subUnitId);
		if (subUnit == null)
		{
			return;
		}

		subUnit.removeUnitMember(objectId);
	}

	public List<UnitMember> getAllMembers()
	{
		Collection<SubUnit> units = getAllSubUnits();
		int size = 0;

		for (SubUnit unit : units)
		{
			size += unit.size();
		}
		List<UnitMember> members = new ArrayList<UnitMember>(size);

		for (SubUnit unit : units)
		{
			members.addAll(unit.getUnitMembers());
		}
		return members;
	}

	public List<Player> getOnlineMembers(int exclude)
	{
		if (getAllSize() == 0)
		{
			return new ArrayList<Player>();
		}
		final List<Player> result = new ArrayList<Player>(getAllSize() - 1);

		for (UnitMember temp : this)
		{
			if (temp != null && temp.isOnline() && temp.getObjectId() != exclude)
			{
				result.add(temp.getPlayer());
			}
		}

		return result;
	}

	public List<Player> getOnlineMembers()
	{
		final List<Player> result = new ArrayList<Player>();

		for (UnitMember temp : this)
		{
			if (temp != null && temp.isOnline())
			{
				result.add(temp.getPlayer());
			}
		}

		return result;
	}

	public List<Player> getOnlineMembers(boolean includeOfflineShops)
	{
		if (getAllSize() == 0)
		{
			return new ArrayList<Player>();
		}

		final List<Player> result = new ArrayList<>();
		for (UnitMember temp : this)
		{
			if (temp != null && temp.getPlayer() != null && ((includeOfflineShops && temp.getPlayer().isInStoreMode()) || temp.isOnline()))
			{
				result.add(temp.getPlayer());
			}
		}

		return result;
	}

	public int getAllyId()
	{
		return _allyId;
	}

	public int getLevel()
	{
		return _level;
	}

	/**
	 * Returns castle owned clan
	 * @return ID castle
	 */
	public int getCastle()
	{
		return _hasCastle;
	}

	/**
	 * Returns the strength, which is owned by the clan
	 * @return ID fortress
	 */
	public int getHasFortress()
	{
		return _hasFortress;
	}

	/**
	 * Returns clan hall owned by a clan
	 * @return ID clan hall
	 */
	public int getHasHideout()
	{
		return _hasHideout;
	}

	public int getResidenceId(ResidenceType r)
	{
		switch (r)
		{
		case Castle:
			return _hasCastle;
		case Fortress:
			return _hasFortress;
		case ClanHall:
			return _hasHideout;
		default:
			return 0;
		}
	}

	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}

	/**
	 * Sets the lock, which is owned by the clan. <BR>
	 * At the same time, and hold a castle and fortress can not be
	 * @param castle
	 */
	public void setHasCastle(int castle)
	{
		if (_hasFortress == 0)
		{
			_hasCastle = castle;
		}
	}

	/**
	 * Sets the fortress, which is owned by the clan. <BR>
	 * At the same time possess a fortress and castle can not be
	 * @param fortress
	 */
	public void setHasFortress(int fortress)
	{
		if (_hasCastle == 0)
		{
			_hasFortress = fortress;
		}
	}

	public void setHasHideout(int hasHideout)
	{
		_hasHideout = hasHideout;
	}

	public void setLevel(int level)
	{
		_level = level;
		// if (_level <= 1)
		// ClanRequest.updateList();
	}

	public boolean isAnyMember(int id)
	{
		for (SubUnit unit : getAllSubUnits())
		{
			if (unit.isUnitMember(id))
			{
				return true;
			}
		}
		return false;
	}

	public void updateClanInDB()
	{
		if (getLeaderId() == 0)
		{
			_log.warn("updateClanInDB with empty LeaderId");
			Thread.dumpStack();
			return;
		}

		if (getClanId() == 0)
		{
			_log.warn("updateClanInDB with empty ClanId");
			Thread.dumpStack();
			return;
		}
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(
						"UPDATE clan_data SET ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,airship=?,dissolved_clan=?,disband_penalty=? WHERE clan_id=?");
			statement.setInt(1, getAllyId());
			statement.setInt(2, getReputationScore());
			statement.setLong(3, getExpelledMemberTime() / 1000);
			statement.setLong(4, getLeavedAllyTime() / 1000);
			statement.setLong(5, getDissolvedAllyTime() / 1000);
			statement.setInt(6, _level);
			statement.setInt(7, getWhBonus());
			statement.setInt(8, isHaveAirshipLicense() ? getAirshipFuel() : -1);
			statement.setLong(9, getDissolvedClanTime() / 1000L);
			statement.setLong(10, getDisbandPenaltyTime() / 1000L);
			statement.setInt(11, getClanId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("error while updating clan '" + _clanId + "' data in db", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void store()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(
						"INSERT INTO clan_data (clan_id,clan_level,hasCastle,hasFortress,hasHideout,ally_id,expelled_member,leaved_ally,dissolved_ally,dissolved_clan,disband_penalty,airship) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, _clanId);
			statement.setInt(2, _level);
			statement.setInt(3, _hasCastle);
			statement.setInt(4, _hasFortress);
			statement.setInt(5, _hasHideout);
			statement.setInt(6, _allyId);
			statement.setLong(7, getExpelledMemberTime() / 1000);
			statement.setLong(8, getLeavedAllyTime() / 1000);
			statement.setLong(9, getDissolvedAllyTime() / 1000);
			statement.setLong(10, getDissolvedClanTime() / 1000);
			statement.setLong(11, getDisbandPenaltyTime() / 1000L);
			statement.setInt(12, isHaveAirshipLicense() ? getAirshipFuel() : -1);
			statement.execute();
			DbUtils.close(statement);

			SubUnit mainSubUnit = _subUnits.get(SUBUNIT_MAIN_CLAN);

			statement = con.prepareStatement("INSERT INTO clan_subpledges (clan_id, type, leader_id, name) VALUES (?,?,?,?)");
			statement.setInt(1, _clanId);
			statement.setInt(2, mainSubUnit.getType());
			statement.setInt(3, mainSubUnit.getLeaderObjectId());
			statement.setString(4, mainSubUnit.getName());
			statement.execute();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE characters SET clanid=?,pledge_type=? WHERE obj_Id=?");
			statement.setInt(1, getClanId());
			statement.setInt(2, mainSubUnit.getType());
			statement.setInt(3, getLeaderId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("Exception while storing Clan", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static Clan restore(int clanId)
	{
		if (clanId == 0) // no clan
		{
			return null;
		}

		Clan clan = null;

		Connection con1 = null;
		PreparedStatement statement1 = null;
		ResultSet clanData = null;
		try
		{
			con1 = DatabaseFactory.getInstance().getConnection();
			statement1 = con1.prepareStatement(
						"SELECT clan_level,hasCastle,hasFortress,hasHideout,ally_id,reputation_score,expelled_member,leaved_ally,dissolved_ally,dissolved_clan,disband_penalty,warehouse,airship FROM clan_data where clan_id=?");
			statement1.setInt(1, clanId);
			clanData = statement1.executeQuery();

			if (clanData.next())
			{
				clan = new Clan(clanId);
				clan.setLevel(clanData.getInt("clan_level"));
				clan.setHasCastle(clanData.getInt("hasCastle"));
				clan.setHasFortress(clanData.getInt("hasFortress"));
				clan.setHasHideout(clanData.getInt("hasHideout"));
				clan.setAllyId(clanData.getInt("ally_id"));
				clan._reputation = clanData.getInt("reputation_score");
				clan.setExpelledMemberTime(clanData.getLong("expelled_member") * 1000L);
				clan.setLeavedAllyTime(clanData.getLong("leaved_ally") * 1000L);
				clan.setDissolvedAllyTime(clanData.getLong("dissolved_ally") * 1000L);
				clan.setDissolvedClanTime(clanData.getLong("dissolved_clan") * 1000L);
				clan.setDisbandPenaltyTime(clanData.getLong("disband_penalty") * 1000L);
				clan.setWhBonus(clanData.getInt("warehouse"));
				clan.setAirshipLicense(clanData.getInt("airship") != -1);
				if (clan.isHaveAirshipLicense())
				{
					clan.setAirshipFuel(clanData.getInt("airship"));
				}
			}
			else
			{
				_log.warn("Clan " + clanId + " doesnt exists!");
				return null;
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while restoring clan!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con1, statement1, clanData);
		}

		if (clan == null)
		{
			_log.warn("Clan " + clanId + " does't exist");
			return null;
		}

		clan.restoreSkills();
		clan.restoreSubPledges();
		clan.restoreClanRecruitment();

		for (SubUnit unit : clan.getAllSubUnits())
		{
			unit.restore();
			unit.restoreSkills();
		}

		clan.restoreRankPrivs();
		clan.setCrestId(CrestCache.getInstance().getPledgeCrestId(clanId));
		clan.setCrestLargeId(CrestCache.getInstance().getPledgeCrestLargeId(clanId));
		return clan;
	}

	public void broadcastToOnlineMembers(IStaticPacket... packets)
	{
		for (UnitMember member : this)
		{
			if (member.isOnline())
			{
				member.getPlayer().sendPacket(packets);
			}
		}
	}

	public void broadcastToOnlineMembers(L2GameServerPacket... packets)
	{
		for (UnitMember member : this)
		{
			if (member.isOnline())
			{
				member.getPlayer().sendPacket(packets);
			}
		}
	}

	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player)
	{
		for (UnitMember member : this)
		{
			if (member.isOnline() && member.getPlayer() != player)
			{
				member.getPlayer().sendPacket(packet);
			}
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public void setCrestId(int newcrest)
	{
		_crestId = newcrest;
	}

	public int getCrestId()
	{
		return _crestId;
	}

	public boolean hasCrest()
	{
		return _crestId > 0;
	}

	public boolean hasAllianceCrest()
	{
		return getAlliance() != null && getAlliance().getAllyCrestId() > 0;
	}

	public int getCrestLargeId()
	{
		return _crestLargeId;
	}

	public void setCrestLargeId(int newcrest)
	{
		_crestLargeId = newcrest;
	}

	public boolean hasCrestLarge()
	{
		return _crestLargeId > 0;
	}

	public long getAdenaCount()
	{
		return _warehouse.getCountOfAdena();
	}

	public ClanWarehouse getWarehouse()
	{
		return _warehouse;
	}

	public int isAtWar()
	{
		if (_atWarWith != null && !_atWarWith.isEmpty())
		{
			return 1;
		}
		return 0;
	}

	public int isAtWarOrUnderAttack()
	{
		if (_atWarWith != null && !_atWarWith.isEmpty() || _underAttackFrom != null && !_underAttackFrom.isEmpty())
		{
			return 1;
		}
		return 0;
	}

	public boolean isAtWarWith(int id)
	{
		Clan clan = ClanTable.getInstance().getClan(id);
		if (_atWarWith != null && !_atWarWith.isEmpty())
		{
			if (_atWarWith.contains(clan))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isUnderAttackFrom(int id)
	{
		Clan clan = ClanTable.getInstance().getClan(id);
		if (_underAttackFrom != null && !_underAttackFrom.isEmpty())
		{
			if (_underAttackFrom.contains(clan))
			{
				return true;
			}
		}
		return false;
	}

	public void setEnemyClan(Clan clan)
	{
		_atWarWith.add(clan);
	}

	public void deleteEnemyClan(Clan clan)
	{
		_atWarWith.remove(clan);
	}

	// clans that are attacking this clan
	public void setAttackerClan(Clan clan)
	{
		_underAttackFrom.add(clan);
	}

	public void deleteAttackerClan(Clan clan)
	{
		_underAttackFrom.remove(clan);
	}

	public List<Clan> getEnemyClans()
	{
		return _atWarWith;
	}

	public List<Clan> getWarClans()
	{
		final List<Clan> warClans = new ArrayList<Clan>(_atWarWith.size());
		for (Clan war : _atWarWith)
		{
			if (war.isAtWarWith(_clanId))
			{
				warClans.add(war);
			}
		}
		return warClans;
	}

	public int getWarsCount()
	{
		return _atWarWith.size();
	}

	public List<Clan> getAttackerClans()
	{
		return _underAttackFrom;
	}

	public void broadcastClanStatus(boolean updateList, boolean needUserInfo, boolean relation)
	{
		List<L2GameServerPacket> listAll = updateList ? listAll() : null;
		PledgeShowInfoUpdate update = new PledgeShowInfoUpdate(this);

		for (UnitMember member : this)
		{
			if (member.isOnline())
			{
				if (updateList)
				{
					member.getPlayer().sendPacket(PledgeShowMemberListDeleteAll.STATIC);
					member.getPlayer().sendPacket(listAll);
				}
				member.getPlayer().sendPacket(update);
				if (needUserInfo)
				{
					member.getPlayer().broadcastCharInfo();
				}
				if (relation)
				{
					member.getPlayer().broadcastRelationChanged();
				}
			}
		}
	}

	public Alliance getAlliance()
	{
		return _allyId == 0 ? null : ClanTable.getInstance().getAlliance(_allyId);
	}

	public void setDissolvedClanTime(long time)
	{
		_dissolvedClanTime = time;
	}

	public long getDissolvedClanTime()
	{
		return _dissolvedClanTime;
	}

	public boolean isPlacedForDisband()
	{
		return _dissolvedClanTime != 0;
	}

	public boolean canDisband()
	{
		return System.currentTimeMillis() > _dissolvedClanTime;
	}

	public long getDisbandPenaltyTime()
	{
		return _disbandPenaltyTime;
	}

	public void setDisbandPenaltyTime(long disbandPenaltyTime)
	{
		_disbandPenaltyTime = disbandPenaltyTime;
	}

	public void unPlaceDisband()
	{
		_dissolvedClanTime = 0;
		_disbandPenaltyTime = System.currentTimeMillis() + DISBAND_PENALTY;
		updateClanInDB();
	}

	public boolean canInvite()
	{
		return System.currentTimeMillis() - _expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
	}

	public boolean canJoinAlly()
	{
		return System.currentTimeMillis() - _leavedAllyTime >= LEAVED_ALLY_PENALTY;
	}

	public boolean canCreateAlly()
	{
		return System.currentTimeMillis() - _dissolvedAllyTime >= DISSOLVED_ALLY_PENALTY;
	}

	public int getRank()
	{
		Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, REPUTATION_COMPARATOR);

		int place = 1;
		for (int i = 0; i < clans.length; i++)
		{
			if (i == REPUTATION_PLACES)
			{
				return 0;
			}

			Clan clan = clans[i];
			if (clan == this)
			{
				return place + i;
			}
		}

		return 0;
	}

	public int getReputationScore()
	{
		return _reputation;
	}

	private void setReputationScore(int rep)
	{
		if (_reputation >= 0 && rep < 0)
		{
			broadcastToOnlineMembers(Msg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED);
			for (UnitMember member : this)
			{
				if (member.isOnline() && member.getPlayer() != null)
				{
					disableSkills(member.getPlayer());
				}
			}
		}
		else if (_reputation < 0 && rep >= 0)
		{
			broadcastToOnlineMembers(Msg.THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER);
			for (UnitMember member : this)
			{
				if (member.isOnline() && member.getPlayer() != null)
				{
					enableSkills(member.getPlayer());
				}
			}
		}

		if (_reputation != rep)
		{
			_reputation = rep;
			broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
		}

		updateClanInDB();
	}

	public int incReputation(int inc, boolean rate, String source)
	{
		if (_level < 5)
		{
			return 0;
		}

		if (rate && Math.abs(inc) <= Config.RATE_CLAN_REP_SCORE_MAX_AFFECTED)
		{
			inc = (int) Math.round(inc * Config.RATE_CLAN_REP_SCORE);
		}

		setReputationScore(_reputation + inc);
		Log.add(getName() + "|" + inc + "|" + _reputation + "|" + source, "clan_reputation");
		return inc;
	}

	public int incReputation(int inc)
	{
		if (_level < 5)
		{
			return 0;
		}

		setReputationScore(_reputation + inc);
		return inc;
	}

	/* ============================ clan skills stuff ============================ */

	private void restoreSkills()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			// Retrieve all skills of this L2Player from the database
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?");
			statement.setInt(1, getClanId());
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while (rset.next())
			{
				int id = rset.getInt("skill_id");
				int level = rset.getInt("skill_level");
				// Create a L2Skill object for each record
				Skill skill = SkillTable.getInstance().getInfo(id, level);
				// Add the L2Skill object to the L2Clan _skills
				_skills.put(skill.getId(), skill);
			}
		}
		catch (SQLException e)
		{
			_log.warn("Could not restore clan skills: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public Collection<Skill> getSkills()
	{
		return _skills.values();
	}

	/**
	 * used to retrieve all skills
	 * @return
	 */
	public final Skill[] getAllSkills()
	{
		if (_reputation < 0)
		{
			return Skill.EMPTY_ARRAY;
		}

		return _skills.values().toArray(new Skill[_skills.values().size()]);
	}

	public List<Skill> getUnitTopSkills()
	{
		final List<Skill> topSkills = new ArrayList<Skill>();
		for (SubUnit unit : _subUnits.values())
		{
			for (Skill skill : unit.getSkills())
			{
				boolean alreadyExists = false;
				for (Skill alreadyTopSkill : topSkills)
				{
					if (alreadyTopSkill.getId() == skill.getId() && alreadyTopSkill.getLevel() >= skill.getLevel())
					{
						alreadyExists = true;
					}
				}
				if (!alreadyExists)
				{
					topSkills.add(skill);
				}
			}
		}
		return topSkills;
	}

	/**
	 * used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
	 * @param newSkill
	 * @param store
	 * @return
	 */
	public Skill addSkill(Skill newSkill, boolean store)
	{
		Skill oldSkill = null;
		if (newSkill != null)
		{
			// Replace oldSkill by newSkill or Add the newSkill
			oldSkill = _skills.put(newSkill.getId(), newSkill);

			if (store)
			{
				Connection con = null;
				PreparedStatement statement = null;

				try
				{
					con = DatabaseFactory.getInstance().getConnection();

					if (oldSkill != null)
					{
						statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
						statement.setInt(1, newSkill.getLevel());
						statement.setInt(2, oldSkill.getId());
						statement.setInt(3, getClanId());
						statement.execute();
					}
					else
					{
						statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?)");
						statement.setInt(1, getClanId());
						statement.setInt(2, newSkill.getId());
						statement.setInt(3, newSkill.getLevel());
						statement.execute();
					}
				}
				catch (SQLException e)
				{
					_log.warn("Error could not store char skills: ", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
			}

			PledgeSkillListAdd p = new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel());
			PledgeSkillList p2 = new PledgeSkillList(this);
			for (UnitMember temp : this)
			{
				if (temp.isOnline())
				{
					Player player = temp.getPlayer();
					if (player != null)
					{
						addSkill(player, newSkill);
						player.sendPacket(p, p2, new SkillList(player));
					}
				}
			}
		}

		return oldSkill;
	}

	public void addSkillsQuietly(Player player)
	{
		for (Skill skill : _skills.values())
		{
			addSkill(player, skill);
		}

		final SubUnit subUnit = getSubUnit(player.getPledgeType());
		if (subUnit != null)
		{
			subUnit.addSkillsQuietly(player);
		}
	}

	public void enableSkills(Player player)
	{
		if (player.isInOlympiadMode()) // do not allow clan skills on Olympiad
		{
			return;
		}

		for (Skill skill : _skills.values())
		{
			if (skill.getMinPledgeClass() <= player.getPledgeClass())
			{
				player.removeUnActiveSkill(skill);
			}
		}

		final SubUnit subUnit = getSubUnit(player.getPledgeType());
		if (subUnit != null)
		{
			subUnit.enableSkills(player);
		}
	}

	public void disableSkills(Player player)
	{
		for (Skill skill : _skills.values())
		{
			player.addUnActiveSkill(skill);
		}

		final SubUnit subUnit = getSubUnit(player.getPledgeType());
		if (subUnit != null)
		{
			subUnit.disableSkills(player);
		}
	}

	private void addSkill(Player player, Skill skill)
	{
		if (skill.getMinPledgeClass() <= player.getPledgeClass())
		{
			player.addSkill(skill, false);
			player.broadcastUserInfo(true);
			if (_reputation < 0 || player.isInOlympiadMode())
			{
				player.addUnActiveSkill(skill);
			}
		}
	}

	/**
	 * Removes the clan skill, without removing it from the base. Used to remove the skills residences. After removing the skill (s) to be sent out boarcastSkillListToOnlineMembers ()
	 * @param skill
	 */
	public void removeSkill(int skill)
	{
		_skills.remove(skill);
		PledgeSkillListAdd p = new PledgeSkillListAdd(skill, 0);
		for (UnitMember temp : this)
		{
			Player player = temp.getPlayer();
			if (player != null && player.isOnline())
			{
				player.removeSkillById(skill);
				player.sendPacket(p, new SkillList(player));
			}
		}
	}

	public void broadcastSkillListToOnlineMembers()
	{
		for (UnitMember temp : this)
		{
			Player player = temp.getPlayer();
			if (player != null && player.isOnline())
			{
				player.sendPacket(new PledgeSkillList(this));
				player.sendPacket(new SkillList(player));
			}
		}
	}

	/* ============================ clan subpledges stuff ============================ */

	public static boolean isAcademy(int pledgeType)
	{
		return pledgeType == SUBUNIT_ACADEMY;
	}

	public static boolean isRoyalGuard(int pledgeType)
	{
		return pledgeType == SUBUNIT_ROYAL1 || pledgeType == SUBUNIT_ROYAL2;
	}

	public static boolean isOrderOfKnights(int pledgeType)
	{
		return pledgeType == SUBUNIT_KNIGHT1 || pledgeType == SUBUNIT_KNIGHT2 || pledgeType == SUBUNIT_KNIGHT3 || pledgeType == SUBUNIT_KNIGHT4;
	}

	public static int getAffiliationRank(int pledgeType)
	{
		if (isAcademy(pledgeType))
		{
			return 9;
		}
		else if (isOrderOfKnights(pledgeType))
		{
			return 8;
		}
		else if (isRoyalGuard(pledgeType))
		{
			return 7;
		}
		else
		{
			return 6;
		}
	}

	public void restartMembers()
	{
		for (SubUnit unit : _subUnits.values())
		{
			unit.restartMembers();
		}
	}

	public boolean hasSubUnit(int pledgeType)
	{
		return _subUnits.containsKey(pledgeType);
	}

	public final SubUnit getSubUnit(int pledgeType)
	{
		return _subUnits.get(pledgeType);
	}

	public final void addSubUnit(SubUnit sp, boolean updateDb)
	{
		_subUnits.put(sp.getType(), sp);

		if (updateDb)
		{
			broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(sp));
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)");
				statement.setInt(1, getClanId());
				statement.setInt(2, sp.getType());
				statement.setInt(3, sp.getLeaderObjectId());
				statement.setString(4, sp.getName());
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.warn("Could not store clan Sub pledges: ", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public int createSubPledge(Player player, int pledgeType, UnitMember leader, String name)
	{
		int temp = pledgeType;
		pledgeType = getAvailablePledgeTypes(pledgeType);

		if (pledgeType == SUBUNIT_NONE)
		{
			if (temp == SUBUNIT_ACADEMY)
			{
				player.sendPacket(Msg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
			}
			else
			{
				player.sendMessage("You can't create any more sub-units of this type");
			}
			return SUBUNIT_NONE;
		}

		switch (pledgeType)
		{
		case SUBUNIT_ACADEMY:
			break;
		case SUBUNIT_ROYAL1:
		case SUBUNIT_ROYAL2:
			if (getReputationScore() < 5000)
			{
				player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
				return SUBUNIT_NONE;
			}
			incReputation(-5000, false, "SubunitCreate");
			break;
		case SUBUNIT_KNIGHT1:
		case SUBUNIT_KNIGHT2:
		case SUBUNIT_KNIGHT3:
		case SUBUNIT_KNIGHT4:
			if (getReputationScore() < 10000)
			{
				player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
				return SUBUNIT_NONE;
			}
			incReputation(-10000, false, "SubunitCreate");
			break;
		}

		addSubUnit(new SubUnit(this, pledgeType, leader, name), true);
		return pledgeType;
	}

	public int getAvailablePledgeTypes(int pledgeType)
	{
		if (pledgeType == SUBUNIT_MAIN_CLAN)
		{
			return SUBUNIT_NONE;
		}

		if (_subUnits.get(pledgeType) != null)
		{
			switch (pledgeType)
			{
			case SUBUNIT_ACADEMY:
				return SUBUNIT_NONE;
			case SUBUNIT_ROYAL1:
				pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
				break;
			case SUBUNIT_ROYAL2:
				return SUBUNIT_NONE;
			case SUBUNIT_KNIGHT1:
				pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
				break;
			case SUBUNIT_KNIGHT2:
				pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
				break;
			case SUBUNIT_KNIGHT3:
				pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
				break;
			case SUBUNIT_KNIGHT4:
				return SUBUNIT_NONE;
			}
		}
		return pledgeType;
	}

	private void restoreSubPledges()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM clan_subpledges WHERE clan_id=?");
			statement.setInt(1, getClanId());
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while (rset.next())
			{
				int type = rset.getInt("type");
				int leaderId = rset.getInt("leader_id");
				String name = rset.getString("name");
				SubUnit pledge = new SubUnit(this, type, leaderId, name);
				addSubUnit(pledge, false);
			}
		}
		catch (SQLException e)
		{
			_log.warn("Could not restore clan SubPledges", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public int getSubPledgeLimit(int pledgeType)
	{
		int limit;
		switch (_level)
		{
		case 0:
			limit = 10;
			break;
		case 1:
			limit = 15;
			break;
		case 2:
			limit = 20;
			break;
		case 3:
			limit = 30;
			break;
		default:
			limit = 40;
			break;
		}
		switch (pledgeType)
		{
		case SUBUNIT_ACADEMY:
		case SUBUNIT_ROYAL1:
		case SUBUNIT_ROYAL2:
			if (getLevel() >= 11)
			{
				limit = 30;
			}
			else
			{
				limit = 20;
			}
			break;
		case SUBUNIT_KNIGHT1:
		case SUBUNIT_KNIGHT2:
			if (getLevel() >= 9)
			{
				limit = 25;
			}
			else
			{
				limit = 10;
			}
			break;
		case SUBUNIT_KNIGHT3:
		case SUBUNIT_KNIGHT4:
			if (getLevel() >= 10)
			{
				limit = 25;
			}
			else
			{
				limit = 10;
			}
			break;
		}
		return limit;
	}

	public int getUnitMembersSize(int pledgeType)
	{
		if (pledgeType == Clan.SUBUNIT_NONE || !_subUnits.containsKey(pledgeType))
		{
			return 0;
		}
		return getSubUnit(pledgeType).size();
	}

	public static void restoreSingleMember(int playerObjectId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement(
								"SELECT `c`.`char_name` AS `char_name`,`c`.`clanid` AS `clanid`,`c`.`pledge_type` AS `pledge_type`,`s`.`level` AS `level`,`s`.`class_id` AS `classid`,`c`.`obj_Id` AS `obj_id`,`c`.`title` AS `title`,`c`.`pledge_rank` AS `pledge_rank`,`c`.`apprentice` AS `apprentice`, `c`.`sex` AS `sex` FROM `characters` `c` LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') WHERE c.obj_Id=? ORDER BY `c`.`lastaccess` DESC"))
		{
			statement.setInt(1, playerObjectId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final int clanId = rset.getInt("clanid");
					if (clanId <= 0)
					{
						return;
					}
					final Clan clan = ClanTable.getInstance().getClan(clanId);
					if (clan == null)
					{
						return;
					}
					final int unitType = rset.getInt("pledge_type");
					if (!clan.hasSubUnit(unitType))
					{
						return;
					}
					final SubUnit unit = clan.getSubUnit(unitType);
					final UnitMember member = new UnitMember(clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("classid"), rset.getInt("obj_Id"), unitType,
								rset.getInt("pledge_rank"), rset.getInt("apprentice"), rset.getInt("sex"), -128);
					unit.addUnitMember(member);
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Error while restoring Single Unit Member(" + playerObjectId + ")", e);
		}
	}

	public void addMember(Player player, int pledgeType)
	{
		player.sendPacket(new JoinPledge(getClanId()));

		SubUnit subUnit = getSubUnit(pledgeType);
		if (subUnit == null)
		{
			return;
		}

		UnitMember member = new UnitMember(this, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), pledgeType, player.getPowerGrade(),
					player.getApprentice(), player.getSex(), Clan.SUBUNIT_NONE);
		subUnit.addUnitMember(member);

		player.setPledgeType(pledgeType);
		player.setClan(this);

		member.setPlayerInstance(player, false);

		if (pledgeType == Clan.SUBUNIT_ACADEMY)
		{
			player.setLvlJoinedAcademy(player.getLevel());
		}

		member.setPowerGrade(getAffiliationRank(player.getPledgeType()));

		broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), player);
		broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdate(this));

		// this activates the clan tab on the new member
		player.sendPacket(SystemMsg.ENTERED_THE_CLAN);
		player.sendPacket(player.getClan().listAll());
		player.updatePledgeClass();

		addSkillsQuietly(player);

		player.sendPacket(new PledgeSkillList(this));
		player.sendPacket(new SkillList(player));

		EventHolder.getInstance().findEvent(player);
		if (getWarDominion() > 0)
		{
			DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);

			siegeEvent.updatePlayer(player, true);
		}
		else
		{
			player.broadcastCharInfo();
		}

		player.store(false);

		// Synerge - Update the clan stats with the new total member count
		// getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_COUNT, getAllSize());

		// Synerge - Add a new recruited member to the stats
		// getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_RECRUITED);
	}

	/* Recruiting members */

	private void restoreClanRecruitment()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{

			try (PreparedStatement statement = con.prepareStatement("SELECT * FROM clan_requiements where clan_id=" + getClanId()); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					_recruting = (rset.getInt("recruting") == 1 ? true : false);
					for (String clas : rset.getString("classes").split(","))
					{
						if (clas.length() > 0)
						{
							_classesNeeded.add(Integer.parseInt(clas));
						}
					}
					for (int i = 1; i <= 8; i++)
					{
						_questions[(i - 1)] = rset.getString("question" + i);
					}
				}
			}

			try (PreparedStatement statement = con.prepareStatement("SELECT * FROM clan_petitions where clan_id=" + getClanId()); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					String[] answers = new String[8];
					for (int i = 1; i <= 8; i++)
					{
						answers[(i - 1)] = rset.getString("answer" + i);
					}
					_petitions.add(new SinglePetition(rset.getInt("sender_id"), answers, rset.getString("comment")));
				}
			}
		}
		catch (NumberFormatException | SQLException e)
		{
			_log.error("Error while restoring Clan Recruitment", e);
		}
	}

	public void updateRecrutationData()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("INSERT INTO clan_requiements VALUES(" + getClanId()
						+ ",0,'','','','','','','','','') ON DUPLICATE KEY UPDATE recruting=?,classes=?,question1=?,question2=?,question3=?,question4=?,question5=?,question6=?,question7=?,question8=?"))
			{
				statement.setInt(1, (_recruting == true ? 1 : 0));
				statement.setString(2, getClassesForData());
				for (int i = 0; i < 8; i++)
				{
					statement.setString(i + 3, _questions[i] == null ? "" : _questions[i]);
				}
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_petitions WHERE clan_id=" + getClanId()))
			{
				statement.execute();
			}

			for (SinglePetition petition : getPetitions())
			{
				try (PreparedStatement statement = con.prepareStatement("INSERT IGNORE INTO clan_petitions VALUES(?,?,?,?,?,?,?,?,?,?,?)"))
				{
					statement.setInt(1, petition.getSenderId());
					statement.setInt(2, getClanId());
					for (int i = 0; i < 8; i++)
					{
						statement.setString(i + 3, petition.getAnswers()[i] == null ? "" : petition.getAnswers()[i]);
					}
					statement.setString(11, petition.getComment());
					statement.execute();
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Error while updating clan recruitment system on clan id '" + _clanId + "' in db", e);
		}
	}

	public void setQuestions(String[] questions)
	{
		_questions = questions;
	}

	public class SinglePetition
	{
		int _sender;
		String[] _answers;
		String _comment;

		private SinglePetition(int sender, String[] answers, String comment)
		{
			_sender = sender;
			_answers = answers;
			_comment = comment;
		}

		public int getSenderId()
		{
			return _sender;
		}

		public String[] getAnswers()
		{
			return _answers;
		}

		public String getComment()
		{
			return _comment;
		}
	}

	public synchronized boolean addPetition(int senderId, String[] answers, String comment)
	{
		if (getPetition(senderId) != null)
		{
			return false;
		}

		_petitions.add(new SinglePetition(senderId, answers, comment));
		updateRecrutationData();

		if (World.getPlayer(getLeaderId()) != null)
		{
			World.getPlayer(getLeaderId()).sendMessage("New Clan Petition has arrived!");
		}
		return true;
	}

	public SinglePetition getPetition(int senderId)
	{
		return _petitions.stream().filter(petition -> petition.getSenderId() == senderId).findAny().orElse(null);
	}

	public ArrayList<SinglePetition> getPetitions()
	{
		return _petitions;
	}

	public synchronized void deletePetition(int senderId)
	{
		SinglePetition petition = _petitions.stream().filter(p -> p.getSenderId() == senderId).findAny().orElse(null);
		if (petition != null)
		{
			_petitions.remove(petition);
			updateRecrutationData();
		}
	}

	public void deletePetition(SinglePetition petition)
	{
		_petitions.remove(petition);
		updateRecrutationData();
	}

	public void setRecrutating(boolean b)
	{
		_recruting = b;
	}

	public void addClassNeeded(int clas)
	{
		_classesNeeded.add(clas);
	}

	public void deleteClassNeeded(int clas) // iti arat cum o generez? neahm, ca cred ca stiu care-i baiu :)
	{
		int indexOfClass = _classesNeeded.indexOf(clas);
		if (indexOfClass != -1)
		{
			_classesNeeded.remove(indexOfClass);
		}
		else
		{
			_log.warn("Tried removing inexistent class: " + clas);
		}
	}

	public String getClassesForData()
	{
		String text = "";
		for (int i = 0; i < getClassesNeeded().size(); i++)
		{
			if (i != 0)
			{
				text += ",";
			}
			text += getClassesNeeded().get(i);
		}
		return text;
	}

	public ArrayList<Integer> getClassesNeeded()
	{
		return _classesNeeded;
	}

	public boolean isRecruting()
	{
		return _recruting;
	}

	public String[] getQuestions()
	{
		return _questions;
	}

	/* ============================ clan privilege ranks stuff ============================ */

	private void restoreRankPrivs()
	{
		if (_privs == null)
		{
			InitializePrivs();
		}
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			// Retrieve all skills of this L2Player from the database
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT privilleges,rank FROM clan_privs WHERE clan_id=?");
			statement.setInt(1, getClanId());
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while (rset.next())
			{
				int rank = rset.getInt("rank");
				// int party = rset.getInt("party"); - unused?
				int privileges = rset.getInt("privilleges");
				// noinspection ConstantConditions
				RankPrivs p = _privs.get(rank);
				if (p != null)
				{
					p.setPrivs(privileges);
				}
				else
				{
					_log.warn("Invalid rank value (" + rank + "), please check clan_privs table");
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Could not restore clan privs by rank: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void InitializePrivs()
	{
		for (int i = RANK_FIRST; i <= RANK_LAST; i++)
		{
			_privs.put(i, new RankPrivs(i, 0, CP_NOTHING));
		}
	}

	public void updatePrivsForRank(int rank)
	{
		for (UnitMember member : this)
		{
			if (member.isOnline() && member.getPlayer() != null && member.getPlayer().getPowerGrade() == rank)
			{
				if (member.getPlayer().isClanLeader())
				{
					continue;
				}
				member.getPlayer().sendUserInfo();
			}
		}
	}

	public RankPrivs getRankPrivs(int rank)
	{
		if (rank < RANK_FIRST || rank > RANK_LAST)
		{
			// TODO: Synerge - Should we warn here, its just a check, no need to show anything else
			// _log.warn("Requested invalid rank value: " + rank);
			// Thread.dumpStack();
			return null;
		}
		if (_privs.get(rank) == null)
		{
			_log.warn("Request of rank before init: " + rank);
			Thread.dumpStack();
			setRankPrivs(rank, CP_NOTHING);
		}
		return _privs.get(rank);
	}

	public int countMembersByRank(int rank)
	{
		int ret = 0;
		for (UnitMember m : this)
		{
			if (m.getPowerGrade() == rank)
			{
				ret++;
			}
		}
		return ret;
	}

	public void setRankPrivs(int rank, int privs)
	{
		if (rank < RANK_FIRST || rank > RANK_LAST)
		{
			_log.warn("Requested set of invalid rank value: " + rank);
			Thread.dumpStack();
			return;
		}

		if (_privs.get(rank) != null)
		{
			_privs.get(rank).setPrivs(privs);
		}
		else
		{
			_privs.put(rank, new RankPrivs(rank, countMembersByRank(rank), privs));
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			// _log.warn("requested store clan privs in db for rank: " + rank + ", privs: " + privs);
			// Retrieve all skills of this L2Player from the database
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)");
			statement.setInt(1, getClanId());
			statement.setInt(2, rank);
			statement.setInt(3, privs);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("Could not store clan privs for rank: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * used to retrieve all privilege ranks
	 * @return
	 */
	public final RankPrivs[] getAllRankPrivs()
	{
		if (_privs == null)
		{
			return new RankPrivs[0];
		}
		return _privs.values().toArray(new RankPrivs[_privs.values().size()]);
	}

	private static class ClanReputationComparator implements Comparator<Clan>, Serializable
	{
		private static final long serialVersionUID = -240267507300918307L;

		@Override
		public int compare(Clan o1, Clan o2)
		{
			if (o1 == null || o2 == null)
			{
				return 0;
			}
			return Integer.compare(o2.getReputationScore(), o1.getReputationScore());
		}
	}

	public int getWhBonus()
	{
		return _whBonus;
	}

	public void setWhBonus(int i)
	{
		if (_whBonus != -1)
		{
			mysql.set("UPDATE `clan_data` SET `warehouse`=? WHERE `clan_id`=?", i, getClanId());
		}
		_whBonus = i;
	}

	public void setAirshipLicense(boolean val)
	{
		_airshipLicense = val;
	}

	public boolean isHaveAirshipLicense()
	{
		return _airshipLicense;
	}

	public ClanAirShip getAirship()
	{
		return _airship;
	}

	public void setAirship(ClanAirShip airship)
	{
		_airship = airship;
	}

	public int getAirshipFuel()
	{
		return _airshipFuel;
	}

	public void setAirshipFuel(int fuel)
	{
		_airshipFuel = fuel;
	}

	public final Collection<SubUnit> getAllSubUnits()
	{
		return _subUnits.values();
	}

	public List<L2GameServerPacket> listAll()
	{
		List<L2GameServerPacket> p = new ArrayList<L2GameServerPacket>(_subUnits.size());
		for (SubUnit unit : getAllSubUnits())
		{
			p.add(new PledgeShowMemberListAll(this, unit));
		}

		return p;
	}

	public boolean canEditNotice(Player player)
	{
		return player.getClanId() == _clanId && player.isClanLeader();
	}

	public String getNotice()
	{
		return _notice;
	}

	public void setNotice(String notice)
	{
		_notice = notice;
	}

	public int getSkillLevel(int id, int def)
	{
		Skill skill = _skills.get(id);
		return skill == null ? def : skill.getLevel();
	}

	public int getSkillLevel(int id)
	{
		return getSkillLevel(id, -1);
	}

	public int getWarDominion()
	{
		return _warDominion;
	}

	public void setWarDominion(int warDominion)
	{
		_warDominion = warDominion;
	}

	public void setSiegeKills(int i)
	{
		_siegeKills = i;
	}

	public void incSiegeKills()
	{
		_siegeKills++;
	}

	public int getSiegeKills()
	{
		return _siegeKills;
	}

	@Override
	public Iterator<UnitMember> iterator()
	{
		List<Iterator<UnitMember>> iterators = new ArrayList<Iterator<UnitMember>>(_subUnits.size());
		for (SubUnit subUnit : _subUnits.values())
		{
			iterators.add(subUnit.getUnitMembers().iterator());
		}
		return new JoinedIterator<UnitMember>(iterators);
	}

	public boolean isFull()
	{
		for (SubUnit unit : getAllSubUnits())
		{
			if (getUnitMembersSize(unit.getType()) < getSubPledgeLimit(unit.getType()))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	public int getAverageLevel()
	{
		int size = 0;
		int level = 0;

		for (SubUnit unit : getAllSubUnits())
		{
			for (UnitMember member : unit.getUnitMembers())
			{
				size++;
				level += member.getLevel();
			}
		}

		return level / size;
	}
	// /**
	// * @param obj
	// * @return
	// */
	// public boolean checkInviteList(int obj)
	// {
	// for (ClanRequest request : ClanRequest.getInviteList(getClanId()))
	// {
	// Player invited = request.getPlayer();

	// if (invited.getObjectId() == obj)
	// return true;
	// }

	// return false;
	// }

	// public List<ClanRequest> getInviteList()
	// {
	// return ClanRequest.getInviteList(getClanId());
	// }

	//
	// /**
	// * @return
	// */
	// public String getDescription()
	// {
	// return _description;
	// }
	//
	// public void setDescription(String description)
	// {
	// _description = description;
	// }
	// Synerge - Clan Stats support for Server Ranking
	// private final ClanStats _stats;

	// public final ClanStats getStats()
	// {
	// return _stats;
	// }
	// }
}
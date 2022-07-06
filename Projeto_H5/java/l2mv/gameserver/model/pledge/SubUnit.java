package l2mv.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.ExSubPledgeSkillAdd;
import l2mv.gameserver.tables.SkillTable;

public class SubUnit
{
	private static final Logger _log = LoggerFactory.getLogger(SubUnit.class);

	private IntObjectMap<Skill> _skills = new CTreeIntObjectMap<Skill>();
	private IntObjectMap<UnitMember> _members = new CHashIntObjectMap<UnitMember>();

	private int _type;

	private int _leaderObjectId;
	private UnitMember _leader;

	private String _name;
	private Clan _clan;

	public SubUnit(Clan c, int type, UnitMember leader, String name)
	{
		_clan = c;
		_type = type;
		_name = name;

		setLeader(leader, false);
	}

	public SubUnit(Clan c, int type, int leader, String name)
	{
		_clan = c;
		_type = type;
		_leaderObjectId = leader;
		_name = name;
	}

	public int getType()
	{
		return _type;
	}

	public String getName()
	{
		return _name;
	}

	public UnitMember getLeader()
	{
		return _leader;
	}

	public boolean isUnitMember(int obj)
	{
		return _members.containsKey(obj);
	}

	public void addUnitMember(UnitMember member)
	{
		_members.put(member.getObjectId(), member);
	}

	public UnitMember getUnitMember(int obj)
	{
		if (obj == 0)
		{
			return null;
		}
		return _members.get(obj);
	}

	public UnitMember getUnitMember(String obj)
	{
		for (UnitMember m : getUnitMembers())
		{
			if (m.getName().equalsIgnoreCase(obj))
			{
				return m;
			}
		}

		return null;
	}

	public void removeUnitMember(int objectId)
	{
		UnitMember m = _members.remove(objectId);
		if (m == null)
		{
			return;
		}

		if (objectId == getLeaderObjectId()) // subpledge leader
		{
			setLeader(null, true); // clan leader has to assign another one, via villagemaster
		}

		if (m.hasSponsor())
		{
			_clan.getAnyMember(m.getSponsor()).setApprentice(0);
		}

		removeMemberInDatabase(m);

		m.setPlayerInstance(null, true);

		// Synerge - We add a new member that withdrew from the clan to the stats
		// _clan.getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_WITHDREW);
	}

	public void replace(int objectId, int newUnitId)
	{
		SubUnit newUnit = _clan.getSubUnit(newUnitId);
		if (newUnit == null)
		{
			return;
		}

		UnitMember m = _members.remove(objectId);
		if (m == null)
		{
			return;
		}

		m.setPledgeType(newUnitId);
		newUnit.addUnitMember(m);

		if (m.getPowerGrade() > 5)
		{
			m.setPowerGrade(_clan.getAffiliationRank(m.getPledgeType()));
		}
	}

	public int getLeaderObjectId()
	{
		return _leader == null ? 0 : _leader.getObjectId();
	}

	public int size()
	{
		return _members.size();
	}

	public Collection<UnitMember> getUnitMembers()
	{
		return _members.values();
	}

	public void setLeader(UnitMember newLeader, boolean updateDB)
	{
		final UnitMember old = _leader;
		if (old != null) // обновляем старого мембера
		{
			old.setLeaderOf(Clan.SUBUNIT_NONE);
		}

		_leader = newLeader;
		_leaderObjectId = newLeader == null ? 0 : newLeader.getObjectId();

		if (newLeader != null)
		{
			newLeader.setLeaderOf(_type);
		}

		if (updateDB)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?");
				statement.setInt(1, getLeaderObjectId());
				statement.setInt(2, _clan.getClanId());
				statement.setInt(3, _type);
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.error("Exception while setting Sub Unit Leader", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public void setName(String name, boolean updateDB)
	{
		_name = name;
		if (updateDB)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? and type=?");
				statement.setString(1, _name);
				statement.setInt(2, _clan.getClanId());
				statement.setInt(3, _type);
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.error("Exception while setting Sub Unit Name", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public String getLeaderName()
	{
		return _leader == null ? StringUtils.EMPTY : _leader.getName();
	}

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
						statement = con.prepareStatement("UPDATE clan_subpledges_skills SET skill_level=? WHERE skill_id=? AND clan_id=? AND type=?");
						statement.setInt(1, newSkill.getLevel());
						statement.setInt(2, oldSkill.getId());
						statement.setInt(3, _clan.getClanId());
						statement.setInt(4, _type);
						statement.execute();
					}
					else
					{
						statement = con.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id,type,skill_id,skill_level) VALUES (?,?,?,?)");
						statement.setInt(1, _clan.getClanId());
						statement.setInt(2, _type);
						statement.setInt(3, newSkill.getId());
						statement.setInt(4, newSkill.getLevel());
						statement.execute();
					}
				}
				catch (SQLException e)
				{
					_log.warn("Exception while adding Skill to SubUnit", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
			}

			ExSubPledgeSkillAdd packet = new ExSubPledgeSkillAdd(_type, newSkill.getId(), newSkill.getLevel());
			for (UnitMember temp : _clan)
			{
				if (temp.isOnline())
				{
					Player player = temp.getPlayer();
					if (player != null)
					{
						player.sendPacket(packet);
						if (player.getPledgeType() == _type)
						{
							addSkill(player, newSkill);
						}
					}
				}
			}
		}

		return oldSkill;
	}

	/**
	 * Функция вызывается из клановской addSkillsQuietly(), отдельно вызывать не нужно
	 * @param player
	 */
	public void addSkillsQuietly(Player player)
	{
		for (Skill skill : _skills.values())
		{
			addSkill(player, skill);
		}
	}

	public void enableSkills(Player player)
	{
		for (Skill skill : _skills.values())
		{
			if (skill.getMinRank() <= player.getPledgeClass())
			{
				player.removeUnActiveSkill(skill);
			}
		}
	}

	public void disableSkills(Player player)
	{
		for (Skill skill : _skills.values())
		{
			player.addUnActiveSkill(skill);
		}
	}

	private void addSkill(Player player, Skill skill)
	{
		if (skill.getMinRank() <= player.getPledgeClass())
		{
			player.addSkill(skill, false);
			if (_clan.getReputationScore() < 0 || player.isInOlympiadMode())
			{
				player.addUnActiveSkill(skill);
			}
		}
	}

	public Collection<Skill> getSkills()
	{
		return _skills.values();
	}

	private static void removeMemberInDatabase(UnitMember member)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET clanid=0, pledge_type=?, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?");
			statement.setInt(1, Clan.SUBUNIT_NONE);
			statement.setLong(2, System.currentTimeMillis() / 1000);
			statement.setInt(3, member.getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Exception while removing Member from Sub Unit", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(//
						"SELECT `c`.`char_name` AS `char_name`," + //
									"`s`.`level` AS `level`," + //
									"`s`.`class_id` AS `classid`," + //
									"`c`.`obj_Id` AS `obj_id`," + //
									"`c`.`title` AS `title`," + //
									"`c`.`pledge_rank` AS `pledge_rank`," + //
									"`c`.`apprentice` AS `apprentice`, " + //
									"`c`.`sex` AS `sex` " + //
									"FROM `characters` `c` " + //
									"LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') " + //
									"WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC");

			statement.setInt(1, _clan.getClanId());
			statement.setInt(2, _type);
			rset = statement.executeQuery();

			while (rset.next())
			{
				UnitMember member = new UnitMember(_clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("classid"), rset.getInt("obj_Id"), _type, rset.getInt("pledge_rank"), rset.getInt("apprentice"), rset.getInt("sex"), Clan.SUBUNIT_NONE);

				addUnitMember(member);
			}

			if (_type != Clan.SUBUNIT_ACADEMY)
			{
				SubUnit mainClan = _clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
				UnitMember leader = mainClan.getUnitMember(_leaderObjectId);
				if (leader != null)
				{
					setLeader(leader, false);
				}
				else if (_type == Clan.SUBUNIT_MAIN_CLAN)
				{
					_log.error("Clan " + _name + " have no leader!");
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Error while restoring clan members for clan: " + _clan.getClanId(), e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void restartMembers()
	{
		_members.clear();
		restore();
	}

	public void restoreSkills()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_subpledges_skills WHERE clan_id=? AND type=?");
			statement.setInt(1, _clan.getClanId());
			statement.setInt(2, _type);
			rset = statement.executeQuery();

			while (rset.next())
			{
				int id = rset.getInt("skill_id");
				int level = rset.getInt("skill_level");

				Skill skill = SkillTable.getInstance().getInfo(id, level);

				_skills.put(skill.getId(), skill);
			}
		}
		catch (SQLException e)
		{
			_log.error("Exception while restoring Sub Unit Skills", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
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
}

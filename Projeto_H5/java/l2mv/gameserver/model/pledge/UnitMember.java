package l2mv.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NickNameChanged;

public class UnitMember
{
	private static final Logger _log = LoggerFactory.getLogger(UnitMember.class);

	private Player _player;

	private Clan _clan;
	private String _name;
	private String _title;
	private int _objectId;
	private int _level;
	private int _classId;
	private int _sex;
	private int _pledgeType;
	private int _powerGrade;
	private int _apprentice;

	private int _leaderOf = Clan.SUBUNIT_NONE;

	public UnitMember(Clan clan, String name, String title, int level, int classId, int objectId, int pledgeType, int powerGrade, int apprentice, int sex, int leaderOf)
	{
		_clan = clan;
		_objectId = objectId;
		_name = name;
		_title = title;
		_level = level;
		_classId = classId;
		_pledgeType = pledgeType;
		_powerGrade = powerGrade;
		_apprentice = apprentice;
		_sex = sex;
		_leaderOf = leaderOf;

		if (powerGrade != 0)
		{
			RankPrivs r = clan.getRankPrivs(powerGrade);
			r.setParty(clan.countMembersByRank(powerGrade));
		}
	}

	public UnitMember(Player player)
	{
		_objectId = player.getObjectId();
		_player = player;
	}

	public void setPlayerInstance(Player player, boolean exit)
	{
		_player = exit ? null : player;
		if (player == null)
		{
			return;
		}

		_clan = player.getClan();
		_name = player.getName();
		_title = player.getTitle();
		_level = player.getLevel();
		_classId = player.getClassId().getId();
		_pledgeType = player.getPledgeType();
		_powerGrade = player.getPowerGrade();
		_apprentice = player.getApprentice();
		_sex = player.getSex();
	}

	public Player getPlayer()
	{
		return _player;
	}

	public boolean isOnline()
	{
		Player player = getPlayer();
		return player != null && !player.isInOfflineMode();
	}

	public Clan getClan()
	{
		Player player = getPlayer();
		return player == null ? _clan : player.getClan();
	}

	public int getClassId()
	{
		Player player = getPlayer();
		return player == null ? _classId : player.getClassId().getId();
	}

	public int getSex()
	{
		Player player = getPlayer();
		return player == null ? _sex : player.getSex();
	}

	public int getLevel()
	{
		Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}

	public String getName()
	{
		Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public String getTitle()
	{
		Player player = getPlayer();
		return player == null ? _title : player.getTitle();
	}

	public void setTitle(String title)
	{
		Player player = getPlayer();
		_title = title;
		if (player != null)
		{
			player.setTitle(title);
			player.broadcastPacket(new NickNameChanged(player));
		}
		else
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?");
				statement.setString(1, title);
				statement.setInt(2, getObjectId());
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.error("Error while setting Unit Member Title", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public SubUnit getSubUnit()
	{
		return _clan.getSubUnit(_pledgeType);
	}

	public int getPledgeType()
	{
		Player player = getPlayer();
		return player == null ? _pledgeType : player.getPledgeType();
	}

	public void setPledgeType(int pledgeType)
	{
		Player player = getPlayer();
		_pledgeType = pledgeType;
		if (player != null)
		{
			player.setPledgeType(pledgeType);
		}
		else
		{
			updatePledgeType();
		}
	}

	private void updatePledgeType()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_type=? WHERE obj_Id=?");
			statement.setInt(1, _pledgeType);
			statement.setInt(2, getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Unit Member Pledge Type", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public int getPowerGrade()
	{
		Player player = getPlayer();
		return player == null ? _powerGrade : player.getPowerGrade();
	}

	public void setPowerGrade(int newPowerGrade)
	{
		Player player = getPlayer();
		int oldPowerGrade = getPowerGrade();
		_powerGrade = newPowerGrade;
		if (player != null)
		{
			player.setPowerGrade(newPowerGrade);
		}
		else
		{
			updatePowerGrade();
		}
		updatePowerGradeParty(oldPowerGrade, newPowerGrade);
	}

	private void updatePowerGradeParty(int oldGrade, int newGrade)
	{
		if (oldGrade != 0)
		{
			RankPrivs r1 = getClan().getRankPrivs(oldGrade);
			r1.setParty(getClan().countMembersByRank(oldGrade));
		}
		if (newGrade != 0)
		{
			RankPrivs r2 = getClan().getRankPrivs(newGrade);
			r2.setParty(getClan().countMembersByRank(newGrade));
		}
	}

	private void updatePowerGrade()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_rank=? WHERE obj_Id=?");
			statement.setInt(1, _powerGrade);
			statement.setInt(2, getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Unit Member Power Grade", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private int getApprentice()
	{
		Player player = getPlayer();
		return player == null ? _apprentice : player.getApprentice();
	}

	public void setApprentice(int apprentice)
	{
		Player player = getPlayer();
		_apprentice = apprentice;
		if (player != null)
		{
			player.setApprentice(apprentice);
		}
		else
		{
			updateApprentice();
		}
	}

	private void updateApprentice()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET apprentice=? WHERE obj_Id=?");
			statement.setInt(1, _apprentice);
			statement.setInt(2, getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Unit Member Apprentice", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public String getApprenticeName()
	{
		if (getApprentice() != 0)
		{
			if (getClan().getAnyMember(getApprentice()) != null)
			{
				return getClan().getAnyMember(getApprentice()).getName();
			}
		}
		return "";
	}

	public boolean hasApprentice()
	{
		return getApprentice() != 0;
	}

	public int getSponsor()
	{
		if (getPledgeType() != Clan.SUBUNIT_ACADEMY)
		{
			return 0;
		}
		int id = getObjectId();
		for (UnitMember element : getClan())
		{
			if (element.getApprentice() == id)
			{
				return element.getObjectId();
			}
		}
		return 0;
	}

	private String getSponsorName()
	{
		int sponsorId = getSponsor();
		if (sponsorId == 0)
		{
			return "";
		}
		else if (getClan().getAnyMember(sponsorId) != null)
		{
			return getClan().getAnyMember(sponsorId).getName();
		}
		return "";
	}

	public boolean hasSponsor()
	{
		return getSponsor() != 0;
	}

	public String getRelatedName()
	{
		if (getPledgeType() == Clan.SUBUNIT_ACADEMY)
		{
			return getSponsorName();
		}
		return getApprenticeName();
	}

	public boolean isClanLeader()
	{
		Player player = getPlayer();
		return player == null ? (_leaderOf == Clan.SUBUNIT_MAIN_CLAN) : player.isClanLeader();
	}

	public int isSubLeader()
	{
		for (SubUnit pledge : getClan().getAllSubUnits())
		{
			if (pledge.getLeaderObjectId() == getObjectId())
			{
				return pledge.getType();
			}
		}
		return 0;
	}

	public void setLeaderOf(int leaderOf)
	{
		_leaderOf = leaderOf;
	}

	public int getLeaderOf()
	{
		return _leaderOf;
	}
}
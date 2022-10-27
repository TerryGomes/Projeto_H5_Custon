package l2mv.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.TeleportLocation;
import l2mv.gameserver.tables.SkillTable;

public class ResidenceFunction
{
	private static final Logger _log = LoggerFactory.getLogger(ResidenceFunction.class);

	// residence functions
	public static final int TELEPORT = 1;
	public static final int ITEM_CREATE = 2;
	public static final int RESTORE_HP = 3;
	public static final int RESTORE_MP = 4;
	public static final int RESTORE_EXP = 5;
	public static final int SUPPORT = 6;
	public static final int CURTAIN = 7;
	public static final int PLATFORM = 8;

	private int _id;
	private int _type;
	private int _level;
	private final Calendar _endDate;
	private boolean _inDebt;
	private boolean _active;

	private Map<Integer, Integer> _leases = new ConcurrentSkipListMap<Integer, Integer>();
	private Map<Integer, TeleportLocation[]> _teleports = new ConcurrentSkipListMap<Integer, TeleportLocation[]>();
	private Map<Integer, int[]> _buylists = new ConcurrentSkipListMap<Integer, int[]>();
	private Map<Integer, Object[][]> _buffs = new ConcurrentSkipListMap<Integer, Object[][]>();

	public ResidenceFunction(int id, int type)
	{
		_id = id;
		_type = type;
		_endDate = Calendar.getInstance();
	}

	public int getResidenceId()
	{
		return _id;
	}

	public int getType()
	{
		return _type;
	}

	public int getLevel()
	{
		return _level;
	}

	public void setLvl(int lvl)
	{
		_level = lvl;
	}

	public long getEndTimeInMillis()
	{
		return _endDate.getTimeInMillis();
	}

	public void setEndTimeInMillis(long time)
	{
		_endDate.setTimeInMillis(time);
	}

	public void setInDebt(boolean inDebt)
	{
		_inDebt = inDebt;
	}

	public boolean isInDebt()
	{
		return _inDebt;
	}

	public void setActive(boolean active)
	{
		_active = active;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void updateRentTime(boolean inDebt)
	{
		setEndTimeInMillis(System.currentTimeMillis() + 86400000);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE residence_functions SET endTime=?, inDebt=? WHERE type=? AND id=?");
			statement.setInt(1, (int) (getEndTimeInMillis() / 1000));
			statement.setInt(2, inDebt ? 1 : 0);
			statement.setInt(3, getType());
			statement.setInt(4, getResidenceId());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Residence Function Rent Time", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public TeleportLocation[] getTeleports()
	{
		return getTeleports(_level);
	}

	public TeleportLocation[] getTeleports(int level)
	{
		return _teleports.get(level);
	}

	public void addTeleports(int level, TeleportLocation[] teleports)
	{
		_teleports.put(level, teleports);
	}

	public int getLease()
	{
		if (_level == 0)
		{
			return 0;
		}
		return getLease(_level);
	}

	public int getLease(int level)
	{
		return _leases.get(level);
	}

	public void addLease(int level, int lease)
	{
		_leases.put(level, lease);
	}

	public int[] getBuylist()
	{
		return getBuylist(_level);
	}

	public int[] getBuylist(int level)
	{
		return _buylists.get(level);
	}

	public void addBuylist(int level, int[] buylist)
	{
		_buylists.put(level, buylist);
	}

	public Object[][] getBuffs()
	{
		return getBuffs(_level);
	}

	public Object[][] getBuffs(int level)
	{
		return _buffs.get(level);
	}

	public void addBuffs(int level)
	{
		_buffs.put(level, buffs_template[level]);
	}

	public Set<Integer> getLevels()
	{
		return _leases.keySet();
	}

	public static final String A = "";
	public static final String W = "W";
	public static final String M = "M";

	private static final Object[][][] buffs_template =
	{
		{
		// level 0 - no buff
		},
		{
			// level 1
			{
				SkillTable.getInstance().getInfo(4342, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 1),
				W
			},
		},
		{
			// level 2
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
		},
		{
			// level 3
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
		},
		{
			// level 4
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 1),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 2),
				A
			},
		},
		{
			// level 5
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 1),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 2),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 1),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 2),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 1),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 1),
				W
			},
		},
		{
		// level 6 - unused
		},
		{
			// level 7
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 4),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 6),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 4),
				W
			},
		},
		{
			// level 8
			{
				SkillTable.getInstance().getInfo(4342, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 4),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 6),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 2),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 4),
				W
			},
			{
				SkillTable.getInstance().getInfo(4355, 1),
				M
			},
			{
				SkillTable.getInstance().getInfo(4356, 1),
				M
			},
			{
				SkillTable.getInstance().getInfo(4357, 1),
				W
			},
			{
				SkillTable.getInstance().getInfo(4359, 1),
				W
			},
			{
				SkillTable.getInstance().getInfo(4360, 1),
				W
			},
		},
		{
		// level 9 - unused
		},
		{
		// level 10 - unused
		},
		{
			// level 11
			{
				SkillTable.getInstance().getInfo(4342, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 5),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 4),
				W
			},
		},
		{
			// level 12
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
		},
		{
			// level 13
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
		},
		{
			// level 14
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 5),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 8),
				A
			},
		},
		{
			// level 15
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 5),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 8),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 3),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 8),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 4),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 5),
				W
			},
		},
		{
		// level 16 - unused
		},
		{
			// level 17
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 12),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 8),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 12),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 12),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 12),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 8),
				W
			},
		},
		{
			// level 18
			{
				SkillTable.getInstance().getInfo(4342, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4343, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4344, 6),
				A
			},
			{
				SkillTable.getInstance().getInfo(4346, 8),
				A
			},
			{
				SkillTable.getInstance().getInfo(4345, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4347, 12),
				A
			},
			{
				SkillTable.getInstance().getInfo(4349, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4350, 8),
				W
			},
			{
				SkillTable.getInstance().getInfo(4348, 12),
				A
			},
			{
				SkillTable.getInstance().getInfo(4351, 12),
				M
			},
			{
				SkillTable.getInstance().getInfo(4352, 4),
				A
			},
			{
				SkillTable.getInstance().getInfo(4353, 12),
				W
			},
			{
				SkillTable.getInstance().getInfo(4358, 6),
				W
			},
			{
				SkillTable.getInstance().getInfo(4354, 8),
				W
			},
			{
				SkillTable.getInstance().getInfo(4355, 4),
				M
			},
			{
				SkillTable.getInstance().getInfo(4356, 4),
				M
			},
			{
				SkillTable.getInstance().getInfo(4357, 3),
				W
			},
			{
				SkillTable.getInstance().getInfo(4359, 4),
				W
			},
			{
				SkillTable.getInstance().getInfo(4360, 4),
				W
			},
		},
	};
}
package l2mv.gameserver.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.LvlupData;
import l2mv.gameserver.model.base.ClassId;

public class LevelUpTable
{
	private static final String SELECT_ALL = "SELECT classid, defaulthpbase, defaulthpadd, defaulthpmod, defaultcpbase, defaultcpadd, defaultcpmod, defaultmpbase, defaultmpadd, defaultmpmod, class_lvl FROM lvlupgain";
	private static final String CLASS_LVL = "class_lvl";
	private static final String MP_MOD = "defaultmpmod";
	private static final String MP_ADD = "defaultmpadd";
	private static final String MP_BASE = "defaultmpbase";
	private static final String HP_MOD = "defaulthpmod";
	private static final String HP_ADD = "defaulthpadd";
	private static final String HP_BASE = "defaulthpbase";
	private static final String CP_MOD = "defaultcpmod";
	private static final String CP_ADD = "defaultcpadd";
	private static final String CP_BASE = "defaultcpbase";
	private static final String CLASS_ID = "classid";

	private static final Logger _log = LoggerFactory.getLogger(LevelUpTable.class);

	private static LevelUpTable _instance;

	private Map<Integer, LvlupData> _lvltable;

	public static LevelUpTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new LevelUpTable();
		}
		return _instance;
	}

	private LevelUpTable()
	{
		_lvltable = new HashMap<Integer, LvlupData>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_ALL);
			rset = statement.executeQuery();
			LvlupData lvlDat;

			while (rset.next())
			{
				lvlDat = new LvlupData();
				lvlDat.set_classid(rset.getInt(CLASS_ID));
				lvlDat.set_classLvl(rset.getInt(CLASS_LVL));
				lvlDat.set_classHpBase(rset.getDouble(HP_BASE));
				lvlDat.set_classHpAdd(rset.getDouble(HP_ADD));
				lvlDat.set_classHpModifier(rset.getDouble(HP_MOD));
				lvlDat.set_classCpBase(rset.getDouble(CP_BASE));
				lvlDat.set_classCpAdd(rset.getDouble(CP_ADD));
				lvlDat.set_classCpModifier(rset.getDouble(CP_MOD));
				lvlDat.set_classMpBase(rset.getDouble(MP_BASE));
				lvlDat.set_classMpAdd(rset.getDouble(MP_ADD));
				lvlDat.set_classMpModifier(rset.getDouble(MP_MOD));

				_lvltable.put(lvlDat.get_classid(), lvlDat);
			}

			_log.info("LevelUpData: Loaded " + _lvltable.size() + " Character Level Up Templates.");
		}
		catch (SQLException e)
		{
			_log.warn("Error while creating Lvl up data table", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	/**
	 * @param template id
	 * @return
	 */
	public LvlupData getTemplate(int classId)
	{
		return _lvltable.get(classId);
	}

	public LvlupData getTemplate(ClassId classId)
	{
		return _lvltable.get(classId.getId());
	}
}

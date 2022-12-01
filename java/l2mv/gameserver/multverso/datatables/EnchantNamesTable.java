/*
 * Copyright (C) 2004-2013 L2J Server
 * This file is part of L2J Server.
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.multverso.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;

/**
 * Table to know all the skills enchant names
 *
 * @author Synerge
 */
public class EnchantNamesTable
{
	private static final Logger _log = LoggerFactory.getLogger(EnchantNamesTable.class);

	private final Map<String, String> _enchantNames = new HashMap<>();

	public EnchantNamesTable()
	{
		load();
	}

	public void load()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement st = con.prepareStatement("SELECT * FROM enchant_names WHERE skill_id > 0"); ResultSet rs = st.executeQuery())
		{
			while (rs.next())
			{
				_enchantNames.put(rs.getInt("skill_id") + "-" + rs.getInt("skill_lvl") / 100, rs.getString("enchant_name"));
			}

			_log.info(getClass().getSimpleName() + ": Loaded: " + _enchantNames.size() + " skill enchant names");
		}
		catch (Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Error while loading skill enchant names: ", e);
		}
	}

	/**
	 * @param skillId
	 * @param enchantType
	 * @return Get the enchant name of a certain enchant path of that skill
	 */
	public String getEnchantName(int skillId, int enchantType)
	{
		return _enchantNames.get(skillId + "-" + enchantType);
	}

	public static EnchantNamesTable getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final EnchantNamesTable _instance = new EnchantNamesTable();
	}
}

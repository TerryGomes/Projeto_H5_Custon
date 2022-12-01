/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * This file is part of Vote Rewarding System.
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.multverso.votingengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;

/**
 * @author UnAfraid
 */
public class VotingRewardCache
{
	private static final Logger _log = LoggerFactory.getLogger(VotingRewardCache.class);
	// SQL Queries
	private static final String INSERT_QUERY = "INSERT INTO mods_voting_reward (data, scope, time) VALUES (?, ?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM mods_voting_reward WHERE time < ?";
	private static final String SELECT_QUERY = "SELECT * FROM mods_voting_reward";
	// Constants
	private static final long VOTING_INTERVAL = TimeUnit.HOURS.toMillis(12);
	// Cache
	private static final Map<UserScope, ScopeContainer> VOTTERS_CACHE = new EnumMap<>(UserScope.class);

	public final void load()
	{
		VOTTERS_CACHE.clear();
		// Initialize the cache
		for (UserScope scope : UserScope.values())
		{
			VOTTERS_CACHE.put(scope, new ScopeContainer());
		}
		// Cleanup old entries and load the data for votters
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_QUERY); Statement st = con.createStatement())
		{
			ps.setLong(1, System.currentTimeMillis());
			ps.execute();
			// Load the data
			try (ResultSet rset = st.executeQuery(SELECT_QUERY))
			{
				while (rset.next())
				{
					final String data = rset.getString("data");
					final UserScope scope = UserScope.findByName(rset.getString("scope"));
					final Long time = rset.getLong("time");
					if (scope != null)
					{
						VOTTERS_CACHE.get(scope).registerVotter(data, time);
					}
				}
			}
		}
		catch (final SQLException e)
		{
			_log.error("Failed to load voting reward data", e);
		}
	}

	public void markAsVotted(Player player)
	{
		final long reuse = System.currentTimeMillis() + VOTING_INTERVAL;
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_QUERY))
		{
			for (UserScope scope : UserScope.values())
			{
				if (scope.isSupported(player))
				{
					final String data = scope.getData(player);
					final ScopeContainer container = VOTTERS_CACHE.get(scope);
					container.registerVotter(data, reuse);
					ps.setString(1, data);
					ps.setString(2, scope.name());
					ps.setLong(3, reuse);
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch (final SQLException e)
		{
			_log.error("Failed to store voting reward data", e);
		}
	}

	public long getLastVotedTime(Player player)
	{
		long lastVotedTime = 0;
		for (Entry<UserScope, ScopeContainer> entry : VOTTERS_CACHE.entrySet())
		{
			if (entry.getKey().isSupported(player))
			{
				final String data = entry.getKey().getData(player);
				final long reuse = entry.getValue().getReuse(data);
				if (reuse > lastVotedTime)
				{
					lastVotedTime = reuse;
				}
			}
		}
		return lastVotedTime;
	}

	public static final VotingRewardCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final VotingRewardCache INSTANCE = new VotingRewardCache();
	}
}

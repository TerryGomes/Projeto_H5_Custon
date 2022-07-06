package l2mv.gameserver.model.entity.tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Debug;

public class TournamentTeamsManager
{
	private static final Logger LOG = LoggerFactory.getLogger(TournamentTeamsManager.class);

	private final List<Team> _teams = new CopyOnWriteArrayList<Team>();
	private final AtomicInteger _lastObjectId = new AtomicInteger(-1);

	private TournamentTeamsManager()
	{
		loadTeamsFromDatabase();
	}

	public Team createNewTeam(List<Player> players)
	{
		final int[] playerIds = new int[players.size()];
		for (int i = 0; i < playerIds.length; ++i)
		{
			playerIds[i] = players.get(i).getObjectId();
		}
		final int newId = _lastObjectId.incrementAndGet();
		final Team createdTeam = new Team(newId, playerIds);
		_teams.add(createdTeam);
		saveInDatabase(createdTeam);
		return createdTeam;
	}

	public void removeTeam(Team team)
	{
		_teams.remove(team);
		deleteFromDatabase(team);
	}

	public List<Team> getTeamsForIterate()
	{
		return _teams;
	}

	public List<Team> getTeamsCopy()
	{
		return new ArrayList<Team>(_teams);
	}

	public int getTeamsCount()
	{
		return _teams.size();
	}

	@Nullable
	public Team getTeamById(int teamId)
	{
		for (Team team : _teams)
		{
			if (team.getId() == teamId)
			{
				return team;
			}
		}
		return null;
	}

	@Nullable
	public Team getTeamByFinalPosition(int finalPosition)
	{
		for (Team team : _teams)
		{
			if (team.getFinalPosition() == finalPosition)
			{
				return team;
			}
		}
		return null;
	}

	public boolean isRegistered(Player player)
	{
		final int playerId = player.getObjectId();
		for (Team team : _teams)
		{
			if (team.isMember(playerId))
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	public Team getMyTeam(Player player)
	{
		final int playerId = player.getObjectId();
		for (Team team : _teams)
		{
			if (team.isMember(playerId))
			{
				return team;
			}
		}
		return null;
	}

	private void loadTeamsFromDatabase()
	{
		final int playersPerTeam = ConfigHolder.getInt("TournamentPlayersInTeam");
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM tournament_teams"); final ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int teamId = rset.getInt("team_id");
				final int finalPosition = rset.getInt("final_position");
				final int[] playerIds = new int[playersPerTeam];
				for (int i = 1; i <= playersPerTeam; ++i)
				{
					playerIds[i - 1] = rset.getInt("player_" + i + "_id");
				}
				_teams.add(new Team(teamId, playerIds, finalPosition));
				if (_lastObjectId.get() < teamId)
				{
					_lastObjectId.set(teamId);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Tournament Teams!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "loadTeamsFromDatabase", _teams);
		}
	}

	public static void saveInDatabase(Team createdTeam)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO tournament_teams VALUES(?,?,?,?,?,?,?,?,?,?,?)"))
		{
			statement.setInt(1, createdTeam.getId());
			statement.setInt(2, createdTeam.getFinalPosition());
			final int[] playerIds = createdTeam.getPlayerIdsForIterate();
			for (int i = 0; i < 9; ++i)
			{
				if (playerIds.length > i)
				{
					statement.setInt(3 + i, playerIds[i]);
				}
				else
				{
					statement.setInt(3 + i, -1);
				}
			}
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while saving " + createdTeam + " to the Database!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(TournamentTeamsManager.class, "saveInDatabase", createdTeam);
		}
	}

	private static void deleteFromDatabase(Team team)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM tournament_teams WHERE team_id = ?"))
		{
			statement.setInt(1, team.getId());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while deleting " + team + " from the Database!", e);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(TournamentTeamsManager.class, "deletingFromDatabase", team);
		}
	}

	public static TournamentTeamsManager getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final TournamentTeamsManager instance = new TournamentTeamsManager();
	}
}

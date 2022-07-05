package l2f.gameserver.model.entity.tournament;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2f.commons.annotations.Nullable;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;

public class BattleRecord
{
	private final int _id;
	private int _team1Id;
	private int _team2Id;
	private final int _round;
	private long _battleDate;
	private int _winnerId;
	private int _winnerWonGames;
	private ScheduledFuture<?> _startBattleThread;
	private BattleInstance _battleInstance;

	protected BattleRecord(int id, int team1Id, int team2Id, int round, long battleDate)
	{
		_id = id;
		_team1Id = team1Id;
		_team2Id = team2Id;
		_round = round;
		_battleDate = battleDate;
		_winnerId = -1;
		_winnerWonGames = -1;
	}

	protected BattleRecord(int id, int team1Id, int team2Id, int round, long battleDate, int winnerId, int winnerWonGames)
	{
		_id = id;
		_team1Id = team1Id;
		_team2Id = team2Id;
		_round = round;
		_battleDate = battleDate;
		_winnerId = winnerId;
		_winnerWonGames = winnerWonGames;
	}

	public int getId()
	{
		return _id;
	}

	public int getTeam1Id()
	{
		return _team1Id;
	}

	public Team getTeam1()
	{
		return TournamentTeamsManager.getInstance().getTeamById(_team1Id);
	}

	public void setTeam1Id(int id)
	{
		_team1Id = id;
	}

	public int getTeam2Id()
	{
		return _team2Id;
	}

	@Nullable
	public Team getTeam2()
	{
		return TournamentTeamsManager.getInstance().getTeamById(_team2Id);
	}

	public void setTeam2Id(int id)
	{
		_team2Id = id;
	}

	public int[] getTeamIds()
	{
		return new int[]
		{
			_team1Id,
			_team2Id
		};
	}

	public Team[] getTeams()
	{
		return new Team[]
		{
			getTeam1(),
			getTeam2()
		};
	}

	public int getTeamIndex(Team team)
	{
		if (_team1Id == team.getId())
		{
			return 0;
		}
		if (_team2Id == team.getId())
		{
			return 1;
		}
		return -1;
	}

	public int getTeamIndex(Playable fighter)
	{
		final Team team = getTeam(fighter);
		if (team == null)
		{
			return -1;
		}
		return this.getTeamIndex(team);
	}

	public boolean isFirstTeam(Team team)
	{
		return _team1Id == team.getId();
	}

	@Nullable
	public Team getTeam(Playable fighter)
	{
		final int objectId = fighter.getPlayer().getObjectId();
		for (Team team : getTeams())
		{
			if (team != null)
			{
				for (Player fighterInTeam : team.getOnlinePlayers())
				{
					if (fighterInTeam.getObjectId() == objectId)
					{
						return team;
					}
				}
			}
		}
		return null;
	}

	public Team getSecondTeam(Team firstTeam)
	{
		for (Team team : getTeams())
		{
			if (team == null && firstTeam != null || team.getId() != firstTeam.getId())
			{
				return team;
			}
		}
		return null;
	}

	public boolean isTeamFighting(Team team)
	{
		final int teamId = team.getId();
		for (Team fightingTeam : getTeams())
		{
			if (fightingTeam != null && fightingTeam.getId() == teamId)
			{
				return true;
			}
		}
		return false;
	}

	public List<Player> getOnlinePlayers()
	{
		final Team[] teams = getTeams();
		final List<Player> players = teams[0].getOnlinePlayers();
		if (teams[1] != null)
		{
			players.addAll(teams[1].getOnlinePlayers());
		}
		return players;
	}

	public int getRound()
	{
		return _round;
	}

	public boolean teamsWonAllBattles()
	{
		return !getTeam1().lostAnyRound();
	}

	public void setBattleDate(long battleDate)
	{
		_battleDate = battleDate;
	}

	public long getBattleDate()
	{
		return _battleDate;
	}

	public boolean isNowLive()
	{
		return _battleInstance != null;
	}

	public boolean isPastBattle()
	{
		return _winnerId >= 0;
	}

	public int getWinnerId()
	{
		return _winnerId;
	}

	@Nullable
	public Team getWinnerTeam()
	{
		return TournamentTeamsManager.getInstance().getTeamById(_winnerId);
	}

	public int getWinnerWonGames()
	{
		return _winnerWonGames;
	}

	public void setBattleWinner(int winnerId, int winnerWonGames)
	{
		_winnerId = winnerId;
		_winnerWonGames = winnerWonGames;
	}

	public void setStartBattleThread(ScheduledFuture<?> startBattleThread)
	{
		_startBattleThread = startBattleThread;
	}

	@Nullable
	public ScheduledFuture<?> getStartBattleThread()
	{
		return _startBattleThread;
	}

	public void setBattleInstance(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	public BattleInstance getBattleInstance()
	{
		return _battleInstance;
	}

	public void updateInDatabase()
	{
		BattleScheduleManager.replaceBattleRecordInDatabase(this);
	}

	public int getIndexInRound()
	{
		return BattleScheduleManager.getInstance().getIndexInRound(this);
	}

	@Override
	public String toString()
	{
		return "BattleRecord{id=" + _id + ", team1Id=" + _team1Id + ", team2Id=" + _team2Id + ", round=" + _round + ", battleDate=" + _battleDate + ", winnerId=" + _winnerId + ", winnerWonGames="
					+ _winnerWonGames + '}';
	}
}

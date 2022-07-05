package l2f.gameserver.model.entity.events.fightclubmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;

public class FightClubTeam implements Serializable
{
	public static enum TEAM_NAMES
	{
		Red(0x162ee1), Blue(0xb53e41), Green(0x3eb541), Yellow(0x2efdff), Gray(0x808080), Orange(0x0087f9), Black(0x161616), White(0xffffff), Violet(0xba2785), Cyan(0xe3e136), Pink(0xde6def);

		public int _nameColor;

		private TEAM_NAMES(int nameColor)
		{
			_nameColor = nameColor;
		}
	}

	private final int _uniqueIndex;
	private FightClubTeamType _teamType;
	private int _teamTypeIndex = -1;
	private String _name;
	private final List<FightClubPlayer> _players = new ArrayList<>();
	private int _score;

	public FightClubTeam(int index)
	{
		_uniqueIndex = index;
		chooseName();
	}

	public int getUniqueIndex()
	{
		return _uniqueIndex;
	}

	public void setTeamType(AbstractFightClub event, FightClubTeamType teamType)
	{
		_teamType = teamType;
		_teamTypeIndex = calcNewTypeIndex(event);
	}

	public FightClubTeamType getTeamType()
	{
		return _teamType;
	}

	public int getIndexByType()
	{
		return _teamTypeIndex;
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public String chooseName()
	{
		_name = TEAM_NAMES.values()[(_uniqueIndex - 1)].toString();
		return _name;
	}

	public int getNickColor()
	{
		return TEAM_NAMES.values()[_uniqueIndex - 1]._nameColor;
	}

	public List<FightClubPlayer> getPlayers()
	{
		return _players;
	}

	public void addPlayer(FightClubPlayer player)
	{
		_players.add(player);
	}

	public void removePlayer(FightClubPlayer player)
	{
		_players.remove(player);
	}

	public void setScore(int newScore)
	{
		_score = newScore;
	}

	public void incScore(int by)
	{
		_score += by;
	}

	public int getScore()
	{
		return _score;
	}

	private int calcNewTypeIndex(AbstractFightClub event)
	{
		int teamTypeCount = event.getMap().getPositionsCount(_teamType);
		List<FightClubTeam> allTeams = event.getTeams();
		final Set<Integer> usedIndexes = new HashSet<>(allTeams.size());
		for (FightClubTeam team : allTeams)
		{
			if (_uniqueIndex != team.getUniqueIndex())
			{
				if (_teamType == team.getTeamType())
				{
					usedIndexes.add(team.getIndexByType());
				}
			}
		}
		int safeLoopLeft = 30;
		int newTypeIndex;
		do
		{
			newTypeIndex = Rnd.get(0, teamTypeCount - 1);
			safeLoopLeft--;
			if (safeLoopLeft <= 0)
			{
				return newTypeIndex;
			}
		}
		while (usedIndexes.contains(newTypeIndex));
		return newTypeIndex;
	}
}

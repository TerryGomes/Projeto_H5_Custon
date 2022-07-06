package l2mv.gameserver.model.entity.events.fightclubmanager;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import l2mv.commons.annotations.Nullable;
import l2mv.commons.collections.MultiValueSet;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.templates.ZoneTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.NpcGroupLocation;

public class FightClubMap
{
	public static final String DOORS_NAME = "doors";
	public static final String INVUL_DOORS_NAME = "invulDoors";
	public static final String DELETED_DOORS_NAME = "deletedDoors";
	private final String _name;
	private final String[] _events;
	private final Map<FightClubTeamType, int[]> _teamsCounts;
	private final int _minAllPlayers;
	private final int _maxAllPlayers;
	private final Map<FightClubTeamType, LinkedList<Location[]>> _teamSpawns;
	private final Map<String, ZoneTemplate> _territories;
	private final Location[] _keyLocations;
	private final NpcGroupLocation[] _npcLocations;
	private final MultiValueSet<String> _params;

	public FightClubMap(MultiValueSet<String> params, Map<FightClubTeamType, LinkedList<Location[]>> teamSpawns, Map<String, ZoneTemplate> territories, Location[] keyLocations, NpcGroupLocation[] npcLocations)
	{
		// Params
		_params = params;
		_name = params.getString("name");
		_events = params.getString("events").split(";");
		_minAllPlayers = Integer.parseInt(params.getString("minAllPlayers", "-1"));
		_maxAllPlayers = Integer.parseInt(params.getString("maxAllPlayers", "-1"));

		// Team Counts
		_teamsCounts = new EnumMap<>(FightClubTeamType.class);
		for (FightClubTeamType teamType : FightClubTeamType.values())
		{
			if (params.containsKey("teamsCount_" + teamType.name()))
			{
				String[] teamCounts = params.getString("teamsCount_" + teamType.name()).split(";");
				int[] intCount = new int[teamCounts.length];
				for (int i = 0; i < teamCounts.length; i++)
				{
					intCount[i] = Integer.parseInt(teamCounts[i]);
				}
				_teamsCounts.put(teamType, intCount);
			}
		}
		_teamSpawns = teamSpawns;
		_territories = territories;
		_keyLocations = keyLocations;
		_npcLocations = npcLocations;
	}

	public String getName()
	{
		return _name;
	}

	public String[] getEvents()
	{
		return _events;
	}

	public Map<FightClubTeamType, int[]> getTeamCounts()
	{
		return _teamsCounts;
	}

	public int getMinAllPlayers()
	{
		return _minAllPlayers;
	}

	public int getMaxAllPlayers()
	{
		return _maxAllPlayers;
	}

	public List<FightClubTeamType> getAllTeamTypes()
	{
		List<FightClubTeamType> teamTypes = new LinkedList<>();
		for (Map.Entry<FightClubTeamType, LinkedList<Location[]>> entry : _teamSpawns.entrySet())
		{
			int countPerType = entry.getValue().size();
			for (int i = 0; i < countPerType; i++)
			{
				teamTypes.add(entry.getKey());
			}
		}
		return teamTypes;
	}

	public int getPositionsCount(FightClubTeamType teamType)
	{
		int count = 0;
		for (Map.Entry<FightClubTeamType, LinkedList<Location[]>> entry : _teamSpawns.entrySet())
		{
			if (entry.getKey() == teamType)
			{
				count += entry.getValue().size();
			}
		}
		return count;
	}

	public Location[] getTeamSpawns(AbstractFightClub event, FightClubTeam team)
	{
		return _teamSpawns.get(team.getTeamType()).get(team.getIndexByType());
	}

	public Location[] getPlayerSpawns(FightClubTeamType teamType)
	{
		return _teamSpawns.get(teamType).get(0);
	}

	public Map<String, ZoneTemplate> getTerritories()
	{
		return _territories;
	}

	public Location[] getKeyLocations()
	{
		return _keyLocations;
	}

	@Nullable
	public NpcGroupLocation[] getNpcLocations()
	{
		return _npcLocations;
	}

	public MultiValueSet<String> getSet()
	{
		return _params;
	}
}

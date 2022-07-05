package l2f.gameserver.model.entity.tournament;

import java.util.Map;

import l2f.gameserver.data.xml.holder.ZoneHolder;
import l2f.gameserver.templates.ZoneTemplate;
import l2f.gameserver.utils.Location;

public class TournamentMap
{
	private final String _name;
	private final ZoneTemplate _zoneTemplate;
	private final Location[] _teamSpawnLocations;
	private final Location _observersLocation;

	public TournamentMap(String name, String territoryName, Map<String, Location> locations)
	{
		_name = name;
		_zoneTemplate = ZoneHolder.getInstance().getTemplate(territoryName);
		_teamSpawnLocations = new Location[2];
		_teamSpawnLocations[0] = locations.get("team_1_spawn");
		_teamSpawnLocations[1] = locations.get("team_2_spawn");
		_observersLocation = locations.get("observers_spawn");
	}

	public String getName()
	{
		return _name;
	}

	public ZoneTemplate getZoneTemplate()
	{
		return _zoneTemplate;
	}

	public Location[] getTeamSpawnLocations()
	{
		return _teamSpawnLocations;
	}

	public Location getTeamSpawnLocation(int teamIndex)
	{
		return _teamSpawnLocations[teamIndex];
	}

	public Location getObserversLocation()
	{
		return _observersLocation;
	}

	@Override
	public String toString()
	{
		return "TournamentMap{name='" + _name + '\'' + '}';
	}
}

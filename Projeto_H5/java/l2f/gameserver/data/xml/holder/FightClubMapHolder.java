package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubMap;

public final class FightClubMapHolder extends AbstractHolder
{
	private static final FightClubMapHolder _instance = new FightClubMapHolder();
	private final ArrayList<FightClubMap> _maps = new ArrayList<>();

	public static FightClubMapHolder getInstance()
	{
		return _instance;
	}

	public void addMap(FightClubMap map)
	{
		_maps.add(map);
	}

	public List<FightClubMap> getMapsForEvent(String eventName)
	{
		List<FightClubMap> maps = new ArrayList<>();
		for (FightClubMap map : _maps)
		{
			for (String possibleName : map.getEvents())
			{
				if (possibleName.equalsIgnoreCase(eventName))
				{
					maps.add(map);
				}
			}
		}
		return maps;
	}

	public int getMinPlayersForEvent(String eventName)
	{
		List<FightClubMap> allMaps = getMapsForEvent(eventName);
		int minPlayers = Integer.MAX_VALUE;
		for (FightClubMap map : allMaps)
		{
			int newMin = map.getMinAllPlayers();
			if (newMin < minPlayers)
			{
				minPlayers = newMin;
			}
		}

		return minPlayers;
	}

	public int getMaxPlayersForEvent(String eventName)
	{
		List<FightClubMap> allMaps = getMapsForEvent(eventName);
		int maxPlayers = 0;

		for (FightClubMap map : allMaps)
		{
			int newMax = map.getMaxAllPlayers();
			if (newMax > maxPlayers)
			{
				maxPlayers = newMax;
			}
		}

		return maxPlayers;
	}

	@Override
	protected void process()
	{
		_maps.trimToSize();
	}

	@Override
	public int size()
	{
		return _maps.size();
	}

	@Override
	public void clear()
	{
		_maps.clear();
	}
}

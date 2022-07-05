package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.data.xml.AbstractHolder;
import l2f.commons.util.Rnd;
import l2f.gameserver.model.entity.tournament.TournamentMap;

public final class TournamentMapHolder extends AbstractHolder
{
	private final List<TournamentMap> _maps = new ArrayList<>();

	public void addMap(TournamentMap map)
	{
		_maps.add(map);
	}

	public TournamentMap getRandomMap()
	{
		return _maps.get(Rnd.get(0, _maps.size() - 1));
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

	@Override
	public String toString()
	{
		return "TournamentMapHolder{maps=" + _maps + '}';
	}

	public static TournamentMapHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final TournamentMapHolder instance = new TournamentMapHolder();
	}
}

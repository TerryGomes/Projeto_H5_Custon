package l2f.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.data.xml.AbstractHolder;

public final class FoundationHolder extends AbstractHolder
{
	private static final FoundationHolder _instance = new FoundationHolder();

	private final Map<Integer, Integer> _foundation = new HashMap<>();

	public static FoundationHolder getInstance()
	{
		return _instance;
	}

	public void addFoundation(int simple, int found)
	{
		_foundation.put(Integer.valueOf(simple), Integer.valueOf(found));
	}

	public int getFoundation(int id)
	{
		if (_foundation.containsKey(Integer.valueOf(id)))
		{
			return _foundation.get(Integer.valueOf(id));
		}
		return -1;
	}

	@Override
	public int size()
	{
		return _foundation.size();
	}

	@Override
	public void clear()
	{
		_foundation.clear();
	}
}
package l2f.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.exchange.Change;

public final class ExchangeItemHolder extends AbstractHolder
{
	private static final ExchangeItemHolder _instance = new ExchangeItemHolder();

	public static ExchangeItemHolder getInstance()
	{
		return _instance;
	}

	private final Map<Integer, Change> _changes = new HashMap<Integer, Change>();
	private final Map<Integer, Change> _upgrades = new HashMap<Integer, Change>();

	public void addChanges(Change armorset)
	{
		if (armorset.isUpgrade())
		{
			_upgrades.put(armorset.getId(), armorset);
		}
		else
		{
			_changes.put(armorset.getId(), armorset);
		}
	}

	public Change getChanges(int id, boolean isUpgrade)
	{
		if (isUpgrade)
		{
			return _upgrades.get(id);
		}
		return _changes.get(id);
	}

	@Override
	public int size()
	{
		return _changes.size() + _upgrades.size();
	}

	@Override
	public void clear()
	{
		_changes.clear();
		_upgrades.clear();
	}
}

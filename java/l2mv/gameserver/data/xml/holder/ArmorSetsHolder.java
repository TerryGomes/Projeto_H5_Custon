package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.ArmorSet;

public final class ArmorSetsHolder extends AbstractHolder
{
	private static final ArmorSetsHolder _instance = new ArmorSetsHolder();

	public static ArmorSetsHolder getInstance()
	{
		return _instance;
	}

	private final List<ArmorSet> _armorSets = new ArrayList<ArmorSet>();

	public void addArmorSet(ArmorSet armorset)
	{
		_armorSets.add(armorset);
	}

	public ArmorSet getArmorSet(int chestItemId)
	{
		for (ArmorSet as : _armorSets)
		{
			if (as.getChestItemIds().contains(chestItemId))
			{
				return as;
			}
		}
		return null;
	}

	@Override
	public int size()
	{
		return _armorSets.size();
	}

	public List<ArmorSet> getAllSets()
	{
		return _armorSets;
	}

	@Override
	public void clear()
	{
		_armorSets.clear();
	}
}

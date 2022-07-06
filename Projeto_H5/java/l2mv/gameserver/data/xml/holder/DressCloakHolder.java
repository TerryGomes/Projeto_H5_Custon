package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.DressCloakData;

public final class DressCloakHolder extends AbstractHolder
{
	private static final DressCloakHolder _instance = new DressCloakHolder();

	public static DressCloakHolder getInstance()
	{
		return _instance;
	}

	private List<DressCloakData> _cloak = new ArrayList<DressCloakData>();

	public void addCloak(DressCloakData cloak)
	{
		_cloak.add(cloak);
	}

	public List<DressCloakData> getAllCloaks()
	{
		return _cloak;
	}

	public DressCloakData getCloak(int id)
	{
		for (DressCloakData cloak : _cloak)
		{
			if (cloak.getId() == id)
			{
				return cloak;
			}
		}

		return null;
	}

	@Override
	public int size()
	{
		return _cloak.size();
	}

	@Override
	public void clear()
	{
		_cloak.clear();
	}
}

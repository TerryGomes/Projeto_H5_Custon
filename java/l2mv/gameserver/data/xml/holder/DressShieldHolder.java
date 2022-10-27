package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.DressShieldData;

public final class DressShieldHolder extends AbstractHolder
{
	private static final DressShieldHolder _instance = new DressShieldHolder();

	public static DressShieldHolder getInstance()
	{
		return _instance;
	}

	private List<DressShieldData> _shield = new ArrayList<DressShieldData>();

	public void addShield(DressShieldData shield)
	{
		_shield.add(shield);
	}

	public List<DressShieldData> getAllShields()
	{
		return _shield;
	}

	public DressShieldData getShield(int id)
	{
		for (DressShieldData shield : _shield)
		{
			if (shield.getId() == id)
			{
				return shield;
			}
		}

		return null;
	}

	@Override
	public int size()
	{
		return _shield.size();
	}

	@Override
	public void clear()
	{
		_shield.clear();
	}
}

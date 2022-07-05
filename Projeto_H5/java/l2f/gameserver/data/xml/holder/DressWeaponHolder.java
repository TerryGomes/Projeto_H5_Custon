package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.DressWeaponData;

public final class DressWeaponHolder extends AbstractHolder
{
	private static final DressWeaponHolder _instance = new DressWeaponHolder();

	public static DressWeaponHolder getInstance()
	{
		return _instance;
	}

	private List<DressWeaponData> _weapons = new ArrayList<DressWeaponData>();

	public void addWeapon(DressWeaponData weapon)
	{
		_weapons.add(weapon);
	}

	public List<DressWeaponData> getAllWeapons()
	{
		return _weapons;
	}

	public DressWeaponData getWeapon(int id)
	{
		for (DressWeaponData weapon : _weapons)
		{
			if (weapon.getId() == id)
			{
				return weapon;
			}
		}

		return null;
	}

	@Override
	public int size()
	{
		return _weapons.size();
	}

	@Override
	public void clear()
	{
		_weapons.clear();
	}
}

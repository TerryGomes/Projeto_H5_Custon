package l2f.gameserver.model;

public class PetData
{
	private int _id;
	private int _level;
	private int _feedMax;
	private int _feedBattle;
	private int _feedNormal;
	private int _pAtk;
	private int _pDef;
	private int _mAtk;
	private int _mDef;
	private int _hp;
	private int _mp;
	private int _hpRegen;
	private int _mpRegen;
	private long _exp;
	private int _accuracy;
	private int _evasion;
	private int _critical;
	private int _speed;
	private int _atkSpeed;
	private int _castSpeed;
	private int _maxLoad;

	private int _controlItemId;
	private int _foodId;
	private int _minLevel;
	private int _addFed;
	private boolean _isMountable;

	public int getFeedBattle()
	{
		return _feedBattle;
	}

	public void setFeedBattle(int feedBattle)
	{
		_feedBattle = feedBattle;
	}

	public int getFeedNormal()
	{
		return _feedNormal;
	}

	public void setFeedNormal(int feedNormal)
	{
		_feedNormal = feedNormal;
	}

	public int getHP()
	{
		return _hp;
	}

	public void setHP(int petHP)
	{
		_hp = petHP;
	}

	public int getID()
	{
		return _id;
	}

	public void setID(int petID)
	{
		_id = petID;
	}

	public int getLevel()
	{
		return _level;
	}

	public void setLevel(int petLevel)
	{
		_level = petLevel;
	}

	public int getMAtk()
	{
		return _mAtk;
	}

	public void setMAtk(int mAtk)
	{
		_mAtk = mAtk;
	}

	public int getFeedMax()
	{
		return _feedMax;
	}

	public void setFeedMax(int feedMax)
	{
		_feedMax = feedMax;
	}

	public int getMDef()
	{
		return _mDef;
	}

	public void setMDef(int mDef)
	{
		_mDef = mDef;
	}

	public long getExp()
	{
		return _exp;
	}

	public void setExp(long exp)
	{
		_exp = exp;
	}

	public int getMP()
	{
		return _mp;
	}

	public void setMP(int mp)
	{
		_mp = mp;
	}

	public int getPAtk()
	{
		return _pAtk;
	}

	public void setPAtk(int pAtk)
	{
		_pAtk = pAtk;
	}

	public int getPDef()
	{
		return _pDef;
	}

	public int getAccuracy()
	{
		return _accuracy;
	}

	public int getEvasion()
	{
		return _evasion;
	}

	public int getCritical()
	{
		return _critical;
	}

	public int getSpeed()
	{
		return _speed;
	}

	public int getAtkSpeed()
	{
		return _atkSpeed;
	}

	public int getCastSpeed()
	{
		return _castSpeed;
	}

	public int getMaxLoad()
	{
		return _maxLoad != 0 ? _maxLoad : _level * 300;
	}

	public void setPDef(int pDef)
	{
		_pDef = pDef;
	}

	public int getHpRegen()
	{
		return _hpRegen;
	}

	public void setHpRegen(int hpRegen)
	{
		_hpRegen = hpRegen;
	}

	public int getMpRegen()
	{
		return _mpRegen;
	}

	public void setMpRegen(int mpRegen)
	{
		_mpRegen = mpRegen;
	}

	public void setAccuracy(int accuracy)
	{
		_accuracy = accuracy;
	}

	public void setEvasion(int evasion)
	{
		_evasion = evasion;
	}

	public void setCritical(int critical)
	{
		_critical = critical;
	}

	public void setSpeed(int speed)
	{
		_speed = speed;
	}

	public void setAtkSpeed(int atkSpeed)
	{
		_atkSpeed = atkSpeed;
	}

	public void setCastSpeed(int castSpeed)
	{
		_castSpeed = castSpeed;
	}

	public void setMaxLoad(int maxLoad)
	{
		_maxLoad = maxLoad;
	}

	public int getControlItemId()
	{
		return _controlItemId;
	}

	public void setControlItemId(int itemId)
	{
		_controlItemId = itemId;
	}

	public int getFoodId()
	{
		return _foodId;
	}

	public void setFoodId(int id)
	{
		_foodId = id;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public void setMinLevel(int level)
	{
		_minLevel = level;
	}

	public int getAddFed()
	{
		return _addFed;
	}

	public void setAddFed(int addFed)
	{
		_addFed = addFed;
	}

	public boolean isMountable()
	{
		return _isMountable;
	}

	public void setMountable(boolean mountable)
	{
		_isMountable = mountable;
	}
}
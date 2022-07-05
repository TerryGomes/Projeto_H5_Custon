package l2f.gameserver.model;

import l2f.gameserver.Config;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.Experience;

public class SubClass
{
	public static final int CERTIFICATION_65 = 1 << 0;
	public static final int CERTIFICATION_70 = 1 << 1;
	public static final int CERTIFICATION_75 = 1 << 2;
	public static final int CERTIFICATION_80 = 1 << 3;

	private int _class = 0;
	private long _exp = Experience.LEVEL[Config.ALT_GAME_START_LEVEL_TO_SUBCLASS], minExp = Experience.LEVEL[Config.ALT_GAME_START_LEVEL_TO_SUBCLASS], maxExp = Experience.LEVEL[Experience.LEVEL.length - 1];
	private int _sp = 0;
	private int _level = Config.ALT_GAME_START_LEVEL_TO_SUBCLASS, _certification;
	private double _Hp = 1, _Mp = 1, _Cp = 1;
	private boolean _active = false, _isBase = false;
	private DeathPenalty _dp;

	public SubClass()
	{
	}

	public int getClassId()
	{
		return _class;
	}

	public long getExp()
	{
		return _exp;
	}

	public long getMaxExp()
	{
		return maxExp;
	}

	public void addExp(long val)
	{
		setExp(_exp + val);
	}

	public long getSp()
	{
		return Math.min(_sp, Integer.MAX_VALUE);
	}

	public void addSp(long val)
	{
		setSp(_sp + val);
	}

	public int getLevel()
	{
		return _level;
	}

	public void setClassId(int classId)
	{
		_class = classId;
	}

	public void setExp(long val)
	{
		val = Math.max(val, minExp);
		val = Math.min(val, maxExp);

		_exp = val;
		_level = Experience.getLevel(_exp);
	}

	public void setSp(long spValue)
	{
		spValue = Math.max(spValue, 0);
		spValue = Math.min(spValue, Integer.MAX_VALUE);

		_sp = (int) spValue;
	}

	public void setHp(double hpValue)
	{
		_Hp = hpValue;
	}

	public double getHp()
	{
		return _Hp;
	}

	public void setMp(double mpValue)
	{
		_Mp = mpValue;
	}

	public double getMp()
	{
		return _Mp;
	}

	public void setCp(double cpValue)
	{
		_Cp = cpValue;
	}

	public double getCp()
	{
		return _Cp;
	}

	public void setActive(boolean active)
	{
		_active = active;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setBase(boolean base)
	{
		_isBase = base;
		minExp = Experience.LEVEL[_isBase ? 1 : Config.ALT_GAME_START_LEVEL_TO_SUBCLASS];
		maxExp = Experience.LEVEL[(_isBase ? Experience.getMaxLevel() : Experience.getMaxSubLevel()) + 1] - 1;
	}

	public boolean isBase()
	{
		return _isBase;
	}

	public DeathPenalty getDeathPenalty(Player player)
	{
		if (_dp == null)
		{
			_dp = new DeathPenalty(player, 0);
		}
		return _dp;
	}

	public void setDeathPenalty(DeathPenalty dp)
	{
		_dp = dp;
	}

	public int getCertification()
	{
		return _certification;
	}

	public void setCertification(int certification)
	{
		_certification = certification;
	}

	public void addCertification(int c)
	{
		_certification |= c;
	}

	public boolean isCertificationGet(int v)
	{
		return (_certification & v) == v;
	}

	@Override
	public String toString()
	{
		return ClassId.VALUES[_class].toString() + " " + _level;
	}

	public String toStringCB()
	{
		return ClassId.VALUES[_class].toString();
	}
}
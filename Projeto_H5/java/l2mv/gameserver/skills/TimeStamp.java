package l2mv.gameserver.skills;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Skill;

public class TimeStamp
{
	private final int _id;
	private final int _level;
	private final long _reuse;
	private final long _endTime;
	private final boolean _isNormalSkill;

	public TimeStamp(int id, long endTime, long reuse)
	{
		_id = id;
		_level = 0;
		_reuse = reuse;
		_endTime = endTime;
		_isNormalSkill = false;
	}

	public TimeStamp(Skill skill, long reuse)
	{
		this(skill, System.currentTimeMillis() + reuse, reuse);
	}

	public TimeStamp(Skill skill, long endTime, long reuse)
	{
		_id = skill.getId();
		_level = skill.getLevel();
		_reuse = reuse;
		_endTime = endTime;

		// Synerge - For the macro bug we only modify non-static reuses and from normal skills, not items or other special skills
		_isNormalSkill = !skill.isReuseDelayPermanent() && skill.isActive() && !skill.isItemSkill() && !skill.isHandler();
	}

	public long getReuseBasic()
	{
		if (_reuse == 0)
		{
			return getReuseCurrent();
		}
		return _reuse;
	}

	public long getReuseCurrent()
	{
		return Math.max(_endTime - System.currentTimeMillis(), 0);
	}

	public long getEndTime()
	{
		return _endTime;
	}

	public boolean hasNotPassed()
	{
		// Synerge - Retail bug. According to UnAfraid, -1.2sec reuse is caused by missynchronization between client reuse and server reuse.
		// When client has reuse, it doesn't send a packet to the server to use the skill upon click. But when used from macro, sends a packet everytime.
		// L2J Servers never had this bug, so this is a custom way to emulate it - all server cooldowns are 1.2secs less while client reuse remains the same.
		if (Config.ALLOW_MACROS_REUSE_BUG && _isNormalSkill)
		{
			return System.currentTimeMillis() < (_endTime - 1200);
		}

		return System.currentTimeMillis() < _endTime;
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel()
	{
		return _level;
	}
}
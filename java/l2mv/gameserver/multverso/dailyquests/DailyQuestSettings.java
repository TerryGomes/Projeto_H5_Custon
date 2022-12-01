package l2mv.gameserver.multverso.dailyquests;

import l2mv.gameserver.multverso.dailyquests.drops.Droplist;
import l2mv.gameserver.multverso.dailyquests.drops.DroplistGroup;
import l2mv.gameserver.templates.StatsSet;

/**
 * @author UnAfraid
 */
public class DailyQuestSettings
{
	private String _name;
	private String _descr;
	private int _minLevel = 1;
	private int _maxLevel = 86;
	private boolean _enabled = true;
	private final Droplist _rewards = new Droplist();
	private final StatsSet _parameters = new StatsSet();
	private boolean _isProtectingReward;

	/********************************************/
	// Name
	/********************************************/
	public void setName(String name)
	{
		_name = name;
	}

	public String getName()
	{
		return _name;
	}

	/********************************************/
	// Description
	/********************************************/
	public void setDescription(String descr)
	{
		_descr = descr;
	}

	public String getDescription()
	{
		return _descr;
	}

	/********************************************/
	// Min Level
	/********************************************/
	public void setMinLevel(int level)
	{
		_minLevel = level;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	/********************************************/
	// Max Level
	/********************************************/
	public void setMaxLevel(int level)
	{
		_maxLevel = level;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	/********************************************/
	// Enabled
	/********************************************/
	public void setEnabled(boolean enabled)
	{
		_enabled = enabled;
	}

	public boolean isEnabled()
	{
		return _enabled;
	}

	/********************************************/
	// Rewards
	/********************************************/

	public void addRewardGroup(DroplistGroup group)
	{
		_rewards.addGroup(group);
	}

	public Droplist getRewards()
	{
		return _rewards;
	}

	/********************************************/
	// Parameters
	/********************************************/

	public void setParameters(StatsSet set)
	{
		_parameters.add(set);
	}

	public StatsSet getParameters()
	{
		return _parameters;
	}

	public void setProtectedReward(boolean val)
	{
		_isProtectingReward = val;
	}

	public boolean isProtectingReward()
	{
		return _isProtectingReward;
	}
}

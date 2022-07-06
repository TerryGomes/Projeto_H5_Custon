/*
 * Copyright (C) 2004-2013 L2J Server
 * This file is part of L2J Server.
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.fandc.dailyquests;

import l2mv.gameserver.fandc.dailyquests.drops.Droplist;
import l2mv.gameserver.fandc.dailyquests.drops.DroplistGroup;
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

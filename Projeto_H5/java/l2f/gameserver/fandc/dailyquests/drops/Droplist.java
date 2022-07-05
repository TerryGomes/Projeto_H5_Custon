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
package l2f.gameserver.fandc.dailyquests.drops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2f.gameserver.fandc.dailyquests.drops.conds.IDroplistCond;
import l2f.gameserver.stats.Env;
import l2f.gameserver.utils.Util;

/**
 * @author UnAfraid
 */
public class Droplist
{
	private List<DroplistGroup> _groups;
	private List<IDroplistCond> _conditions;

	public void addGroup(DroplistGroup group)
	{
		if (_groups == null)
		{
			_groups = new ArrayList<>();
		}
		_groups.add(group);
	}

	public List<DroplistGroup> getGroups()
	{
		return _groups != null ? _groups : Collections.<DroplistGroup>emptyList();
	}

	public void addCondition(IDroplistCond cond)
	{
		if (_conditions == null)
		{
			_conditions = new ArrayList<>();
		}
		_conditions.add(cond);
	}

	public boolean verifyConditions(Env env)
	{
		if (_conditions != null)
		{
			for (IDroplistCond cond : _conditions)
			{
				if (!cond.test(env))
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean hasDrops()
	{
		return (_groups != null) && !_groups.isEmpty();
	}

	public List<DroplistGroup> getDrops()
	{
		return _groups != null ? _groups : Collections.<DroplistGroup>emptyList();
	}

	public List<DroplistItem> calculateDrops(Env env)
	{
		return Util.calculateDroplistItems(env, this);
	}
}

/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * This file is part of Vote Rewarding System.
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.fandc.votingengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2f.commons.util.Rnd;

/**
 * @author UnAfraid
 */
public class RewardList
{
	private List<RewardGroup> _groups;

	public void addGroup(RewardGroup group)
	{
		if (_groups == null)
		{
			_groups = new ArrayList<>();
		}
		_groups.add(group);
	}

	public List<RewardGroup> getGroups()
	{
		return _groups != null ? _groups : Collections.<RewardGroup>emptyList();
	}

	public boolean hasDrops()
	{
		return _groups != null && !_groups.isEmpty();
	}

	public List<RewardGroup> getDrops()
	{
		return _groups != null ? _groups : Collections.<RewardGroup>emptyList();
	}

	public List<RewardItem> calculateDrops()
	{
		List<RewardItem> itemsToDrop = null;
		for (RewardGroup group : _groups)
		{
			final double groupRandom = 100 * Rnd.nextDouble();
			if (groupRandom < group.getChance())
			{
				final double itemRandom = 100 * Rnd.nextDouble();
				float cumulativeChance = 0;
				for (RewardItem item : group.getItems())
				{
					if (itemRandom < (cumulativeChance += item.getChance()))
					{
						if (itemsToDrop == null)
						{
							itemsToDrop = new ArrayList<>();
						}
						itemsToDrop.add(item);
						break;
					}
				}
			}
		}
		return itemsToDrop != null ? itemsToDrop : Collections.<RewardItem>emptyList();
	}
}

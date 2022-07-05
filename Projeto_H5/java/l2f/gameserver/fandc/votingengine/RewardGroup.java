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

/**
 * @author UnAfraid
 */
public class RewardGroup
{
	private final double _chance;
	private List<RewardItem> _items;

	public RewardGroup(float chance)
	{
		_chance = chance;
	}

	public double getChance()
	{
		return _chance;
	}

	public List<RewardItem> getItems()
	{
		return _items != null ? _items : Collections.<RewardItem>emptyList();
	}

	public void addItem(RewardItem item)
	{
		if (_items == null)
		{
			_items = new ArrayList<>();
		}
		_items.add(item);
	}
}

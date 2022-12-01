/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.multverso.dailyquests.drops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid
 */
public class DroplistGroup
{
	private final double _chance;
	private List<DroplistItem> _items;

	public DroplistGroup(float chance)
	{
		_chance = chance;
	}

	public double getChance()
	{
		return _chance;
	}

	public List<DroplistItem> getItems()
	{
		return _items != null ? _items : Collections.<DroplistItem>emptyList();
	}

	public void addItem(DroplistItem item)
	{
		if (_items == null)
		{
			_items = new ArrayList<>();
		}
		_items.add(item);
	}
}

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
package l2mv.gameserver.fandc.dailyquests.drops;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.items.ItemHold;

/**
 * @author UnAfraid
 */
public class DroplistItem extends ItemHold
{
	private final int _min;
	private final int _max;
	private final double _chance;

	public DroplistItem(int itemId, int min, int max, double chance)
	{
		super(itemId, 0);
		_min = min;
		_max = max;
		_chance = chance;
	}

	@Override
	public long getCount()
	{
		return Rnd.get(_min, _max);
	}

	public int getMin()
	{
		return _min;
	}

	public int getMax()
	{
		return _max;
	}

	public double getChance()
	{
		return _chance;
	}
}

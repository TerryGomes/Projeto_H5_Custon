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
package l2f.gameserver.fandc.dailyquests.drops;

/**
 * @author UnAfraid
 */
public class DroplistItemModifier
{
	public static final int GLOBAL_SPOIL_ID = -1;
	public static final int GLOBAL_DROP_ID = -2;

	private final int _itemId;
	private final float _rate;
	private final int _maxQuantity;

	public DroplistItemModifier(int itemId, float rate, int maxQuantity)
	{
		_itemId = itemId;
		_rate = rate;
		_maxQuantity = maxQuantity;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public float getRate()
	{
		return _rate;
	}

	public int getMaxQuantity()
	{
		return _maxQuantity;
	}
}

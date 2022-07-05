/*
 * Copyright (C) 2004-2014 L2J Server
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
package l2f.gameserver.kara.vote;

/**
 * @author Kara`
 */
public class VoteReward
{
	private final int _itemId;
	private final int _count;

	private int _chance;
	private int _enchant;

	public VoteReward(int itemId, int count)
	{
		_itemId = itemId;
		_count = count;
	}

	public void setChance(int chance)
	{
		_chance = chance;
	}

	public int getChance()
	{
		return _chance;
	}

	public void setEnchant(int enchant)
	{
		_enchant = enchant;
	}

	public int getEnchant()
	{
		return _enchant;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getCount()
	{
		return _count;
	}
}
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
package l2mv.gameserver.multverso.dailyquests.drops;

/**
 * @author UnAfraid
 */
public class LevelHolder implements Comparable<LevelHolder>
{
	private final int _minLevel;
	private final int _maxLevel;

	public LevelHolder(int minLevel, int maxLevel)
	{
		_minLevel = minLevel;
		_maxLevel = maxLevel;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	@Override
	public int compareTo(LevelHolder o)
	{
		return Integer.compare(getMinLevel(), o.getMinLevel());
	}
}

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
package l2mv.gameserver.multverso.votingengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScopeContainer
{
	private final Map<String, Long> _votters = new ConcurrentHashMap<>();

	public void registerVotter(String data, long reuse)
	{
		_votters.put(data, reuse);
	}

	public long getReuse(String data)
	{
		if (_votters.containsKey(data))
		{
			final long time = _votters.get(data);
			if (time > System.currentTimeMillis())
			{
				return time;
			}
			// Cleanup expired time-stamps
			_votters.remove(data);
		}
		return 0;
	}
}

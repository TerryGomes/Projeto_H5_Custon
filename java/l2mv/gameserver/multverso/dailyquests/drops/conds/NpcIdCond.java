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
package l2mv.gameserver.multverso.dailyquests.drops.conds;

import java.util.Set;

import l2mv.gameserver.stats.Env;

/**
 * @author UnAfraid
 */
public class NpcIdCond implements IDroplistCond
{
	private final Set<Integer> _npcs;

	public NpcIdCond(Set<Integer> npcIds)
	{
		_npcs = npcIds;
	}

	@Override
	public boolean test(Env env)
	{
		return _npcs.contains(env.character.getNpcId());
	}
}

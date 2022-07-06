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
package l2mv.gameserver.kara.vote;

import l2mv.gameserver.model.Skill;

/**
 * @author Kara`
 */
public class VoteBuff
{
	private final Skill _buff;
	private int _chance;

	public VoteBuff(final Skill buff)
	{
		_buff = buff;
	}

	public void setChance(int chance)
	{
		_chance = chance;
	}

	public int getChance()
	{
		return _chance;
	}

	public Skill getSkill()
	{
		return _buff;
	}
}
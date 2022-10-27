/*
 * Copyright (C) 2004-2015 L2J Server
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
package l2mv.gameserver.skills;

import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTable;

/**
 * Simple class for storing skill id/level.
 * @author BiggBoss
 */
public class SkillHold
{
	private final int _skillId;
	private final int _skillLvl;

	public SkillHold(int skillId, int skillLvl)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
	}

	public SkillHold(Skill skill)
	{
		_skillId = skill.getId();
		_skillLvl = skill.getLevel();
	}

	public final int getSkillId()
	{
		return _skillId;
	}

	public final int getSkillLvl()
	{
		return _skillLvl;
	}

	public final Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(_skillId, Math.max(_skillLvl, 1));
	}

	@Override
	public String toString()
	{
		return "[SkillId: " + _skillId + " Level: " + _skillLvl + "]";
	}
}
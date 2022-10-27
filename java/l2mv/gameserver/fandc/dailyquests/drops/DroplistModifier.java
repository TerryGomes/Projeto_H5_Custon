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
package l2mv.gameserver.fandc.dailyquests.drops;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class DroplistModifier
{
	private Map<Integer, DroplistItemModifier> _dropModifiers;

	public DroplistItemModifier getDropModifier(int itemId, boolean isSpoil)
	{
		if (_dropModifiers == null)
		{
			return null;
		}

		DroplistItemModifier modif = _dropModifiers.get(itemId);
		if (modif == null)
		{
			modif = _dropModifiers.get(isSpoil ? DroplistItemModifier.GLOBAL_SPOIL_ID : DroplistItemModifier.GLOBAL_DROP_ID);
		}
		return modif;
	}

	public Map<Integer, DroplistItemModifier> getDropModifiers()
	{
		return _dropModifiers != null ? _dropModifiers : Collections.<Integer, DroplistItemModifier>emptyMap();
	}

	public void addDropModifier(DroplistItemModifier modifier)
	{
		if (_dropModifiers == null)
		{
			_dropModifiers = new LinkedHashMap<>();
		}
		_dropModifiers.put(modifier.getItemId(), modifier);
	}
}

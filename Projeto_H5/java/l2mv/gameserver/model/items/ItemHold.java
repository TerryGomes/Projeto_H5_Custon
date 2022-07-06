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
package l2mv.gameserver.model.items;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * A simple DTO for items; contains item ID and count.<br>
 * Extended by {@link ItemChanceHolder}, {@link QuestItemHolder}, {@link UniqueItemHolder}.
 * @author UnAfraid
 */
public class ItemHold
{
	private final int _id;
	private final long _count;

	public ItemHold(int id, long count)
	{
		_id = id;
		_count = count;
	}

	/**
	 * @return the ID of the item contained in this object
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @return the count of items contained in this object
	 */
	public long getCount()
	{
		return _count;
	}

	public ItemInstance createItem()
	{
		if ((getCount() < 1) || (ItemHolder.getInstance().getTemplate(getId()) == null))
		{
			return null;
		}

		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), getId());
		item.setLocation(ItemLocation.VOID);
		item.setCount(getCount());
		return item;
	}

	public void giveItem(Playable playable, boolean notify)
	{
		if (playable.isPlayer())
		{
			ItemFunctions.addItem(playable.getPlayer(), _id, getCount(), notify, "ItemHold");
		}
		else if (playable.isPet())
		{
			((PetInstance) playable).getInventory().addItem(_id, getCount(), notify);
		}
	}

	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName() + "] ID: " + _id + ", count: " + getCount();
	}
}

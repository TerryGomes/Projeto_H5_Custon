package l2f.gameserver.model.items;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse
{
	public PcWarehouse(Player owner)
	{
		super(owner.getObjectId());
	}

	public PcWarehouse(int ownerId)
	{
		super(ownerId);
	}

	@Override
	public ItemLocation getItemLocation()
	{
		return ItemLocation.WAREHOUSE;
	}
}
package l2f.gameserver.model.items;

import l2f.gameserver.model.Player;

public class PcFreight extends Warehouse
{
	public PcFreight(Player player)
	{
		super(player.getObjectId());
	}

	public PcFreight(int objectId)
	{
		super(objectId);
	}

	@Override
	public ItemInstance.ItemLocation getItemLocation()
	{
		return ItemInstance.ItemLocation.FREIGHT;
	}
}

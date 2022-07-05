package l2f.gameserver.model.items;

import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.pledge.Clan;

public final class ClanWarehouse extends Warehouse
{
	private Clan owner;

	public ClanWarehouse(Clan clan)
	{
		super(clan.getClanId());
		owner = clan;
	}

	public boolean destroyItem(ItemInstance item, long count, String log)
	{
		return destroyItem(item, count, owner.toString(), log);
	}

	public boolean destroyItem(ItemInstance item, String log)
	{
		return destroyItem(item, owner.toString(), log);
	}

	public boolean destroyItemByItemId(int itemId, long count, String log)
	{
		return destroyItemByItemId(itemId, count, owner.toString(), log);
	}

	public boolean destroyItemByObjectId(int objectId, long count, String log)
	{
		return destroyItemByObjectId(objectId, count, owner.toString(), log);
	}

	public ItemInstance addItem(ItemInstance item, String log)
	{
		return addItem(item, owner.toString(), log);
	}

	public ItemInstance addItem(int itemId, long count, String log)
	{
		return addItem(itemId, count, owner.toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, long count, String log)
	{
		return removeItem(item, count, owner.toString(), log);
	}

	public ItemInstance removeItem(ItemInstance item, String log)
	{
		return removeItem(item, owner.toString(), log);
	}

	public ItemInstance removeItemByItemId(int itemId, long count, String log)
	{
		return removeItemByItemId(itemId, count, owner.toString(), log);
	}

	public ItemInstance removeItemByObjectId(int objectId, long count, String log)
	{
		return removeItemByObjectId(objectId, count, owner.toString(), log);
	}

	@Override
	public ItemLocation getItemLocation()
	{
		return ItemLocation.CLANWH;
	}
}
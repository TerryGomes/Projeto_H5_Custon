package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;

public class PetInventoryUpdate extends L2GameServerPacket
{
	public static final int UNCHANGED = 0;
	public static final int ADDED = 1;
	public static final int MODIFIED = 2;
	public static final int REMOVED = 3;

	private final List<ItemInfo> _items = new ArrayList<ItemInfo>(1);

	public PetInventoryUpdate()
	{
	}

	public PetInventoryUpdate addNewItem(ItemInstance item)
	{
		this.addItem(item).setLastChange(ADDED);
		return this;
	}

	public PetInventoryUpdate addModifiedItem(ItemInstance item)
	{
		this.addItem(item).setLastChange(MODIFIED);
		return this;
	}

	public PetInventoryUpdate addRemovedItem(ItemInstance item)
	{
		this.addItem(item).setLastChange(REMOVED);
		return this;
	}

	private ItemInfo addItem(ItemInstance item)
	{
		ItemInfo info;
		this._items.add(info = new ItemInfo(item));
		return info;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb4);
		this.writeH(this._items.size());
		for (ItemInfo temp : this._items)
		{
			this.writeH(temp.getLastChange());
			this.writeItemInfo(temp);
		}
	}
}
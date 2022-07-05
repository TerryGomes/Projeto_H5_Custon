package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.instances.PetInstance;
import l2f.gameserver.model.items.ItemInstance;

public class PetItemList extends L2GameServerPacket
{
	private ItemInstance[] items;

	public PetItemList(PetInstance cha)
	{
		items = cha.getInventory().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb3);
		writeH(items.length);

		for (ItemInstance item : items)
		{
			writeItemInfo(item);
		}
	}
}
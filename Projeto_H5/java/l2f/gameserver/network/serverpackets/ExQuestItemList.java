package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.LockType;

/**
 * @author VISTALL
 * @date 1:02/23.02.2011
 */
public class ExQuestItemList extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;

	private LockType _lockType;
	private int[] _lockItems;

	public ExQuestItemList(int size, ItemInstance[] t, LockType lockType, int[] lockItems)
	{
		_size = size;
		_items = t;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xC6);
		writeH(_size);

		for (ItemInstance temp : _items)
		{
			if (!temp.getTemplate().isQuest())
			{
				continue;
			}

			writeItemInfo(temp);
		}

		writeH(_lockItems.length);
		if (_lockItems.length > 0)
		{
			writeC(_lockType.ordinal());
			for (int i : _lockItems)
			{
				writeD(i);
			}
		}
	}
}

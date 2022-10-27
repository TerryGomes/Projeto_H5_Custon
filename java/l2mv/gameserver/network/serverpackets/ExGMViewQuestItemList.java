package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 * @date 4:20/06.05.2011
 */
public class ExGMViewQuestItemList extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;

	private int _limit;
	private String _name;

	public ExGMViewQuestItemList(Player player, ItemInstance[] items, int size)
	{
		this._items = items;
		this._size = size;
		this._name = player.getName();
		this._limit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0xC7);
		this.writeS(this._name);
		this.writeD(this._limit);
		this.writeH(this._size);
		for (ItemInstance temp : this._items)
		{
			if (temp.getTemplate().isQuest())
			{
				this.writeItemInfo(temp);
			}
		}
	}
}

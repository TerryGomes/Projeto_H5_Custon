package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.Warehouse;

/**
 * @author VISTALL
 * @date 20:46/16.05.2011
 */
public class PackageSendableList extends L2GameServerPacket
{
	private int _targetObjectId;
	private long _adena;
	private List<ItemInfo> _itemList;

	public PackageSendableList(int objectId, Player cha)
	{
		this._adena = cha.getAdena();
		this._targetObjectId = objectId;

		ItemInstance[] items = cha.getInventory().getItems();
		ArrayUtils.eqSort(items, Warehouse.ItemClassComparator.getInstance());
		this._itemList = new ArrayList<ItemInfo>(items.length);
		for (ItemInstance item : items)
		{
			if (item.getTemplate().isFreightable())
			{
				this._itemList.add(new ItemInfo(item));
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xD2);
		this.writeD(this._targetObjectId);
		this.writeQ(this._adena);
		this.writeD(this._itemList.size());
		for (ItemInfo item : this._itemList)
		{
			this.writeItemInfo(item);
			this.writeD(item.getObjectId());
		}
	}
}

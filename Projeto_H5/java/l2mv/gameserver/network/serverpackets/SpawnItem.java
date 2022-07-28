package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.items.ItemInstance;

/**
 * 15
 * ee cc 11 43 		object id
 * 39 00 00 00 		item id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 01 00 00 00 		show item count
 * 7a 00 00 00      count                                         .
 *
 * format  dddddddd
 */
public class SpawnItem extends L2GameServerPacket
{
	private int _objectId;
	private int _itemId;
	private int _x, _y, _z;
	private int _stackable;
	private long _count;

	public SpawnItem(ItemInstance item)
	{
		this._objectId = item.getObjectId();
		this._itemId = item.getItemId();
		this._x = item.getX();
		this._y = item.getY();
		this._z = item.getZ();
		this._stackable = item.isStackable() ? 0x01 : 0x00;
		this._count = item.getCount();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x05);
		this.writeD(this._objectId);
		this.writeD(this._itemId);

		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z + Config.CLIENT_Z_SHIFT);
		this.writeD(this._stackable);
		this.writeQ(this._count);
		this.writeD(0x00); // c2
	}
}
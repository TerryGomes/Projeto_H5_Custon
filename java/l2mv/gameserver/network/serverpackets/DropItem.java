package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.utils.Location;

public class DropItem extends L2GameServerPacket
{
	private Location _loc;
	private int _playerId, item_obj_id, item_id, _stackable;
	private long _count;

	/**
	 * Constructor<?> of the DropItem server packet
	 * @param item : L2ItemInstance designating the item
	 * @param playerId : int designating the player ID who dropped the item
	 */
	public DropItem(ItemInstance item, int playerId)
	{
		this._playerId = playerId;
		this.item_obj_id = item.getObjectId();
		this.item_id = item.getItemId();
		this._loc = item.getLoc();
		this._stackable = item.isStackable() ? 1 : 0;
		this._count = item.getCount();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x16);
		this.writeD(this._playerId);
		this.writeD(this.item_obj_id);
		this.writeD(this.item_id);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
		this.writeD(this._stackable);
		this.writeQ(this._count);
		this.writeD(1); // unknown
	}
}
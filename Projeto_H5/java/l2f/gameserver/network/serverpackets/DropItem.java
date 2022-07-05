package l2f.gameserver.network.serverpackets;

import l2f.gameserver.Config;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.utils.Location;

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
		_playerId = playerId;
		item_obj_id = item.getObjectId();
		item_id = item.getItemId();
		_loc = item.getLoc();
		_stackable = item.isStackable() ? 1 : 0;
		_count = item.getCount();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x16);
		writeD(_playerId);
		writeD(item_obj_id);
		writeD(item_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(_stackable);
		writeQ(_count);
		writeD(1); // unknown
	}
}
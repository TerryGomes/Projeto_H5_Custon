package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.utils.Location;

/**
 * 0000: 17  1a 95 20 48  9b da 12 40  44 17 02 00  03 f0 fc ff  98 f1 ff ff                                     .....
 * format  ddddd
 */
public class GetItem extends L2GameServerPacket
{
	private int _playerId, _itemObjId;
	private Location _loc;

	public GetItem(ItemInstance item, int playerId)
	{
		_itemObjId = item.getObjectId();
		_loc = item.getLoc();
		_playerId = playerId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x17);
		writeD(_playerId);
		writeD(_itemObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}
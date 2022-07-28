package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.utils.Location;

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
		this._itemObjId = item.getObjectId();
		this._loc = item.getLoc();
		this._playerId = playerId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x17);
		this.writeD(this._playerId);
		this.writeD(this._itemObjId);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
	}
}
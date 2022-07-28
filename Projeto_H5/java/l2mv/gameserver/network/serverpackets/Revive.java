package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.GameObject;

/**
 * sample
 * 0000: 0c  9b da 12 40                                     ....@
 *
 * format  d
 */
public class Revive extends L2GameServerPacket
{
	private int _objectId;

	public Revive(GameObject obj)
	{
		this._objectId = obj.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x01);
		this.writeD(this._objectId);
	}
}
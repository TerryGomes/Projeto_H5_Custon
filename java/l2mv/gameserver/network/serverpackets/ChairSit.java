package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.StaticObjectInstance;

/**
 * format: d
 */
public class ChairSit extends L2GameServerPacket
{
	private int _objectId;
	private int _staticObjectId;

	public ChairSit(Player player, StaticObjectInstance throne)
	{
		this._objectId = player.getObjectId();
		this._staticObjectId = throne.getUId();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xed);
		this.writeD(this._objectId);
		this.writeD(this._staticObjectId);
	}
}
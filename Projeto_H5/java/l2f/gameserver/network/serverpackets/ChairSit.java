package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.StaticObjectInstance;

/**
 * format: d
 */
public class ChairSit extends L2GameServerPacket
{
	private int _objectId;
	private int _staticObjectId;

	public ChairSit(Player player, StaticObjectInstance throne)
	{
		_objectId = player.getObjectId();
		_staticObjectId = throne.getUId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xed);
		writeD(_objectId);
		writeD(_staticObjectId);
	}
}
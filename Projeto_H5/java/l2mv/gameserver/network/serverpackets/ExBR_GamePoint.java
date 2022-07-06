package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExBR_GamePoint extends L2GameServerPacket
{
	private int _objectId;
	private long _points;

	public ExBR_GamePoint(Player player)
	{
		_objectId = player.getObjectId();
		_points = player.getPremiumPoints();
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xD5);
		writeD(_objectId);
		writeQ(_points);
		writeD(0x00); // ??
	}
}
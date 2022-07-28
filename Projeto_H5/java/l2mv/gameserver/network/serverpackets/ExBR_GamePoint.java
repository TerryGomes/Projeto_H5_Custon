package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExBR_GamePoint extends L2GameServerPacket
{
	private int _objectId;
	private long _points;

	public ExBR_GamePoint(Player player)
	{
		this._objectId = player.getObjectId();
		this._points = player.getPremiumPoints();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xD5);
		this.writeD(this._objectId);
		this.writeQ(this._points);
		this.writeD(0x00); // ??
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.objects.KrateisCubePlayerObject;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCMyRecord extends L2GameServerPacket
{
	private int _points;

	public ExPVPMatchCCMyRecord(KrateisCubePlayerObject player)
	{
		this._points = player.getPoints();
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x8A);
		this.writeD(this._points);
	}
}
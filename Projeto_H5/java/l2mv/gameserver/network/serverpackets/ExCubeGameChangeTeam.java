package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (chd) ddd
 * d: always -1
 * d: player team
 * d: player object id
 */
public class ExCubeGameChangeTeam extends L2GameServerPacket
{
	private int _objectId;
	private boolean _fromRedTeam;

	public ExCubeGameChangeTeam(Player player, boolean fromRedTeam)
	{
		this._objectId = player.getObjectId();
		this._fromRedTeam = fromRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x05);

		this.writeD(this._objectId);
		this.writeD(this._fromRedTeam ? 0x01 : 0x00);
		this.writeD(this._fromRedTeam ? 0x00 : 0x01);
	}
}
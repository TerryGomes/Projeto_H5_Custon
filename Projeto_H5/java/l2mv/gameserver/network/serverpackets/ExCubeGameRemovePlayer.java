package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (chd) ddd
 * d: always -1
 * d: player team
 * d: player object id
 */
public class ExCubeGameRemovePlayer extends L2GameServerPacket
{
	private int _objectId;
	private boolean _isRedTeam;

	public ExCubeGameRemovePlayer(Player player, boolean isRedTeam)
	{
		this._objectId = player.getObjectId();
		this._isRedTeam = isRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x02);

		this.writeD(0xffffffff);

		this.writeD(this._isRedTeam ? 0x01 : 0x00);
		this.writeD(this._objectId);
	}
}
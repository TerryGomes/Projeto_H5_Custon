package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (chd) dddS
 * d: always -1
 * d: player team
 * d: player object id
 * S: player name
 */
public class ExCubeGameAddPlayer extends L2GameServerPacket
{
	private int _objectId;
	private String _name;
	boolean _isRedTeam;

	public ExCubeGameAddPlayer(Player player, boolean isRedTeam)
	{
		this._objectId = player.getObjectId();
		this._name = player.getName();
		this._isRedTeam = isRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x01);

		this.writeD(0xffffffff);

		this.writeD(this._isRedTeam ? 0x01 : 0x00);
		this.writeD(this._objectId);
		this.writeS(this._name);
	}
}
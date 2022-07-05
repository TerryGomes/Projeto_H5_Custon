package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

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
		_objectId = player.getObjectId();
		_name = player.getName();
		_isRedTeam = isRedTeam;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x97);
		writeD(0x01);

		writeD(0xffffffff);

		writeD(_isRedTeam ? 0x01 : 0x00);
		writeD(_objectId);
		writeS(_name);
	}
}
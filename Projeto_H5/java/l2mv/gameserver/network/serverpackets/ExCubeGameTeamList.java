package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Player;

/**
 * Format: (chd) ddd[dS]d[dS]
 * d: unknown
 * d: always -1
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 */
public class ExCubeGameTeamList extends L2GameServerPacket
{
	List<Player> _bluePlayers;
	List<Player> _redPlayers;
	int _roomNumber;

	public ExCubeGameTeamList(List<Player> redPlayers, List<Player> bluePlayers, int roomNumber)
	{
		this._redPlayers = redPlayers;
		this._bluePlayers = bluePlayers;
		this._roomNumber = roomNumber - 1;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x97);
		this.writeD(0x00);

		this.writeD(this._roomNumber);
		this.writeD(0xffffffff);

		this.writeD(this._bluePlayers.size());
		for (Player player : this._bluePlayers)
		{
			this.writeD(player.getObjectId());
			this.writeS(player.getName());
		}
		this.writeD(this._redPlayers.size());
		for (Player player : this._redPlayers)
		{
			this.writeD(player.getObjectId());
			this.writeS(player.getName());
		}
	}
}
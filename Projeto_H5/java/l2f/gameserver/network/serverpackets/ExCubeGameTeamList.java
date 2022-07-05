package l2f.gameserver.network.serverpackets;

import java.util.List;

import l2f.gameserver.model.Player;

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
		_redPlayers = redPlayers;
		_bluePlayers = bluePlayers;
		_roomNumber = roomNumber - 1;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x97);
		writeD(0x00);

		writeD(_roomNumber);
		writeD(0xffffffff);

		writeD(_bluePlayers.size());
		for (Player player : _bluePlayers)
		{
			writeD(player.getObjectId());
			writeS(player.getName());
		}
		writeD(_redPlayers.size());
		for (Player player : _redPlayers)
		{
			writeD(player.getObjectId());
			writeS(player.getName());
		}
	}
}
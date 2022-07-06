package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

/**
 * Format: (ch) dd
 */
public class RequestDismissPartyRoom extends L2GameClientPacket
{
	private int _roomId;

	@Override
	protected void readImpl()
	{
		_roomId = readD(); // room id
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		final MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getId() != _roomId || room.getType() != MatchingRoom.PARTY_MATCHING || (room.getLeader() != player))
		{
			return;
		}

		room.disband();
	}
}
package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

/**
 * Format (ch) dd
 */
public class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestWithdrawPartyRoom.class);
	private int _roomId;

	@Override
	protected void readImpl()
	{
		_roomId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		MatchingRoom room = player.getMatchingRoom();

		if (room == null)
		{
			_log.warn("Null matching room in CLIENT PACKET - should not happen, for player: " + player.getName() + "| Object Id: " + player.getObjectId());
			return;
		}

		if (room.getId() != _roomId || room.getType() != MatchingRoom.PARTY_MATCHING || (room.getLeader() == player))
		{
			return;
		}

		room.removeMember(player, false);
	}
}
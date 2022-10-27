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
		this._roomId = this.readD(); // room id
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		final MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getId() != this._roomId || room.getType() != MatchingRoom.PARTY_MATCHING || (room.getLeader() != player))
		{
			return;
		}

		room.disband();
	}
}
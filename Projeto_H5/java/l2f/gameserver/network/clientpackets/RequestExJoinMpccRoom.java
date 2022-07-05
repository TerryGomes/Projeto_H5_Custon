package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.MatchingRoomManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.matching.MatchingRoom;

public class RequestExJoinMpccRoom extends L2GameClientPacket
{
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
		if ((player == null) || (player.getMatchingRoom() != null))
		{
			return;
		}

		MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.CC_MATCHING, _roomId);
		if (room == null)
		{
			return;
		}

		room.addMember(player);
	}
}
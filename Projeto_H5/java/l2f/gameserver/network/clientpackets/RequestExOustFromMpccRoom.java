package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class RequestExOustFromMpccRoom extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
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
		if (room == null || room.getType() != MatchingRoom.CC_MATCHING || (room.getLeader() != player))
		{
			return;
		}

		Player member = GameObjectsStorage.getPlayer(_objectId);
		if ((member == null) || (member == room.getLeader()))
		{
			return;
		}

		room.removeMember(member, true);
	}
}
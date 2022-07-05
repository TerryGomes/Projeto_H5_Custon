package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2f.gameserver.instancemanager.MatchingRoomManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.matching.MatchingRoom;

/**
 * Format:(c) dddddds
 */
public class ListPartyWaiting extends L2GameServerPacket
{
	private Collection<MatchingRoom> _rooms;
	private int _fullSize;

	public ListPartyWaiting(int region, boolean allLevels, int page, Player activeChar)
	{
		int first = (page - 1) * 64;
		int firstNot = page * 64;
		_rooms = new ArrayList<MatchingRoom>();

		int i = 0;
		List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);
		_fullSize = temp.size();
		for (MatchingRoom room : temp)
		{
			if (i < first || i >= firstNot)
			{
				continue;
			}
			_rooms.add(room);
			i++;
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9c);
		writeD(_fullSize);
		writeD(_rooms.size());

		for (MatchingRoom room : _rooms)
		{
			writeD(room.getId()); // room id
			writeS(room.getLeader() == null ? "None" : room.getLeader().getName());
			writeD(room.getLocationId());
			writeD(room.getMinLevel()); // min level
			writeD(room.getMaxLevel()); // max level
			writeD(room.getMaxMembersSize()); // max members coun
			writeS(room.getTopic()); // room name

			Collection<Player> players = room.getPlayers();
			writeD(players.size()); // members count
			for (Player player : players)
			{
				writeD(player.getClassId().getId());
				writeS(player.getName());
			}
		}
	}
}
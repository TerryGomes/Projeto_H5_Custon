package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

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
		this._rooms = new ArrayList<MatchingRoom>();

		int i = 0;
		List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);
		this._fullSize = temp.size();
		for (MatchingRoom room : temp)
		{
			if (i < first || i >= firstNot)
			{
				continue;
			}
			this._rooms.add(room);
			i++;
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x9c);
		this.writeD(this._fullSize);
		this.writeD(this._rooms.size());

		for (MatchingRoom room : this._rooms)
		{
			this.writeD(room.getId()); // room id
			this.writeS(room.getLeader() == null ? "None" : room.getLeader().getName());
			this.writeD(room.getLocationId());
			this.writeD(room.getMinLevel()); // min level
			this.writeD(room.getMaxLevel()); // max level
			this.writeD(room.getMaxMembersSize()); // max members coun
			this.writeS(room.getTopic()); // room name

			Collection<Player> players = room.getPlayers();
			this.writeD(players.size()); // members count
			for (Player player : players)
			{
				this.writeD(player.getClassId().getId());
				this.writeS(player.getName());
			}
		}
	}
}
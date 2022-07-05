package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.instancemanager.MatchingRoomManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class ExListMpccWaiting extends L2GameServerPacket
{
	private static final int PAGE_SIZE = 10;
	private int _fullSize;
	private List<MatchingRoom> _list;

	public ExListMpccWaiting(Player player, int page, int location, boolean allLevels)
	{
		int first = (page - 1) * PAGE_SIZE;
		int firstNot = page * PAGE_SIZE;
		int i = 0;
		Collection<MatchingRoom> all = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.CC_MATCHING, location, allLevels, player);
		_fullSize = all.size();
		_list = new ArrayList<MatchingRoom>(PAGE_SIZE);
		for (MatchingRoom c : all)
		{
			if (i < first || i >= firstNot)
			{
				continue;
			}

			_list.add(c);
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		writeEx(0x9C);
		writeD(_fullSize);
		writeD(_list.size());
		for (MatchingRoom room : _list)
		{
			writeD(room.getId());
			writeS(room.getTopic());
			writeD(room.getPlayers().size());
			writeD(room.getMinLevel());
			writeD(room.getMaxLevel());
			writeD(1); // min group
			writeD(room.getMaxMembersSize()); // max group
			Player leader = room.getLeader();
			writeS(leader == null ? StringUtils.EMPTY : leader.getName());
		}
	}
}

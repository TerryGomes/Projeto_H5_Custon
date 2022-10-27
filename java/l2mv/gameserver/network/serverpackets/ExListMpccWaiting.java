package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

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
		this._fullSize = all.size();
		this._list = new ArrayList<MatchingRoom>(PAGE_SIZE);
		for (MatchingRoom c : all)
		{
			if (i < first || i >= firstNot)
			{
				continue;
			}

			this._list.add(c);
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x9C);
		this.writeD(this._fullSize);
		this.writeD(this._list.size());
		for (MatchingRoom room : this._list)
		{
			this.writeD(room.getId());
			this.writeS(room.getTopic());
			this.writeD(room.getPlayers().size());
			this.writeD(room.getMinLevel());
			this.writeD(room.getMaxLevel());
			this.writeD(1); // min group
			this.writeD(room.getMaxMembersSize()); // max group
			Player leader = room.getLeader();
			this.writeS(leader == null ? StringUtils.EMPTY : leader.getName());
		}
	}
}

package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

/**
 * Format:(ch) d d [dsdddd]
 */
public class ExPartyRoomMember extends L2GameServerPacket
{
	private int _type;
	private List<PartyRoomMemberInfo> _members = Collections.emptyList();

	public ExPartyRoomMember(MatchingRoom room, Player activeChar)
	{
		this._type = room.getMemberType(activeChar);
		this._members = new ArrayList<PartyRoomMemberInfo>(room.getPlayers().size());
		for (Player $member : room.getPlayers())
		{
			this._members.add(new PartyRoomMemberInfo($member, room.getMemberType($member)));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x08);
		this.writeD(this._type);
		this.writeD(this._members.size());
		for (PartyRoomMemberInfo member_info : this._members)
		{
			this.writeD(member_info.objectId);
			this.writeS(member_info.name);
			this.writeD(member_info.classId);
			this.writeD(member_info.level);
			this.writeD(member_info.location);
			this.writeD(member_info.memberType);
			this.writeD(member_info.instanceReuses.length);
			for (int i : member_info.instanceReuses)
			{
				this.writeD(i);
			}
		}
	}

	static class PartyRoomMemberInfo
	{
		public final int objectId, classId, level, location, memberType;
		public final String name;
		public final int[] instanceReuses;

		public PartyRoomMemberInfo(Player member, int type)
		{
			this.objectId = member.getObjectId();
			this.name = member.getName();
			this.classId = member.getClassId().ordinal();
			this.level = member.getLevel();
			this.location = MatchingRoomManager.getInstance().getLocation(member);
			this.memberType = type;
			this.instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
		}
	}
}
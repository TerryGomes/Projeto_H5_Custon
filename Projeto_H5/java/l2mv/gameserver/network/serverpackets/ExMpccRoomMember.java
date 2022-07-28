package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class ExMpccRoomMember extends L2GameServerPacket
{
	private int _type;
	private List<MpccRoomMemberInfo> _members = Collections.emptyList();

	public ExMpccRoomMember(MatchingRoom room, Player player)
	{
		this._type = room.getMemberType(player);
		this._members = new ArrayList<MpccRoomMemberInfo>(room.getPlayers().size());

		for (Player member : room.getPlayers())
		{
			this._members.add(new MpccRoomMemberInfo(member, room.getMemberType(member)));
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x9F);
		this.writeD(this._type);
		this.writeD(this._members.size());
		for (MpccRoomMemberInfo member : this._members)
		{
			this.writeD(member.objectId);
			this.writeS(member.name);
			this.writeD(member.level);
			this.writeD(member.classId);
			this.writeD(member.location);
			this.writeD(member.memberType);
		}
	}

	static class MpccRoomMemberInfo
	{
		public final int objectId;
		public final int classId;
		public final int level;
		public final int location;
		public final int memberType;
		public final String name;

		public MpccRoomMemberInfo(Player member, int type)
		{
			this.objectId = member.getObjectId();
			this.name = member.getName();
			this.classId = member.getClassId().ordinal();
			this.level = member.getLevel();
			this.location = MatchingRoomManager.getInstance().getLocation(member);
			this.memberType = type;
		}
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class ExManageMpccRoomMember extends L2GameServerPacket
{
	public static int ADD_MEMBER = 0;
	public static int UPDATE_MEMBER = 1;
	public static int REMOVE_MEMBER = 2;

	private int _type;
	private MpccRoomMemberInfo _memberInfo;

	public ExManageMpccRoomMember(int type, MatchingRoom room, Player target)
	{
		_type = type;
		_memberInfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x9E);
		writeD(_type);
		writeD(_memberInfo.objectId);
		writeS(_memberInfo.name);
		writeD(_memberInfo.level);
		writeD(_memberInfo.classId);
		writeD(_memberInfo.location);
		writeD(_memberInfo.memberType);
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
			objectId = member.getObjectId();
			name = member.getName();
			classId = member.getClassId().ordinal();
			level = member.getLevel();
			location = MatchingRoomManager.getInstance().getLocation(member);
			memberType = type;
		}
	}
}
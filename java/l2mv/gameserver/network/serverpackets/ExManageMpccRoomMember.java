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
		this._type = type;
		this._memberInfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x9E);
		this.writeD(this._type);
		this.writeD(this._memberInfo.objectId);
		this.writeS(this._memberInfo.name);
		this.writeD(this._memberInfo.level);
		this.writeD(this._memberInfo.classId);
		this.writeD(this._memberInfo.location);
		this.writeD(this._memberInfo.memberType);
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
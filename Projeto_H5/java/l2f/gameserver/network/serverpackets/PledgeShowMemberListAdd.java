package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAdd extends L2GameServerPacket
{
	private PledgePacketMember _member;

	public PledgeShowMemberListAdd(UnitMember member)
	{
		_member = new PledgePacketMember(member);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5c);
		writeS(_member._name);
		writeD(_member._level);
		writeD(_member._classId);
		writeD(_member._sex);
		writeD(_member._race);
		writeD(_member._online);
		writeD(_member._pledgeType);
	}

	private class PledgePacketMember
	{
		private String _name;
		private int _level;
		private int _classId;
		private int _sex;
		private int _race;
		private int _online;
		private int _pledgeType;

		public PledgePacketMember(UnitMember m)
		{
			_name = m.getName();
			_level = m.getLevel();
			_classId = m.getClassId();
			_sex = m.getSex();
			_race = 0; // TODO m.getRace()
			_online = m.isOnline() ? m.getObjectId() : 0;
			_pledgeType = m.getPledgeType();
		}
	}
}
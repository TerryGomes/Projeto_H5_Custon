package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.pledge.UnitMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private UnitMember _member;

	public PledgeReceiveMemberInfo(UnitMember member)
	{
		_member = member;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x3e);

		writeD(_member.getPledgeType());
		writeS(_member.getName());
		writeS(_member.getTitle());
		writeD(_member.getPowerGrade());
		writeS(_member.getSubUnit().getName());
		writeS(_member.getRelatedName()); // apprentice/sponsor name if any
	}
}
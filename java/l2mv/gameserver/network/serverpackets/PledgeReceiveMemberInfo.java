package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.UnitMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private UnitMember _member;

	public PledgeReceiveMemberInfo(UnitMember member)
	{
		this._member = member;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3e);

		this.writeD(this._member.getPledgeType());
		this.writeS(this._member.getName());
		this.writeS(this._member.getTitle());
		this.writeD(this._member.getPowerGrade());
		this.writeS(this._member.getSubUnit().getName());
		this.writeS(this._member.getRelatedName()); // apprentice/sponsor name if any
	}
}
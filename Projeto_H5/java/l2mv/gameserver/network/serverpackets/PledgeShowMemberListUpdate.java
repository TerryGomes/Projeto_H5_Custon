package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListUpdate extends L2GameServerPacket
{
	private String _name;
	private int _lvl;
	private int _classId;
	private int _sex;
	private int _isOnline;
	private int _objectId;
	private int _pledgeType;
	private int _isApprentice;

	public PledgeShowMemberListUpdate(Player player)
	{
		this._name = player.getName();
		this._lvl = player.getLevel();
		this._classId = player.getClassId().getId();
		this._sex = player.getSex();
		this._objectId = player.getObjectId();
		this._isOnline = player.isOnline() ? 1 : 0;
		this._pledgeType = player.getPledgeType();
		SubUnit subUnit = player.getSubUnit();
		UnitMember member = subUnit == null ? null : subUnit.getUnitMember(this._objectId);
		if (member != null)
		{
			this._isApprentice = member.hasSponsor() ? 1 : 0;
		}
	}

	public PledgeShowMemberListUpdate(UnitMember cm)
	{
		this._name = cm.getName();
		this._lvl = cm.getLevel();
		this._classId = cm.getClassId();
		this._sex = cm.getSex();
		this._objectId = cm.getObjectId();
		this._isOnline = cm.isOnline() ? 1 : 0;
		this._pledgeType = cm.getPledgeType();
		this._isApprentice = cm.hasSponsor() ? 1 : 0;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x5b);
		this.writeS(this._name);
		this.writeD(this._lvl);
		this.writeD(this._classId);
		this.writeD(this._sex);
		this.writeD(this._objectId);
		this.writeD(this._isOnline); // 1=online 0=offline
		this.writeD(this._pledgeType);
		this.writeD(this._isApprentice); // does a clan member have a sponsor
	}
}
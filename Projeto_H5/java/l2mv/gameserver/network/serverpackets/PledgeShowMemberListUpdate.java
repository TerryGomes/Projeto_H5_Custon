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
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getClassId().getId();
		_sex = player.getSex();
		_objectId = player.getObjectId();
		_isOnline = player.isOnline() ? 1 : 0;
		_pledgeType = player.getPledgeType();
		SubUnit subUnit = player.getSubUnit();
		UnitMember member = subUnit == null ? null : subUnit.getUnitMember(_objectId);
		if (member != null)
		{
			_isApprentice = member.hasSponsor() ? 1 : 0;
		}
	}

	public PledgeShowMemberListUpdate(UnitMember cm)
	{
		_name = cm.getName();
		_lvl = cm.getLevel();
		_classId = cm.getClassId();
		_sex = cm.getSex();
		_objectId = cm.getObjectId();
		_isOnline = cm.isOnline() ? 1 : 0;
		_pledgeType = cm.getPledgeType();
		_isApprentice = cm.hasSponsor() ? 1 : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5b);
		writeS(_name);
		writeD(_lvl);
		writeD(_classId);
		writeD(_sex);
		writeD(_objectId);
		writeD(_isOnline); // 1=online 0=offline
		writeD(_pledgeType);
		writeD(_isApprentice); // does a clan member have a sponsor
	}
}
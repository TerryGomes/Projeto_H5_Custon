package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAll extends L2GameServerPacket
{
	private final int _clanObjectId, _clanCrestId, _level, _rank, _reputation;
	private int _allianceObjectId;
	private int _allianceCrestId;
	private final int _hasCastle, _hasClanHall, _hasFortress, _atClanWar;
	private final String _unitName, _leaderName;
	private String _allianceName;
	private final int _pledgeType, _territorySide;
	private final List<PledgePacketMember> _members;
	private final boolean _isDisbanded;

	public PledgeShowMemberListAll(Clan clan, SubUnit sub)
	{
		this._pledgeType = sub.getType();
		this._clanObjectId = clan.getClanId();
		this._unitName = sub.getName();
		this._leaderName = sub.getLeaderName();
		this._clanCrestId = clan.getCrestId();
		this._level = clan.getLevel();
		this._hasCastle = clan.getCastle();
		this._hasClanHall = clan.getHasHideout();
		this._hasFortress = clan.getHasFortress();
		this._rank = clan.getRank();
		this._reputation = clan.getReputationScore();
		this._atClanWar = clan.isAtWarOrUnderAttack();
		this._territorySide = clan.getWarDominion();
		this._isDisbanded = clan.isPlacedForDisband();

		Alliance ally = clan.getAlliance();

		if (ally != null)
		{
			this._allianceObjectId = ally.getAllyId();
			this._allianceName = ally.getAllyName();
			this._allianceCrestId = ally.getAllyCrestId();
		}

		this._members = new ArrayList<PledgePacketMember>(sub.size());

		for (UnitMember m : sub.getUnitMembers())
		{
			this._members.add(new PledgePacketMember(m));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x5a);

		this.writeD(this._pledgeType == Clan.SUBUNIT_MAIN_CLAN ? 0 : 1);
		this.writeD(this._clanObjectId);
		this.writeD(this._pledgeType);
		this.writeS(this._unitName);
		this.writeS(this._leaderName);
		this.writeD(this._clanCrestId); // crest id .. is used again
		this.writeD(this._level);
		this.writeD(this._hasCastle);
		this.writeD(this._hasClanHall);
		this.writeD(this._hasFortress);
		this.writeD(this._rank);
		this.writeD(this._reputation);
		this.writeD(this._isDisbanded ? 3 : 0);
		this.writeD(0x00);
		this.writeD(this._allianceObjectId);
		this.writeS(this._allianceName);
		this.writeD(this._allianceCrestId);
		this.writeD(this._atClanWar);
		this.writeD(this._territorySide);// territory Id

		this.writeD(this._members.size());
		for (PledgePacketMember m : this._members)
		{
			this.writeS(m._name);
			this.writeD(m._level);
			this.writeD(m._classId);
			this.writeD(m._sex);
			this.writeD(m._race);
			this.writeD(m._online);
			this.writeD(m._hasSponsor ? 1 : 0);
		}
	}

	private class PledgePacketMember
	{
		private final String _name;
		private final int _level;
		private final int _classId;
		private final int _sex;
		private final int _race;
		private final int _online;
		private final boolean _hasSponsor;

		public PledgePacketMember(UnitMember m)
		{
			this._name = m.getName();
			this._level = m.getLevel();
			this._classId = m.getClassId();
			this._sex = m.getSex();
			this._race = 0; // TODO m.getRace()
			this._online = m.isOnline() ? m.getObjectId() : 0;
			this._hasSponsor = m.getSponsor() != 0;
		}
	}
}
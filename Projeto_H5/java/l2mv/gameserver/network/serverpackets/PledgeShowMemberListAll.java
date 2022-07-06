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
		_pledgeType = sub.getType();
		_clanObjectId = clan.getClanId();
		_unitName = sub.getName();
		_leaderName = sub.getLeaderName();
		_clanCrestId = clan.getCrestId();
		_level = clan.getLevel();
		_hasCastle = clan.getCastle();
		_hasClanHall = clan.getHasHideout();
		_hasFortress = clan.getHasFortress();
		_rank = clan.getRank();
		_reputation = clan.getReputationScore();
		_atClanWar = clan.isAtWarOrUnderAttack();
		_territorySide = clan.getWarDominion();
		_isDisbanded = clan.isPlacedForDisband();

		Alliance ally = clan.getAlliance();

		if (ally != null)
		{
			_allianceObjectId = ally.getAllyId();
			_allianceName = ally.getAllyName();
			_allianceCrestId = ally.getAllyCrestId();
		}

		_members = new ArrayList<PledgePacketMember>(sub.size());

		for (UnitMember m : sub.getUnitMembers())
		{
			_members.add(new PledgePacketMember(m));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5a);

		writeD(_pledgeType == Clan.SUBUNIT_MAIN_CLAN ? 0 : 1);
		writeD(_clanObjectId);
		writeD(_pledgeType);
		writeS(_unitName);
		writeS(_leaderName);
		writeD(_clanCrestId); // crest id .. is used again
		writeD(_level);
		writeD(_hasCastle);
		writeD(_hasClanHall);
		writeD(_hasFortress);
		writeD(_rank);
		writeD(_reputation);
		writeD(_isDisbanded ? 3 : 0);
		writeD(0x00);
		writeD(_allianceObjectId);
		writeS(_allianceName);
		writeD(_allianceCrestId);
		writeD(_atClanWar);
		writeD(_territorySide);// territory Id

		writeD(_members.size());
		for (PledgePacketMember m : _members)
		{
			writeS(m._name);
			writeD(m._level);
			writeD(m._classId);
			writeD(m._sex);
			writeD(m._race);
			writeD(m._online);
			writeD(m._hasSponsor ? 1 : 0);
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
			_name = m.getName();
			_level = m.getLevel();
			_classId = m.getClassId();
			_sex = m.getSex();
			_race = 0; // TODO m.getRace()
			_online = m.isOnline() ? m.getObjectId() : 0;
			_hasSponsor = m.getSponsor() != 0;
		}
	}
}
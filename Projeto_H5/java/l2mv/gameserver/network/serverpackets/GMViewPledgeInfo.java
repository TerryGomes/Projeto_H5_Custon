package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.UnitMember;

public class GMViewPledgeInfo extends L2GameServerPacket
{
	private String char_name, clan_name, leader_name, ally_name;
	private int clan_id, clan_crest_id, clan_level, rank, rep, ally_id, ally_crest_id;
	private int hasCastle, hasHideout, hasFortress, atWar;
	private List<PledgeMemberInfo> infos = new ArrayList<PledgeMemberInfo>();

	public GMViewPledgeInfo(Player activeChar)
	{
		Clan clan = activeChar.getClan();
		for (UnitMember member : clan)
		{
			if (member == null)
			{
				continue;
			}
			this.char_name = member.getName();
			this.clan_level = member.getLevel();
			this.clan_id = member.getClassId();
			this.clan_crest_id = member.isOnline() ? member.getObjectId() : 0;
			this.rep = member.getSponsor() != 0 ? 1 : 0;
			this.infos.add(new PledgeMemberInfo(this.char_name, this.clan_level, this.clan_id, this.clan_crest_id, member.getSex(), 1, this.rep));
		}

		this.char_name = activeChar.getName();
		this.clan_id = clan.getClanId();
		this.clan_name = clan.getName();
		this.leader_name = clan.getLeaderName();
		this.clan_crest_id = clan.getCrestId();
		this.clan_level = clan.getLevel();
		this.hasCastle = clan.getCastle();
		this.hasHideout = clan.getHasHideout();
		this.hasFortress = clan.getHasFortress();
		this.rank = clan.getRank();
		this.rep = clan.getReputationScore();
		this.ally_id = clan.getAllyId();
		if (clan.getAlliance() != null)
		{
			this.ally_name = clan.getAlliance().getAllyName();
			this.ally_crest_id = clan.getAlliance().getAllyCrestId();
		}
		else
		{
			this.ally_name = "";
			this.ally_crest_id = 0;
		}
		this.atWar = clan.isAtWar();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x96);

		this.writeS(this.char_name);
		this.writeD(this.clan_id);
		this.writeD(0x00);
		this.writeS(this.clan_name);
		this.writeS(this.leader_name);

		this.writeD(this.clan_crest_id);
		this.writeD(this.clan_level);
		this.writeD(this.hasCastle);
		this.writeD(this.hasHideout);
		this.writeD(this.hasFortress);
		this.writeD(this.rank);
		this.writeD(this.rep);
		this.writeD(0);
		this.writeD(0);
		this.writeD(this.ally_id);
		this.writeS(this.ally_name);
		this.writeD(this.ally_crest_id);
		this.writeD(this.atWar);
		this.writeD(0); // Territory castle ID

		this.writeD(this.infos.size());
		for (PledgeMemberInfo _info : this.infos)
		{
			this.writeS(_info._name);
			this.writeD(_info.level);
			this.writeD(_info.class_id);
			this.writeD(_info.sex);
			this.writeD(_info.race);
			this.writeD(_info.online);
			this.writeD(_info.sponsor);
		}
		this.infos.clear();
	}

	static class PledgeMemberInfo
	{
		public String _name;
		public int level, class_id, online, sex, race, sponsor;

		public PledgeMemberInfo(String __name, int _level, int _class_id, int _online, int _sex, int _race, int _sponsor)
		{
			this._name = __name;
			this.level = _level;
			this.class_id = _class_id;
			this.online = _online;
			this.sex = _sex;
			this.race = _race;
			this.sponsor = _sponsor;
		}
	}
}
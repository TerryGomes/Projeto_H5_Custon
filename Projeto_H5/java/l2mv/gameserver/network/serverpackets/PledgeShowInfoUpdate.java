package l2mv.gameserver.network.serverpackets;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private final int clan_id, clan_level, clan_rank, clan_rep, crest_id, ally_id;
	private int ally_crest;
	private final int atwar;
	private final int _territorySide;
	private String ally_name = StringUtils.EMPTY;
	private final int HasCastle, HasHideout, HasFortress;
	private final boolean _isDisbanded;

	public PledgeShowInfoUpdate(Clan clan)
	{
		this.clan_id = clan.getClanId();
		this.clan_level = clan.getLevel();
		this.HasCastle = clan.getCastle();
		this.HasHideout = clan.getHasHideout();
		this.HasFortress = clan.getHasFortress();
		this.clan_rank = clan.getRank();
		this.clan_rep = clan.getReputationScore();
		this.crest_id = clan.getCrestId();
		this.ally_id = clan.getAllyId();
		this.atwar = clan.isAtWar();
		this._territorySide = clan.getWarDominion();
		this._isDisbanded = clan.isPlacedForDisband();
		Alliance ally = clan.getAlliance();
		if (ally != null)
		{
			this.ally_name = ally.getAllyName();
			this.ally_crest = ally.getAllyCrestId();
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x8e);
		// sending empty data so client will ask all the info in response ;)
		this.writeD(this.clan_id);
		this.writeD(this.crest_id);
		this.writeD(this.clan_level);
		this.writeD(this.HasCastle);
		this.writeD(this.HasHideout);
		this.writeD(this.HasFortress);
		this.writeD(this.clan_rank);// displayed in the "tree" view (with the clan skills)
		this.writeD(this.clan_rep);
		this.writeD(this._isDisbanded ? 3 : 0);
		this.writeD(0);
		this.writeD(this.ally_id); // c5
		this.writeS(this.ally_name); // c5
		this.writeD(this.ally_crest); // c5
		this.writeD(this.atwar); // c5

		this.writeD(0x00);
		this.writeD(this._territorySide);
	}
}
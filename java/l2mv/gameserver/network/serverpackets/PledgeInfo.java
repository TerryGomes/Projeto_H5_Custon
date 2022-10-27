package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.Clan;

public class PledgeInfo extends L2GameServerPacket
{
	private int clan_id;
	private String clan_name, ally_name;

	public PledgeInfo(Clan clan)
	{
		this.clan_id = clan.getClanId();
		this.clan_name = clan.getName();
		this.ally_name = clan.getAlliance() == null ? "" : clan.getAlliance().getAllyName();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x89);
		this.writeD(this.clan_id);
		this.writeS(this.clan_name);
		this.writeS(this.ally_name);
	}
}
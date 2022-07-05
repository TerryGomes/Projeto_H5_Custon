package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.pledge.Clan;

public class PledgeInfo extends L2GameServerPacket
{
	private int clan_id;
	private String clan_name, ally_name;

	public PledgeInfo(Clan clan)
	{
		clan_id = clan.getClanId();
		clan_name = clan.getName();
		ally_name = clan.getAlliance() == null ? "" : clan.getAlliance().getAllyName();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x89);
		writeD(clan_id);
		writeS(clan_name);
		writeS(ally_name);
	}
}
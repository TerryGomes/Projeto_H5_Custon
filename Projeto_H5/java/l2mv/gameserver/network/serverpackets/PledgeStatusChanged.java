package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.Clan;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00    ....H...........
 * 0010: 00 00 00 00 00                                     .....
 *
 * format   ddddd
 */
public class PledgeStatusChanged extends L2GameServerPacket
{
	private int leader_id, clan_id, level;

	public PledgeStatusChanged(Clan clan)
	{
		this.leader_id = clan.getLeaderId();
		this.clan_id = clan.getClanId();
		this.level = clan.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xCD);
		this.writeD(this.leader_id);
		this.writeD(this.clan_id);
		this.writeD(0);
		this.writeD(this.level);
		this.writeD(0);
		this.writeD(0);
		this.writeD(0);
	}
}
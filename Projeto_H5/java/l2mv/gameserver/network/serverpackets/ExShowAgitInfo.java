package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2mv.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.tables.ClanTable;

public class ExShowAgitInfo extends L2GameServerPacket
{
	private List<AgitInfo> _clanHalls = Collections.emptyList();

	public ExShowAgitInfo()
	{
		List<ClanHall> chs = ResidenceHolder.getInstance().getResidenceList(ClanHall.class);
		this._clanHalls = new ArrayList<AgitInfo>(chs.size());

		for (ClanHall clanHall : chs)
		{
			int ch_id = clanHall.getId();
			int getType;
			if (clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class)
			{
				getType = 0;
			}
			else if (clanHall.getSiegeEvent().getClass() == ClanHallMiniGameEvent.class)
			{
				getType = 2;
			}
			else
			{
				getType = 1;
			}

			Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
			String clan_name = clanHall.getOwnerId() == 0 || clan == null ? StringUtils.EMPTY : clan.getName();
			String leader_name = clanHall.getOwnerId() == 0 || clan == null ? StringUtils.EMPTY : clan.getLeaderName();
			this._clanHalls.add(new AgitInfo(clan_name, leader_name, ch_id, getType));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x16);
		this.writeD(this._clanHalls.size());
		for (AgitInfo info : this._clanHalls)
		{
			this.writeD(info.ch_id);
			this.writeS(info.clan_name);
			this.writeS(info.leader_name);
			this.writeD(info.getType);
		}
	}

	static class AgitInfo
	{
		public String clan_name, leader_name;
		public int ch_id, getType;

		public AgitInfo(String clan_name, String leader_name, int ch_id, int lease)
		{
			this.clan_name = clan_name;
			this.leader_name = leader_name;
			this.ch_id = ch_id;
			this.getType = lease;
		}
	}
}
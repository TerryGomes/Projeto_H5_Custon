package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.pledge.Clan;

public class PledgeReceiveWarList extends L2GameServerPacket
{
	private List<WarInfo> infos = new ArrayList<WarInfo>();
	private int _updateType;
	@SuppressWarnings("unused")
	private int _page;

	public PledgeReceiveWarList(Clan clan, int type, int page)
	{
		this._updateType = type;
		this._page = page;

		List<Clan> clans = this._updateType == 1 ? clan.getAttackerClans() : clan.getEnemyClans();
		for (Clan _clan : clans)
		{
			if (_clan == null)
			{
				continue;
			}
			this.infos.add(new WarInfo(_clan.getName(), this._updateType, 0));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3f);
		this.writeD(this._updateType); // which type of war list sould be revamped by this packet
		this.writeD(0x00); // page number goes here(_page ), made it static cuz not sure how many war to add to one page so TODO here
		this.writeD(this.infos.size());
		for (WarInfo _info : this.infos)
		{
			this.writeS(_info.clan_name);
			this.writeD(_info.unk1);
			this.writeD(_info.unk2); // filler ??
		}
	}

	static class WarInfo
	{
		public String clan_name;
		public int unk1, unk2;

		public WarInfo(String _clan_name, int _unk1, int _unk2)
		{
			this.clan_name = _clan_name;
			this.unk1 = _unk1;
			this.unk2 = _unk2;
		}
	}
}
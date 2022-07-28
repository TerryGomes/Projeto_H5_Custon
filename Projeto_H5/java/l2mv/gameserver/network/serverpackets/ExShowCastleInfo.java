package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.tables.ClanTable;

public class ExShowCastleInfo extends L2GameServerPacket
{
	private List<CastleInfo> _infos = Collections.emptyList();

	public ExShowCastleInfo()
	{
		String ownerName;
		int id, tax, nextSiege;

		List<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		this._infos = new ArrayList<CastleInfo>(castles.size());
		for (Castle castle : castles)
		{
			ownerName = ClanTable.getInstance().getClanName(castle.getOwnerId());
			id = castle.getId();
			tax = castle.getTaxPercent();
			nextSiege = (int) (castle.getSiegeDate().getTimeInMillis() / 1000);
			this._infos.add(new CastleInfo(ownerName, id, tax, nextSiege));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x14);
		this.writeD(this._infos.size());
		for (CastleInfo info : this._infos)
		{
			this.writeD(info._id);
			this.writeS(info._ownerName);
			this.writeD(info._tax);
			this.writeD(info._nextSiege);
		}
		this._infos.clear();
	}

	private static class CastleInfo
	{
		public String _ownerName;
		public int _id, _tax, _nextSiege;

		public CastleInfo(String ownerName, int id, int tax, int nextSiege)
		{
			this._ownerName = ownerName;
			this._id = id;
			this._tax = tax;
			this._nextSiege = nextSiege;
		}
	}
}
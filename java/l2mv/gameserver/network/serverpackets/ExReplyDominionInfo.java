package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.entity.residence.Dominion;

public class ExReplyDominionInfo extends L2GameServerPacket
{
	private List<TerritoryInfo> _dominionList = Collections.emptyList();

	public ExReplyDominionInfo()
	{
		List<Dominion> dominions = ResidenceHolder.getInstance().getResidenceList(Dominion.class);
		this._dominionList = new ArrayList<TerritoryInfo>(dominions.size());

		for (Dominion dominion : dominions)
		{
			if (dominion.getSiegeDate().getTimeInMillis() == 0)
			{
				continue;
			}

			this._dominionList.add(new TerritoryInfo(dominion.getId(), dominion.getName(), dominion.getOwner().getName(), dominion.getFlags(), (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L)));
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x92);
		this.writeD(this._dominionList.size());
		for (TerritoryInfo cf : this._dominionList)
		{
			this.writeD(cf.id);
			this.writeS(cf.terr);
			this.writeS(cf.clan);
			this.writeD(cf.flags.length);
			for (int f : cf.flags)
			{
				this.writeD(f);
			}
			this.writeD(cf.startTime);
		}
	}

	private class TerritoryInfo
	{
		public int id;
		public String terr;
		public String clan;
		public int[] flags;
		public int startTime;

		public TerritoryInfo(int id, String terr, String clan, int[] flags, int startTime)
		{
			this.id = id;
			this.terr = terr;
			this.clan = clan;
			this.flags = flags;
			this.startTime = startTime;
		}
	}
}
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
		_dominionList = new ArrayList<TerritoryInfo>(dominions.size());

		for (Dominion dominion : dominions)
		{
			if (dominion.getSiegeDate().getTimeInMillis() == 0)
			{
				continue;
			}

			_dominionList.add(new TerritoryInfo(dominion.getId(), dominion.getName(), dominion.getOwner().getName(), dominion.getFlags(), (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L)));
		}
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x92);
		writeD(_dominionList.size());
		for (TerritoryInfo cf : _dominionList)
		{
			writeD(cf.id);
			writeS(cf.terr);
			writeS(cf.clan);
			writeD(cf.flags.length);
			for (int f : cf.flags)
			{
				writeD(f);
			}
			writeD(cf.startTime);
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
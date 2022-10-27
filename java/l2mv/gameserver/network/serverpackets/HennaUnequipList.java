package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.Henna;

public class HennaUnequipList extends L2GameServerPacket
{
	private int _emptySlots;
	private long _adena;
	private List<Henna> availHenna = new ArrayList<Henna>(3);

	public HennaUnequipList(Player player)
	{
		this._adena = player.getAdena();
		this._emptySlots = player.getHennaEmptySlots();
		for (int i = 1; i <= 3; i++)
		{
			if (player.getHenna(i) != null)
			{
				this.availHenna.add(player.getHenna(i));
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xE6);

		this.writeQ(this._adena);
		this.writeD(this._emptySlots);
		this.writeD(this.availHenna.size());
		for (Henna henna : this.availHenna)
		{
			this.writeD(henna.getSymbolId()); // symbolid
			this.writeD(henna.getDyeId()); // itemid of dye
			this.writeQ(henna.getDrawCount());
			this.writeQ(henna.getPrice());
			this.writeD(1); // meet the requirement or not
		}
	}
}
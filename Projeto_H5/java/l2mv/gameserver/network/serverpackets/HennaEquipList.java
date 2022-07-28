package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.data.xml.holder.HennaHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.Henna;

public class HennaEquipList extends L2GameServerPacket
{
	private int _emptySlots;
	private long _adena;
	private List<Henna> _hennas = new ArrayList<Henna>();

	public HennaEquipList(Player player)
	{
		this._adena = player.getAdena();
		this._emptySlots = player.getHennaEmptySlots();

		List<Henna> list = HennaHolder.getInstance().generateList(player);
		for (Henna element : list)
		{
			if (player.getInventory().getItemByItemId(element.getDyeId()) != null)
			{
				this._hennas.add(element);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xee);

		this.writeQ(this._adena);
		this.writeD(this._emptySlots);
		if (this._hennas.size() != 0)
		{
			this.writeD(this._hennas.size());
			for (Henna henna : this._hennas)
			{
				this.writeD(henna.getSymbolId()); // symbolid
				this.writeD(henna.getDyeId()); // itemid of dye
				this.writeQ(henna.getDrawCount());
				this.writeQ(henna.getPrice());
				this.writeD(1); // meet the requirement or not
			}
		}
		else
		{
			this.writeD(0x01);
			this.writeD(0x00);
			this.writeD(0x00);
			this.writeQ(0x00);
			this.writeQ(0x00);
			this.writeD(0x00);
		}
	}
}
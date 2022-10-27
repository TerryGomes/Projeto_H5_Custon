package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.templates.manor.CropProcure;

public class SellListProcure extends L2GameServerPacket
{
	private long _money;
	private Map<ItemInstance, Long> _sellList = new HashMap<ItemInstance, Long>();
	private List<CropProcure> _procureList = new ArrayList<CropProcure>();
	private int _castle;

	public SellListProcure(Player player, int castleId)
	{
		this._money = player.getAdena();
		this._castle = castleId;
		this._procureList = ResidenceHolder.getInstance().getResidence(Castle.class, this._castle).getCropProcure(0);
		for (CropProcure c : this._procureList)
		{
			ItemInstance item = player.getInventory().getItemByItemId(c.getId());
			if (item != null && c.getAmount() > 0)
			{
				this._sellList.put(item, c.getAmount());
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xef);
		this.writeQ(this._money);
		this.writeD(0x00); // lease ?
		this.writeH(this._sellList.size()); // list size

		for (ItemInstance item : this._sellList.keySet())
		{
			this.writeH(item.getTemplate().getType1());
			this.writeD(item.getObjectId());
			this.writeD(item.getItemId());
			this.writeQ(this._sellList.get(item));
			this.writeH(item.getTemplate().getType2ForPackets());
			this.writeH(0); // size of [dhhh]
			this.writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
		}
	}
}
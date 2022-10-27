package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;

public class TradeStart extends L2GameServerPacket
{
	private List<ItemInfo> _tradelist = new ArrayList<ItemInfo>();
	private int targetId;

	public TradeStart(Player player, Player target)
	{
		this.targetId = target.getObjectId();

		ItemInstance[] items = player.getInventory().getItems();
		for (ItemInstance item : items)
		{
			if (item.canBeTraded(player))
			{
				this._tradelist.add(new ItemInfo(item));
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x14);
		this.writeD(this.targetId);
		this.writeH(this._tradelist.size());
		for (ItemInfo item : this._tradelist)
		{
			this.writeItemInfo(item);
		}
	}
}
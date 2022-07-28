package l2mv.gameserver.network.serverpackets;

import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.PremiumItem;

/**
 * @author Gnacik
 * @corrected by n0nam3
 **/
public class ExGetPremiumItemList extends L2GameServerPacket
{
	private int _objectId;
	private Map<Integer, PremiumItem> _list;

	public ExGetPremiumItemList(Player activeChar)
	{
		this._objectId = activeChar.getObjectId();
		this._list = activeChar.getPremiumItemList();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x86);
		if (!this._list.isEmpty())
		{
			this.writeD(this._list.size());
			for (Map.Entry<Integer, PremiumItem> entry : this._list.entrySet())
			{
				this.writeD(entry.getKey());
				this.writeD(this._objectId);
				this.writeD(entry.getValue().getItemId());
				this.writeQ(entry.getValue().getCount());
				this.writeD(0);
				this.writeS(entry.getValue().getSender());
			}
		}
	}

}
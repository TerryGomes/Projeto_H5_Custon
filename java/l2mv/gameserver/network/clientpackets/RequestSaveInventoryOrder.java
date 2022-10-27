package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestSaveInventoryOrder extends L2GameClientPacket
{
	// format: (ch)db, b - array of (dd)
	int[][] _items;

	@Override
	protected void readImpl()
	{
		int size = this.readD();
		if (size > 125)
		{
			size = 125;
		}
		if (size * 8 > this._buf.remaining() || size < 1)
		{
			this._items = null;
			return;
		}
		this._items = new int[size][2];
		for (int i = 0; i < size; i++)
		{
			this._items[i][0] = this.readD(); // item id
			this._items[i][1] = this.readD(); // slot
		}
	}

	@Override
	protected void runImpl()
	{
		if (this._items == null)
		{
			return;
		}
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.getInventory().sort(this._items);
	}
}
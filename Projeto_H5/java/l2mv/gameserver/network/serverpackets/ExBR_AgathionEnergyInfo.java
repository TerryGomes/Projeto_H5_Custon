package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 */
public class ExBR_AgathionEnergyInfo extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _itemList = null;

	public ExBR_AgathionEnergyInfo(int size, ItemInstance... item)
	{
		this._itemList = item;
		this._size = size;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xDE);
		this.writeD(this._size);
		for (ItemInstance item : this._itemList)
		{
			if (item.getTemplate().getAgathionEnergy() == 0)
			{
				continue;
			}
			this.writeD(item.getObjectId());
			this.writeD(item.getItemId());
			this.writeD(0x200000);
			this.writeD(item.getAgathionEnergy());// current energy
			this.writeD(item.getTemplate().getAgathionEnergy()); // max energy
		}
	}
}
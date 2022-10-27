package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.clientpackets.RequestRefineCancel;

/**
 * @author VISTALL
 */
public class ExPutItemResultForVariationCancel extends L2GameServerPacket
{
	private int _itemObjectId;
	private int _itemId;
	private int _aug1;
	private int _aug2;
	private long _price;

	public ExPutItemResultForVariationCancel(ItemInstance item)
	{
		this._itemObjectId = item.getObjectId();
		this._itemId = item.getItemId();
		this._aug1 = 0x0000FFFF & item.getAugmentationId();
		this._aug2 = item.getAugmentationId() >> 16;
		this._price = RequestRefineCancel.getRemovalPrice(item.getTemplate());
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x57);
		this.writeD(this._itemObjectId);
		this.writeD(this._itemId);
		this.writeD(this._aug1);
		this.writeD(this._aug2);
		this.writeQ(this._price);
		this.writeD(0x01);
	}
}
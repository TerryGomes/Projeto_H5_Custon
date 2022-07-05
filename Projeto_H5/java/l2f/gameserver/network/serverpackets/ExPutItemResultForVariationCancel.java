package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.clientpackets.RequestRefineCancel;

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
		_itemObjectId = item.getObjectId();
		_itemId = item.getItemId();
		_aug1 = 0x0000FFFF & item.getAugmentationId();
		_aug2 = item.getAugmentationId() >> 16;
		_price = RequestRefineCancel.getRemovalPrice(item.getTemplate());
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x57);
		writeD(_itemObjectId);
		writeD(_itemId);
		writeD(_aug1);
		writeD(_aug2);
		writeQ(_price);
		writeD(0x01);
	}
}
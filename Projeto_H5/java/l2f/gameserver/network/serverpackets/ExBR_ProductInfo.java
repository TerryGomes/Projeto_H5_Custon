package l2f.gameserver.network.serverpackets;

import l2f.gameserver.data.xml.holder.ProductHolder;
import l2f.gameserver.model.ProductItem;
import l2f.gameserver.model.ProductItemComponent;

public class ExBR_ProductInfo extends L2GameServerPacket
{
	private ProductItem _productId;

	public ExBR_ProductInfo(int id)
	{
		_productId = ProductHolder.getInstance().getProduct(id);
	}

	@Override
	protected void writeImpl()
	{
		if (_productId == null)
		{
			return;
		}

		writeEx(0xD7);

		writeD(_productId.getProductId()); // product id
		writeD(_productId.getPoints()); // points
		writeD(_productId.getComponents().size()); // size

		for (ProductItemComponent com : _productId.getComponents())
		{
			writeD(com.getItemId()); // item id
			writeD(com.getCount()); // quality
			writeD(com.getWeight()); // weight
			writeD(com.isDropable() ? 1 : 0); // 0 - dont drop/trade
		}
	}
}
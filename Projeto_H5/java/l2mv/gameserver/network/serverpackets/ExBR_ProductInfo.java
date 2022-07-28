package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.data.xml.holder.ProductHolder;
import l2mv.gameserver.model.ProductItem;
import l2mv.gameserver.model.ProductItemComponent;

public class ExBR_ProductInfo extends L2GameServerPacket
{
	private ProductItem _productId;

	public ExBR_ProductInfo(int id)
	{
		this._productId = ProductHolder.getInstance().getProduct(id);
	}

	@Override
	protected void writeImpl()
	{
		if (this._productId == null)
		{
			return;
		}

		this.writeEx(0xD7);

		this.writeD(this._productId.getProductId()); // product id
		this.writeD(this._productId.getPoints()); // points
		this.writeD(this._productId.getComponents().size()); // size

		for (ProductItemComponent com : this._productId.getComponents())
		{
			this.writeD(com.getItemId()); // item id
			this.writeD(com.getCount()); // quality
			this.writeD(com.getWeight()); // weight
			this.writeD(com.isDropable() ? 1 : 0); // 0 - dont drop/trade
		}
	}
}
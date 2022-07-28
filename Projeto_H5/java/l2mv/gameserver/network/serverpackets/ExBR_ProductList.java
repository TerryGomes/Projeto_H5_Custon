package l2mv.gameserver.network.serverpackets;

import java.util.Collection;

import l2mv.gameserver.data.xml.holder.ProductHolder;
import l2mv.gameserver.model.ProductItem;

public class ExBR_ProductList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeEx(0xD6);
		Collection<ProductItem> items = ProductHolder.getInstance().getAllItems();
		this.writeD(items.size());

		for (ProductItem template : items)
		{
			if ((System.currentTimeMillis() < template.getStartTimeSale()) || (System.currentTimeMillis() > template.getEndTimeSale()))
			{
				continue;
			}

			this.writeD(template.getProductId()); // product id
			this.writeH(template.getCategory()); // category 1 - enchant 2 - supplies 3 - decoration 4 - package 5 - other
			this.writeD(template.getPoints()); // points
			this.writeD(template.getTabId()); // show tab 2-th group - 1 показывает окошко про итем
			this.writeD((int) (template.getStartTimeSale() / 1000)); // start sale unix date in seconds
			this.writeD((int) (template.getEndTimeSale() / 1000)); // end sale unix date in seconds
			this.writeC(127); // day week (127 = not daily goods)
			this.writeC(template.getStartHour()); // start hour
			this.writeC(template.getStartMin()); // start min
			this.writeC(template.getEndHour()); // end hour
			this.writeC(template.getEndMin()); // end min
			this.writeD(0); // stock
			this.writeD(-1); // max stock
		}
	}
}
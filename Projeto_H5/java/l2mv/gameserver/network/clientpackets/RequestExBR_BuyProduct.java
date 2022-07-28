package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.ProductHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.ProductItem;
import l2mv.gameserver.model.ProductItemComponent;
import l2mv.gameserver.network.serverpackets.ExBR_BuyProduct;
import l2mv.gameserver.network.serverpackets.ExBR_GamePoint;
import l2mv.gameserver.templates.item.ItemTemplate;

public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private int _productId;
	private int _count;

	@Override
	protected void readImpl()
	{
		this._productId = this.readD();
		this._count = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();

		if ((activeChar == null) || this._count > 99 || this._count < 0)
		{
			return;
		}

		ProductItem product = ProductHolder.getInstance().getProduct(this._productId);
		if (product == null)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			return;
		}

		if ((System.currentTimeMillis() < product.getStartTimeSale()) || (System.currentTimeMillis() > product.getEndTimeSale()))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SALE_PERIOD_ENDED));
			return;
		}

		int totalPoints = product.getPoints() * this._count;

		if (totalPoints < 0)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			return;
		}

		final long gamePointSize = activeChar.getPremiumPoints();

		if (totalPoints > gamePointSize)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
			return;
		}

		int totalWeight = 0;
		for (ProductItemComponent com : product.getComponents())
		{
			totalWeight += com.getWeight();
		}

		totalWeight *= this._count; // увеличиваем вес согласно количеству

		int totalCount = 0;

		for (ProductItemComponent com : product.getComponents())
		{
			ItemTemplate item = ItemHolder.getInstance().getTemplate(com.getItemId());
			if (item == null)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
				return; // what
			}
			totalCount += item.isStackable() ? 1 : com.getCount() * this._count;
		}

		if (!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
			return;
		}

		activeChar.reducePremiumPoints(totalPoints);

		for (ProductItemComponent $comp : product.getComponents())
		{
			activeChar.getInventory().addItem($comp.getItemId(), $comp.getCount() * this._count, "RequestExBR_BuyProduct");
		}

		activeChar.sendPacket(new ExBR_GamePoint(activeChar));
		activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
		activeChar.sendChanges();
	}
}
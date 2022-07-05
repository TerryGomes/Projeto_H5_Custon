package l2f.gameserver.network.clientpackets;

import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.ProductHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.ProductItem;
import l2f.gameserver.model.ProductItemComponent;
import l2f.gameserver.network.serverpackets.ExBR_BuyProduct;
import l2f.gameserver.network.serverpackets.ExBR_GamePoint;
import l2f.gameserver.templates.item.ItemTemplate;

public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private int _productId;
	private int _count;

	@Override
	protected void readImpl()
	{
		_productId = readD();
		_count = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if ((activeChar == null) || _count > 99 || _count < 0)
		{
			return;
		}

		ProductItem product = ProductHolder.getInstance().getProduct(_productId);
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

		int totalPoints = product.getPoints() * _count;

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

		totalWeight *= _count; // увеличиваем вес согласно количеству

		int totalCount = 0;

		for (ProductItemComponent com : product.getComponents())
		{
			ItemTemplate item = ItemHolder.getInstance().getTemplate(com.getItemId());
			if (item == null)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
				return; // what
			}
			totalCount += item.isStackable() ? 1 : com.getCount() * _count;
		}

		if (!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
			return;
		}

		activeChar.reducePremiumPoints(totalPoints);

		for (ProductItemComponent $comp : product.getComponents())
		{
			activeChar.getInventory().addItem($comp.getItemId(), $comp.getCount() * _count, "RequestExBR_BuyProduct");
		}

		activeChar.sendPacket(new ExBR_GamePoint(activeChar));
		activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
		activeChar.sendChanges();
	}
}
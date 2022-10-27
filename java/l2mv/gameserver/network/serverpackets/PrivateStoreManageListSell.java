package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;

public class PrivateStoreManageListSell extends L2GameServerPacket
{
	private final int _sellerId;
	private final long _adena;
	private final boolean _package;
	private final List<TradeItem> _sellList;
	private final List<TradeItem> _sellList0;

	/**
	 * Окно управления личным магазином продажи
	 * @param seller
	 */
	public PrivateStoreManageListSell(Player seller, boolean pkg)
	{
		this._sellerId = seller.getObjectId();
		this._adena = seller.getAdena();
		this._package = pkg;
		this._sellList0 = seller.getSellList(this._package);
		this._sellList = new ArrayList<TradeItem>();

		// Проверяем список вещей в инвентаре, если вещь остутствует - убираем из списка продажи
		for (TradeItem si : this._sellList0)
		{
			if (si.getCount() <= 0)
			{
				this._sellList0.remove(si);
				continue;
			}

			ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
			if (item == null)
			{
				// вещь недоступна, пробуем найти такую же по itemId
				item = seller.getInventory().getItemByItemId(si.getItemId());
			}

			if (item == null || !item.canBeTraded(seller) || item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
			{
				this._sellList0.remove(si);
				continue;
			}

			// корректируем количество
			si.setCount(Math.min(item.getCount(), si.getCount()));
		}

		ItemInstance[] items = seller.getInventory().getItems();
		// Проверяем список вещей в инвентаре, если вещь остутствует в списке продажи, добавляем в список доступных для продажи
		loop:
		for (ItemInstance item : items)
		{
			if (item.canBeTraded(seller) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			{
				// Synerge - Dont allow selling equipment No-Grade
				if ((item.isWeapon() || item.isArmor() || item.isAccessory()) && item.getCrystalType() == Grade.NONE)
				{
					continue loop;
				}

				for (TradeItem si : this._sellList0)
				{
					if (si.getObjectId() == item.getObjectId())
					{
						if (si.getCount() == item.getCount())
						{
							continue loop;
						}

						// Показывает остаток вещей для продажи
						TradeItem ti = new TradeItem(item);
						ti.setCount(item.getCount() - si.getCount());
						this._sellList.add(ti);
						continue loop;
					}
				}
				this._sellList.add(new TradeItem(item));
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xA0);
		// section 1
		this.writeD(this._sellerId);
		this.writeD(this._package ? 1 : 0);
		this.writeQ(this._adena);

		// Список имеющихся вещей
		this.writeD(this._sellList.size());
		for (TradeItem si : this._sellList)
		{
			this.writeItemInfo(si);
			this.writeQ(si.getStorePrice());
		}

		// Список вещей уже поставленых на продажу
		this.writeD(this._sellList0.size());
		for (TradeItem si : this._sellList0)
		{
			this.writeItemInfo(si);
			this.writeQ(si.getOwnersPrice());
			this.writeQ(si.getStorePrice());
		}
	}
}
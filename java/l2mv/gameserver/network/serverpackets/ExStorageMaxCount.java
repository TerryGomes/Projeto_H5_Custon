package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

public class ExStorageMaxCount extends L2GameServerPacket
{
	private int _inventory;
	private int _warehouse;
	private int _clan;
	private int _privateSell;
	private int _privateBuy;
	private int _recipeDwarven;
	private int _recipeCommon;
	private int _inventoryExtraSlots;
	private int _questItemsLimit;

	public ExStorageMaxCount(Player player)
	{
		this._inventory = player.getInventoryLimit();
		this._warehouse = player.getWarehouseLimit();
		this._clan = Config.WAREHOUSE_SLOTS_CLAN;
		this._privateBuy = this._privateSell = player.getTradeLimit();
		this._recipeDwarven = player.getDwarvenRecipeLimit();
		this._recipeCommon = player.getCommonRecipeLimit();
		this._inventoryExtraSlots = player.getBeltInventoryIncrease();
		this._questItemsLimit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x2f);

		this.writeD(this._inventory);
		this.writeD(this._warehouse);
		this.writeD(this._clan);
		this.writeD(this._privateSell);
		this.writeD(this._privateBuy);
		this.writeD(this._recipeDwarven);
		this.writeD(this._recipeCommon);
		this.writeD(this._inventoryExtraSlots); // belt inventory slots increase count
		this.writeD(this._questItemsLimit); // quests list by off 100 maximum
	}
}
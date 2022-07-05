package l2f.gameserver.network.serverpackets;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;

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
		_inventory = player.getInventoryLimit();
		_warehouse = player.getWarehouseLimit();
		_clan = Config.WAREHOUSE_SLOTS_CLAN;
		_privateBuy = _privateSell = player.getTradeLimit();
		_recipeDwarven = player.getDwarvenRecipeLimit();
		_recipeCommon = player.getCommonRecipeLimit();
		_inventoryExtraSlots = player.getBeltInventoryIncrease();
		_questItemsLimit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x2f);

		writeD(_inventory);
		writeD(_warehouse);
		writeD(_clan);
		writeD(_privateSell);
		writeD(_privateBuy);
		writeD(_recipeDwarven);
		writeD(_recipeCommon);
		writeD(_inventoryExtraSlots); // belt inventory slots increase count
		writeD(_questItemsLimit); // quests list by off 100 maximum
	}
}
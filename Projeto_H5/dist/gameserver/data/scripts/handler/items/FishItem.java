package handler.items;

import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.reward.RewardData;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.FishTable;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Util;

public class FishItem extends ScriptItemHandler implements ScriptFile
{
	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
		{
			player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return false;
		}

		if (!player.getInventory().destroyItem(item, 1L, "FishItem"))
		{
			player.sendActionFailed();
			return false;
		}

		int count = 0;
		List<RewardData> rewards = FishTable.getInstance().getFishReward(item.getItemId());
		for (RewardData d : rewards)
		{
			long roll = Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * Config.RATE_FISH_DROP_COUNT * Config.RATE_DROP_ITEMS * player.getRateItems(), false);
			if (roll > 0)
			{
				ItemFunctions.addItem(player, d.getItemId(), roll, true, "FishItem");
				count++;
			}
		}
		if (count == 0)
		{
			player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return FishTable.getInstance().getFishIds();
	}
}
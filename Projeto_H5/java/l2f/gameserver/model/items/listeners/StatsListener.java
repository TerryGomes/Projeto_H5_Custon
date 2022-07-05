package l2f.gameserver.model.items.listeners;

import l2f.gameserver.listener.inventory.OnEquipListener;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.stats.funcs.Func;

public final class StatsListener implements OnEquipListener
{
	private static final StatsListener _instance = new StatsListener();

	public static StatsListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		actor.removeStatsOwner(item);
		actor.updateStats();
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		Func[] funcs = item.getStatFuncs();
		actor.addStatFuncs(funcs);
		actor.updateStats();
	}
}
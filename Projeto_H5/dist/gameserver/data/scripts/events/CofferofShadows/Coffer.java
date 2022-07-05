package events.CofferofShadows;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import handler.items.ScriptItemHandler;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.handler.items.ItemHandler;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.reward.RewardData;
import l2f.gameserver.model.reward.RewardList;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.ItemFunctions;

public class Coffer extends ScriptItemHandler implements ScriptFile
{
	// Дроп для эвентого сундука Coffer of Shadows
	private static final int[] _itemIds =
	{
		8659
	};

	protected static final RewardData[] _dropmats = new RewardData[]
	{
		// Item Chance
		// Материалы
		new RewardData(4041, 1, 1, 250), // Mold Hardener 0.025%
		new RewardData(4042, 1, 1, 450), // Enria 0.045%
		new RewardData(4040, 1, 1, 500), // Mold Lubricant 0.05%
		new RewardData(1890, 1, 3, 833), // Mithril Alloy 0.0833%
		new RewardData(5550, 1, 3, 833), // Durable Metal Plate 0.0833%
		new RewardData(4039, 1, 1, 833), // Mold Glue 0.0833%
		new RewardData(4043, 1, 1, 833), // Asofe 0.0833%
		new RewardData(4044, 1, 1, 833), // Thons 0.0833%
		new RewardData(1888, 1, 3, 1000), // Synthetic Cokes 0.1%
		new RewardData(1877, 1, 3, 1000), // Adamantite Nugget 0.1%
		new RewardData(1894, 1, 3, 3000), // Crafted Leather 0.3%
		new RewardData(1874, 1, 5, 3000), // Oriharukon Ore 0.3%
		new RewardData(1875, 1, 5, 3000), // Stone of Purity 0.3%
		new RewardData(1887, 1, 3, 3000), // Varnish of Purity 0.3%
		new RewardData(1866, 1, 10, 16666), // Suede 1.6666%
		new RewardData(1882, 1, 10, 16666), // Leather 1.6666%
		new RewardData(1881, 1, 10, 10000), // Coarse Bone Powder 1%
		new RewardData(1873, 1, 10, 10000), // Silver Nugget 1%
		new RewardData(1879, 1, 5, 10000), // Cokes 1%
		new RewardData(1880, 1, 5, 10000), // Steel 1%
		new RewardData(1876, 1, 5, 10000), // Mithril Ore 1%
		new RewardData(1864, 1, 20, 25000), // Stem 2.5%
		new RewardData(1865, 1, 20, 25000), // Varnish 2.5%
		new RewardData(1868, 1, 15, 25000), // Thread 2.5%
		new RewardData(1869, 1, 15, 25000), // Iron Ore 2.5%
		new RewardData(1870, 1, 15, 25000), // Coal 2.5%
		new RewardData(1871, 1, 15, 25000), // Charcoal 2.5%
		new RewardData(1872, 1, 20, 30000), // Animal Bone 3%
		new RewardData(1867, 1, 20, 33333), // Animal Skin 3.3333%
	};

	protected static final RewardData[] _dropacc = new RewardData[]
	{
		// Аксессуары и сувениры
		new RewardData(8660, 1, 1, 1000), // Demon Horns 0.1%
		new RewardData(8661, 1, 1, 1000), // Mask of Spirits 0.1%
		new RewardData(4393, 1, 1, 300), // Calculator 0.03%
		new RewardData(5590, 1, 1, 200), // Squeaking Shoes 0.02%
		new RewardData(7058, 1, 1, 50), // Chrono Darbuka 0.005%
		new RewardData(8350, 1, 1, 50), // Chrono Maracas 0.005%
		new RewardData(5133, 1, 1, 50), // Chrono Unitus 0.005%
		new RewardData(5817, 1, 1, 50), // Chrono Campana 0.005%
		new RewardData(9140, 1, 1, 30), // Salvation Bow 0.003%
		// Призрачные аксессуары - шанс 0.01%
		new RewardData(9177, 1, 1, 100), // Teddy Bear Hat - Blessed Resurrection Effect
		new RewardData(9178, 1, 1, 100), // Piggy Hat - Blessed Resurrection Effect
		new RewardData(9179, 1, 1, 100), // Jester Hat - Blessed Resurrection Effect
		new RewardData(9180, 1, 1, 100), // Wizard's Hat - Blessed Resurrection Effect
		new RewardData(9181, 1, 1, 100), // Dapper Cap - Blessed Resurrection Effect
		new RewardData(9182, 1, 1, 100), // Romantic Chapeau - Blessed Resurrection Effect
		new RewardData(9183, 1, 1, 100), // Iron Circlet - Blessed Resurrection Effect
		new RewardData(9184, 1, 1, 100), // Teddy Bear Hat - Blessed Escape Effect
		new RewardData(9185, 1, 1, 100), // Piggy Hat - Blessed Escape Effect
		new RewardData(9186, 1, 1, 100), // Jester Hat - Blessed Escape Effect
		new RewardData(9187, 1, 1, 100), // Wizard's Hat - Blessed Escape Effect
		new RewardData(9188, 1, 1, 100), // Dapper Cap - Blessed Escape Effect
		new RewardData(9189, 1, 1, 100), // Romantic Chapeau - Blessed Escape Effect
		new RewardData(9190, 1, 1, 100), // Iron Circlet - Blessed Escape Effect
		new RewardData(9191, 1, 1, 100), // Teddy Bear Hat - Big Head
		new RewardData(9192, 1, 1, 100), // Piggy Hat - Big Head
		new RewardData(9193, 1, 1, 100), // Jester Hat - Big Head
		new RewardData(9194, 1, 1, 100), // Wizard Hat - Big Head
		new RewardData(9195, 1, 1, 100), // Dapper Hat - Big Head
		new RewardData(9196, 1, 1, 100), // Romantic Chapeau - Big Head
		new RewardData(9197, 1, 1, 100), // Iron Circlet - Big Head
		new RewardData(9198, 1, 1, 100), // Teddy Bear Hat - Firework
		new RewardData(9199, 1, 1, 100), // Piggy Hat - Firework
		new RewardData(9200, 1, 1, 100), // Jester Hat - Firework
		new RewardData(9201, 1, 1, 100), // Wizard's Hat - Firework
		new RewardData(9202, 1, 1, 100), // Dapper Hat - Firework
		new RewardData(9203, 1, 1, 100), // Romantic Chapeau - Firework
		new RewardData(9204, 1, 1, 100) // Iron Circlet - Firework
	};

	protected static final RewardData[] _dropevents = new RewardData[]
	{
		// Эвентовые скролы
		new RewardData(9146, 1, 1, 3000), // Scroll of Guidance 0.3%
		new RewardData(9147, 1, 1, 3000), // Scroll of Death Whisper 0.3%
		new RewardData(9148, 1, 1, 3000), // Scroll of Focus 0.3%
		new RewardData(9149, 1, 1, 3000), // Scroll of Acumen 0.3%
		new RewardData(9150, 1, 1, 3000), // Scroll of Haste 0.3%
		new RewardData(9151, 1, 1, 3000), // Scroll of Agility 0.3%
		new RewardData(9152, 1, 1, 3000), // Scroll of Empower 0.3%
		new RewardData(9153, 1, 1, 3000), // Scroll of Might 0.3%
		new RewardData(9154, 1, 1, 3000), // Scroll of Wind Walk 0.3%
		new RewardData(9155, 1, 1, 3000), // Scroll of Shield 0.3%
		new RewardData(9156, 1, 1, 2000), // BSoE 0.2%
		new RewardData(9157, 1, 1, 1000), // BRES 0.1%

		// Хлам
		new RewardData(5234, 1, 5, 25000), // Mystery Potion 2.5%
		new RewardData(7609, 50, 100, 24000), // Proof of Catching a Fish 1.2%
		new RewardData(7562, 2, 4, 10000), // Dimensional Diamond 0.1%
		new RewardData(6415, 1, 3, 20000), // Ugly Green Fish :) 0.1%
		new RewardData(1461, 1, 3, 15000), // Crystal: A-Grade 0.5%
		new RewardData(6406, 1, 3, 20000), // Firework 1%
		new RewardData(6407, 1, 1, 20000), // Large Firework 1%
		new RewardData(6403, 1, 5, 20000), // Star Shard 1%
		new RewardData(6036, 1, 5, 30000), // GMHP 1%
		new RewardData(5595, 1, 1, 15000), // SP Scroll: High Grade 1%
		new RewardData(9898, 1, 1, 6000), // SP Scroll: Highest Grade 0.3%
		new RewardData(1374, 1, 5, 20000), // GHP 1%
		new RewardData(1375, 1, 5, 20000), // GSAP 1%
		new RewardData(1540, 1, 3, 20000), // Quick Healing Potion 1%
		new RewardData(5126, 1, 1, 1000) // Dualsword Craft Stamp 0.1%
	};

	protected static final RewardData[] _dropench = new RewardData[]
	{
		// Заточки
		new RewardData(955, 1, 1, 400), // EWD 0.04%
		new RewardData(956, 1, 1, 2000), // EAD 0.2%
		new RewardData(951, 1, 1, 300), // EWC 0.03%
		new RewardData(952, 1, 1, 1500), // EAC 0.15%
		new RewardData(947, 1, 1, 200), // EWB 0.02%
		new RewardData(948, 1, 1, 1000), // EAB 0.1%
		new RewardData(729, 1, 1, 100), // EWA 0.01%
		new RewardData(730, 1, 1, 500), // EAA 0.05%
		new RewardData(959, 1, 1, 50), // EWS 0.005%
		new RewardData(960, 1, 1, 300), // EAS 0.03%

		// Soul Cry 11-14 lvl
		new RewardData(5577, 1, 1, 90), // Red 11 0.009%
		new RewardData(5578, 1, 1, 90), // Green 11 0.009%
		new RewardData(5579, 1, 1, 90), // Blue 11 0.009%
		new RewardData(5580, 1, 1, 70), // Red 12 0.007%
		new RewardData(5581, 1, 1, 70), // Green 12 0.007%
		new RewardData(5582, 1, 1, 70), // Blue 12 0.007%
		new RewardData(5908, 1, 1, 50), // Red 13 0.005%
		new RewardData(5911, 1, 1, 50), // Green 13 0.005%
		new RewardData(5914, 1, 1, 50), // Blue 13 0.005%
		new RewardData(9570, 1, 1, 30), // Red 14 0.003%
		new RewardData(9571, 1, 1, 30), // Green 14 0.003%
		new RewardData(9572, 1, 1, 30) // Blue 14 0.003%
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (!playable.isPlayer())
		{
			return false;
		}
		Player activeChar = playable.getPlayer();

		if (!activeChar.isQuestContinuationPossible(true))
		{
			return false;
		}

		Map<Integer, Long> items = new HashMap<Integer, Long>();
		long count = 0;
		do
		{
			count++;
			getGroupItem(activeChar, _dropmats, items);
			getGroupItem(activeChar, _dropacc, items);
			getGroupItem(activeChar, _dropevents, items);
			getGroupItem(activeChar, _dropench, items);
		}
		while (ctrl && item.getCount() > count && activeChar.isQuestContinuationPossible(false));

		activeChar.getInventory().destroyItem(item, count, "CofferofShadows"); // FIXME [G1ta0] item-API
		activeChar.sendPacket(SystemMessage2.removeItems(item.getItemId(), count));
		for (Entry<Integer, Long> e : items.entrySet())
		{
			activeChar.sendPacket(SystemMessage2.obtainItems(e.getKey(), e.getValue(), 0));
		}
		return true;
	}

	/*
	 * Выбирает 1 предмет из группы
	 */
	public void getGroupItem(Player activeChar, RewardData[] dropData, Map<Integer, Long> report)
	{
		ItemInstance item;
		long count = 0;
		for (RewardData d : dropData)
		{
			if (Rnd.get(1, RewardList.MAX_CHANCE) <= d.getChance() * Config.EVENT_CofferOfShadowsRewardRate)
			{
				count = Rnd.get(d.getMinDrop(), d.getMaxDrop());
				item = ItemFunctions.createItem(d.getItemId());
				item.setCount(count);
				activeChar.getInventory().addItem(item, "CofferofShadows");
				Long old = report.get(d.getItemId());
				report.put(d.getItemId(), old != null ? old + count : count);
			}
		}
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
	public final int[] getItemIds()
	{
		return _itemIds;
	}

}
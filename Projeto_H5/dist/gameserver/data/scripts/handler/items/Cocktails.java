package handler.items;

import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.SkillTable;

public class Cocktails extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		10178,
		15356,
		20393,
		10179,
		15357,
		20394,
		14739
	};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

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

	// Sweet Fruit Cocktail
	private static final int[] sweet_list =
	{
		2404, // Might
		2405, // Shield
		2406, // Wind Walk
		2407, // Focus
		2408, // Death Whisper
		2409, // Guidance
		2410, // Bless Shield
		2411, // Bless Body
		2412, // Haste
		2413, // Vampiric Rage
	};

	// Fresh Fruit Cocktail
	private static final int[] fresh_list =
	{
		2414, // Berserker Spirit
		2411, // Bless Body
		2415, // Magic Barrier
		2405, // Shield
		2406, // Wind Walk
		2416, // Bless Soul
		2417, // Empower
		2418, // Acumen
		2419, // Clarity
	};

	// Event - Fresh Milk
	private static final int[] milk_list =
	{
		2873,
		2874,
		2875,
		2876,
		2877,
		2878,
		2879,
		2885,
		2886,
		2887,
		2888,
		2889,
		2890,
	};

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if (player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}

		if (!useItem(player, item, 1))
		{
			return false;
		}

		switch (itemId)
		{
		// Sweet Fruit Cocktail
		case 10178:
		case 15356:
		case 20393:
			for (int skill : sweet_list)
			{
				player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
			}
			break;
		// Fresh Fruit Cocktail
		case 10179:
		case 15357:
		case 20394:
			for (int skill : fresh_list)
			{
				player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
			}
			break;
		// Event - Fresh Milk
		case 14739:
			player.broadcastPacket(new MagicSkillUse(player, player, 2873, 1, 0, 0));
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2891, 6));
			for (int skill : milk_list)
			{
				player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(skill, 1));
			}
		default:
			return false;
		}

		return true;
	}
}
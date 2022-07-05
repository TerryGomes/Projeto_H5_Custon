package events.SantaEvent;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;

public class SantaEvent extends Functions implements ScriptFile, OnDeathListener
{
	private static final double[][] RANDOM_MONSTER_REWARD_CHANCE =
	{
		{
			5557,
			0.3
		},
		{
			5556,
			0.3
		},
		{
			5558,
			1.0
		},
		{
			5559,
			0.09
		}
	};
	private static final int[][] SANTA_REWARD_REQUIRED_ITEM_AMOUNT =
	{
		{
			5557,
			4
		},
		{
			5556,
			4
		},
		{
			5558,
			10
		},
		{
			5559,
			1
		}
	};
	private static final int SANTA_TREE = 5560;
	private static final int SANTA_HAT = 7836;
	private static final int MAX_LEVEL_DIFFERENCE = 5;

	private static final int SANTA_NPC_ID = 105;
	private static final Location SANTA_LOC = new Location(83608, 149192, -3400, 49524);

	@Override
	public void onLoad()
	{
		if (isActive())
		{
			CharListenerList.addGlobal(this);
			spawn(SANTA_LOC, SANTA_NPC_ID);
		}
	}

	private static boolean isActive()
	{
		return Config.EVENT_SANTA_ALLOW;
	}

	@Override
	public void onDeath(Creature actor, Creature killer)
	{
		if (killer != null && killer.isPlayable() && isActive() && Math.abs(actor.getLevel() - killer.getLevel()) <= MAX_LEVEL_DIFFERENCE)
		{
			onMonsterKilled(killer.getPlayer());
		}
	}

	private static void onMonsterKilled(Player player)
	{
		for (double[] reward : RANDOM_MONSTER_REWARD_CHANCE)
		{
			double chance = reward[1] * Config.EVENT_SANTA_CHANCE_MULT;
			if (Rnd.chance(chance))
			{
				giveMonsterReward(player, (int) reward[0]);
				return;
			}
		}
	}

	private static void giveMonsterReward(Player player, int itemId)
	{
		Functions.addItem(player, itemId, 1L, "SantaEventReward");
		player.sendMessage("Hey! Santa needs it! Tell him what you have found!");
	}

	public void getRewardFromSanta()// Method run from Santa Npc
	{
		Player player = getSelf();
		if (!checkRequiredItems(player, false))
		{
			player.sendMessage("Sorry but you don't have required Items!");
			return;
		}
		checkRequiredItems(player, true);
		Functions.addItem(player, SANTA_TREE, 1L, "SantaEventReward");
		Functions.addItem(player, SANTA_HAT, 1L, "SantaEventReward");
		player.sendMessage("Santa is really grateful! You can still bring him more!");
	}

	private static boolean checkRequiredItems(Player player, boolean delete)
	{
		for (int[] requiredItem : SANTA_REWARD_REQUIRED_ITEM_AMOUNT)
		{
			if (player.getInventory().getCountOf(requiredItem[0]) >= requiredItem[1])
			{
				if (delete)
				{
					Functions.removeItem(player, requiredItem[0], requiredItem[1], "SantaEventReward");
				}
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}

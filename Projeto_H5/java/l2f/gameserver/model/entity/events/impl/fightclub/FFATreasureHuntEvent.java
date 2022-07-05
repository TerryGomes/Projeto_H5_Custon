package l2f.gameserver.model.entity.events.impl.fightclub;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.model.instances.NpcInstance;

public class FFATreasureHuntEvent extends AbstractFightClub
{
	private static final int CHEST_ID = 37030;
	private final double badgesOpenChest;
	private final int scoreForKilledPlayer;
	private final int scoreForChest;
	private final long timeForRespawningChest;
	private final int numberOfChests;
	private final Collection<NpcInstance> spawnedChests;

	public FFATreasureHuntEvent(MultiValueSet<String> set)
	{
		super(set);
		badgesOpenChest = set.getDouble("badgesOpenChest");
		scoreForKilledPlayer = set.getInteger("scoreForKilledPlayer");
		scoreForChest = set.getInteger("scoreForChest");
		timeForRespawningChest = set.getLong("timeForRespawningChest");
		numberOfChests = set.getInteger("numberOfChests");
		spawnedChests = new CopyOnWriteArrayList<>();
	}

	@Override
	public String getShortName()
	{
		return "Treasure";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if (actor != null && actor.isPlayable())
		{
			FightClubPlayer realActor = getFightClubPlayer(actor.getPlayer());
			if (realActor != null)
			{
				if (victim.isPlayer())
				{
					realActor.increaseKills(true);
					realActor.increaseScore(scoreForKilledPlayer);
					updatePlayerScore(realActor);
					sendMessageToPlayer(realActor, MessageType.GM, "You have killed " + victim.getName());
				}
				actor.getPlayer().sendUserInfo();
			}
		}

		if (victim.isPlayer())
		{
			FightClubPlayer realVictim = getFightClubPlayer(victim);
			if (realVictim != null)
			{
				realVictim.increaseDeaths();
				if (actor != null)
				{
					sendMessageToPlayer(realVictim, MessageType.GM, "You have been killed by " + actor.getName());
				}
				victim.getPlayer().sendUserInfo();
			}
		}

		super.onKilled(actor, victim);
	}

	private void spawnChest()
	{
		spawnedChests.add(chooseLocAndSpawnNpc(CHEST_ID, getMap().getKeyLocations(), 0));
	}

	@Override
	public void startRound()
	{
		super.startRound();

		for (int i = 0; i < numberOfChests; i++)
		{
			spawnChest();
		}
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();

		for (NpcInstance chest : spawnedChests)
		{
			if (chest != null && !chest.isDead())
			{
				chest.deleteMe();
			}
		}
		spawnedChests.clear();
	}

	/**
	 * @param player
	 * @param npc
	 * @return should it disappear?
	 */
	public boolean openTreasure(Player player, NpcInstance npc)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);
		if ((fPlayer == null) || (getState() != EventState.STARTED))
		{
			return false;
		}

		player.sendMessage("You have opened the Chest. Score was increased!");
		fPlayer.increaseEventSpecificScore("chest");
		fPlayer.increaseScore(scoreForChest);
		updatePlayerScore(fPlayer);
		player.sendUserInfo();

		ThreadPoolManager.getInstance().schedule(new SpawnChest(this), timeForRespawningChest * 1000L);

		spawnedChests.remove(npc);

		return true;
	}

	private static class SpawnChest implements Runnable
	{
		private final FFATreasureHuntEvent event;

		private SpawnChest(FFATreasureHuntEvent event)
		{
			this.event = event;
		}

		@Override
		public void run()
		{
			if (event.getState() != EventState.NOT_ACTIVE)
			{
				event.spawnChest();
			}
		}
	}

	@Override
	protected int getBadgesEarned(FightClubPlayer fPlayer, int currentValue, boolean isTopKiller)
	{
		int newValue = currentValue + addMultipleBadgeToPlayer(fPlayer.getEventSpecificScore("chest"), badgesOpenChest);
		return super.getBadgesEarned(fPlayer, newValue, isTopKiller);
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);

		if (fPlayer == null)
		{
			return currentTitle;
		}

		return "Chests: " + fPlayer.getEventSpecificScore("chest") + " Kills: " + fPlayer.getKills(true);
	}
}

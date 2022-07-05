package l2f.gameserver.model.entity.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l2f.commons.annotations.NotNull;
import l2f.commons.permission.Permission;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.data.xml.holder.TournamentMapHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.entity.tournament.listener.TournamentBroadcastStatusListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentDeathListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentExitListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentLeaveZoneListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentObservationEndListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentOnDeleteCreatureListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentReceiveDamageListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentSpawnSummonListener;
import l2f.gameserver.model.entity.tournament.listener.TournamentTeleportOutOfZoneListener;
import l2f.gameserver.model.entity.tournament.permission.TournamentAttackPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentAttributeItemPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentEnchantItemPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentIgnoreAttackBlockadesPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentLogOutPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentLoseItemPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentResurrectPermission;
import l2f.gameserver.model.entity.tournament.permission.TournamentUseItemPermission;
import l2f.gameserver.model.items.ItemInstance;

public class BattleInstance
{
	public static final int FIRST_FIGHT_INDEX = 0;
	private final BattleRecord battleRecord;
	private final TournamentMap map;
	private final Reflection reflection;
	private final Map<Team, List<Player>> fightersPerTeam;
	private final List<Player> allFighters;
	private final Map<Player, List<ItemInstance>> receivedItems;
	private final TournamentExitListener exitListener;
	private final TournamentLeaveZoneListener zoneListener;
	private final TournamentDeathListener deathListener;
	private final TournamentReceiveDamageListener receiveDamageListener;
	private final TournamentSpawnSummonListener spawnSummonListener;
	private final TournamentBroadcastStatusListener broadcastStatusListener;
	private final TournamentOnDeleteCreatureListener deleteCreatureListener;
	private final TournamentTeleportOutOfZoneListener teleportOutOfZoneListener;
	private final TournamentObservationEndListener observationEndListener;
	private static final Permission<Creature> RESURRECT_PERMISSION = new TournamentResurrectPermission();
	private final Permission<Creature> attackPermission;
	private static final Permission<Creature> IGNORE_ATTACK_BLOCKADES_PERMISSION = new TournamentIgnoreAttackBlockadesPermission();
	private final Permission<Creature> useItemPermission;
	private static final Permission<Creature> ATTRIBUTE_ITEM_PERMISSION = new TournamentAttributeItemPermission();
	private static final Permission<Creature> ENCHANT_ITEM_PERMISSION = new TournamentEnchantItemPermission();
	private static final Permission<Creature> LOG_OUT_PERMISSION = new TournamentLogOutPermission();
	private static final Permission<Creature> LOSE_ITEM_PERMISSION = new TournamentLoseItemPermission();
	private final List<Permission<Creature>> permissions = new ArrayList<>();
	private ScheduledFuture<?> startFightThread;
	private ScheduledFuture<?> stopFightThread;
	private int fightIndex = 0;
	private boolean isFightTime = false;
	private int team1Wins = 0;
	private int team2Wins = 0;
	private volatile Double team1DoneDamage = 0.0;
	private volatile Double team2DoneDamage = 0.0;
	private final List<Player> observers = new CopyOnWriteArrayList<Player>();
	private final List<Player> pastObservers = new CopyOnWriteArrayList<Player>();

	protected BattleInstance(BattleRecord battleRecord)
	{
		this.battleRecord = battleRecord;
		map = TournamentMapHolder.getInstance().getRandomMap();
		reflection = ActiveBattleManager.createReflection(map);
		final Team[] teams = battleRecord.getTeams();
		fightersPerTeam = new HashMap<Team, List<Player>>(teams.length);
		for (Team team : teams)
		{
			fightersPerTeam.put(team, new ArrayList<Player>(team.getPlayerIdsForIterate().length));
		}
		final int maxFighters = teams.length * ConfigHolder.getInt("TournamentPlayersInTeam");
		allFighters = new ArrayList<Player>(maxFighters);
		receivedItems = new ConcurrentHashMap<Player, List<ItemInstance>>(maxFighters);
		exitListener = new TournamentExitListener(this);
		zoneListener = new TournamentLeaveZoneListener(this);
		deathListener = new TournamentDeathListener(this);
		receiveDamageListener = new TournamentReceiveDamageListener(this);
		spawnSummonListener = new TournamentSpawnSummonListener(this);
		broadcastStatusListener = new TournamentBroadcastStatusListener(this);
		deleteCreatureListener = new TournamentOnDeleteCreatureListener(this);
		teleportOutOfZoneListener = new TournamentTeleportOutOfZoneListener(this);
		observationEndListener = new TournamentObservationEndListener(this);
		attackPermission = new TournamentAttackPermission(this);
		useItemPermission = new TournamentUseItemPermission(this);
		permissions.add(BattleInstance.RESURRECT_PERMISSION);
		permissions.add(attackPermission);
		permissions.add(BattleInstance.IGNORE_ATTACK_BLOCKADES_PERMISSION);
		permissions.add(useItemPermission);
		permissions.add(BattleInstance.ATTRIBUTE_ITEM_PERMISSION);
		permissions.add(BattleInstance.ENCHANT_ITEM_PERMISSION);
		permissions.add(BattleInstance.LOG_OUT_PERMISSION);
		permissions.add(BattleInstance.LOSE_ITEM_PERMISSION);
	}

	@NotNull
	public BattleRecord getBattleRecord()
	{
		final BattleRecord battleRecord = this.battleRecord;
		if (battleRecord == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getBattleRecord"));
		}
		return battleRecord;
	}

	@NotNull
	public TournamentMap getMap()
	{
		final TournamentMap map = this.map;
		if (map == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getMap"));
		}
		return map;
	}

	@NotNull
	public Reflection getReflection()
	{
		final Reflection reflection = this.reflection;
		if (reflection == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getReflection"));
		}
		return reflection;
	}

	public void addFighter(Team team, Player player)
	{
		fightersPerTeam.get(team).add(player);
		allFighters.add(player);
		receivedItems.put(player, new ArrayList<ItemInstance>());
	}

	public List<Player> getFightersForIterate(Team team)
	{
		return fightersPerTeam.get(team);
	}

	public List<Player> getAllFightersForIterate()
	{
		return allFighters;
	}

	public List<Player> getAllFightersCopy()
	{
		return new ArrayList<Player>(allFighters);
	}

	public boolean isFighter(Player player)
	{
		return allFighters.contains(player);
	}

	public void removeFighter(Player player)
	{
		allFighters.remove(player);
		for (List<Player> list : fightersPerTeam.values())
		{
			list.remove(player);
		}
	}

	public void addReceivedItem(Player player, ItemInstance item)
	{
		receivedItems.get(player).add(item);
	}

	public void removeReceivedItem(Player player, ItemInstance item)
	{
		receivedItems.get(player).remove(item);
	}

	public boolean containsReceivedItem(Player player, ItemInstance item)
	{
		return receivedItems.containsKey(player) && receivedItems.get(player).contains(item);
	}

	public ItemInstance getReceivedItemByItemId(Player player, int itemId)
	{
		for (ItemInstance item : receivedItems.get(player))
		{
			if (item.getItemId() == itemId)
			{
				return item;
			}
		}
		return null;
	}

	public List<ItemInstance> getReceivedItemsForIterate(Player player)
	{
		return receivedItems.getOrDefault(player, Collections.emptyList());
	}

	public List<ItemInstance> getReceivedItemsCopy(Player player)
	{
		return new ArrayList<ItemInstance>(receivedItems.getOrDefault(player, Collections.emptyList()));
	}

	public void clearReceivedItems(Player player)
	{
		receivedItems.remove(player);
	}

	@NotNull
	public TournamentExitListener getExitListener()
	{
		final TournamentExitListener exitListener = this.exitListener;
		if (exitListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getExitListener"));
		}
		return exitListener;
	}

	@NotNull
	public TournamentLeaveZoneListener getZoneListener()
	{
		final TournamentLeaveZoneListener zoneListener = this.zoneListener;
		if (zoneListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getZoneListener"));
		}
		return zoneListener;
	}

	@NotNull
	public TournamentDeathListener getDeathListener()
	{
		final TournamentDeathListener deathListener = this.deathListener;
		if (deathListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getDeathListener"));
		}
		return deathListener;
	}

	@NotNull
	public TournamentReceiveDamageListener getReceiveDamageListener()
	{
		final TournamentReceiveDamageListener receiveDamageListener = this.receiveDamageListener;
		if (receiveDamageListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getReceiveDamageListener"));
		}
		return receiveDamageListener;
	}

	@NotNull
	public TournamentSpawnSummonListener getSpawnSummonListener()
	{
		final TournamentSpawnSummonListener spawnSummonListener = this.spawnSummonListener;
		if (spawnSummonListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getSpawnSummonListener"));
		}
		return spawnSummonListener;
	}

	@NotNull
	public TournamentBroadcastStatusListener getBroadcastStatusListener()
	{
		final TournamentBroadcastStatusListener broadcastStatusListener = this.broadcastStatusListener;
		if (broadcastStatusListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getBroadcastStatusListener"));
		}
		return broadcastStatusListener;
	}

	@NotNull
	public TournamentOnDeleteCreatureListener getDeleteCreatureListener()
	{
		final TournamentOnDeleteCreatureListener deleteCreatureListener = this.deleteCreatureListener;
		if (deleteCreatureListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getDeleteCreatureListener"));
		}
		return deleteCreatureListener;
	}

	@NotNull
	public TournamentTeleportOutOfZoneListener getTeleportOutOfZoneListener()
	{
		final TournamentTeleportOutOfZoneListener teleportOutOfZoneListener = this.teleportOutOfZoneListener;
		if (teleportOutOfZoneListener == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/model/entity/tournament/BattleInstance", "getTeleportOutOfZoneListener"));
		}
		return teleportOutOfZoneListener;
	}

	public ScheduledFuture<?> getStartFightThread()
	{
		return startFightThread;
	}

	public void setStartFightThread(ScheduledFuture<?> startFightThread)
	{
		this.startFightThread = startFightThread;
	}

	public ScheduledFuture<?> getStopFightThread()
	{
		return stopFightThread;
	}

	public void setStopFightThread(ScheduledFuture<?> stopFightThread)
	{
		this.stopFightThread = stopFightThread;
	}

	public int getFightIndex()
	{
		return fightIndex;
	}

	public void setFightIndex(int fightIndex)
	{
		this.fightIndex = fightIndex;
	}

	public void incFightIndex()
	{
		++fightIndex;
	}

	public boolean isFightTime()
	{
		return isFightTime;
	}

	public void setFightTime(boolean isFightTime)
	{
		this.isFightTime = isFightTime;
	}

	public void incTeam1Wins()
	{
		++team1Wins;
	}

	public int getTeam1Wins()
	{
		return team1Wins;
	}

	public void incTeam2Wins()
	{
		++team2Wins;
	}

	public int getTeam2Wins()
	{
		return team2Wins;
	}

	public List<Permission<Creature>> getPermissions()
	{
		return permissions;
	}

	public void addDoneDamage(int teamIndex, double damage)
	{
		if (teamIndex == 0)
		{
			team1DoneDamage += damage;
		}
		else
		{
			team2DoneDamage += damage;
		}
	}

	public void clearDamageDone()
	{
		team1DoneDamage = 0.0;
		team2DoneDamage = 0.0;
	}

	public double getDoneDamage(int teamIndex)
	{
		return teamIndex == 0 ? team1DoneDamage : team2DoneDamage;
	}

	public boolean isBattleOver()
	{
		return !battleRecord.isNowLive();
	}

	public void addObserver(Player player)
	{
		observers.add(player);
	}

	public void removeObserver(Player player)
	{
		observers.remove(player);
	}

	public List<Player> getObserversForIterate()
	{
		return observers;
	}

	public List<Player> getObserversCopy(boolean concurrent)
	{
		if (concurrent)
		{
			return new CopyOnWriteArrayList<Player>(observers);
		}
		return new ArrayList<Player>(observers);
	}

	public void addPastObserver(Player player)
	{
		pastObservers.add(player);
	}

	public List<Player> getPastObserversForIterate()
	{
		return pastObservers;
	}

	public TournamentObservationEndListener getObservationEndListener()
	{
		return observationEndListener;
	}

	@Override
	public String toString()
	{
		return "BattleInstance{battleRecord=" + battleRecord + ", map=" + map + ", fightersPerTeam=" + fightersPerTeam + ", fightIndex=" + fightIndex + ", isFightTime=" + isFightTime + ", team1Wins="
					+ team1Wins + ", team2Wins=" + team2Wins + ", observers=" + observers + '}';
	}
}

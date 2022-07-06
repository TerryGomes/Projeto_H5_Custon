package l2mv.gameserver.model.entity.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import l2mv.commons.annotations.NotNull;
import l2mv.commons.annotations.Nullable;
import l2mv.commons.permission.Permission;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.EffectsDAO;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.data.xml.holder.InstantZoneHolder;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.bypass.BypassHandler;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.game.OnConfigsReloaded;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.tournament.listener.TournamentDeathListener;
import l2mv.gameserver.model.entity.tournament.listener.TournamentExitListener;
import l2mv.gameserver.model.entity.tournament.listener.TournamentLeaveZoneListener;
import l2mv.gameserver.model.entity.tournament.permission.TournamentUseItemPermission;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.HideBoard;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.SkillCoolTime;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.TutorialCloseHtml;
import l2mv.gameserver.network.serverpackets.TutorialEnableClientEvent;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.permission.PlayablePermission;
import l2mv.gameserver.permission.PlayerPermission;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.InstantZone;
import l2mv.gameserver.templates.ZoneTemplate;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Debug;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Language;
import l2mv.gameserver.utils.Location;

public final class ActiveBattleManager
{
	private static final int INSTANT_ZONE_ID = 401;

	private long _maxBattleDurationMillis;
	private final Object endFightLock = new Object();

	private ActiveBattleManager()
	{
		calculateMaxBattleDuration();
		GameServer.getInstance().addListener(new ConfigsReloadedInstance());
	}

	public static void initialize()
	{
		getInstance();
	}

	public static long getMaxBattleDuration(TimeUnit unit)
	{
		return unit.convert(getInstance()._maxBattleDurationMillis, TimeUnit.MILLISECONDS);
	}

	private void calculateMaxBattleDuration()
	{
		_maxBattleDurationMillis = 0L;
		_maxBattleDurationMillis += ConfigHolder.getMillis("TournamentFirstFightPreparation", TimeUnit.SECONDS);
		_maxBattleDurationMillis += (ConfigHolder.getLong("TournamentFightsToResult") - 1L) * ConfigHolder.getMillis("TournamentNextFightsPreparation", TimeUnit.SECONDS);
		_maxBattleDurationMillis += ConfigHolder.getLong("TournamentFightsToResult") * ConfigHolder.getMillis("TournamentMaxFightTimeForResult", TimeUnit.SECONDS);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(this, "calculateMaxBattleDuration", _maxBattleDurationMillis, ConfigHolder.getMillis("TournamentFirstFightPreparation", TimeUnit.SECONDS), ConfigHolder.getLong("TournamentFightsToResult"), ConfigHolder.getMillis("TournamentNextFightsPreparation", TimeUnit.SECONDS), ConfigHolder.getMillis("TournamentMaxFightTimeForResult", TimeUnit.SECONDS));
		}
	}

	public static void startScheduleThread()
	{
		if (!BattleScheduleManager.getInstance().isScheduleActive())
		{
			return;
		}

		final long currentTimeMillis = System.currentTimeMillis();
		for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate())
		{
			if (record.getBattleDate() > currentTimeMillis && !ArrayUtils.contains(record.getTeams(), (Object) null))
			{
				final ScheduledFuture<?> thread = ThreadPoolManager.getInstance().schedule(new StartBattle(record), record.getBattleDate() - currentTimeMillis);
				record.setStartBattleThread(thread);
				if (!Debug.TOURNAMENT.isActive())
				{
					continue;
				}
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "startScheduleThread", record, record.getBattleDate(), currentTimeMillis);
			}
		}
	}

	private static Team getWalkoverWinner(BattleRecord battleRecord, boolean checkTeleportCriteria)
	{
		final Team[] teams = battleRecord.getTeams();
		for (int i = 0; i < teams.length; ++i)
		{
			final Team team = teams[i];
			if (team == null)
			{
				return i == 1 ? teams[0] : teams[1];
			}

			final List<Player> onlinePlayers = team.getOnlinePlayers();
			List<Player> playersMeetingCriteria;
			if (checkTeleportCriteria)
			{
				playersMeetingCriteria = new ArrayList<Player>(onlinePlayers.size());
				for (Player player : onlinePlayers)
				{
					if (getCriteriaErrorMessage(player) == null)
					{
						playersMeetingCriteria.add(player);
					}
				}
			}
			else
			{
				playersMeetingCriteria = onlinePlayers;
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getWalkoverWinner", battleRecord, checkTeleportCriteria, playersMeetingCriteria);
			}
			if (playersMeetingCriteria.size() < ConfigHolder.getInt("TournamentRequiredPlayersToFight"))
			{
				return i == 1 ? teams[0] : teams[1];
			}
		}
		return null;
	}

	private static Team getWalkoverWinner(BattleInstance battleInstance)
	{
		final Team[] teams = battleInstance.getBattleRecord().getTeams();
		for (int i = 0; i < teams.length; ++i)
		{
			if (battleInstance.getFightersForIterate(teams[i]).isEmpty())
			{
				return teams[i];
			}
		}
		return null;
	}

	@Nullable
	private static String getCriteriaErrorMessage(Player player)
	{
		if (player.isBlocked())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "Blocked", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Blocked", new Object[0]);
		}
		if (player.isInJail())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "isInJail", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Jailed", new Object[0]);
		}
		if (player.isInOfflineMode())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "isInOfflineMode", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Offline", new Object[0]);
		}
		if (player.getLevel() < ConfigHolder.getInt("TournamentMinLevel"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "tooLowLevel", player, player.getLevel());
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.TooLowLevel", new Object[0]);
		}
		if (player.getClassId().getLevel() < ConfigHolder.getInt("TournamentMinimumClassLevel"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "tooLowClassLevel", player, player.getClassId().getLevel());
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.TooLowClassLevel", new Object[0]);
		}
		if (ArrayUtils.contains(ConfigHolder.getIntArray("TournamentDisabledClasses"), player.getClassId().getId()))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "DisabledClass", player, player.getClassId().getId());
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.DisabledClass", new Object[0]);
		}
		if (Olympiad.isRegistered(player) || player.isInOlympiadMode())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "Olympiad", player, Olympiad.isRegistered(player), player.isInOlympiadMode());
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Olympiad", new Object[0]);
		}
		if (player.isCursedWeaponEquipped())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "isCursedWeaponEquipped", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.CursedWeapons", new Object[0]);
		}
		if (player.getPvpFlag() > 0 && !ConfigHolder.getBool("TournamentAllowTeleportPvPFlag"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "getPvpFlag", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Pvp", new Object[0]);
		}
		if (player.getKarma() > 0 && !ConfigHolder.getBool("TournamentAllowTeleportKarma"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "getKarma", player);
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.Karma", new Object[0]);
		}
		if (player.isInFightClub() || FightClubEventManager.getInstance().isPlayerRegistered(player, false))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getCriteriaErrorMessage", "isInFightClub", player, player.isInFightClub(), FightClubEventManager.getInstance().isPlayerRegistered(player, false));
			}
			return StringHolder.getNotNull(player, "Tournament.TeleportFail.FightClub", new Object[0]);
		}
		return null;
	}

	private static void setWalkover(BattleRecord record, Team winnerTeam)
	{
		synchronized (getInstance().endFightLock)
		{
			if (record.isPastBattle())
			{
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(ActiveBattleManager.class, "setWalkover", "PastBattle", record, winnerTeam);
				}
				return;
			}
			final BattleInstance battleInstance = record.getBattleInstance();
			if (battleInstance != null)
			{
				finalCleanBattleInstance(battleInstance);
				TeleportToTownThread.scheduleTeleportToTown(battleInstance);
			}
			if (!winnerTeam.getOnlinePlayers().isEmpty())
			{
				if (battleInstance != null)
				{
					showScreenMsgAll(battleInstance, TournamentExitListener.getWonBattleMessageToShow(winnerTeam));
				}
				else if (ConfigHolder.getBool("TournamentGlobalNotificationStartBattle"))
				{
					showGlobalNotification(null, TournamentExitListener.getWonBattleGlobalMessageToShow(winnerTeam));
				}
			}
			else if (ConfigHolder.getBool("TournamentGlobalNotificationStartBattle"))
			{
				showGlobalNotification(null, TournamentExitListener.getWonBattleGlobalMessageToShow(winnerTeam));
			}
			record.setBattleWinner(winnerTeam.getId(), ConfigHolder.getInt("TournamentFightsToResult"));
			if (battleInstance == null)
			{
				BattleScheduleManager.getInstance().checkRoundOver();
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "setWalkover", record, winnerTeam, battleInstance);
			}
		}
		record.updateInDatabase();
	}

	private static void resetBattleInstance(BattleInstance battleInstance)
	{
		battleInstance.setFightTime(false);
		if (battleInstance.getStopFightThread() != null)
		{
			battleInstance.getStopFightThread().cancel(false);
		}
		if (battleInstance.getStartFightThread() != null)
		{
			battleInstance.getStartFightThread().cancel(false);
		}
		battleInstance.clearDamageDone();
		teleportFighters(battleInstance);
		showTournamentMenu(battleInstance);
		prepareFighters(battleInstance, false);
	}

	private static void finalCleanBattleInstance(BattleInstance battleInstance)
	{
		battleInstance.setFightTime(false);
		if (battleInstance.getStopFightThread() != null)
		{
			battleInstance.getStopFightThread().cancel(false);
		}
		if (battleInstance.getStartFightThread() != null)
		{
			battleInstance.getStartFightThread().cancel(false);
		}
		changeBattleZone(battleInstance, Zone.ZoneType.peace_zone);
		battleInstance.clearDamageDone();
		prepareFighters(battleInstance, false);
	}

	private static void startBattle(BattleRecord battleRecord)
	{
		final BattleInstance battleInstance = new BattleInstance(battleRecord);
		battleRecord.setBattleInstance(battleInstance);
		changeBattleZone(battleInstance, Zone.ZoneType.peace_zone);
		for (Team team : battleRecord.getTeams())
		{
			for (Player player : team.getOnlinePlayers())
			{
				final String errorMessage = getCriteriaErrorMessage(player);
				if (errorMessage == null)
				{
					battleInstance.addFighter(team, player);
				}
				else
				{
					player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, StringHolder.getNotNull(player, "Tournament.TeleportFailMsgSender", new Object[0]), errorMessage));
				}
			}
		}
		manageZoneListener(battleInstance, true);
		manageLogoutListener(battleInstance, true);
		teleportFighters(battleInstance);
		prepareFighters(battleInstance, true);
		addBaseItems(battleInstance);
		showTournamentMenu(battleInstance);
		StartFightThread.scheduleStartFight(battleInstance);
		globalNotificationBattleStarted(battleInstance);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "startBattle", battleRecord, battleInstance, Arrays.toString(battleRecord.getTeams()));
		}
	}

	private static void globalNotificationBattleStarted(BattleInstance battleInstance)
	{
		if (ConfigHolder.getBool("TournamentGlobalNotificationStartBattle"))
		{
			final Team[] teams = battleInstance.getBattleRecord().getTeams();
			final List<Player> team1Players = battleInstance.getFightersForIterate(teams[0]);
			final List<Player> team2Players = battleInstance.getFightersForIterate(teams[1]);
			if (team1Players.size() > 2 || team2Players.size() > 2)
			{
				showGlobalNotification(battleInstance, "Tournament.GlobalNotification.BattleStart.Else", new String[0]);
			}
			else if (team1Players.size() == 2 && team2Players.size() == 2)
			{
				showGlobalNotification(battleInstance, "Tournament.GlobalNotification.BattleStart.22", team1Players.get(0).getName(), team1Players.get(1).getName(), team2Players.get(0).getName(), team2Players.get(1).getName());
			}
			else if (team1Players.size() == 2)
			{
				showGlobalNotification(battleInstance, "Tournament.GlobalNotification.BattleStart.21", team1Players.get(0).getName(), team1Players.get(1).getName(), team2Players.get(0).getName());
			}
			else if (team2Players.size() == 2)
			{
				showGlobalNotification(battleInstance, "Tournament.GlobalNotification.BattleStart.12", team1Players.get(0).getName(), team2Players.get(0).getName(), team2Players.get(1).getName());
			}
			else
			{
				showGlobalNotification(battleInstance, "Tournament.GlobalNotification.BattleStart.11", team1Players.get(0).getName(), team2Players.get(0).getName());
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "globalNotificationBattleStarted", battleInstance, Arrays.toString(teams), team1Players, team2Players);
			}
		}
	}

	public static Reflection createReflection(TournamentMap map)
	{
		final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(INSTANT_ZONE_ID);
		final Map<String, ZoneTemplate> zones = new HashMap<String, ZoneTemplate>(0);
		zones.put(map.getZoneTemplate().getName(), map.getZoneTemplate());
		final Reflection reflection = new Reflection();
		reflection.init(iz);
		reflection.init(new HashIntObjectMap<>(0), zones);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "createReflection", map, reflection, zones, iz);
		}
		return reflection;
	}

	private static void changeBattleZone(BattleInstance battleInstance, Zone.ZoneType zoneType)
	{
		for (Zone zone : battleInstance.getReflection().getZones())
		{
			zone.setType(zoneType);
			zone.setActive(true);
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "changeBattleZone", battleInstance, zoneType, zone);
			}
		}
	}

	private static void manageZoneListener(@Nullable final BattleInstance battleInstance, boolean startListener)
	{
		if (battleInstance == null)
		{
			return;
		}
		if (startListener)
		{
			for (Zone zone : battleInstance.getReflection().getZones())
			{
				zone.addListener(battleInstance.getZoneListener());
			}
		}
		else
		{
			for (Zone zone : battleInstance.getReflection().getZones())
			{
				zone.removeListener(battleInstance.getZoneListener());
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "manageZoneListener", battleInstance, startListener);
		}
	}

	private static void manageLogoutListener(@Nullable final BattleInstance battleInstance, boolean startListener)
	{
		if (battleInstance == null)
		{
			return;
		}
		if (startListener)
		{
			for (Player player : battleInstance.getAllFightersForIterate())
			{
				player.addListener(battleInstance.getExitListener());
			}
		}
		else
		{
			for (Player player : battleInstance.getAllFightersForIterate())
			{
				player.removeListener(battleInstance.getExitListener());
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "manageLogoutListener", battleInstance, startListener);
		}
	}

	private static void teleportFighters(BattleInstance battleInstance)
	{
		final Team[] teams = battleInstance.getBattleRecord().getTeams();
		final Location backLocation = ConfigHolder.getLocation("TournamentBackLocation");
		for (int i = 0; i < teams.length; ++i)
		{
			final Location loc = battleInstance.getMap().getTeamSpawnLocation(i);
			for (Player fighter : battleInstance.getFightersForIterate(teams[i]))
			{
				fighter.teleToLocation(loc, battleInstance.getReflection());
				fighter.setVar("ObservationBackLoc", backLocation.toXYZString(), -1L);
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "teleportFighters", battleInstance, Arrays.toString(teams));
		}
	}

	private static void prepareFighters(BattleInstance battleInstance, boolean removeBuffs)
	{
		final Team[] teams = battleInstance.getBattleRecord().getTeams();
		for (int i = 0; i < teams.length; ++i)
		{
			for (Player fighter : battleInstance.getFightersForIterate(teams[i]))
			{
				prepareFighter(battleInstance, i, fighter, removeBuffs);
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "prepareFighters", battleInstance, removeBuffs, Arrays.toString(teams));
		}
	}

	private static void prepareFighter(BattleInstance battleInstance, int teamIndex, Player player, boolean removeBuffs)
	{
		for (int slot : Inventory.PAPERDOLL_ORDER)
		{
			final ItemInstance item = player.getInventory().getPaperdollItem(slot);
			if (item != null && !TournamentUseItemPermission.canUseItem(battleInstance, player, item))
			{
				player.getInventory().unEquipItem(item);
			}
		}
		player.setKarma(0);
		player.abortCast(true, false);
		player.abortAttack(true, false);
		player.getEffectList().stopAllEffects();
		player.resetReuse();
		player.sendPacket(new SkillCoolTime(player));
		player.stopPvPFlag();
		if (removeBuffs)
		{
			EffectsDAO.deletePossiblePetEffects(player);
			player.getEffectList().stopAllEffects();
		}
		if (player.isDead())
		{
			player.doRevive(100.0);
		}
		player.setCurrentHp(player.getMaxHp(), true, false);
		player.setCurrentCp(player.getMaxCp(), false);
		player.setCurrentMp(player.getMaxMp(), false);
		player.stopAttackStanceTask();
		if (player.isMounted())
		{
			player.setMount(0, 0, 0);
		}
		if (player.getTransformation() > 0)
		{
			player.setTransformation(0);
			player.setTransformationTemplate(0);
			player.setTransformationName(null);
		}
		player.store(true);
		player.setTeam(teamIndex == 0 ? TeamType.RED : TeamType.BLUE);
		player.broadcastUserInfo(true);
		player.broadcastStatusUpdate();
		player.broadcastCharInfo();
		if (player.getPet() != null)
		{
			final Summon pet = player.getPet();
			if (pet.isSummon())
			{
				if (removeBuffs)
				{
					pet.getEffectList().stopAllEffects();
				}
				pet.setCurrentHpMp(pet.getMaxHp(), pet.getMaxMp(), true);
				pet.setCurrentCp(pet.getMaxCp());
				pet.stopAttackStanceTask();
				onSpawnedSummon(battleInstance, pet, false);
			}
			else
			{
				pet.unSummon();
			}
		}
		for (Permission<Creature> permission : battleInstance.getPermissions())
		{
			player.addPermission(permission);
		}
		player.addListener(battleInstance.getDeathListener());
		player.addListener(battleInstance.getReceiveDamageListener());
		player.addListener(battleInstance.getTeleportOutOfZoneListener());
		player.addListener(battleInstance.getBroadcastStatusListener());
		player.addListener(battleInstance.getSpawnSummonListener());
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "prepareFighter", battleInstance, teamIndex, player, teamIndex, player.getPet());
		}

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				player.stopAttackStanceTask();
			}
		}, 2000L);
	}

	private static void showTournamentMenu(BattleInstance battleInstance)
	{
		for (Player fighter : battleInstance.getAllFightersForIterate())
		{
			BypassHandler.getInstance().useBypassCommandHandler(fighter, "tournament main");
		}
	}

	private static void hideTournamentMenu(BattleInstance battleInstance)
	{
		for (Player fighter : battleInstance.getAllFightersForIterate())
		{
			fighter.sendPacket(new TutorialEnableClientEvent(0));
			fighter.sendPacket(TutorialCloseHtml.STATIC);
		}
	}

	private static void hideCommunityBoard(BattleInstance battleInstance)
	{
		for (Player fighter : battleInstance.getAllFightersForIterate())
		{
			fighter.sendPacket(HideBoard.PACKET);
		}
	}

	public static void onStartFightThreadOver(BattleInstance battle)
	{
		showScreenMsgAll(battle, ChatUtil.getMessagePerLang("Tournament.FightStartDate.Now", new Object[0]));
		startFight(battle);
		battle.setStartFightThread(null);
	}

	private static void startFight(BattleInstance battle)
	{
		battle.setFightTime(true);
		changeBattleZone(battle, Zone.ZoneType.battle_zone);
		EndFightByTimeThread.scheduleBattleEnd(battle);
		hideTournamentMenu(battle);
		hideCommunityBoard(battle);
		for (Player fighter : battle.getAllFightersForIterate())
		{
			fighter.setCurrentHpMp(fighter.getMaxHp(), fighter.getMaxMp(), true);
			fighter.setCurrentCp(fighter.getMaxCp());
			if (fighter.getPet() != null)
			{
				final Playable pet = fighter.getPet();
				pet.setCurrentHpMp(pet.getMaxHp(), pet.getMaxMp(), true);
				pet.setCurrentCp(pet.getMaxCp());
			}
		}
		if (ConfigHolder.getBool("TournamentGlobalNotificationStartFight"))
		{
			showGlobalNotification(battle, "Tournament.GlobalNotification.FightStarts", new String[0]);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "startFight", battle, battle.getAllFightersForIterate());
		}
	}

	public static void addItem(BattleInstance battle, Player player, int itemId, boolean equip, boolean equipOnSlotTaken, String log)
	{
		final ItemInstance item = ItemFunctions.createItem(itemId);
		if (!item.isStackable())
		{
			item.setSavableInDatabase(false);
		}
		player.getInventory().addItem(item, log);
		if (equip && item.isEquipable() && ItemFunctions.checkIfCanEquip(player, item) == null)
		{
			final boolean slotTaken = player.getInventory().isSlotTaken(item);
			if (equipOnSlotTaken || !slotTaken)
			{
				player.getInventory().equipItem(item);
			}
		}
		battle.addReceivedItem(player, item);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "addItem", battle, player, item, itemId, equip, equipOnSlotTaken, log);
		}
	}

	public static void addItems(BattleInstance battle, Player player, int itemId, long itemCount, boolean equip, boolean equipOnSlotTaken, String log)
	{
		final ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if (template.isStackable())
		{
			final ItemInstance item = ItemFunctions.createItem(itemId);
			item.setCount(itemCount);
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "addItems", "Stackable", battle, player, item, itemId, itemCount, equip, equipOnSlotTaken, log);
			}
		}
		else
		{
			for (long l = 0L; l < itemCount; ++l)
			{
				final ItemInstance item2 = ItemFunctions.createItem(itemId);
				item2.setSavableInDatabase(false);
				player.getInventory().addItem(item2, log);
				if (equip && item2.isEquipable() && ItemFunctions.checkIfCanEquip(player, item2) == null)
				{
					final boolean slotTaken = player.getInventory().isSlotTaken(item2);
					if (equipOnSlotTaken || !slotTaken)
					{
						player.getInventory().equipItem(item2);
					}
				}
				battle.addReceivedItem(player, item2);
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(ActiveBattleManager.class, "addItems", battle, player, item2, itemId, itemCount, equip, equipOnSlotTaken, log);
				}
			}
		}
	}

	private static void addBaseItems(BattleInstance battle)
	{
		for (Player fighter : battle.getAllFightersForIterate())
		{
			addBaseItems(battle, fighter);
		}
	}

	private static void addBaseItems(BattleInstance battle, Player player)
	{
		for (Map.Entry<Integer, Long> item : ConfigHolder.getMap("TournamentItemsToEveryPlayer", Integer.class, Long.class).entrySet())
		{
			addItems(battle, player, item.getKey(), item.getValue(), true, false, "AddingTournamentBaseItems");
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "addBaseItems", battle, player, item);
			}
		}
	}

	public static void deleteItems(BattleInstance battleInstance, Player player, int[] itemIds, String log)
	{
		int count = 0;
		for (int itemId : itemIds)
		{
			final ItemInstance receivedItem = battleInstance.getReceivedItemByItemId(player, itemId);
			if (receivedItem != null)
			{
				player.getInventory().destroyItem(receivedItem, log);
				battleInstance.removeReceivedItem(player, receivedItem);
				++count;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "deleteItems", battleInstance, player, Arrays.toString(itemIds), log, count);
		}
	}

	private static void deleteReceivedItems(BattleInstance battle, Player player, String log)
	{
		int count = 0;
		for (ItemInstance receivedItem : battle.getReceivedItemsCopy(player))
		{
			player.getInventory().destroyItem(receivedItem, log);
			battle.removeReceivedItem(player, receivedItem);
			++count;
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "deleteReceivedItems", battle, player, log, count);
		}
	}

	public static void onExitGame(BattleInstance battleInstance, Player player)
	{
		if (battleInstance.getAllFightersForIterate().contains(player) && !battleInstance.isBattleOver())
		{
			removePlayer(battleInstance, player);
			final Team walkoverWinner = getWalkoverWinner(battleInstance);
			if (walkoverWinner != null)
			{
				setWalkover(battleInstance.getBattleRecord(), walkoverWinner);
			}
			else if (checkFightOver(battleInstance))
			{
				endFight(battleInstance, getLoserTeam(battleInstance, true), FightEndType.LOG_OUT);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onExitGame", battleInstance, player);
			}
		}
	}

	public static void onEnterZone(BattleInstance battleInstance, Zone zone, Creature actor)
	{
		if (battleInstance.isBattleOver())
		{
			return;
		}
		if (!actor.isPlayable())
		{
			actor.deleteMe();
			return;
		}
		final Player pcActor = actor.getPlayer();
		for (Player fighter : battleInstance.getAllFightersForIterate())
		{
			if (fighter.equals(pcActor))
			{
				return;
			}
		}
		if (battleInstance.getObserversForIterate().contains(actor.getPlayer()))
		{
			return;
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onEnterZone", battleInstance, actor, zone);
		}
		pcActor.teleToLocation(ConfigHolder.getLocation("TournamentBackLocation"), ReflectionManager.DEFAULT);
	}

	public static void onLeaveZone(BattleInstance battleInstance, Zone zone, Creature actor)
	{
		if (!actor.isPlayable() || battleInstance.isBattleOver() || actor.isTeleporting())
		{
			return;
		}
		final Player pcActor = actor.getPlayer();
		for (Team team : battleInstance.getBattleRecord().getTeams())
		{
			for (Player fighter : battleInstance.getFightersForIterate(team))
			{
				if (fighter.equals(pcActor))
				{
					final Location spawn = battleInstance.getMap().getTeamSpawnLocation(battleInstance.getBattleRecord().getTeamIndex(team));
					actor.teleToLocation(spawn);
					if (Debug.TOURNAMENT.isActive())
					{
						Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onLeaveZone", battleInstance, actor, zone, team, fighter);
					}
					return;
				}
			}
		}
	}

	public static boolean canUseBuffer(Player player, boolean premiumBuff)
	{
		final BattleRecord battle = BattleScheduleManager.getInstance().getBattle(player);
		if (battle == null)
		{
			return true;
		}
		final BattleInstance battleInstance = battle.getBattleInstance();
		if (battleInstance == null || battleInstance.isBattleOver())
		{
			return true;
		}
		if (battleInstance.isFightTime() || !ConfigHolder.getBool("TournamentAllowBuffer") || (premiumBuff && ConfigHolder.getBool("TournamentPremiumBuffsDisabled")))
		{
			return false;
		}
		return true;
	}

	public static void onReceivedDamage(BattleInstance battleInstance, Creature attacker, Creature actor, double damage)
	{
		if (battleInstance.isBattleOver())
		{
			return;
		}
		if (damage > 0.0 && actor.isPlayer() && attacker.isPlayable())
		{
			final int attackerTeamIndex = battleInstance.getBattleRecord().getTeamIndex((Playable) attacker);
			if (attackerTeamIndex >= 0)
			{
				battleInstance.addDoneDamage(attackerTeamIndex, damage);
			}
		}
	}

	public static void onSpawnedSummon(BattleInstance battleInstance, Summon summon, boolean isNewSummon)
	{
		if (!summon.isSummon())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onSpawnedSummon", "NotSummon", battleInstance, summon, isNewSummon);
			}
			summon.unSummon();
			return;
		}
		final int teamIndex = battleInstance.getBattleRecord().getTeamIndex(summon);
		summon.setTeam(teamIndex == 0 ? TeamType.RED : TeamType.BLUE);
		summon.broadcastCharInfo();
		for (Permission<Creature> permission : battleInstance.getPermissions())
		{
			if (permission instanceof PlayablePermission && !(permission instanceof PlayerPermission))
			{
				summon.addPermission(permission);
			}
		}
		summon.addListener(battleInstance.getBroadcastStatusListener());
		summon.addListener(battleInstance.getDeleteCreatureListener());
		if (isNewSummon)
		{
			BattleObservationManager.broadcastObserversSpawnedSummon(battleInstance, summon);
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onSpawnedSummon", battleInstance, summon, isNewSummon);
		}
	}

	public static void onUnsummonPet(BattleInstance battleInstance, Summon summon)
	{
		BattleObservationManager.broadcastObserversUnspawnedSummon(battleInstance, summon);
	}

	private static boolean isFighting(BattleInstance battleInstance, Player fighter)
	{
		if (fighter.isDead())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isFighting", "isDead", battleInstance, fighter);
			}
			return false;
		}
		if (battleInstance.getReflection().getId() != fighter.getReflectionId())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isFighting", battleInstance, fighter, fighter.getReflectionId());
			}
			return false;
		}
		if (fighter.isInOfflineMode())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isFighting", "isInOfflineMode", battleInstance, fighter);
			}
			return false;
		}
		return true;
	}

	public static void onDeath(BattleInstance battleInstance, Creature actor, Creature killer)
	{
		if (actor.isPet() || battleInstance.isBattleOver())
		{
			return;
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onDeath", battleInstance, actor, killer, battleInstance.isFightTime());
		}
		if (battleInstance.isFightTime())
		{
			if (killer == null)
			{
				showScreenMsgAll(battleInstance, ChatUtil.getMessagePerLang("Tournament.Kill.KillerNull", actor.getName()));
				if (ConfigHolder.getBool("TournamentGlobalNotificationKill"))
				{
					showGlobalNotification(battleInstance, "Tournament.GlobalNotification.Kill.KillerNull", actor.getName());
				}
			}
			else
			{
				showScreenMsgAll(battleInstance, ChatUtil.getMessagePerLang("Tournament.Kill.KillerNotNull", actor.getName(), killer.getName()));
				if (ConfigHolder.getBool("TournamentGlobalNotificationKill"))
				{
					showGlobalNotification(battleInstance, "Tournament.GlobalNotification.Kill.KillerNotNull", actor.getName(), killer.getName());
				}
			}
			if (checkFightOver(battleInstance))
			{
				endFight(battleInstance, getLoserTeam(battleInstance, true), FightEndType.KILL);
			}
		}
		else
		{
			actor.setCurrentHp(actor.getMaxHp(), true, false);
			actor.setCurrentCp(actor.getMaxCp(), false);
			actor.setCurrentMp(actor.getMaxMp(), false);
			actor.broadcastStatusUpdate();
			actor.sendChanges();
		}
	}

	public static boolean clearRestartTypes(Player player)
	{
		final BattleRecord battle = BattleScheduleManager.getInstance().getBattle(player);
		return battle != null && battle.getBattleInstance() != null;
	}

	private static boolean checkFightOver(BattleInstance battleInstance)
	{
		if (!battleInstance.isFightTime())
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "checkFightOver", "NotFightTime", battleInstance);
			}
			return false;
		}
		final Team loserTeam = getLoserTeam(battleInstance, false);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "checkFightOver", battleInstance, loserTeam);
		}
		return loserTeam != null;
	}

	public static Team endFight(BattleInstance battleInstance, FightEndType endType)
	{
		return endFight(battleInstance, getLoserTeam(battleInstance, true), endType);
	}

	public static Team endFight(BattleInstance battleInstance, @NotNull final Team fightLoserTeam, FightEndType endType)
	{
		if (fightLoserTeam == null)
		{
			throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "fightLoserTeam", "l2f/gameserver/model/entity/tournament/ActiveBattleManager", "endFight"));
		}
		synchronized (getInstance().endFightLock)
		{
			final BattleRecord record = battleInstance.getBattleRecord();
			if (!battleInstance.isFightTime() || record.isPastBattle())
			{
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(ActiveBattleManager.class, "endFight", battleInstance, fightLoserTeam, endType, battleInstance.isFightTime(), record.isPastBattle());
				}
				return null;
			}
			battleInstance.incFightIndex();
			if (record.isFirstTeam(fightLoserTeam))
			{
				battleInstance.incTeam2Wins();
			}
			else
			{
				battleInstance.incTeam1Wins();
			}
			final Team fightWinnerTeam = record.getSecondTeam(fightLoserTeam);
			if (isEndOfBattle(battleInstance))
			{
				final Team battleWinnerTeam = decideBattleWinner(battleInstance);
				giveConsolationPrize(record.getSecondTeam(battleWinnerTeam));
				finalCleanBattleInstance(battleInstance);
				record.setBattleWinner(battleWinnerTeam.getId(), record.isFirstTeam(battleWinnerTeam) ? battleInstance.getTeam1Wins() : battleInstance.getTeam2Wins());
				record.updateInDatabase();
				TeleportToTownThread.scheduleTeleportToTown(battleInstance);
				showWonBattleEffect(battleWinnerTeam);
				showScreenMsgAll(battleInstance, TournamentDeathListener.getWonBattleMessageToShow(battleWinnerTeam));
				if (ConfigHolder.getBool("TournamentGlobalNotificationWonBattle"))
				{
					showGlobalNotification(battleInstance, TournamentDeathListener.getWonBattleGlobalMessageToShow(battleWinnerTeam));
				}
				BattleObservationManager.onBattleOver(battleInstance);
			}
			else
			{
				resetBattleInstance(battleInstance);
				StartFightThread.scheduleStartFight(battleInstance);
				showScreenMsgAll(battleInstance, endType.getWonFightMessage(fightWinnerTeam));
				if (ConfigHolder.getBool("TournamentGlobalNotificationWonFight"))
				{
					showGlobalNotification(battleInstance, TournamentDeathListener.getWonFightGlobalMessageToShow(fightWinnerTeam));
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "endFight", battleInstance, record, fightLoserTeam, endType, fightWinnerTeam, battleInstance.getFightIndex());
			}
		}
		return fightLoserTeam;
	}

	private static void showWonBattleEffect(Team team)
	{
		if (!ConfigHolder.checkIsEmpty("TournamentWonBattleEffect"))
		{
			final Map.Entry<Integer, Integer> effect = ConfigHolder.getMapEntry("TournamentWonBattleEffect", Integer.class, Integer.class);
			for (Player player : team.getOnlinePlayers())
			{
				player.broadcastPacket(new MagicSkillUse(player, player, effect.getKey(), 1, effect.getValue(), 0L));
			}
		}
	}

	private static boolean isEndOfBattle(BattleInstance battleInstance)
	{
		if (battleInstance.getFightIndex() == ConfigHolder.getInt("TournamentFightsToResult"))
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isEndOfBattle", "ReachedFightIndex", battleInstance.getFightIndex());
			}
			return true;
		}
		if (ConfigHolder.getBool("TournamentEndBattleOnCertainWin"))
		{
			final int battlesForCertainWin = (int) Math.floor(ConfigHolder.getDouble("TournamentFightsToResult") / 2.0) + 1;
			if (battleInstance.getTeam1Wins() >= battlesForCertainWin || battleInstance.getTeam2Wins() >= battlesForCertainWin)
			{
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isEndOfBattle", "CertainWin", battlesForCertainWin, battleInstance.getTeam1Wins(), battleInstance.getTeam2Wins());
				}
				return true;
			}
		}
		final Team walkoverWinner = getWalkoverWinner(battleInstance);
		if (walkoverWinner != null)
		{
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "isEndOfBattle", "Walkover", walkoverWinner);
			}
			return true;
		}
		return false;
	}

	private static Team decideBattleWinner(BattleInstance battleInstance)
	{
		if (battleInstance.getTeam1Wins() > ConfigHolder.getDouble("TournamentFightsToResult") / 2.0)
		{
			return battleInstance.getBattleRecord().getTeam1();
		}
		if (battleInstance.getTeam2Wins() > ConfigHolder.getDouble("TournamentFightsToResult") / 2.0)
		{
			return battleInstance.getBattleRecord().getTeam2();
		}
		final Team walkoverWinner = getWalkoverWinner(battleInstance);
		if (walkoverWinner != null)
		{
			return walkoverWinner;
		}
		return battleInstance.getTeam1Wins() > battleInstance.getTeam2Wins() ? battleInstance.getBattleRecord().getTeam1() : battleInstance.getBattleRecord().getTeam2();
	}

	@Nullable
	private static Team getLoserTeam(BattleInstance battleInstance, boolean mustChooseLoser)
	{
		for (Team team : battleInstance.getBattleRecord().getTeams())
		{
			int fightingPlayers = 0;
			for (Player fighter : battleInstance.getFightersForIterate(team))
			{
				if (isFighting(battleInstance, fighter))
				{
					++fightingPlayers;
				}
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getLoserTeam", battleInstance, mustChooseLoser, team, fightingPlayers);
			}
			if (fightingPlayers == 0)
			{
				return team;
			}
		}
		if (!mustChooseLoser)
		{
			return null;
		}
		double leastDamageDone = Double.MAX_VALUE;
		Team leastDoneDamageTeam = null;
		for (Team team : battleInstance.getBattleRecord().getTeams())
		{
			final int teamIndex = battleInstance.getBattleRecord().getTeamIndex(team);
			final double doneDamage = battleInstance.getDoneDamage(teamIndex);
			if (doneDamage < leastDamageDone)
			{
				leastDamageDone = doneDamage;
				leastDoneDamageTeam = team;
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "getLoserTeam", battleInstance, leastDamageDone, leastDoneDamageTeam);
		}
		return leastDoneDamageTeam;
	}

	private static void giveConsolationPrize(Team battleLoserTeam)
	{
		if (!ConfigHolder.checkIsEmpty("TournamentExtraLoserReward"))
		{
			final Map.Entry<Integer, Long> prize = ConfigHolder.getMapEntry("TournamentExtraLoserReward", Integer.class, Long.class);
			for (Player player : battleLoserTeam.getOnlinePlayers())
			{
				Functions.addItem(player, prize.getKey(), prize.getValue(), "TournamentConsolationPrize");
				if (Debug.TOURNAMENT.isActive())
				{
					Debug.TOURNAMENT.debug(ActiveBattleManager.class, "giveConsolationPrize", battleLoserTeam, player, prize);
				}
			}
		}
	}

	private static void removePlayer(BattleInstance battleInstance, Player player)
	{
		battleInstance.removeFighter(player);
		player.removeListener(battleInstance.getExitListener());
		for (Permission<Creature> permission : battleInstance.getPermissions())
		{
			player.removePermission(permission);
		}
		player.removeListener(battleInstance.getDeathListener());
		player.removeListener(battleInstance.getReceiveDamageListener());
		player.removeListener(battleInstance.getTeleportOutOfZoneListener());
		player.removeListener(battleInstance.getBroadcastStatusListener());
		player.removeListener(battleInstance.getSpawnSummonListener());
		player.setTeam(TeamType.NONE);
		player.broadcastCharInfo();
		deleteReceivedItems(battleInstance, player, "RemovePlayer");
		if (player.getPet() != null)
		{
			final Summon summon = player.getPet();
			summon.setTeam(TeamType.NONE);
			summon.broadcastCharInfo();
			for (Permission<Creature> permission2 : battleInstance.getPermissions())
			{
				if (permission2 instanceof PlayablePermission && !(permission2 instanceof PlayerPermission))
				{
					summon.removePermission(permission2);
				}
			}
			summon.removeListener(battleInstance.getBroadcastStatusListener());
			summon.removeListener(battleInstance.getDeleteCreatureListener());
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "removePlayer", battleInstance, player, player.getPet());
		}
	}

	public static void teleportBackToTown(BattleInstance battle)
	{
		for (Player fighter : battle.getAllFightersCopy())
		{
			removePlayer(battle, fighter);
			final Location loc = Location.findAroundPosition(ConfigHolder.getLocation("TournamentBackLocation"), 0, 50, fighter.getGeoIndex());
			fighter.teleToLocation(loc, ReflectionManager.DEFAULT);
			fighter.unsetVar("ObservationBackLoc");
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "teleportBackToTown", battle, fighter, loc);
			}
		}
	}

	public static void cleanBattleRecord(BattleRecord battleRecord)
	{
		manageZoneListener(battleRecord.getBattleInstance(), false);
		battleRecord.setBattleInstance(null);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "cleanBattleRecord", battleRecord);
		}
	}

	public static void showScreenMsgAll(BattleInstance battle, Map<Language, String> msgToShow)
	{
		final Map<Language, ExShowScreenMessage> screenPackets = new EnumMap<Language, ExShowScreenMessage>(Language.class);
		for (Map.Entry<Language, String> msg : msgToShow.entrySet())
		{
			screenPackets.put(msg.getKey(), new ExShowScreenMessage(msg.getValue(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		}
		for (Player fighter : battle.getAllFightersForIterate())
		{
			fighter.sendPacket(screenPackets.get(fighter.getLanguage()));
		}
		if (ConfigHolder.getBool("TournamentObserversHaveBattleNotifications"))
		{
			for (Player observer : battle.getObserversForIterate())
			{
				observer.sendPacket(screenPackets.get(observer.getLanguage()));
			}
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "showScreenMsgAll", battle, msgToShow, battle.getObserversForIterate());
		}
	}

	public static void showSystemMsgAll(BattleInstance battle, Map<Language, String> msgToShow)
	{
		final Map<Language, SystemMessage> screenPackets = new EnumMap<Language, SystemMessage>(Language.class);
		for (Map.Entry<Language, String> msg : msgToShow.entrySet())
		{
			screenPackets.put(msg.getKey(), new SystemMessage(msg.getValue()));
		}
		for (Player fighter : battle.getAllFightersForIterate())
		{
			fighter.sendPacket(screenPackets.get(fighter.getLanguage()));
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "showSystemMsgAll", battle, msgToShow, battle.getAllFightersForIterate());
		}
	}

	public static void showGlobalNotification(@Nullable final BattleInstance battle, String address, String... parameters)
	{
		final Map<Language, IStaticPacket> packetPerLang = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language lang : Language.values())
		{
			packetPerLang.put(lang, new Say2(0, ConfigHolder.getChatType("TournamentGlobalNotificationChat"), StringHolder.getNotNull(lang, "Tournament.GlobalNotification.SenderName", new Object[0]), StringHolder.getNotNull(lang, address, (Object[]) parameters)));
		}
		final List<Player> players = GameObjectsStorage.getAllPlayersCopy();
		if (battle != null)
		{
			players.removeAll(battle.getAllFightersForIterate());
			players.removeAll(battle.getObserversForIterate());
		}
		for (Player player : players)
		{
			player.sendPacket(packetPerLang.get(player.getLanguage()));
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "showGlobalNotification", battle, address, Arrays.toString(parameters), players);
		}
	}

	public static void showGlobalNotification(@Nullable final BattleInstance battle, Map<Language, String> message)
	{
		final Map<Language, IStaticPacket> packetPerLang = new EnumMap<Language, IStaticPacket>(Language.class);
		for (Language lang : Language.values())
		{
			packetPerLang.put(lang, new Say2(0, ConfigHolder.getChatType("TournamentGlobalNotificationChat"), StringHolder.getNotNull(lang, "Tournament.GlobalNotification.SenderName", new Object[0]), message.get(lang)));
		}
		final List<Player> players = GameObjectsStorage.getAllPlayersCopy();
		if (battle != null)
		{
			players.removeAll(battle.getAllFightersForIterate());
			players.removeAll(battle.getObserversForIterate());
		}
		for (Player player : players)
		{
			player.sendPacket(packetPerLang.get(player.getLanguage()));
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "showGlobalNotification", battle, message, players);
		}
	}

	private static ActiveBattleManager getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ActiveBattleManager instance = new ActiveBattleManager();
	}

	private static class ConfigsReloadedInstance implements OnConfigsReloaded
	{
		@Override
		public void onConfigsReloaded()
		{
			getInstance().calculateMaxBattleDuration();
		}
	}

	private static class StartBattle extends RunnableImpl
	{
		private final BattleRecord _battleRecord;

		public StartBattle(BattleRecord battleRecord)
		{
			_battleRecord = battleRecord;
		}

		@Override
		public void runImpl()
		{
			final Team walkoverWinner = getWalkoverWinner(_battleRecord, true);
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(this, "StartBattle", _battleRecord, walkoverWinner);
			}
			if (walkoverWinner == null)
			{
				startBattle(_battleRecord);
			}
			else
			{
				setWalkover(_battleRecord, walkoverWinner);
			}
		}
	}

	public enum FightEndType
	{
		KILL, TIME, LOG_OUT, LEAVE_ZONE;

		public Map<Language, String> getWonFightMessage(Team winnerTeam)
		{
			switch (this)
			{
			case TIME:
			{
				return EndFightByTimeThread.getWonFightMessageToShow(winnerTeam);
			}
			case KILL:
			{
				return TournamentDeathListener.getWonFightMessageToShow(winnerTeam);
			}
			case LOG_OUT:
			{
				return TournamentExitListener.getWonFightMessageToShow(winnerTeam);
			}
			case LEAVE_ZONE:
			{
				return TournamentLeaveZoneListener.getWonFightMessageToShow(winnerTeam);
			}
			default:
			{
				throw new AssertionError("Won Fight Message not initialized for Fight End Type " + toString());
			}
			}
		}
	}
}

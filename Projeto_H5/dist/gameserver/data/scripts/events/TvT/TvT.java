package events.TvT;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerExitListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class TvT extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{

	/*
	 * private static final Logger _log = LoggerFactory.getLogger(TvT.class);
	 * private static ScheduledFuture<?> _startTask;
	 * private static final int[] doors = new int[] { 24190001, 24190002, 24190003, 24190004 };
	 * private static List<Long> players_list1 = new CopyOnWriteArrayList<Long>();
	 * private static List<Long> players_list2 = new CopyOnWriteArrayList<Long>();
	 * private static List<Long> live_list1 = new CopyOnWriteArrayList<Long>();
	 * private static List<Long> live_list2 = new CopyOnWriteArrayList<Long>();
	 * private static int[][] mage_buffs = new int[Config.EVENT_TvTMageBuffs.length][2];
	 * private static int[][] fighter_buffs = new int[Config.EVENT_TvTFighterBuffs.length][2];
	 * private static long _startedTime = 0;
	 * private static int[][] rewards = new int[Config.EVENT_TvTRewards.length][2];
	 * private static Map<Long, Location> playerRestoreCoord = new LinkedHashMap<Long, Location>();
	 * private static Map<Long, String> boxes = new LinkedHashMap<Long, String>();
	 * private static boolean _isRegistrationActive = false;
	 * private static int _status = 0;
	 * private static int _time_to_start;
	 * private static int _category;
	 * private static int _minLevel;
	 * private static int _maxLevel;
	 * private static int _autoContinue = 0;
	 * private static boolean _active = false;
	 * private static Skill buff;
	 * private static Reflection reflection = ReflectionManager.TVT_EVENT;
	 * private static ScheduledFuture<?> _endTask;
	 * private static Zone _zone;
	 * private static Zone _zone1;
	 * private static Zone _zone2;
	 * private static Zone _zone3;
	 * private static Zone _zone4;
	 * private static Zone _zone5;
	 * //new
	 * private static Zone _zone6;
	 * private static Zone _zone7;
	 * private static Zone _zone8;
	 * private static Zone _zone9;
	 * private static Zone _zone10;
	 * private static Zone _myZone = null;
	 * private static Territory territory = null;
	 * private static Map<Integer, Integer> _pScore = new HashMap<Integer, Integer>();
	 * private static Map<Integer, Integer> _pScoreStatic = new HashMap<Integer, Integer>();
	 * private static Map<String, ZoneTemplate> _zones = new HashMap<String, ZoneTemplate>();
	 * private static IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap<DoorTemplate>();
	 * private static ZoneListener _zoneListener = new ZoneListener();
	 * private static int bluePoints = 0;
	 * private static int redPoints = 0;
	 */

	@Override
	public void onLoad()
	{
		/*
		 * CharListenerList.addGlobal(this);
		 * _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
		 * _zones.put("[baylor_tvt]", ReflectionUtils.getZone("[baylor_tvt]").getTemplate());
		 * _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
		 * _zones.put("[beleth_tvt]", ReflectionUtils.getZone("[beleth_tvt]").getTemplate());
		 * _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
		 * _zones.put("[cleft_tvt]", ReflectionUtils.getZone("[cleft_tvt]").getTemplate());
		 * //new
		 * _zones.put("[baium_lair_tvt]", ReflectionUtils.getZone("[baium_lair_tvt]").getTemplate());
		 * _zones.put("[parnas_tvt]", ReflectionUtils.getZone("[parnas_tvt]").getTemplate());
		 * _zones.put("[tully_tvt]", ReflectionUtils.getZone("[tully_tvt]").getTemplate());
		 * //new
		 * _zones.put("[destruction_tvt]", ReflectionUtils.getZone("[destruction_tvt]").getTemplate());
		 * _zones.put("[cleft_tvt]", ReflectionUtils.getZone("[cleft_tvt]").getTemplate());
		 * for (final int doorId : doors)
		 * _doors.put(doorId, ReflectionUtils.getDoor(doorId).getTemplate());
		 * int geoIndex = GeoEngine.NextGeoIndex(24, 19, reflection.getId());
		 * reflection.setGeoIndex(geoIndex);
		 * reflection.init(_doors, _zones);
		 * _zone = reflection.getZone("[hellbound_quarry_tvt]");
		 * _zone1 = reflection.getZone("[baylor_tvt]");
		 * _zone2 = reflection.getZone("[hellbound_quarry_tvt]");
		 * _zone3 = reflection.getZone("[beleth_tvt]");
		 * _zone4 = reflection.getZone("[hellbound_quarry_tvt]");
		 * _zone5 = reflection.getZone("[cleft_tvt]");
		 * //new
		 * _zone6 = reflection.getZone("[baium_lair_tvt]");
		 * _zone7 = reflection.getZone("[parnas_tvt]");
		 * _zone8 = reflection.getZone("[tully_tvt]");
		 * //new
		 * _zone9 = reflection.getZone("[destruction_tvt]");
		 * _zone10 = reflection.getZone("[cleft_tvt]");
		 * _active = ServerVariables.getString("TvT", "off").equalsIgnoreCase("on");
		 * if (isActive())
		 * scheduleEventStart();
		 * _zone.addListener(_zoneListener);
		 * _zone1.addListener(_zoneListener);
		 * _zone2.addListener(_zoneListener);
		 * _zone3.addListener(_zoneListener);
		 * _zone4.addListener(_zoneListener);
		 * _zone5.addListener(_zoneListener);
		 * _zone6.addListener(_zoneListener);
		 * _zone7.addListener(_zoneListener);
		 * _zone8.addListener(_zoneListener);
		 * _zone9.addListener(_zoneListener);
		 * _zone10.addListener(_zoneListener);
		 * int i = 0;
		 * if (Config.EVENT_TvTBuffPlayers && Config.EVENT_TvTMageBuffs.length != 0)
		 * for (String skill : Config.EVENT_TvTMageBuffs) {
		 * String[] splitSkill = skill.split(",");
		 * mage_buffs[i][0] = Integer.parseInt(splitSkill[0]);
		 * mage_buffs[i][1] = Integer.parseInt(splitSkill[1]);
		 * i++;
		 * }
		 * i = 0;
		 * if (Config.EVENT_TvTBuffPlayers && Config.EVENT_TvTMageBuffs.length != 0)
		 * for (String skill : Config.EVENT_TvTFighterBuffs) {
		 * String[] splitSkill = skill.split(",");
		 * fighter_buffs[i][0] = Integer.parseInt(splitSkill[0]);
		 * fighter_buffs[i][1] = Integer.parseInt(splitSkill[1]);
		 * i++;
		 * }
		 * i = 0;
		 * if (Config.EVENT_TvTRewards.length != 0)
		 * for (String reward : Config.EVENT_TvTRewards) {
		 * String[] splitReward = reward.split(",");
		 * rewards[i][0] = Integer.parseInt(splitReward[0]);
		 * rewards[i][1] = Integer.parseInt(splitReward[1]);
		 * i++;
		 * }
		 * _log.info("Loaded Event: TvT");
		 */
	}

	@Override
	public void onReload()
	{
		/*
		 * if(_startTask != null) {
		 * _startTask.cancel(false);
		 * _startTask = null;
		 * }
		 */
	}

	@Override
	public void onShutdown()
	{
		// onReload();
	}

	public void activateEvent()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (!isActive())
		 * {
		 * if (_startTask == null)
		 * scheduleEventStart();
		 * ServerVariables.set("TvT", "on");
		 * _log.info("Event 'TvT' activated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStarted", null);
		 * }
		 * else
		 * player.sendMessage("Event 'TvT' already active.");
		 * _active = true;
		 * show("admin/events/events.htm", player);
		 */
	}

	public void deactivateEvent()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (isActive()) {
		 * if (_startTask != null) {
		 * _startTask.cancel(false);
		 * _startTask = null;
		 * }
		 * ServerVariables.unset("TvT");
		 * _log.info("Event 'TvT' deactivated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStoped", null);
		 * }
		 * else
		 * player.sendMessage("Event 'TvT' not active.");
		 * _active = false;
		 * show("admin/events/events.htm", player);
		 */
	}

	public static boolean isRunned()
	{
		// return _isRegistrationActive || _status > 0;
		return false;
	}

	public static int getMinLevelForCategory(int category)
	{
		/*
		 * switch(category) {
		 * case 1:
		 * return 20;
		 * case 2:
		 * return 30;
		 * case 3:
		 * return 40;
		 * case 4:
		 * return 52;
		 * case 5:
		 * return 62;
		 * case 6:
		 * return 76;
		 * }
		 */
		return 0;
	}

	public static int getMaxLevelForCategory(int category)
	{
		/*
		 * switch(category)
		 * {
		 * case 1:
		 * return 29;
		 * case 2:
		 * return 39;
		 * case 3:
		 * return 51;
		 * case 4:
		 * return 61;
		 * case 5:
		 * return 75;
		 * case 6:
		 * return 85;
		 * }
		 */
		return 0;
	}

	public static int getCategory(int level)
	{
		/*
		 * if(level >= 20 && level <= 29)
		 * return 1;
		 * else if (level >= 30 && level <= 39)
		 * return 2;
		 * else if (level >= 40 && level <= 51)
		 * return 3;
		 * else if (level >= 52 && level <= 61)
		 * return 4;
		 * else if (level >= 62 && level <= 75)
		 * return 5;
		 * else if (level >= 76)
		 * return 6;
		 */
		return 0;
	}

	public void start(String[] var)
	{
		/*
		 * Player player = getSelf();
		 * if (var.length != 2)
		 * {
		 * show(new CustomMessage("common.Error", player), player);
		 * return;
		 * }
		 * Integer category;
		 * Integer autoContinue;
		 * try
		 * {
		 * category = Integer.valueOf(var[0]);
		 * autoContinue = Integer.valueOf(var[1]);
		 * }
		 * catch (Exception e)
		 * {
		 * show(new CustomMessage("common.Error", player), player);
		 * return;
		 * }
		 * _category = category;
		 * _autoContinue = autoContinue;
		 * if (_category == -1)
		 * {
		 * _minLevel = 75;
		 * _maxLevel = 85;
		 * }
		 * else
		 * {
		 * _minLevel = getMinLevelForCategory(_category);
		 * _maxLevel = getMaxLevelForCategory(_category);
		 * }
		 * if (_endTask != null)
		 * {
		 * show(new CustomMessage("common.TryLater", player), player);
		 * return;
		 * }
		 * _status = 0;
		 * _isRegistrationActive = true;
		 * _time_to_start = Config.EVENT_TvTTime;
		 * players_list1 = new CopyOnWriteArrayList<Long>();
		 * players_list2 = new CopyOnWriteArrayList<Long>();
		 * live_list1 = new CopyOnWriteArrayList<Long>();
		 * live_list2 = new CopyOnWriteArrayList<Long>();
		 * playerRestoreCoord = new LinkedHashMap<Long, Location>();
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)
		 * };
		 * sayToAll("scripts.events.TvT.AnnouncePreStart", param);
		 * executeTask("events.TvT.TvT", "question", new Object[0], 10000);
		 * executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
		 */
	}

	public static void sayToAll(String address, String[] replacements)
	{
		/* Announcements.getInstance().announceByCustomMessage(address, replacements, ChatType.CRITICAL_ANNOUNCE); */
	}

	public static void question()
	{
		/*
		 * for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		 * if (player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault() && !player.isInOlympiadMode()
		 * && !player.isInObserverMode())
		 * player.scriptRequest(new CustomMessage("scripts.events.TvT.AskPlayer", player).toString(), "events.TvT.TvT:addPlayer", new Object[0]);
		 */
	}

	public static void announce()
	{
		/*
		 * if(_time_to_start > 1) {
		 * _time_to_start--;
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)
		 * };
		 * sayToAll("scripts.events.TvT.AnnouncePreStart", param);
		 * executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
		 * }
		 * else
		 * {
		 * if (players_list1.isEmpty() || players_list2.isEmpty() || players_list1.size() < Config.EVENT_TvTMinPlayerInTeam || players_list2.size() < Config.EVENT_TvTMinPlayerInTeam)
		 * {
		 * sayToAll("scripts.events.TvT.AnnounceEventCancelled", null);
		 * _isRegistrationActive = false;
		 * _status = 0;
		 * boxes.clear();
		 * executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
		 * return;
		 * }
		 * else
		 * {
		 * _status = 1;
		 * _isRegistrationActive = false;
		 * sayToAll("scripts.events.TvT.AnnounceEventStarting", null);
		 * executeTask("events.TvT.TvT", "prepare", new Object[0], 5000);
		 * }
		 * }
		 */
	}

	public void addPlayer()
	{
		/*
		 * Player player = getSelf();
		 * if (player == null || !checkPlayer(player, true) || !checkDualBox(player))
		 * return;
		 * int team = 0, size1 = players_list1.size(), size2 = players_list2.size();
		 * if (size1 == Config.EVENT_TvTMaxPlayerInTeam && size2 == Config.EVENT_TvTMaxPlayerInTeam) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledCount", player), player);
		 * _isRegistrationActive = false;
		 * return;
		 * }
		 * if (!Config.EVENT_TvTAllowMultiReg)
		 * {
		 * // if ("IP".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod))
		 * // boxes.put(player.getStoredId(), player.getIP());
		 * if ("HWid".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod))
		 * boxes.put(player.getStoredId(), player.getNetConnection().getHWID());
		 * }
		 * if (size1 > size2)
		 * team = 2;
		 * else if (size1 < size2)
		 * team = 1;
		 * else
		 * team = Rnd.get(1, 2);
		 * if (team == 1)
		 * {
		 * players_list1.add(player.getStoredId());
		 * live_list1.add(player.getStoredId());
		 * show(new CustomMessage("scripts.events.TvT.Registered", player), player);
		 * }
		 * else if (team == 2)
		 * {
		 * players_list2.add(player.getStoredId());
		 * live_list2.add(player.getStoredId());
		 * show(new CustomMessage("scripts.events.TvT.Registered", player), player);
		 * }
		 * else
		 * _log.info("WTF??? Command id 0 in TvT...");
		 */
	}

	public static boolean checkPlayer(Player player, boolean first)
	{

		/*
		 * if(first && (!_isRegistrationActive || player.isDead())) {
		 * show(new CustomMessage("scripts.events.Late", player), player);
		 * return false;
		 * }
		 * if (first && (players_list1.contains(player.getStoredId()) || players_list2.contains(player.getStoredId()))) {
		 * show(new CustomMessage("scripts.events.TvT.Cancelled", player), player);
		 * if (players_list1.contains(player.getStoredId()))
		 * players_list1.remove(player.getStoredId());
		 * if (players_list2.contains(player.getStoredId()))
		 * players_list2.remove(player.getStoredId());
		 * if (live_list1.contains(player.getStoredId()))
		 * live_list1.remove(player.getStoredId());
		 * if (live_list2.contains(player.getStoredId()))
		 * live_list2.remove(player.getStoredId());
		 * if (boxes.containsKey(player.getStoredId()))
		 * boxes.remove(player.getStoredId());
		 * return false;
		 * }
		 * if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledLevel", player), player);
		 * return false;
		 * }
		 * if (player.isMounted()) {
		 * show(new CustomMessage("scripts.events.TvT.Cancelled", player), player);
		 * return false;
		 * }
		 * if (player.isCursedWeaponEquipped()) {
		 * show(new CustomMessage("scripts.events.CtF.Cancelled", player), player);
		 * return false;
		 * }
		 * if (player.isInDuel()) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledDuel", player), player);
		 * return false;
		 * }
		 * if (player.getTeam() != TeamType.NONE) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player), player);
		 * return false;
		 * }
		 * if (player.getOlympiadGame() != null || first && Olympiad.isRegistered(player)) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledOlympiad", player), player);
		 * return false;
		 * }
		 * if (player.isInParty() && player.getParty().isInDimensionalRift()) {
		 * show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player), player);
		 * return false;
		 * }
		 * if (player.isInObserverMode()) {
		 * show(new CustomMessage("scripts.event.TvT.CancelledObserver", player), player);
		 * return false;
		 * }
		 * if (player.isTeleporting())
		 * {
		 * show(new CustomMessage("scripts.events.TvT.CancelledTeleport", player), player);
		 * return false;
		 * }
		 * return true;
		 */
		return false;
	}

	public static void prepare()
	{
		/*
		 * for(DoorInstance door : reflection.getDoors())
		 * door.openMe();
		 * for (Zone z : reflection.getZones())
		 * z.setType(ZoneType.peace_zone);
		 * cleanPlayers();
		 * clearArena();
		 * executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000);
		 * executeTask("events.TvT.TvT", "teleportPlayersToColiseum", new Object[0], 3000);
		 * executeTask("events.TvT.TvT", "paralyzePlayers", new Object[0], 4000);
		 * executeTask("events.TvT.TvT", "buffPlayers", new Object[0], 5000);
		 * executeTask("events.TvT.TvT", "go", new Object[0], 60000);
		 * sayToAll("scripts.events.TvT.AnnounceFinalCountdown", null);
		 */
	}

	public static void go()
	{
		/*
		 * _status = 2;
		 * upParalyzePlayers();
		 * checkLive();
		 * clearArena();
		 * sayToAll("scripts.events.TvT.AnnounceFight", null);
		 * for (Zone z : reflection.getZones())
		 * z.setType(ZoneType.battle_zone);
		 * _endTask = executeTask("events.TvT.TvT", "endBattle", new Object[0], 360000); //test
		 * _startedTime = System.currentTimeMillis() + 360000;
		 * final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(360, bluePoints, redPoints);
		 * ExCubeGameExtendedChangePoints clientSetUp;
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * clientSetUp = new ExCubeGameExtendedChangePoints(360, bluePoints, redPoints, true, player, 0);
		 * player.sendPacket(clientSetUp);
		 * player.sendPacket(initialPoints);
		 * player.sendPacket(new ExCubeGameAddPlayer(player, true));
		 * player.broadcastCharInfo();
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * clientSetUp = new ExCubeGameExtendedChangePoints(360, bluePoints, redPoints, false, player, 0);
		 * player.sendPacket(clientSetUp);
		 * player.sendPacket(initialPoints);
		 * player.sendPacket(new ExCubeGameAddPlayer(player, false));
		 * player.broadcastCharInfo();
		 * }
		 */
	}

	public static void endBattle()
	{
		/*
		 * _status = 0;
		 * removeAura();
		 * for (Zone z : reflection.getZones())
		 * z.setType(ZoneType.peace_zone);
		 * boxes.clear();
		 * if (bluePoints > redPoints)
		 * {
		 * sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
		 * giveItemsToWinner(false, true, 1);
		 * }
		 * else if (bluePoints < redPoints)
		 * {
		 * sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
		 * giveItemsToWinner(true, false, 1);
		 * }
		 * else if (bluePoints == redPoints)
		 * {
		 * sayToAll("scripts.events.TvT.AnnounceFinishedDraw", null);
		 * giveItemsToWinner(true, true, 0.5);
		 * }
		 * sayToAll("scripts.events.TvT.AnnounceEnd", null);
		 * executeTask("events.TvT.TvT", "end", new Object[0], 30000);
		 * _isRegistrationActive = false;
		 * if (_endTask != null)
		 * {
		 * _endTask.cancel(false);
		 * _endTask = null;
		 * }
		 * boolean _isRedWinner = bluePoints < redPoints ? true : false;
		 * final ExCubeGameEnd end = new ExCubeGameEnd(_isRedWinner);
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * player.sendPacket(end);
		 * player.broadcastCharInfo();
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * player.sendPacket(end);
		 * player.broadcastCharInfo();
		 * }
		 * bluePoints = 0;
		 * redPoints = 0;
		 * _startedTime = 0;
		 * _myZone = null;
		 * territory = null;
		 */
	}

	public static void end()
	{
		/*
		 * executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000);
		 * executeTask("events.TvT.TvT", "teleportPlayers", new Object[0], 3000);
		 * executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
		 */
	}

	public void autoContinue()
	{
		/*
		 * live_list1.clear();
		 * live_list2.clear();
		 * players_list1.clear();
		 * players_list2.clear();
		 * if (_autoContinue > 0)
		 * {
		 * if (_autoContinue >= 6)
		 * {
		 * _autoContinue = 0;
		 * return;
		 * }
		 * start(new String[]{
		 * "" + (_autoContinue + 1),
		 * "" + (_autoContinue + 1)
		 * });
		 * } else
		 * scheduleEventStart();
		 */
	}

	public static void giveItemsToWinner(boolean team1, boolean team2, double rate)
	{
		/*
		 * if(team1)
		 * for (Player player : getPlayers(players_list1))
		 * for (int i = 0; i < rewards.length; i++)
		 * addItem(player, rewards[i][0], Math.round((Config.EVENT_TvTrate ? player.getLevel() : 1) * rewards[i][1] * rate));
		 * if (team2)
		 * for (Player player : getPlayers(players_list2))
		 * for (int i = 0; i < rewards.length; i++)
		 * addItem(player, rewards[i][0], Math.round((Config.EVENT_TvTrate ? player.getLevel() : 1) * rewards[i][1] * rate));
		 */
	}

	public static void teleportPlayersToColiseum()
	{
		/*
		 * switch(Rnd.get(1,11))
		 * {
		 * case 1:
		 * _myZone = _zone;
		 * break;
		 * case 2:
		 * _myZone = _zone1;
		 * break;
		 * case 3:
		 * _myZone = _zone2;
		 * break;
		 * case 4:
		 * _myZone = _zone3;
		 * break;
		 * case 5:
		 * _myZone = _zone4;
		 * break;
		 * case 6:
		 * _myZone = _zone5;
		 * break;
		 * case 7:
		 * _myZone = _zone6;
		 * break;
		 * case 8:
		 * _myZone = _zone7;
		 * break;
		 * case 9:
		 * _myZone = _zone8;
		 * break;
		 * case 10:
		 * _myZone = _zone9;
		 * break;
		 * case 11:
		 * _myZone = _zone10;
		 * break;
		 * default:
		 * _myZone = _zone;
		 * }
		 * territory = _myZone.getTerritory();
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * unRide(player);
		 * if (!Config.EVENT_TvTAllowSummons)
		 * unSummonPet(player, true);
		 * DuelEvent duel = player.getEvent(DuelEvent.class);
		 * if (duel != null)
		 * duel.abortDuel(player);
		 * playerRestoreCoord.put(player.getStoredId(), new Location(player.getX(), player.getY(), player.getZ()));
		 * player.teleToLocation(Territory.getRandomLoc(territory), reflection);
		 * player.setIsInTvT(true);
		 * if (!Config.EVENT_TvTAllowBuffs)
		 * {
		 * player.getEffectList().stopAllEffects();
		 * if (player.getPet() != null)
		 * player.getPet().getEffectList().stopAllEffects();
		 * }
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * unRide(player);
		 * if (!Config.EVENT_TvTAllowSummons)
		 * unSummonPet(player, true);
		 * playerRestoreCoord.put(player.getStoredId(), new Location(player.getX(), player.getY(), player.getZ()));
		 * player.teleToLocation(Territory.getRandomLoc(territory), reflection);
		 * player.setIsInTvT(true);
		 * if (!Config.EVENT_TvTAllowBuffs) {
		 * player.getEffectList().stopAllEffects();
		 * if (player.getPet() != null)
		 * player.getPet().getEffectList().stopAllEffects();
		 * }
		 * }
		 */
	}

	public static void teleportPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1)) {
		 * if (player == null || !playerRestoreCoord.containsKey(player.getStoredId()))
		 * continue;
		 * player.teleToLocation(playerRestoreCoord.get(player.getStoredId()), ReflectionManager.DEFAULT);
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * if (player == null || !playerRestoreCoord.containsKey(player.getStoredId()))
		 * continue;
		 * player.teleToLocation(playerRestoreCoord.get(player.getStoredId()), ReflectionManager.DEFAULT);
		 * }
		 * playerRestoreCoord.clear();
		 */
	}

	public static void paralyzePlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1)) {
		 * if (player == null)
		 * continue;
		 * if (!player.isRooted()) {
		 * player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		 * player.startRooted();
		 * player.startAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * if (player.getPet() != null && !player.getPet().isRooted()) {
		 * player.getPet().startRooted();
		 * player.getPet().startAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * if (!player.isRooted()) {
		 * player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		 * player.startRooted();
		 * player.startAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * if (player.getPet() != null && !player.getPet().isRooted()) {
		 * player.getPet().startRooted();
		 * player.getPet().startAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * }
		 */
	}

	public static void upParalyzePlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1)) {
		 * if (player.isRooted()) {
		 * player.stopRooted();
		 * player.stopAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * if (player.getPet() != null && player.getPet().isRooted()) {
		 * player.getPet().stopRooted();
		 * player.getPet().stopAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * if (player.isRooted()) {
		 * player.stopRooted();
		 * player.stopAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * if (player.getPet() != null && player.getPet().isRooted()) {
		 * player.getPet().stopRooted();
		 * player.getPet().stopAbnormalEffect(AbnormalEffect.ROOT);
		 * }
		 * }
		 */
	}

	public static void ressurectPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1))
		 * if (player.isDead())
		 * {
		 * player.restoreExp();
		 * player.setCurrentCp(player.getMaxCp());
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.broadcastPacket(new Revive(player));
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * if (player.isDead())
		 * {
		 * player.restoreExp();
		 * player.setCurrentCp(player.getMaxCp());
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.broadcastPacket(new Revive(player));
		 * }
		 */
	}

	public static void healPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1))
		 * {
		 * player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 * }
		 */
	}

	public static void cleanPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1))
		 * if (!checkPlayer(player, false))
		 * removePlayer(player);
		 * for (Player player : getPlayers(players_list2))
		 * if (!checkPlayer(player, false))
		 * removePlayer(player);
		 */
	}

	public static void checkLive()
	{
		/*
		 * List<Long> new_live_list1 = new CopyOnWriteArrayList<Long>();
		 * List<Long> new_live_list2 = new CopyOnWriteArrayList<Long>();
		 * for (Long storeId : live_list1)
		 * {
		 * Player player = GameObjectsStorage.getAsPlayer(storeId);
		 * if (player != null)
		 * new_live_list1.add(storeId);
		 * }
		 * for (Long storeId : live_list2)
		 * {
		 * Player player = GameObjectsStorage.getAsPlayer(storeId);
		 * if (player != null)
		 * new_live_list2.add(storeId);
		 * }
		 * live_list1 = new_live_list1;
		 * live_list2 = new_live_list2;
		 * for (Player player : getPlayers(live_list1))
		 * if (!player.isDead() && !player.isLogoutStarted())
		 * player.setTeam(TeamType.RED);
		 * else
		 * loosePlayer(player);
		 * for (Player player : getPlayers(live_list2))
		 * if (!player.isDead() && !player.isLogoutStarted())
		 * player.setTeam(TeamType.BLUE);
		 * else
		 * loosePlayer(player);
		 * if (live_list1.size() < 1 || live_list2.size() < 1)
		 * endBattle();
		 */
	}

	public static void removeAura()
	{
		/*
		 * for(Player player : getPlayers(live_list1)) {
		 * player.setTeam(TeamType.NONE);
		 * if (player.getPet() != null)
		 * player.getPet().setTeam(TeamType.NONE);
		 * player.setIsInTvT(false);
		 * }
		 * for (Player player : getPlayers(live_list2)) {
		 * player.setTeam(TeamType.NONE);
		 * if (player.getPet() != null)
		 * player.getPet().setTeam(TeamType.NONE);
		 * player.setIsInTvT(false);
		 * }
		 */
	}

	public static void clearArena()
	{
		/*
		 * if(_myZone == null)
		 * return;
		 * for (GameObject obj : _myZone.getObjects())
		 * if (obj != null)
		 * {
		 * Player player = obj.getPlayer();
		 * if (player != null && !live_list1.contains(player.getStoredId()) && !live_list2.contains(player.getStoredId()))
		 * player.teleToLocation(147451, 46728, -3410, ReflectionManager.DEFAULT);
		 * }
		 */
	}

	@Override
	public void onDeath(Creature self, Creature killer)
	{
		/*
		 * if(_status > 1 && self.isPlayer() && self.getTeam() != TeamType.NONE && (live_list1.contains(self.getStoredId()) || live_list2.contains(self.getStoredId())))
		 * {
		 * //loosePlayer((Player) self);
		 * //checkLive();
		 * increasePoints(killer);
		 * resurrectAtBase(self);
		 * checkKillsAndAnnounce(killer.getPlayer());
		 * _pScore.remove(self.getPlayer().getObjectId());
		 * //self.getPlayer().setIsInTvT(false);
		 * }
		 */

	}

	public static void resurrectAtBase(Creature self)
	{
		/*
		 * Player player = self.getPlayer();
		 * if (player == null)
		 * return;
		 * if (player.getTeam() == TeamType.NONE)
		 * return;
		 * player.broadcastPacket(new Revive(player));
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 * player.teleToLocation(Territory.getRandomLoc(territory), reflection);
		 * buffPlayer(player);
		 */
	}

	public static void buffPlayer(Player player)
	{
		/*
		 * if(player.isMageClass())
		 * mageBuff(player);
		 * else
		 * fighterBuff(player);
		 */
	}

	@Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		/*
		 * if(_myZone == null)
		 * return;
		 * if (_myZone.checkIfInZone(x, y, z, reflection))
		 * return;
		 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (live_list1.contains(player.getStoredId()) || live_list2.contains(player.getStoredId())))
		 * {
		 * removePlayer(player);
		 * checkLive
		 */
	}

	@Override
	public void onPlayerExit(Player player)
	{
		/*
		 * if(player.getTeam() == TeamType.NONE)
		 * return;
		 * if (_status == 0 && _isRegistrationActive && player.getTeam() != TeamType.NONE && (live_list1.contains(player.getStoredId()) || live_list2.contains(player.getStoredId()))) {
		 * removePlayer(player);
		 * return;
		 * }
		 * if (_status == 1 && (live_list1.contains(player.getStoredId()) || live_list2.contains(player.getStoredId()))) {
		 * player.teleToLocation(playerRestoreCoord.get(player.getStoredId()), ReflectionManager.DEFAULT);
		 * removePlayer(player);
		 * return;
		 * }
		 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE
		 * && (live_list1.contains(player.getStoredId()) || live_list2.contains(player.getStoredId()))) {
		 * removePlayer(player);
		 * checkLive();
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			/*
			 * if(cha == null)
			 * return;
			 * Player player = cha.getPlayer();
			 * if (_status > 0 && player != null && !live_list1.contains(player.getStoredId()) && !live_list2.contains(player.getStoredId()))
			 * player.teleToLocation(147451, 46728, -3410, ReflectionManager.DEFAULT);
			 */
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			/*
			 * if(cha == null)
			 * return;
			 * Player player = cha.getPlayer();
			 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (live_list1.contains(player.getStoredId()) || live_list2.contains(player.getStoredId())))
			 * {
			 * double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // СѓРіРѕР» РІ РіСЂР°РґСѓСЃР°С…
			 * double radian = Math.toRadians(angle - 90); // СѓРіРѕР» РІ СЂР°РґРёР°РЅР°С…
			 * int x = (int) (cha.getX() + 250 * Math.sin(radian));
			 * int y = (int) (cha.getY() - 250 * Math.cos(radian));
			 * int z = cha.getZ();
			 * player.teleToLocation(x, y, z, reflection);
			 * }
			 */
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	public static void buffPlayers()
	{

		/*
		 * for(Player player : getPlayers(players_list1)) {
		 * if (player.isMageClass())
		 * mageBuff(player);
		 * else
		 * fighterBuff(player);
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * if (player.isMageClass())
		 * mageBuff(player);
		 * else
		 * fighterBuff(player);
		 * }
		 */
	}

	public void scheduleEventStart()
	{
		/*
		 * try {
		 * Calendar currentTime = Calendar.getInstance();
		 * Calendar nextStartTime = null;
		 * Calendar testStartTime = null;
		 * for (String timeOfDay : Config.EVENT_TvTStartTime) {
		 * testStartTime = Calendar.getInstance();
		 * testStartTime.setLenient(true);
		 * String[] splitTimeOfDay = timeOfDay.split(":");
		 * testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
		 * testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
		 * if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
		 * testStartTime.add(Calendar.DAY_OF_MONTH, 1);
		 * if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
		 * nextStartTime = testStartTime;
		 * if (_startTask != null) {
		 * _startTask.cancel(false);
		 * _startTask = null;
		 * }
		 * _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), nextStartTime.getTimeInMillis() - System.currentTimeMillis());
		 * }
		 * currentTime = null;
		 * nextStartTime = null;
		 * testStartTime = null;
		 * } catch (Exception e) {
		 * _log.warn("TvT: Error figuring out a start time. Check TvTEventInterval in config file.");
		 * }
		 */
	}

	public static void mageBuff(Player player)
	{
		/*
		 * for(int i = 0; i < mage_buffs.length; i++)
		 * {
		 * buff = SkillTable.getInstance().getInfo(mage_buffs[i][0], mage_buffs[i][1]);
		 * if (buff == null)
		 * continue;
		 * buff.getEffects(player, player, false, false);
		 * }
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 */
	}

	public static void fighterBuff(Player player)
	{
		/*
		 * for(int i = 0; i < fighter_buffs.length; i++)
		 * {
		 * buff = SkillTable.getInstance().getInfo(fighter_buffs[i][0], fighter_buffs[i][1]);
		 * if (buff == null)
		 * continue;
		 * buff.getEffects(player, player, false, false);
		 * }
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 */
	}

	public class StartTask extends RunnableImpl
	{

		@Override
		public void runImpl()
		{
			/*
			 * if(!_active)
			 * return;
			 * if (isPvPEventStarted()) {
			 * _log.info("TvT not started: another event is already running");
			 * return;
			 * }
			 * for (Residence c : ResidenceHolder.getInstance().getResidenceList(Castle.class))
			 * if (c.getSiegeEvent() != null && c.getSiegeEvent().isInProgress()) {
			 * _log.debug("TvT not started: CastleSiege in progress");
			 * return;
			 * }
			 * if (Config.EVENT_TvTCategories)
			 * start(new String[]{ "1", "1"});
			 * else
			 * start(new String[] {"-1", "-1"});
			 */
		}
	}
}
package events.KoreanStyle;

import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class KoreanStyle extends Functions /* implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener, OnCurrentHpDamageListener */
{

	/*
	 * private static final Logger _log = LoggerFactory.getLogger(KoreanStyle.class);
	 * private static List<String> _alreadyUsed = new ArrayList<String>();
	 * private static ScheduledFuture<?> _startTask;
	 * private static List<Integer> players_list1 = new CopyOnWriteArrayList<Integer>();
	 * private static List<Integer> players_list2 = new CopyOnWriteArrayList<Integer>();
	 * private static LinkedBlockingDeque<Integer> live_list1 = new LinkedBlockingDeque<Integer>();
	 * private static LinkedBlockingDeque<Integer> live_list2 = new LinkedBlockingDeque<Integer>();
	 * private static Player _redFighter = null;
	 * private static Player _blueFighter = null;
	 * private static int[][] mage_buffs = new int[Config.EVENT_KOREAN_MAGE_BUFFS.length][2];
	 * private static int[][] fighter_buffs = new int[Config.EVENT_KOREAN_FIGHTER_BUFFS.length][2];
	 * private static int[][] rewards = new int[Config.EVENT_KOREAN_WINNER_REWARDS.length][2];
	 * private static Map<Integer, Location> playerRestoreCoord = new LinkedHashMap<Integer, Location>();
	 * private static Map<Integer, Location> playerSpawnCoord = new FastMap<Integer, Location>();
	 * private static Map<Integer, String> boxes = new LinkedHashMap<Integer, String>();
	 * private static boolean _isRegistrationActive = false;
	 * private static int _status = 0;
	 * private static int _time_to_start;
	 * private static int _category;
	 * private static int _minLevel;
	 * private static int _maxLevel;
	 * private static boolean _active = false;
	 * private static Skill buff;
	 * private static Reflection _reflection;
	 * private static ScheduledFuture<?> _endTask;
	 * private static Reflection _myZone = null;
	 * private static Map<Integer, Integer> _pScore = new HashMap<Integer, Integer>();
	 */

	/*
	 * @Override
	 * public void onLoad()
	 * {
	 * CharListenerList.addGlobal(this);
	 * int i = 0;
	 * if (Config.EVENT_KOREAN_BUFF_PLAYERS && Config.EVENT_KOREAN_MAGE_BUFFS.length != 0)
	 * for (String skill : Config.EVENT_KOREAN_MAGE_BUFFS) {
	 * String[] splitSkill = skill.split(",");
	 * mage_buffs[i][0] = Integer.parseInt(splitSkill[0]);
	 * mage_buffs[i][1] = Integer.parseInt(splitSkill[1]);
	 * i++;
	 * }
	 * i = 0;
	 * if (Config.EVENT_KOREAN_BUFF_PLAYERS && Config.EVENT_KOREAN_FIGHTER_BUFFS.length != 0)
	 * for (String skill : Config.EVENT_KOREAN_FIGHTER_BUFFS) {
	 * String[] splitSkill = skill.split(",");
	 * fighter_buffs[i][0] = Integer.parseInt(splitSkill[0]);
	 * fighter_buffs[i][1] = Integer.parseInt(splitSkill[1]);
	 * i++;
	 * }
	 * i = 0;
	 * if (Config.EVENT_KOREAN_WINNER_REWARDS.length != 0)
	 * for (String reward : Config.EVENT_KOREAN_WINNER_REWARDS) {
	 * String[] splitReward = reward.split(",");
	 * rewards[i][0] = Integer.parseInt(splitReward[0]);
	 * rewards[i][1] = Integer.parseInt(splitReward[1]);
	 * i++;
	 * }
	 * _log.info("Loaded Event: Korean Style");
	 */
	/*
	 * }
	 * @Override
	 * public void onReload() {
	 * if (_startTask != null) {
	 * _startTask.cancel(false);
	 * _startTask = null;
	 * }
	 * }
	 * @Override
	 * public void onShutdown()
	 * {
	 * //onReload();
	 * }
	 */

	@SuppressWarnings("unused")
	private static boolean isActive()
	{
		return false;
	}

	public void activateEvent()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (!isActive())
		 * {
		 * _active = true;
		 * if (_startTask == null)
		 * ThreadPoolManager.getInstance().execute(new StartTask());
		 * _log.info("Event 'Korean Style' activated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.KoreanStyle.AnnounceEventStarted", null);
		 * }
		 * else
		 * {
		 * player.sendMessage("Event 'Korean Style' already active.");
		 * }
		 * show("admin/events/events.htm", player);
		 */
	}

	public void deactivateEvent()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (isActive())
		 * {
		 * if (_startTask != null) {
		 * _startTask.cancel(false);
		 * _startTask = null;
		 * }
		 * _log.info("Event 'Korean Style' deactivated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.KoreanStyle.AnnounceEventStoped", null);
		 * }
		 * else
		 * {
		 * player.sendMessage("Event 'Korean Style' not active.");
		 * }
		 * _isRegistrationActive = false;
		 * _active = false;
		 * show("admin/events/events.htm", player);
		 */
	}

	public static boolean isRunned()
	{
		return false;// _isRegistrationActive || _status > 0;
	}

	public void start()
	{
		/*
		 * if(!_active)
		 * return;
		 * Player player = getSelf();
		 * if (_category == -1)
		 * {
		 * _minLevel = 75;
		 * _maxLevel = 85;
		 * }
		 * else
		 * {
		 * _minLevel = Config.EVENT_KOREAN_MIN_LEVEL;
		 * _maxLevel = Config.EVENT_KOREAN_MAX_LEVEL;
		 * }
		 * if (_endTask != null)
		 * {
		 * show(new CustomMessage("common.TryLater", player), player);
		 * return;
		 * }
		 * _status = 0;
		 * _isRegistrationActive = true;
		 * _time_to_start = Config.EVENT_KOREAN_TIME_TO_TP;
		 * players_list1 = new CopyOnWriteArrayList<Integer>();
		 * players_list2 = new CopyOnWriteArrayList<Integer>();
		 * live_list1 = new LinkedBlockingDeque<Integer>();
		 * live_list2 = new LinkedBlockingDeque<Integer>();
		 * playerRestoreCoord = new LinkedHashMap<Integer, Location>();
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)
		 * };
		 * sayToAll("scripts.events.KoreanStyle.AnnouncePreStart", param);
		 * executeTask("events.KoreanStyle.KoreanStyle", "question", new Object[0], 10000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "announce", new Object[0], 60000);
		 */
	}

	public static void sayToAll(String address, String[] replacements)
	{
		// Announcements.getInstance().announceByCustomMessage(address, replacements, ChatType.CRITICAL_ANNOUNCE);
	}

	public static void question()
	{
		/*
		 * for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		 * if (player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault()
		 * && !player.isInOlympiadMode() && !player.isInObserverMode() && !_alreadyUsed.contains(player.getHWID()))
		 * player.scriptRequest(new CustomMessage("scripts.events.KoreanStyle.AskPlayer", player).toString(), "events.KoreanStyle.KoreanStyle:addPlayer", new Object[0]);
		 */
	}

	public static void announce()
	{
		/*
		 * if(!_active)
		 * return;
		 * if (_time_to_start > 1) {
		 * _time_to_start--;
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)
		 * };
		 * sayToAll("scripts.events.KoreanStyle.AnnouncePreStart", param);
		 * executeTask("events.KoreanStyle.KoreanStyle", "announce", new Object[0], 60000);
		 * }
		 * else
		 * {
		 * if (players_list1.isEmpty() || players_list2.isEmpty())
		 * {
		 * sayToAll("scripts.events.KoreanStyle.AnnounceEventCancelled", null);
		 * _isRegistrationActive = false;
		 * _status = 0;
		 * boxes.clear();
		 * executeTask("events.KoreanStyle.KoreanStyle", "autoContinue", new Object[0], 10000);
		 * return;
		 * }
		 * else
		 * {
		 * _status = 1;
		 * _isRegistrationActive = false;
		 * sayToAll("scripts.events.KoreanStyle.AnnounceEventStarting", null);
		 * executeTask("events.KoreanStyle.KoreanStyle", "prepare", new Object[0], 5000);
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
		 * if (size1 == Config.EVENT_KOREAN_PLAYERS_IN_TEAM && size2 == Config.EVENT_KOREAN_PLAYERS_IN_TEAM) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledCount", player), player);
		 * _isRegistrationActive = false;
		 * return;
		 * }
		 * if ("IP".equalsIgnoreCase(Config.EVENT_KOREAN_CHECK_WINDOW_METHOD))
		 * boxes.put(player.getObjectId(), player.getIP());
		 * if ("HWid".equalsIgnoreCase(Config.EVENT_KOREAN_CHECK_WINDOW_METHOD))
		 * boxes.put(player.getObjectId(), player.getNetConnection().getHWID());
		 * if (size1 > size2)
		 * team = 2;
		 * else if (size1 < size2)
		 * team = 1;
		 * else
		 * team = Rnd.get(1, 2);
		 * if (team == 1)
		 * {
		 * players_list1.add(player.getObjectId());
		 * live_list1.add(player.getObjectId());
		 * show(new CustomMessage("scripts.events.KoreanStyle.Registered", player), player);
		 * }
		 * else if (team == 2)
		 * {
		 * players_list2.add(player.getObjectId());
		 * live_list2.add(player.getObjectId());
		 * show(new CustomMessage("scripts.events.KoreanStyle.Registered", player), player);
		 * }
		 * else
		 * _log.info("WTF??? Command id 0 in KoreanStyle...");
		 */
	}

	public static boolean checkPlayer(Player player, boolean first)
	{
		/*
		 * if(first && (!_isRegistrationActive || player.isDead())) {
		 * show(new CustomMessage("scripts.events.Late", player), player);
		 * return false;
		 * }
		 * if (first && (players_list1.contains(player.getObjectId()) || players_list2.contains(player.getObjectId()))) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.Cancelled", player), player);
		 * if (players_list1.contains(player.getObjectId()))
		 * players_list1.remove((Integer)player.getObjectId());
		 * if (players_list2.contains(player.getObjectId()))
		 * players_list2.remove((Integer)player.getObjectId());
		 * if (live_list1.contains(player.getObjectId()))
		 * live_list1.remove((Integer)player.getObjectId());
		 * if (live_list2.contains(player.getObjectId()))
		 * live_list2.remove((Integer)player.getObjectId());
		 * if (boxes.containsKey(player.getObjectId()))
		 * boxes.remove((Integer)player.getObjectId());
		 * return false;
		 * }
		 * if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledLevel", player), player);
		 * return false;
		 * }
		 * if (player.isMounted()) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.Cancelled", player), player);
		 * return false;
		 * }
		 * if (player.isCursedWeaponEquipped()) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.Cancelled", player), player);
		 * return false;
		 * }
		 * if (player.isInDuel()) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledDuel", player), player);
		 * return false;
		 * }
		 * if (player.getTeam() != TeamType.NONE) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledOtherEvent", player), player);
		 * return false;
		 * }
		 * if (player.getOlympiadGame() != null || first && Olympiad.isRegistered(player)) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledOlympiad", player), player);
		 * return false;
		 * }
		 * if (player.isInParty() && player.getParty().isInDimensionalRift()) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledOtherEvent", player), player);
		 * return false;
		 * }
		 * if (player.isInObserverMode()) {
		 * show(new CustomMessage("scripts.event.KoreanStyle.CancelledObserver", player), player);
		 * return false;
		 * }
		 * if (player.isTeleporting())
		 * {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledTeleport", player), player);
		 * return false;
		 * }
		 * return true;
		 */
		return false;
	}

	public static void prepare()
	{
		/*
		 * cleanPlayers();
		 * clearArena();
		 * executeTask("events.KoreanStyle.KoreanStyle", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "healPlayers", new Object[0], 2000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "teleportPlayersToColiseum", new Object[0], 3000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "paralyzePlayers", new Object[0], 4000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "buffPlayers", new Object[0], 5000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "go", new Object[0], 20000);
		 * sayToAll("scripts.events.KoreanStyle.AnnounceFinalCountdown", null);
		 */
	}

	public static void go()
	{
		/*
		 * _status = 2;
		 * getNewFighters();
		 * clearArena();
		 * sayToAll("scripts.events.KoreanStyle.AnnounceFight", null);
		 * for (Zone z : _reflection.getZones())
		 * z.setType(ZoneType.battle_zone);
		 * ThreadPoolManager.getInstance().schedule(new CheckActiveTask(), 5100);
		 */
	}

	public static void endBattle(boolean blueIsWinner)
	{
		/*
		 * if(_status == 0)
		 * return;
		 * _status = 0;
		 * removeAura();
		 * for (Zone z : _reflection.getZones())
		 * z.setType(ZoneType.peace_zone);
		 * boxes.clear();
		 * if (!blueIsWinner)
		 * {
		 * sayToAll("scripts.events.KoreanStyle.AnnounceFinishedBlueWins", null);
		 * giveItemsToWinner(false, true, 1);
		 * }
		 * else if (blueIsWinner)
		 * {
		 * sayToAll("scripts.events.KoreanStyle.AnnounceFinishedRedWins", null);
		 * giveItemsToWinner(true, false, 1);
		 * }
		 * sayToAll("scripts.events.KoreanStyle.AnnounceEnd", null);
		 * executeTask("events.KoreanStyle.KoreanStyle", "end", new Object[0], 30000);
		 * _isRegistrationActive = false;
		 * if (_endTask != null)
		 * {
		 * _endTask.cancel(false);
		 * _endTask = null;
		 * }
		 * final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(360, (!blueIsWinner ? Config.EVENT_KOREAN_PLAYERS_IN_TEAM : 0), (!blueIsWinner ? 0 :
		 * Config.EVENT_KOREAN_PLAYERS_IN_TEAM));
		 * //ExCubeGameExtendedChangePoints clientSetUp;
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * //clientSetUp = new ExCubeGameExtendedChangePoints(360, (blueIsWinner ? 1 : 0), redPoints, true, player, 0);
		 * //player.sendPacket(clientSetUp);
		 * player.sendPacket(initialPoints);
		 * player.sendPacket(new ExCubeGameAddPlayer(player, true));
		 * player.broadcastCharInfo();
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * //player.sendPacket(clientSetUp);
		 * player.sendPacket(initialPoints);
		 * player.sendPacket(new ExCubeGameAddPlayer(player, false));
		 * player.broadcastCharInfo();
		 * }
		 * final ExCubeGameEnd end = new ExCubeGameEnd(blueIsWinner);
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * player.sendPacket(end);
		 * player.broadcastCharInfo();
		 * if (player.isParalyzed())
		 * player.stopParalyzed();
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * player.sendPacket(end);
		 * player.broadcastCharInfo();
		 * if (player.isParalyzed())
		 * player.stopParalyzed();
		 * }
		 * _myZone = null;
		 * _active = false;
		 * playerSpawnCoord.clear();
		 */
	}

	public static void end()
	{
		/*
		 * executeTask("events.KoreanStyle.KoreanStyle", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "healPlayers", new Object[0], 2000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "teleportPlayers", new Object[0], 3000);
		 * executeTask("events.KoreanStyle.KoreanStyle", "autoContinue", new Object[0], 10000);
		 */
	}

	public static void autoContinue()
	{
		/*
		 * live_list1.clear();
		 * live_list2.clear();
		 * players_list1.clear();
		 * players_list2.clear();
		 */
	}

	public static void giveItemsToWinner(boolean team1, boolean team2, double rate)
	{
		/*
		 * if(team1)
		 * for (Player player : getPlayers(players_list1))
		 * for (int i = 0; i < rewards.length; i++)
		 * addItem(player, rewards[i][0], Math.round(rewards[i][1] * rate), "koreanStyle");
		 * if (team2)
		 * for (Player player : getPlayers(players_list2))
		 * for (int i = 0; i < rewards.length; i++)
		 * addItem(player, rewards[i][0], Math.round(rewards[i][1] * rate), "koreanStyle");
		 */
	}

	public static void teleportPlayersToColiseum()
	{
		/*
		 * String instanceId = Rnd.get(Config.EVENT_KOREAN_REFLECTIONS);
		 * StatsSet params;
		 * Location leftCorner = null;
		 * Location rightCorner = null;
		 * int index = 0;
		 * for (Player player : getPlayers(players_list1))
		 * {
		 * if (index == 0)
		 * {
		 * _reflection = ReflectionUtils.enterReflection(player, Integer.parseInt(instanceId));
		 * for (Zone z : _reflection.getZones())
		 * z.setType(ZoneType.peace_zone);
		 * params = _reflection.getInstancedZone().getAddParams();
		 * leftCorner = getPoint(params.getString("Right1"));
		 * rightCorner = getPoint(params.getString("Right2"));
		 * }
		 * else
		 * ReflectionUtils.enterReflection(player, _reflection, Integer.parseInt(instanceId));
		 * unRide(player);
		 * DuelEvent duel = player.getEvent(DuelEvent.class);
		 * if (duel != null)
		 * duel.abortDuel(player);
		 * playerRestoreCoord.put(player.getObjectId(), new Location(player.getX(), player.getY(), player.getZ()));
		 * Location loc = new Location(leftCorner.x, leftCorner.y, leftCorner.z, leftCorner.h);
		 * loc.x += ((double)(rightCorner.x-leftCorner.x)/Config.EVENT_KOREAN_PLAYERS_IN_TEAM)*index;
		 * loc.y += ((double)(rightCorner.y-leftCorner.y)/Config.EVENT_KOREAN_PLAYERS_IN_TEAM)*index;
		 * player.teleToLocation(loc, _reflection);
		 * player.setHeading(loc.h);
		 * player.broadcastCharInfo();
		 * playerSpawnCoord.put(player.getObjectId(), loc);
		 * player.setIsInTvT(true);
		 * player.setTeam(TeamType.RED);
		 * _alreadyUsed.add(player.getHWID());
		 * if (!Config.EVENT_KOREAN_ALLOW_BUFFS)
		 * {
		 * player.getEffectList().stopAllEffects();
		 * if (player.getPet() != null)
		 * player.getPet().getEffectList().stopAllEffects();
		 * }
		 * if (player.getPet() != null)
		 * player.getPet().deleteMe();
		 * index++;
		 * }
		 * boolean justFinishedFirst = true;
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * if (justFinishedFirst)
		 * {
		 * params = _reflection.getInstancedZone().getAddParams();
		 * leftCorner = getPoint(params.getString("Left1"));
		 * rightCorner = getPoint(params.getString("Left2"));
		 * index = 0;
		 * justFinishedFirst = false;
		 * }
		 * else
		 * ReflectionUtils.enterReflection(player, _reflection, Integer.parseInt(instanceId));
		 * unRide(player);
		 * DuelEvent duel = player.getEvent(DuelEvent.class);
		 * if (duel != null)
		 * duel.abortDuel(player);
		 * playerRestoreCoord.put(player.getObjectId(), new Location(player.getX(), player.getY(), player.getZ()));
		 * Location loc = new Location(leftCorner.x, leftCorner.y, leftCorner.z, leftCorner.h);
		 * loc.x += ((double)(rightCorner.x-leftCorner.x)/Config.EVENT_KOREAN_PLAYERS_IN_TEAM)*index;
		 * loc.y += ((double)(rightCorner.y-leftCorner.y)/Config.EVENT_KOREAN_PLAYERS_IN_TEAM)*index;
		 * player.teleToLocation(loc, _reflection);
		 * player.setHeading(loc.h);
		 * player.broadcastCharInfo();
		 * playerSpawnCoord.put(player.getObjectId(), loc);
		 * player.setIsInTvT(true);
		 * player.setTeam(TeamType.BLUE);
		 * _alreadyUsed.add(player.getHWID());
		 * if (!Config.EVENT_KOREAN_ALLOW_BUFFS)
		 * {
		 * player.getEffectList().stopAllEffects();
		 * if (player.getPet() != null)
		 * player.getPet().getEffectList().stopAllEffects();
		 * }
		 * if (player.getPet() != null)
		 * player.getPet().deleteMe();
		 * index++;
		 * }
		 */
	}

	public static void teleportPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1)) {
		 * if (player == null || !playerRestoreCoord.containsKey(player.getObjectId()))
		 * continue;
		 * player.teleToLocation(playerRestoreCoord.get(player.getObjectId()), ReflectionManager.DEFAULT);
		 * }
		 * for (Player player : getPlayers(players_list2)) {
		 * if (player == null || !playerRestoreCoord.containsKey(player.getObjectId()))
		 * continue;
		 * player.teleToLocation(playerRestoreCoord.get(player.getObjectId()), ReflectionManager.DEFAULT);
		 * }
		 * playerRestoreCoord.clear();
		 * _reflection.collapse();
		 */
	}

	public static void paralyzePlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list1))
		 * {
		 * if (player == null)
		 * continue;
		 * sitDown(player);
		 * player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		 * player.startParalyzed();
		 * if (player.getPet() != null)
		 * {
		 * player.getPet().doDecay();
		 * }
		 * }
		 * for (Player player : getPlayers(players_list2))
		 * {
		 * if (player == null)
		 * continue;
		 * sitDown(player);
		 * player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		 * player.startParalyzed();
		 * if (player.getPet() != null)
		 * {
		 * player.getPet().doDecay();
		 * }
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static void sitDown(Player player)
	{
		/*
		 * player.broadcastPacket(new ChangeWaitType(player, ChangeWaitType.WT_SITTING));
		 * player.setSitting(true);
		 * player.sittingTaskLaunched = true;
		 * ThreadPoolManager.getInstance().schedule(new EndSitDownTask(player), 2500);
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

	public static void getNewFighters()
	{
		/*
		 * startFightTime = System.currentTimeMillis();
		 * List<Integer> toDelete = new ArrayList<Integer>();
		 * Player redFighter = null;
		 * Player blueFighter = null;
		 * for (Integer objId : live_list1)
		 * {
		 * Player player = GameObjectsStorage.getPlayer(objId);
		 * if (player == null || player.isDead() || player.getReflection() != _reflection || player.getTeam() == TeamType.NONE || !players_list1.contains(objId))
		 * toDelete.add(objId);
		 * else if (!player.isSitting())
		 * redFighter = player;
		 * }
		 * for (Integer objId : toDelete)
		 * live_list1.remove(objId);
		 * toDelete.clear();
		 * for (Integer objId : live_list2)
		 * {
		 * Player player = GameObjectsStorage.getPlayer(objId);
		 * if (player == null || player.isDead() || player.getReflection() != _reflection || player.getTeam() == TeamType.NONE || !players_list2.contains(objId))
		 * toDelete.add(objId);
		 * else if (!player.isSitting())
		 * blueFighter = player;
		 * }
		 * for (Integer objId : toDelete)
		 * live_list2.remove(objId);
		 * if (live_list1.size() < 1)
		 * endBattle(false);
		 * else if (live_list2.size() < 1)
		 * endBattle(true);
		 * else
		 * {
		 * _redFighter = null;
		 * _blueFighter = null;
		 * if (redFighter == null)
		 * {
		 * _redFighter = prepareFighter(live_list1.getFirst());
		 * }
		 * else
		 * {
		 * _redFighter = redFighter;
		 * prepareFighter(redFighter.getObjectId());
		 * }
		 * if (blueFighter == null)
		 * {
		 * _blueFighter = prepareFighter(live_list2.getFirst());
		 * }
		 * else
		 * {
		 * _blueFighter = blueFighter;
		 * prepareFighter(blueFighter.getObjectId());
		 * }
		 * _redFighter.sendPacket(new Say2(_redFighter.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Korean Style", "Fight against "+_blueFighter.getName()+"!"));
		 * _blueFighter.sendPacket(new Say2(_blueFighter.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Korean Style", "Fight against "+_redFighter.getName()+"!"));
		 * for (Player player : getPlayers(players_list1))
		 * if (!player.equals(_redFighter))
		 * player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Korean Style", _redFighter.getName()+" VS "+_blueFighter.getName()+"!"));
		 * for (Player player : getPlayers(players_list2))
		 * if (!player.equals(_blueFighter))
		 * player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Korean Style", _redFighter.getName()+" VS "+_blueFighter.getName()+"!"));
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static Player prepareFighter(int objId)
	{
		/*
		 * layer player = GameObjectsStorage.getPlayer(objId);
		 * if (player == null)
		 * return null;
		 * player.getEffectList().stopAllEffects();
		 * if (player.isMageClass())
		 * mageBuff(player);
		 * else
		 * fighterBuff(player);
		 * player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());
		 * player.resetReuse();
		 * player.sendPacket(new SkillCoolTime(player));
		 * if (player.isParalyzed())
		 * player.stopParalyzed();
		 * player.stopAbnormalEffect(AbnormalEffect.HOLD_1);
		 * player.standUp();
		 * StatsSet params = _reflection.getInstancedZone().getAddParams();
		 * if (player.getTeam() == TeamType.BLUE)
		 * player.teleToLocation(getPoint(params.getString("LeftCenter")));
		 * if (player.getTeam() == TeamType.RED)
		 * player.teleToLocation(getPoint(params.getString("RightCenter")));
		 * return player;
		 */
		return null;
	}

	public static void removeAura()
	{
		/*
		 * for(Integer objId : players_list1)
		 * {
		 * Player player = GameObjectsStorage.getPlayer(objId);
		 * player.setTeam(TeamType.NONE);
		 * if (player.getPet() != null)
		 * player.getPet().setTeam(TeamType.NONE);
		 * player.setIsInTvT(false);
		 * }
		 * for (Integer objId : players_list2)
		 * {
		 * Player player = GameObjectsStorage.getPlayer(objId);
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
		 * for (Player player : _reflection.getPlayers())
		 * {
		 * if (player != null && !live_list1.contains(player.getObjectId()) && !live_list2.contains(player.getObjectId()))
		 * player.teleToLocation(147451, 46728, -3410, ReflectionManager.DEFAULT);
		 * }
		 */
	}

	// @Override
	public void onDeath(Creature self, Creature killer)
	{
		/*
		 * if(_status > 1 && self.isPlayer() && (live_list1.contains(self.getObjectId()) || live_list2.contains(self.getObjectId())))
		 * {
		 * if (killer != null && killer.isPlayable())
		 * {
		 * String[] reward = Config.EVENT_KOREAN_KILL_REWARD;
		 * String id = reward[0];
		 * int id1 = Integer.parseInt(id);
		 * String count = reward[1];
		 * int count1 = Integer.parseInt(count);
		 * Functions.addItem(killer.getPlayer(), id1, count1, true, "koreanStyle");
		 * }
		 * //loosePlayer((Player) self);
		 * getNewFighters();
		 * _pScore.remove((Integer)self.getPlayer().getObjectId());
		 * //self.getPlayer().setIsInTvT(false);
		 * }
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

	// @Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		/*
		 * if(_myZone == null)
		 * return;
		 * if (_myZone.checkIfInZone(x, y, z, reflection))
		 * return;
		 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (live_list1.contains(player.getObjectId()) || live_list2.contains(player.getObjectId())))
		 * {
		 * removePlayer(player);
		 * checkLive
		 */
	}

	// @Override
	public void onPlayerExit(Player player)
	{
		/*
		 * if(player.getTeam() == TeamType.NONE)
		 * return;
		 * if (_status == 0 && _isRegistrationActive && player.getTeam() != TeamType.NONE && (live_list1.contains(player.getObjectId()) || live_list2.contains(player.getObjectId()))) {
		 * removePlayer(player);
		 * return;
		 * }
		 * if (_status == 1 && (live_list1.contains(player.getObjectId()) || live_list2.contains(player.getObjectId()))) {
		 * player.teleToLocation(playerRestoreCoord.get(player.getObjectId()), ReflectionManager.DEFAULT);
		 * removePlayer(player);
		 * return;
		 * }
		 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE
		 * && (live_list1.contains(player.getObjectId()) || live_list2.contains(player.getObjectId()))) {
		 * removePlayer(player);
		 * getNewFighters();
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static void removePlayer(Player player)
	{
		/*
		 * if(player != null)
		 * {
		 * _reflection.removeObject(player);
		 * live_list1.remove((Integer)player.getObjectId());
		 * live_list2.remove((Integer)player.getObjectId());
		 * players_list1.remove((Integer)player.getObjectId());
		 * players_list2.remove((Integer)player.getObjectId());
		 * playerRestoreCoord.remove((Integer)player.getObjectId());
		 * player.setIsInTvT(false);
		 * boolean isRed = player.getTeam() == TeamType.RED ? true : false;
		 * boxes.remove((Integer)player.getObjectId());
		 * for (Player player2 : getPlayers(players_list1))
		 * player2.sendPacket(new ExCubeGameRemovePlayer(player, isRed));
		 * for (Player player3 : getPlayers(players_list2))
		 * player3.sendPacket(new ExCubeGameRemovePlayer(player, isRed));
		 * player.setTeam(TeamType.NONE);
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static List<Player> getPlayers(List<Integer> list)
	{
		/*
		 * List<Player> result = new ArrayList<Player>();
		 * for (Integer objId : list)
		 * {
		 * Player player = GameObjectsStorage.getPlayer(objId);
		 * if (player != null)
		 * result.add(player);
		 * }
		 * return result;
		 */
		return null;
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

	@SuppressWarnings("unused")
	private static boolean checkDualBox(Player player)
	{
		/*
		 * if("IP".equalsIgnoreCase(Config.EVENT_KOREAN_CHECK_WINDOW_METHOD)) {
		 * if (boxes.containsValue(player.getIP())) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledBox", player), player);
		 * return false;
		 * }
		 * }
		 * else if ("HWid".equalsIgnoreCase(Config.EVENT_KOREAN_CHECK_WINDOW_METHOD))
		 * {
		 * if (boxes.containsValue(player.getNetConnection().getHWID())) {
		 * show(new CustomMessage("scripts.events.KoreanStyle.CancelledBox", player), player);
		 * return false;
		 * }
		 * }
		 */
		return true;
	}

	@SuppressWarnings("unused")
	private static long startFightTime = 0;

	public static class CheckActiveTask extends RunnableImpl
	{

		@Override
		public void runImpl()
		{
			/*
			 * if(!_active || _status != 2)
			 * return;
			 * //If they are fighting for at least 5 seconds
			 * if (startFightTime + Config.EVENT_KOREAN_SEC_UNTIL_KILL*1000 < System.currentTimeMillis())
			 * {
			 * //Check if they are actually fighting
			 * if (_redFighter == null || _blueFighter == null || _redFighter.isDead() || _blueFighter.isDead())
			 * getNewFighters();
			 * else
			 * {
			 * if (_redFighter.lastActive + Config.EVENT_KOREAN_SEC_UNTIL_KILL*1000 < System.currentTimeMillis())
			 * _redFighter.doDie(_blueFighter);
			 * else if (_blueFighter.lastActive + Config.EVENT_KOREAN_SEC_UNTIL_KILL*1000 < System.currentTimeMillis())
			 * _blueFighter.doDie(_redFighter);
			 * }
			 * }
			 * ThreadPoolManager.getInstance().schedule(this, 5100);
			 */
		}
	}

	public class StartTask extends RunnableImpl
	{

		@Override
		public void runImpl()
		{
			/*
			 * if(!_active)
			 * return;
			 * if (isPvPEventStarted())
			 * {
			 * _log.info("KoreanStyle not started: another event is already running");
			 * return;
			 * }
			 * for (Residence c : ResidenceHolder.getInstance().getResidenceList(Castle.class))
			 * if (c.getSiegeEvent() != null && c.getSiegeEvent().isInProgress()) {
			 * _log.debug("KoreanStyle not started: CastleSiege in progress");
			 * return;
			 * }
			 * start();
			 */
		}
	}

	@SuppressWarnings("unused")
	private static Location getPoint(String s)
	{
		/*
		 * String[] locs = s.split(", ");
		 * Integer x = Integer.parseInt(locs[0]);
		 * Integer y = Integer.parseInt(locs[1]);
		 * Integer z = Integer.parseInt(locs[2]);
		 * Integer h = locs.length > 3 ? Integer.parseInt(locs[3]) : 0;
		 * Location loc = new Location(x, y, z, h);
		 * return loc;
		 */
		return null;
	}

	// @Override
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	{
		/*
		 * if(actor.isPlayable() && actor.getReflection() == _reflection)
		 * {
		 * if (actor.getPlayer().isSitting())
		 * {
		 * actor.setCurrentCp(actor.getMaxCp());
		 * actor.setCurrentHp(actor.getMaxHp(), false);
		 * }
		 * }
		 */
	}
}
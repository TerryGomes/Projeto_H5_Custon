package events.lastHero;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerExitListener;
import l2mv.gameserver.listener.actor.player.OnTeleportListener;
import l2mv.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class LastHero extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{
	/*
	 * private static final Logger _log = LoggerFactory.getLogger(LastHero.class);
	 * private static List<Long> players_list = new CopyOnWriteArrayList<Long>();
	 * private static List<Long> live_list = new CopyOnWriteArrayList<Long>();
	 * private static boolean _isRegistrationActive = false;
	 * private static int _status = 0;
	 * private static int _time_to_start;
	 * private static int _category;
	 * private static int _pre_category;
	 * private static int _minLevel;
	 * private static int _maxLevel;
	 * private static ScheduledFuture<?> _endTask;
	 * private static Zone _zone = ReflectionUtils.getZone("[colosseum_battle]");
	 * private static ZoneListener _zoneListener = new ZoneListener();
	 * private static final Location _enter = new Location(149505, 46719, -3417);
	 * private static Calendar _date = Calendar.getInstance();
	 */

	@Override
	public void onLoad()
	{
		/*
		 * CharListenerList.addGlobal(this);
		 * _zone.addListener(_zoneListener);
		 * executeTask("events.lastHero.LastHero", "preLoad", new Object[0], 12000);
		 * _active = ServerVariables.getString("LastHero", "off").equalsIgnoreCase("on");
		 * _log.info("Loaded Event: Last Hero");
		 */
	}

	@Override
	public void onReload()
	{
		// _zone.removeListener(_zoneListener);
	}

	@Override
	public void onShutdown()
	{
		// onReload();
	}

	@SuppressWarnings("unused")
	private static boolean _active = false;

	public void activateEvent()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (!isActive())
		 * {
		 * executeTask("events.lastHero.LastHero", "preLoad", new Object[0], 10000);
		 * ServerVariables.set("LastHero", "on");
		 * _log.info("Event 'Last Hero' activated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStarted", null);
		 * }
		 * else
		 * player.sendMessage("Event 'Last Hero' already active.");
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
		 * if (isActive())
		 * {
		 * ServerVariables.unset("LastHero");
		 * _log.info("Event 'Last Hero' deactivated.");
		 * Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStoped", null);
		 * }
		 * else
		 * player.sendMessage("Event 'LastHero' not active.");
		 * _active = false;
		 * show("admin/events/events.htm", player);
		 */
	}

	public static boolean isRunned()
	{
		return false;// _isRegistrationActive || _status > 0;
	}

	public static int getMinLevelForCategory(int category)
	{
		/*
		 * switch(category)
		 * {
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

	public void start(int var)
	{
		/*
		 * Player player = getSelf();
		 * _category = var;
		 * if (_category == -1){
		 * _minLevel = 1;
		 * _maxLevel = 85;}else{
		 * _minLevel = getMinLevelForCategory(_category);
		 * _maxLevel = getMaxLevelForCategory(_category);}
		 * if (_endTask != null){
		 * show(new CustomMessage("common.TryLater", player), player);
		 * return;}
		 * _log.info("LastHero started: LvL - " + _minLevel + " - " + _maxLevel);
		 * _status = 0;
		 * _isRegistrationActive = true;
		 * _time_to_start = EventsConfig.getInt("LastHero_time");
		 * players_list = new CopyOnWriteArrayList<Long>();
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)
		 * };
		 * sayToAll("scripts.events.LastHero.AnnouncePreStart", param);
		 * executeTask("events.lastHero.LastHero", "question", new Object[0], 10000);
		 * executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000);
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
		 * {
		 * if (player.getVar("isPvPevents") == null)
		 * if (player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault() && !player.isInOlympiadMode()
		 * && !player.isInObserverMode())
		 * player.scriptRequest(new CustomMessage("scripts.events.LastHero.AskPlayer", player).toString(), "events.lastHero.LastHero:addPlayer", new Object[0]);
		 * }
		 */
	}

	public static void announce()
	{
		/*
		 * if(players_list.size() < 2 && _time_to_start <= 1){
		 * sayToAll("scripts.events.LastHero.AnnounceEventCancelled", null);
		 * _isRegistrationActive = false;
		 * _status = 0;
		 * executeTask("events.lastHero.LastHero", "preLoad", new Object[0], 10000);
		 * return;}
		 * if (_time_to_start > 1){
		 * _time_to_start--;
		 * String[] param = {
		 * String.valueOf(_time_to_start),
		 * String.valueOf(_minLevel),
		 * String.valueOf(_maxLevel)};
		 * sayToAll("scripts.events.LastHero.AnnouncePreStart", param);
		 * executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000);} else {
		 * _status = 1;
		 * _isRegistrationActive = false;
		 * sayToAll("scripts.events.LastHero.AnnounceEventStarting", null);
		 * executeTask("events.lastHero.LastHero", "prepare", new Object[0], 5000);}
		 */
	}

	public void addPlayer()
	{
		/*
		 * Player player = getSelf();
		 * if (player == null || !checkPlayer(player, true))
		 * return;
		 * if (player.getVar("isPvPevents") == null)
		 * {
		 * players_list.add(player.getStoredId());
		 * live_list.add(player.getStoredId());
		 * player.setVar("isPvPevents", "LastHero", -1);
		 * }
		 * else
		 * {
		 * show(new CustomMessage("scripts.events.Cancelled", player), player);
		 * return;
		 * }
		 * show(new CustomMessage("scripts.events.LastHero.Registered", player), player);
		 */}

	public static boolean checkPlayer(Player player, boolean first)
	{
		/*
		 * if(first && (!_isRegistrationActive || player.isDead())){
		 * show(new CustomMessage("scripts.events.Late", player), player);
		 * return false;}
		 * if (first && players_list.contains(player.getStoredId())){
		 * show(new CustomMessage("scripts.events.LastHero.Cancelled", player), player);
		 * return false;}
		 * if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledLevel", player), player);
		 * return false;}
		 * if (player.isMounted()){
		 * show(new CustomMessage("scripts.events.LastHero.Cancelled", player), player);
		 * return false;}
		 * if (player.isInDuel()){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledDuel", player), player);
		 * return false;}
		 * if (player.getTeam() != TeamType.NONE){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player), player);
		 * return false;}
		 * if (player.getOlympiadGame() != null || first && Olympiad.isRegistered(player)){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledOlympiad", player), player);
		 * return false;}
		 * if (player.isInParty() && player.getParty().isInDimensionalRift()){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player), player);
		 * return false;}
		 * if (player.isTeleporting()){
		 * show(new CustomMessage("scripts.events.LastHero.CancelledTeleport", player), player);
		 * return false;}
		 * if (!player.getReflection().isDefault())
		 * {
		 * player.sendMessage(player.isLangRus() ? "?? ?? ?????? ???????????? ? ??????!" : "You can not participate in the opening event!!");
		 * return false;
		 * }
		 */

		return true;
	}

	public static void prepare()
	{
		/*
		 * ReflectionUtils.getDoor(24190002).closeMe();
		 * ReflectionUtils.getDoor(24190003).closeMe();
		 * cleanPlayers();
		 * clearArena();
		 * executeTask("events.lastHero.LastHero", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.lastHero.LastHero", "healPlayers", new Object[0], 2000);
		 * executeTask("events.lastHero.LastHero", "saveBackCoords", new Object[0], 3000);
		 * executeTask("events.lastHero.LastHero", "paralyzePlayers", new Object[0], 4000);
		 * executeTask("events.lastHero.LastHero", "teleportPlayersToColiseum", new Object[0], 5000);
		 * executeTask("events.lastHero.LastHero", "go", new Object[0], EventsConfig.getInt("LastHero_Time_Paralyze") * 1000);
		 * sayToAll("scripts.events.LastHero.AnnounceFinalCountdown", null);
		 */
	}

	public static void go()
	{
		/*
		 * _status = 2;
		 * upParalyzePlayers();
		 * checkLive();
		 * clearArena();
		 * sayToAll("scripts.events.LastHero.AnnounceFight", null);
		 * _endTask = executeTask("events.lastHero.LastHero", "endBattle", new Object[0], EventsConfig.getInt("LastHero_Time_Batle") * 60000);
		 */}

	public static void endBattle()
	{
		/*
		 * ReflectionUtils.getDoor(24190002).openMe();
		 * ReflectionUtils.getDoor(24190003).openMe();
		 * _status = 0;
		 * removeAura();
		 * if (live_list.size() == 1)
		 * for (Player player : getPlayers(live_list)){
		 * String[] repl = {player.getName()};
		 * sayToAll("scripts.events.LastHero.AnnounceWiner", repl);
		 * for (int i = 0; i < EventsConfig.getIntArray("LastHero_final_bonus_id").length; i++)
		 * addItem(player, EventsConfig.getIntArray("LastHero_final_bonus_id")[i], Math.round(EventsConfig.getBoolean("LastHero_rate_final") ? player.getLevel() *
		 * EventsConfig.getIntArray("LastHero_final_bonus_count")[i] : 1 * EventsConfig.getIntArray("LastHero_final_bonus_count")[i]));
		 * if (EventsConfig.getBoolean("LastHero_Allow_Hero_Aura")){
		 * player.setHero(true);
		 * Hero.addSkills(player);
		 * player.updatePledgeClass();
		 * player.sendPacket(new SkillList(player));
		 * player.broadcastUserInfo(true);}
		 * break;}
		 * sayToAll("scripts.events.LastHero.AnnounceEnd", null);
		 * executeTask("events.lastHero.LastHero", "end", new Object[0], 30000);
		 * _isRegistrationActive = false;
		 * if (_endTask != null){
		 * _endTask.cancel(false);
		 * _endTask = null;}
		 */
	}

	public static void end()
	{
		/*
		 * executeTask("events.lastHero.LastHero", "ressurectPlayers", new Object[0], 1000);
		 * executeTask("events.lastHero.LastHero", "healPlayers", new Object[0], 2000);
		 * executeTask("events.lastHero.LastHero", "teleportPlayersToSavedCoords", new Object[0], 3000);
		 * executeTask("events.lastHero.LastHero", "preLoad", new Object[0], 10000);
		 */
	}

	public static void saveBackCoords()
	{
		/*
		 * for(Player player : getPlayers(players_list))
		 * player.setVar("LastHero_backCoords", player.getX() + " " + player.getY() + " " + player.getZ() + " " + player.getReflectionId(), -1);
		 */
	}

	public static void teleportPlayersToColiseum()
	{
		/*
		 * for(Player player : getPlayers(players_list)){
		 * unRide(player);
		 * unSummonPet(player, true);
		 * player.teleToLocation(Location.findPointToStay(_enter, 150, 500, ReflectionManager.DEFAULT.getGeoIndex()), ReflectionManager.DEFAULT);}
		 */
	}

	public static void teleportPlayersToSavedCoords()
	{
		/*
		 * for(Player player : getPlayers(players_list))
		 * try{
		 * String var = player.getVar("LastHero_backCoords");
		 * if (var == null || var.equals(""))
		 * continue;
		 * String[] coords = var.split(" ");
		 * if (coords.length != 4)
		 * continue;
		 * player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
		 * player.unsetVar("LastHero_backCoords");
		 * if (player.getVar("isPvPevents") != null)
		 * player.unsetVar("isPvPevents");}
		 * catch (Exception e){e.printStackTrace();}
		 */
	}

	public static void paralyzePlayers()
	{
		/*
		 * Skill revengeSkill = SkillTable.getInstance().getInfo(Skill.SKILL_RAID_CURSE, 1);
		 * for (Player player : getPlayers(players_list)){
		 * if (EventsConfig.getBoolean("LastHero_DisableEffect"))
		 * player.getEffectList().stopAllEffects();
		 * player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		 * revengeSkill.getEffects(player, player, false, false);
		 * if (player.getPet() != null){
		 * revengeSkill.getEffects(player, player.getPet(), false, false);
		 * if (EventsConfig.getBoolean("LastHero_DisablePetEffect"))
		 * player.getPet().getEffectList().stopAllEffects();}}
		 */
	}

	public static void upParalyzePlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list)){
		 * player.getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
		 * if (player.getPet() != null)
		 * player.getPet().getEffectList().stopEffect(Skill.SKILL_RAID_CURSE);
		 * player.leaveParty();}
		 */
	}

	public static void ressurectPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list))
		 * if (player.isDead()){
		 * player.restoreExp();
		 * player.setCurrentCp(player.getMaxCp());
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.broadcastPacket(new Revive(player));}
		 */
	}

	public static void healPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list)){
		 * player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		 * player.setCurrentCp(player.getMaxCp());}
		 */
	}

	public static void cleanPlayers()
	{
		/*
		 * for(Player player : getPlayers(players_list))
		 * if (!checkPlayer(player, false))
		 * removePlayer(player);
		 */
	}

	public static void checkLive()
	{
		/*
		 * List<Long> new_live_list = new CopyOnWriteArrayList<Long>();
		 * for (Long storeId : live_list){
		 * Player player = GameObjectsStorage.getAsPlayer(storeId);
		 * if (player != null)
		 * new_live_list.add(storeId);}
		 * live_list = new_live_list;
		 * for (Player player : getPlayers(live_list))
		 * if (player.isInZone(_zone) && !player.isDead() && !player.isLogoutStarted())
		 * player.setTeam(TeamType.RED);
		 * else if (player.isDead())
		 * {
		 * player.restoreExp();
		 * player.setCurrentCp(player.getMaxCp());
		 * player.setCurrentHp(player.getMaxHp(), true);
		 * player.setCurrentMp(player.getMaxMp());
		 * player.broadcastPacket(new Revive(player));
		 * loosePlayer(player);
		 * }
		 * else
		 * loosePlayer(player);
		 * if (live_list.size() <= 1)
		 * endBattle();
		 */
	}

	public static void removeAura()
	{
		/*
		 * for(Player player : getPlayers(live_list))
		 * player.setTeam(TeamType.NONE);
		 */
	}

	public static void clearArena()
	{
		/*
		 * for(GameObject obj : _zone.getObjects())
		 * if (obj != null){
		 * Player player = obj.getPlayer();
		 * if (player != null && !live_list.contains(player.getStoredId()))
		 * player.teleToLocation(147451, 46728, -3410);}
		 */
	}

	@Override
	public void onDeath(Creature self, Creature killer)
	{
		/*
		 * if(_status > 1 && self.isPlayer() && self.getTeam() != TeamType.NONE && live_list.contains(self.getStoredId())){
		 * Player player = (Player) self;
		 * loosePlayer(player);
		 * checkLive();
		 * if (killer != null && killer.isPlayer() && killer.getPlayer().expertiseIndex - player.expertiseIndex > 2 && !killer.getPlayer().getIP().equals(player.getIP()))
		 * for (int i = 0; i < EventsConfig.getIntArray("LastHero_bonus_id").length; i++)
		 * addItem((Player) killer, EventsConfig.getIntArray("LastHero_bonus_id")[i], Math.round(EventsConfig.getBoolean("LastHero_rate") ? player.getLevel() *
		 * EventsConfig.getIntArray("LastHero_bonus_count")[i] : 1 * EventsConfig.getIntArray("LastHero_bonus_count")[i]));}
		 */
	}

	@Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		/*
		 * if(_zone.checkIfInZone(x, y, z, reflection))
		 * return;
		 * if (_status > 1 && player.getTeam() != TeamType.NONE && live_list.contains(player.getStoredId())){
		 * removePlayer(player);
		 * checkLive();}
		 */
	}

	@Override
	public void onPlayerExit(Player player)
	{
		/*
		 * if(player.getTeam() == TeamType.NONE)
		 * return;
		 * // ????? ??? ??????? ?? ????? ???????????
		 * if (_status == 0 && _isRegistrationActive && live_list.contains(player.getStoredId())){
		 * removePlayer(player);
		 * return;}
		 * // ????? ??? ??????? ?? ????? ????????????
		 * if (_status == 1 && live_list.contains(player.getStoredId())){
		 * removePlayer(player);
		 * try{
		 * String var = player.getVar("LastHero_backCoords");
		 * if (var == null || var.equals(""))
		 * return;
		 * String[] coords = var.split(" ");
		 * if (coords.length != 4)
		 * return;
		 * player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
		 * player.unsetVar("LastHero_backCoords");}
		 * catch (Exception e){e.printStackTrace();}
		 * return;}
		 * // ????? ??? ??????? ?? ????? ??????
		 * if (_status > 1 && player.getTeam() != TeamType.NONE && live_list.contains(player.getStoredId())){
		 * removePlayer(player);
		 * checkLive();}
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
			 * if (_status > 0 && player != null && !live_list.contains(player.getStoredId()))
			 * ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, new Location(147451, 46728, -3410)), 3000);
			 */
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			/*
			 * if(cha == null)
			 * return;
			 * Player player = cha.getPlayer();
			 * if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && live_list.contains(player.getStoredId())){
			 * double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // ???? ? ????????
			 * double radian = Math.toRadians(angle - 90); // ???? ? ????????
			 * int x = (int) (cha.getX() + 50 * Math.sin(radian));
			 * int y = (int) (cha.getY() - 50 * Math.cos(radian));
			 * int z = cha.getZ();
			 * ThreadPoolManager.getInstance().schedule(new TeleportTask(cha, new Location(x, y, z)), 3000);}}
			 */
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	@SuppressWarnings("unused")
	private static class TeleportTask extends RunnableImpl
	{
		Location loc;
		Creature target;

		public TeleportTask(Creature target, Location loc)
		{
			this.target = target;
			this.loc = loc;
			target.block();
		}

		@Override
		public void runImpl() throws Exception
		{
			target.unblock();
			target.teleToLocation(loc);
		}
	}

	public static void preLoad()
	{
		/*
		 * int day;
		 * if (EventsConfig.getBoolean("LastHero_Allow_Calendar_Day"))
		 * day = 4;
		 * else
		 * day = 3;
		 * for (int i = 0; i < EventsConfig.getIntArray("LastHero_Time_Start").length; i += day){
		 * if (EventsConfig.getBoolean("LastHero_Allow_Calendar_Day")){
		 * _date.set(Calendar.DAY_OF_MONTH, EventsConfig.getIntArray("LastHero_Time_Start")[i]);
		 * _date.set(Calendar.HOUR_OF_DAY, EventsConfig.getIntArray("LastHero_Time_Start")[i + 1]);
		 * _date.set(Calendar.MINUTE, EventsConfig.getIntArray("LastHero_Time_Start")[i + 2]);}else{
		 * _date.set(Calendar.HOUR_OF_DAY, EventsConfig.getIntArray("LastHero_Time_Start")[i]);
		 * _date.set(Calendar.MINUTE, EventsConfig.getIntArray("LastHero_Time_Start")[i + 1]);}
		 * if (_date.getTimeInMillis() > System.currentTimeMillis()){
		 * if (EventsConfig.getBoolean("LastHero_Allow_Calendar_Day"))
		 * _pre_category = EventsConfig.getIntArray("LastHero_Time_Start")[i + 3];
		 * else
		 * _pre_category = EventsConfig.getIntArray("LastHero_Time_Start")[i + 2];
		 * _active = true;
		 * executeTask("events.lastHero.LastHero", "preStartTask", new Object[0], (int)getMillisToStart() - 20000);
		 * return;}}
		 */
	}

	public void preStartTask()
	{
		// start(_pre_category);
	}
}
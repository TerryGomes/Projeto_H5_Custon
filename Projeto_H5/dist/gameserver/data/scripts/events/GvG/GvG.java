package events.GvG;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class GvG extends Functions implements ScriptFile
{
	/*
	 * private static final Logger _log = LoggerFactory.getLogger(GvG.class);
	 * public static final Location TEAM1_LOC = new Location(139736, 145832, -15264); // Team location after teleportation
	 * public static final Location TEAM2_LOC = new Location(139736, 139832, -15264);
	 * public static final Location RETURN_LOC = new Location(43816, -48232, -822);
	 * public static final int[] everydayStartTime = { 21, 30, 00 };
	 * private static boolean _active = false;
	 * private static boolean _isRegistrationActive = false;
	 * private static int _minLevel = 76;
	 * private static int _maxLevel = 85;
	 * private static int _groupsLimit = 100; // Limit of groups can register
	 * private static int _minPartyMembers = 6; // self-explanatory
	 * private static long regActiveTime = 10 * 60 * 1000L; // Timelimit for registration
	 * private static ScheduledFuture<?> _globalTask;
	 * private static ScheduledFuture<?> _regTask;
	 * private static ScheduledFuture<?> _countdownTask1;
	 * private static ScheduledFuture<?> _countdownTask2;
	 * private static ScheduledFuture<?> _countdownTask3;
	 * private static List<HardReference<Player>> leaderList = new CopyOnWriteArrayList<HardReference<Player>>();
	 */

	public static class RegTask extends RunnableImpl
	{
		@SuppressWarnings("unused")
		@Override
		public void runImpl() throws Exception
		{
			// prepare();
		}
	}

	public static class Countdown extends RunnableImpl
	{
		int _timer;

		public Countdown(int timer)
		{
			_timer = timer;
		}

		@SuppressWarnings("unused")
		@Override
		public void runImpl() throws Exception
		{
			// Announcements.getInstance().announceToAll("GvG: Until the end of the applications for the tournament remains " + Integer.toString(_timer) + " min.");
		}
	}

	@Override
	public void onLoad()
	{
		/*
		 * _log.info("Loaded Event: GvG");
		 * initTimer();
		 */
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	@SuppressWarnings("unused")
	private static void initTimer()
	{
		/*
		 * long day = 24 * 60 * 60 * 1000L;
		 * Calendar ci = Calendar.getInstance();
		 * ci.set(Calendar.HOUR_OF_DAY, everydayStartTime[0]);
		 * ci.set(Calendar.MINUTE, everydayStartTime[1]);
		 * ci.set(Calendar.SECOND, everydayStartTime[2]);
		 * long delay = ci.getTimeInMillis() - System.currentTimeMillis();
		 * if (delay < 0)
		 * delay = delay + day;
		 * if (_globalTask != null)
		 * _globalTask.cancel(true);
		 * _globalTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Launch(), delay, day);
		 */
	}

	public static class Launch extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			// activateEvent();
		}
	}

	@SuppressWarnings("unused")
	private static boolean canBeStarted()
	{
		/*
		 * for(Castle c : ResidenceHolder.getInstance().getResidenceList(Castle.class))
		 * if (c.getSiegeEvent() != null && c.getSiegeEvent().isInProgress())
		 * return false;
		 */
		return true;
	}

	@SuppressWarnings("unused")
	private static boolean isActive()
	{
		// return _active;
		return false;
	}

	public static void activateEvent()
	{
		/*
		 * if(!isActive() && canBeStarted())
		 * {
		 * _regTask = ThreadPoolManager.getInstance().schedule(new RegTask(), regActiveTime);
		 * if (regActiveTime > 2 * 60 * 1000L) //display countdown announcements only when timelimit for registration is more than 3 mins
		 * {
		 * if (regActiveTime > 5 * 60 * 1000L)
		 * _countdownTask3 = ThreadPoolManager.getInstance().schedule(new Countdown(5), regActiveTime - 300 * 1000);
		 * _countdownTask1 = ThreadPoolManager.getInstance().schedule(new Countdown(2), regActiveTime - 120 * 1000);
		 * _countdownTask2 = ThreadPoolManager.getInstance().schedule(new Countdown(1), regActiveTime - 60 * 1000);
		 * }
		 * ServerVariables.set("GvG", "on");
		 * _log.info("Event 'GvG' activated.");
		 * Announcements.getInstance().announceToAll("Registration for GvG Tournament has begun! Community Board (Alt + B) -> Event -> GvG (registration, description)");
		 * Announcements.getInstance().announceToAll("Applications will be accepted during the " + regActiveTime / 60000 + " minutes");
		 * _active = true;
		 * _isRegistrationActive = true;
		 * }
		 */
	}

	/**
	 * Cancels the event during registration time
	 */
	public static void deactivateEvent()
	{
		/*
		 * if(isActive())
		 * {
		 * stopTimers();
		 * ServerVariables.unset("GvG");
		 * _log.info("Event 'GvG' canceled.");
		 * Announcements.getInstance().announceToAll("GvG: Tournament canceled");
		 * _active = false;
		 * _isRegistrationActive = false;
		 * leaderList.clear();
		 * }
		 */
	}

	/**
	 * Shows groups and their leaders who's currently in registration list
	 */
	public void showStats()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (!isActive())
		 * {
		 * player.sendMessage("GvG event is not launched");
		 * return;
		 * }
		 * StringBuilder string = new StringBuilder();
		 * String refresh =
		 * "<button value=\"Refresh\" action=\"bypass -h scripts_events.GvG.GvG:showStats\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
		 * String start = "<button value=\"Start Now\" action=\"bypass -h scripts_events.GvG.GvG:startNow\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
		 * int i = 0;
		 * if (!leaderList.isEmpty())
		 * {
		 * for (Player leader : HardReferences.unwrap(leaderList))
		 * {
		 * if (!leader.isInParty())
		 * continue;
		 * string.append("*").append(leader.getName()).append("*").append(" | group members: ").append(leader.getParty().size()).append("\n\n");
		 * i++;
		 * }
		 * show("There are " + i + " group leaders who registered for the event:\n\n" + string + "\n\n" + refresh + "\n\n" + start, player, null);
		 * }
		 * else
		 * show("There are no participants at the time\n\n" + refresh, player, null);
		 */
	}

	public void startNow()
	{
		/*
		 * Player player = getSelf();
		 * if (!player.getPlayerAccess().IsEventGm)
		 * return;
		 * if (!isActive() || !canBeStarted())
		 * {
		 * player.sendMessage("GvG event is not launched");
		 * return;
		 * }
		 * prepare();
		 */
	}

	/**
	 * Handles the group applications and apply restrictions
	 */
	public void addGroup()
	{
		/*
		 * Player player = getSelf();
		 * if (player == null)
		 * return;
		 * if (!_isRegistrationActive)
		 * {
		 * player.sendMessage("GvG tournament inactive.");
		 * return;
		 * }
		 * if (leaderList.contains(player.getRef()))
		 * {
		 * player.sendMessage("You have already registered on GvG Tournament");
		 * return;
		 * }
		 * if (!player.isInParty())
		 * {
		 * player.sendMessage("You should be in party to can apply");
		 * return;
		 * }
		 * if (!player.getParty().isLeader(player))
		 * {
		 * player.sendMessage("Only the leader of the group can apply");
		 * return;
		 * }
		 * if (player.getParty().isInCommandChannel())
		 * {
		 * player.sendMessage("To participate in the tournament, you have to leave the command channel");
		 * return;
		 * }
		 * if (leaderList.size() >= _groupsLimit)
		 * {
		 * player.sendMessage("Limit is reached the number of groups to participate in the tournament. Application rejected");
		 * return;
		 * }
		 * List<Player> party = player.getParty().getMembers();
		 * String[] abuseReason = {
		 * "is not in the game",
		 * "is not in the group",
		 * "is an incomplete group. Minimum number of group members - 6.",
		 * "is not the leader of the group, to apply",
		 * "does not meet the levels for the tournament",
		 * "uses the mount, contrary to the requirements of the tournament",
		 * "is in a duel, which contradicts the requirements of the tournament",
		 * "takes part in another event period, contrary to the requirements of the tournament",
		 * "is in the waiting list for the Olympics or takes part in it",
		 * "is set to teleport, which contradicts the requirements of the tournament",
		 * "is in the Dimensional Rift, contrary to the requirements of the tournament",
		 * "possesses the cursed sword, contrary to the requirements of the tournament",
		 * "is not in a peaceful zone",
		 * "is in observ mode",
		 * "is an instance", };
		 * for (Player eachmember : party)
		 * {
		 * int abuseId = checkPlayer(eachmember, false);
		 * if (abuseId != 0)
		 * {
		 * player.sendMessage("player " + eachmember.getName() + " " + abuseReason[abuseId - 1]);
		 * return;
		 * }
		 * }
		 * leaderList.add(player.getRef());
		 * player.getParty().
		 * broadcastMessageToPartyMembers("Your group has been included in the waiting list. Please do not register in the other event, and do not engage in duels before the tournament. A complete list of the requirements of the tournament in Community Board (Alt + B)"
		 * );
		 */
	}

	@SuppressWarnings("unused")
	private static void stopTimers()
	{
		/*
		 * if(_regTask != null)
		 * {
		 * _regTask.cancel(false);
		 * _regTask = null;
		 * }
		 * if (_countdownTask1 != null)
		 * {
		 * _countdownTask1.cancel(false);
		 * _countdownTask1 = null;
		 * }
		 * if (_countdownTask2 != null)
		 * {
		 * _countdownTask2.cancel(false);
		 * _countdownTask2 = null;
		 * }
		 * if (_countdownTask3 != null)
		 * {
		 * _countdownTask3.cancel(false);
		 * _countdownTask3 = null;
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static void prepare()
	{
		/*
		 * checkPlayers();
		 * shuffleGroups();
		 * if (isActive())
		 * {
		 * stopTimers();
		 * ServerVariables.unset("GvG");
		 * _active = false;
		 * _isRegistrationActive = false;
		 * }
		 * if (leaderList.size() < 2)
		 * {
		 * leaderList.clear();
		 * Announcements.getInstance().announceToAll("GvG: Tournament canceled due to lack of participants");
		 * return;
		 * }
		 * Announcements.getInstance().announceToAll("GvG: Receipt of applications is completed. Starting the tournament.");
		 * start();
		 */
	}

	/**
	 * @param player
	 * @param doCheckLeadership
	 * @return
	 * Handles all limits for every group member. Called 2 times: when registering group and before sending it to the instance
	 */
	@SuppressWarnings("unused")
	private static int checkPlayer(Player player, boolean doCheckLeadership)
	{
		/*
		 * if(!player.isOnline())
		 * return 1;
		 * if (!player.isInParty())
		 * return 2;
		 * if (doCheckLeadership && (player.getParty() == null || !player.getParty().isLeader(player)))
		 * return 4;
		 * if (player.getParty() == null || player.getParty().size() < _minPartyMembers)
		 * return 3;
		 * if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel)
		 * return 5;
		 * if (player.isMounted())
		 * return 6;
		 * if (player.isInDuel())
		 * return 7;
		 * if (player.getTeam() != TeamType.NONE)
		 * return 8;
		 * if (player.getOlympiadGame() != null || Olympiad.isRegistered(player))
		 * return 9;
		 * if (player.isTeleporting())
		 * return 10;
		 * if (player.getParty().isInDimensionalRift())
		 * return 11;
		 * if (player.isCursedWeaponEquipped())
		 * return 12;
		 * if (!player.isInPeaceZone())
		 * return 13;
		 * if (player.isInObserverMode())
		 * return 14;
		 * if (!player.getReflection().isDefault())
		 * {
		 * return 15;
		 * }
		 */
		return 0;
	}

	/**
	 */
	@SuppressWarnings("unused")
	private static void shuffleGroups()
	{
		/*
		 * if(leaderList.size() % 2 != 0) // If there are odd quantity of groups in the list we should remove one of them to make it even
		 * {
		 * int rndindex = Rnd.get(leaderList.size());
		 * Player expelled = leaderList.remove(rndindex).get();
		 * if (expelled != null)
		 * expelled.sendMessage("In forming the list of participants in the tournament your team has been deselected. Sorry, try the next time.");
		 * }
		 */

		/*
		 * for(int i = 0; i < leaderList.size(); i++)
		 * {
		 * int rndindex = Rnd.get(leaderList.size());
		 * leaderList.set(i, leaderList.set(rndindex, leaderList.get(i)));
		 * }
		 */
	}

	@SuppressWarnings("unused")
	private static void checkPlayers()
	{
		/*
		 * for(Player player : HardReferences.unwrap(leaderList))
		 * {
		 * if (checkPlayer(player, true) != 0)
		 * {
		 * leaderList.remove(player.getRef());
		 * continue;
		 * }
		 * for (Player partymember : player.getParty().getMembers())
		 * {
		 * if (checkPlayer(partymember, false) != 0)
		 * {
		 * player.sendMessage("Your team has been disqualified and removed from the tournament because one or more members of the group had violated the conditions of participation");
		 * leaderList.remove(player.getRef());
		 * break;
		 * }
		 * }
		 * }
		 */
	}

	public static void updateWinner(Player winner)
	{

	}

	@SuppressWarnings("unused")
	private static void start()
	{
		/*
		 * int instancedZoneId = 504;
		 * InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
		 * if (iz == null)
		 * {
		 * _log.warn("GvG: InstanceZone : " + instancedZoneId + " not found!");
		 * return;
		 * }
		 * for (int i = 0; i < leaderList.size(); i += 2)
		 * {
		 * Player team1Leader = leaderList.get(i).get();
		 * Player team2Leader = leaderList.get(i + 1).get();
		 * GvGInstance r = new GvGInstance();
		 * r.setTeam1(team1Leader.getParty());
		 * r.setTeam2(team2Leader.getParty());
		 * r.init(iz);
		 * r.setReturnLoc(GvG.RETURN_LOC);
		 * for (Player member : team1Leader.getParty().getMembers())
		 * {
		 * if (Config.EVENT_GvGDisableEffect)
		 * member.getEffectList().stopAllEffects();
		 * Functions.unRide(member);
		 * Functions.unSummonPet(member, true);
		 * member.setTransformation(0);
		 * member.setInstanceReuse(instancedZoneId, System.currentTimeMillis());
		 * member.dispelBuffs();
		 * member.teleToLocation(Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, r.getGeoIndex()), r);
		 * }
		 * for (Player member : team2Leader.getParty().getMembers())
		 * {
		 * if (Config.EVENT_GvGDisableEffect)
		 * member.getEffectList().stopAllEffects();
		 * Functions.unRide(member);
		 * Functions.unSummonPet(member, true);
		 * member.setTransformation(0);
		 * member.setInstanceReuse(instancedZoneId, System.currentTimeMillis());
		 * member.dispelBuffs();
		 * member.teleToLocation(Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, r.getGeoIndex()), r);
		 * }
		 * r.start();
		 * }
		 * leaderList.clear();
		 * _log.info("GvG: Event started successfuly.");
		 */
	}
}
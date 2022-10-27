package bosses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bosses.FourSepulchersSpawn.GateKeeper;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;
import npc.model.SepulcherNpcInstance;

public class FourSepulchersManager extends Functions implements ScriptFile, OnDeathListener
{
	private static final Logger _log = LoggerFactory.getLogger(FourSepulchersManager.class);

	public static final String QUEST_ID = "_620_FourGoblets";

	private static Zone[] _zone = new Zone[4];

	private static final int ENTRANCE_PASS = 7075;
	private static final int USED_PASS = 7261;
	private static final int CHAPEL_KEY = 7260;
	private static final int ANTIQUE_BROOCH = 7262;

	private static boolean _inEntryTime = false;
	private static boolean _inAttackTime = false;

	private static ScheduledFuture<?> _changeCoolDownTimeTask = null, _changeEntryTimeTask = null, _changeWarmUpTimeTask = null, _changeAttackTimeTask = null;

	private static long _coolDownTimeEnd = 0;
	private static long _entryTimeEnd = 0;
	private static long _warmUpTimeEnd = 0;
	private static long _attackTimeEnd = 0;

	private static int _newCycleMin = 55;

	private static boolean _firstTimeRun;

	public void init()
	{
		CharListenerList.addGlobal(this);

		_zone[0] = ReflectionUtils.getZone("[FourSepulchers1]");
		_zone[1] = ReflectionUtils.getZone("[FourSepulchers2]");
		_zone[2] = ReflectionUtils.getZone("[FourSepulchers3]");
		_zone[3] = ReflectionUtils.getZone("[FourSepulchers4]");

		if (_changeCoolDownTimeTask != null)
		{
			_changeCoolDownTimeTask.cancel(false);
		}
		if (_changeEntryTimeTask != null)
		{
			_changeEntryTimeTask.cancel(false);
		}
		if (_changeWarmUpTimeTask != null)
		{
			_changeWarmUpTimeTask.cancel(false);
		}
		if (_changeAttackTimeTask != null)
		{
			_changeAttackTimeTask.cancel(false);
		}

		_changeCoolDownTimeTask = null;
		_changeEntryTimeTask = null;
		_changeWarmUpTimeTask = null;
		_changeAttackTimeTask = null;

		_inEntryTime = false;
		_inAttackTime = false;

		_firstTimeRun = true;

		FourSepulchersSpawn.init();

		timeSelector();
	}

	// phase select on server launch
	private static void timeSelector()
	{
		timeCalculator();
		long currentTime = System.currentTimeMillis();
		// if current time >= time of entry beginning and if current time < time of entry beginning + time of entry end
		if (currentTime >= _coolDownTimeEnd && currentTime < _entryTimeEnd) // entry time check
		{
			cleanUp();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeEntryTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Entry time");
		}
		else if (currentTime >= _entryTimeEnd && currentTime < _warmUpTimeEnd) // warmup time check
		{
			cleanUp();
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeWarmUpTime(), 0);
			_log.info("FourSepulchersManager: Beginning in WarmUp time");
		}
		else if (currentTime >= _warmUpTimeEnd && currentTime < _attackTimeEnd) // attack time check
		{
			cleanUp();
			_changeAttackTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeAttackTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Attack time");
		}
		else
		// else cooldown time and without cleanup because it's already implemented
		{
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeCoolDownTime(), 0);
			_log.info("FourSepulchersManager: Beginning in Cooldown time");
		}
	}

	// phase end times calculator
	private static void timeCalculator()
	{
		Calendar tmp = Calendar.getInstance();
		if (tmp.get(Calendar.MINUTE) < _newCycleMin)
		{
			tmp.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) - 1);
		}
		tmp.set(Calendar.MINUTE, _newCycleMin);
		_coolDownTimeEnd = tmp.getTimeInMillis();
		_entryTimeEnd = _coolDownTimeEnd + 3 * 60000;
		_warmUpTimeEnd = _entryTimeEnd + 2 * 60000;
		_attackTimeEnd = _warmUpTimeEnd + 50 * 60000;
	}

	private static void cleanUp()
	{
		for (Player player : getPlayersInside())
		{
			player.teleToClosestTown();
		}

		FourSepulchersSpawn.deleteAllMobs();

		FourSepulchersSpawn.closeAllDoors();

		FourSepulchersSpawn._hallInUse.clear();
		FourSepulchersSpawn._hallInUse.put(31921, false);
		FourSepulchersSpawn._hallInUse.put(31922, false);
		FourSepulchersSpawn._hallInUse.put(31923, false);
		FourSepulchersSpawn._hallInUse.put(31924, false);

		if (!FourSepulchersSpawn._archonSpawned.isEmpty())
		{
			Set<Integer> npcIdSet = FourSepulchersSpawn._archonSpawned.keySet();
			for (int npcId : npcIdSet)
			{
				FourSepulchersSpawn._archonSpawned.put(npcId, false);
			}
		}
	}

	public static boolean isEntryTime()
	{
		return _inEntryTime;
	}

	public static boolean isAttackTime()
	{
		return _inAttackTime;
	}

	public static synchronized void tryEntry(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		switch (npcId)
		{
		// ID ok
		case 31921:
		case 31922:
		case 31923:
		case 31924:
			break;
		// ID not ok
		default:
			return;
		}

		if (FourSepulchersSpawn._hallInUse.get(npcId))
		{
			showHtmlFile(player, npcId + "-FULL.htm", npc, null);
			return;
		}

		if (!player.isInParty() || player.getParty().size() < 4)
		{
			showHtmlFile(player, npcId + "-SP.htm", npc, null);
			return;
		}

		if (!player.getParty().isLeader(player))
		{
			showHtmlFile(player, npcId + "-NL.htm", npc, null);
			return;
		}

		for (Player mem : player.getParty().getMembers())
		{
			QuestState qs = mem.getQuestState(QUEST_ID);
			if (qs == null || !qs.isStarted() && !qs.isCompleted())
			{
				showHtmlFile(player, npcId + "-NS.htm", npc, mem);
				return;
			}

			if (mem.getInventory().getItemByItemId(ENTRANCE_PASS) == null)
			{
				showHtmlFile(player, npcId + "-SE.htm", npc, mem);
				return;
			}

			if (!mem.isQuestContinuationPossible(true) || mem.isDead() || !mem.isInRange(player, 700))
			{
				return;
			}
		}

		if (!isEntryTime())
		{
			showHtmlFile(player, npcId + "-NE.htm", npc, null);
			return;
		}

		showHtmlFile(player, npcId + "-OK.htm", npc, null);

		entry(npcId, player);
	}

	private static void entry(int npcId, Player player)
	{
		Location loc = FourSepulchersSpawn._startHallSpawns.get(npcId);
		for (Player member : player.getParty().getMembers())
		{
			member.teleToLocation(Location.findPointToStay(member, loc, 0, 80));
			Functions.removeItem(member, ENTRANCE_PASS, 1, "FourSepulchersManager");
			if (member.getInventory().getItemByItemId(ANTIQUE_BROOCH) == null)
			{
				Functions.addItem(member, USED_PASS, 1, "FourSepulchersManager");
			}
			Functions.removeItem(member, CHAPEL_KEY, 999999, "FourSepulchersManager");
		}
		FourSepulchersSpawn._hallInUse.put(npcId, true);
	}

	@Override
	public void onDeath(Creature self, Creature killer)
	{
		if (self.isPlayer() && self.getZ() >= -7250 && self.getZ() <= -6841 && checkIfInZone(self))
		{
			checkAnnihilated((Player) self);
		}
	}

	public static void checkAnnihilated(final Player player)
	{
		if (isPlayersAnnihilated())
		{
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					if (player.getParty() != null)
					{
						for (Player mem : player.getParty().getMembers())
						{
							if (!mem.isDead())
							{
								break;
							}
							mem.teleToLocation(169589 + Rnd.get(-80, 80), -90493 + Rnd.get(-80, 80), -2914);
						}
					}
					else
					{
						player.teleToLocation(169589 + Rnd.get(-80, 80), -90493 + Rnd.get(-80, 80), -2914);
					}
				}
			}, 5000);
		}
	}

	private static int minuteSelect(int min)
	{
		switch (min % 5)
		{
		case 0:
			return min;
		case 1:
			return min - 1;
		case 2:
			return min - 2;
		case 3:
			return min + 2;
		default:
			return min + 1;
		}
	}

	public static void managerSay(int min)
	{
		// for attack phase, sending message every 5 minutes
		if (_inAttackTime)
		{
			// do not shout when < 5 minutes
			if (min < 5)
			{
				return;
			}
			min = minuteSelect(min);
			String msg = min + " minute(s) have passed."; // now this is a proper message^^
			if (min == 90)
			{
				msg = "Game over. The teleport will appear momentarily";
			}
			for (SepulcherNpcInstance npc : FourSepulchersSpawn._managers)
			{
				// hall not used right now, so its manager will not tell you anything :)
				// if you don't need this - delete next two lines.
				if (!FourSepulchersSpawn._hallInUse.get(npc.getNpcId()))
				{
					continue;
				}
				npc.sayInShout(msg);
			}
		}
		else if (_inEntryTime)
		{
			String msg1 = "You may now enter the Sepulcher";
			String msg2 = "If you place your hand on the stone statue in front of each sepulcher," + " you will be able to enter";
			for (SepulcherNpcInstance npc : FourSepulchersSpawn._managers)
			{
				npc.sayInShout(msg1);
				npc.sayInShout(msg2);
			}
		}
	}

	private static class ManagerSay extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (_inAttackTime)
			{
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(System.currentTimeMillis() - _warmUpTimeEnd);
				if (tmp.get(Calendar.MINUTE) + 5 < 50)
				{
					managerSay(tmp.get(Calendar.MINUTE)); // byte because minute cannot be more than 59
					ThreadPoolManager.getInstance().schedule(new ManagerSay(), 5 * 60000);
				}
				// attack time ending chat
				else if (tmp.get(Calendar.MINUTE) + 5 >= 50)
				{
					managerSay(90); // sending a unique id :D
				}
			}
			else if (_inEntryTime)
			{
				managerSay(0);
			}
		}
	}

	private static class ChangeEntryTime extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_inEntryTime = true;
			_inAttackTime = false;

			long interval = 0;
			// if this is first launch - search time when entry time will be ended:
			// counting difference between time when entry time ends and current time and then launching change time task
			if (_firstTimeRun)
			{
				interval = _entryTimeEnd - System.currentTimeMillis();
			}
			else
			{
				interval = 3 * 60000; // else use stupid method
			}
			// launching saying process...
			ThreadPoolManager.getInstance().execute(new ManagerSay());
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeWarmUpTime(), interval);
			if (_changeEntryTimeTask != null)
			{
				_changeEntryTimeTask.cancel(false);
				_changeEntryTimeTask = null;
			}
		}
	}

	private static class ChangeWarmUpTime extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_inEntryTime = true;
			_inAttackTime = false;

			long interval = 0;
			// searching time when warmup time will be ended:
			// counting difference between time when warmup time ends and current time and then launching change time task
			if (_firstTimeRun)
			{
				interval = _warmUpTimeEnd - System.currentTimeMillis();
			}
			else
			{
				interval = 2 * 60000;
			}
			_changeAttackTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeAttackTime(), interval);

			if (_changeWarmUpTimeTask != null)
			{
				_changeWarmUpTimeTask.cancel(false);
				_changeWarmUpTimeTask = null;
			}
		}
	}

	private static class ChangeAttackTime extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_inEntryTime = false;
			_inAttackTime = true;

			for (GateKeeper gk : FourSepulchersSpawn._GateKeepers)
			{
				SepulcherNpcInstance npc = new SepulcherNpcInstance(IdFactory.getInstance().getNextId(), gk.template);
				npc.spawnMe(gk);
				FourSepulchersSpawn._allMobs.add(npc);
			}

			FourSepulchersSpawn.locationShadowSpawns();

			FourSepulchersSpawn.spawnMysteriousBox(31921);
			FourSepulchersSpawn.spawnMysteriousBox(31922);
			FourSepulchersSpawn.spawnMysteriousBox(31923);
			FourSepulchersSpawn.spawnMysteriousBox(31924);

			if (!_firstTimeRun)
			{
				_warmUpTimeEnd = System.currentTimeMillis();
			}

			long interval = 0;
			// say task
			if (_firstTimeRun)
			{
				for (double min = Calendar.getInstance().get(Calendar.MINUTE); min < _newCycleMin; min++)
				{
					// looking for next shout time....
					if (min % 5 == 0) // check if min can be divided by 5
					{
						_log.info(Calendar.getInstance().getTime() + " Atk announce scheduled to " + min + " minute of this hour.");
						Calendar inter = Calendar.getInstance();
						inter.set(Calendar.MINUTE, (int) min);
						ThreadPoolManager.getInstance().schedule(new ManagerSay(), inter.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
						break;
					}
				}
			}
			else
			{
				ThreadPoolManager.getInstance().schedule(new ManagerSay(), 5 * 60400);
			}

			// searching time when attack time will be ended:
			// counting difference between time when attack time ends and current time and then launching change time task
			if (_firstTimeRun)
			{
				interval = _attackTimeEnd - System.currentTimeMillis();
			}
			else
			{
				interval = 50 * 60000;
			}
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeCoolDownTime(), interval);

			if (_changeAttackTimeTask != null)
			{
				_changeAttackTimeTask.cancel(false);
				_changeAttackTimeTask = null;
			}
		}
	}

	private static class ChangeCoolDownTime extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_inEntryTime = false;
			_inAttackTime = false;

			cleanUp();

			Calendar time = Calendar.getInstance();
			// one hour = 55th min to 55 min of next hour, so we check for this, also check for first launch
			if (Calendar.getInstance().get(Calendar.MINUTE) > _newCycleMin && !_firstTimeRun)
			{
				time.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) + 1);
			}
			time.set(Calendar.MINUTE, _newCycleMin);
			_log.info("FourSepulchersManager: Entry time: " + time.getTime());
			if (_firstTimeRun)
			{
				_firstTimeRun = false; // cooldown phase ends event hour, so it will be not first run
			}

			long interval = time.getTimeInMillis() - System.currentTimeMillis();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeEntryTime(), interval);

			if (_changeCoolDownTimeTask != null)
			{
				_changeCoolDownTimeTask.cancel(false);
				_changeCoolDownTimeTask = null;
			}
		}
	}

	public static GateKeeper getHallGateKeeper(int npcId)
	{
		for (GateKeeper gk : FourSepulchersSpawn._GateKeepers)
		{
			if (gk.template.npcId == npcId)
			{
				return gk;
			}
		}
		return null;
	}

	public static void showHtmlFile(Player player, String file, NpcInstance npc, Player member)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile("SepulcherNpc/" + file);
		if (member != null)
		{
			html.replace("%member%", member.getName());
		}
		player.sendPacket(html);
	}

	private static boolean isPlayersAnnihilated()
	{
		for (Player pc : getPlayersInside())
		{
			if (!pc.isDead())
			{
				return false;
			}
		}
		return true;
	}

	private static List<Player> getPlayersInside()
	{
		List<Player> result = new ArrayList<Player>();
		for (Zone zone : getZones())
		{
			result.addAll(zone.getInsidePlayers());
		}
		return result;
	}

	private static boolean checkIfInZone(Creature cha)
	{
		for (Zone zone : getZones())
		{
			if (zone.checkIfInZone(cha))
			{
				return true;
			}
		}
		return false;
	}

	public static Zone[] getZones()
	{
		return _zone;
	}

	@Override
	public void onLoad()
	{
		init();
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
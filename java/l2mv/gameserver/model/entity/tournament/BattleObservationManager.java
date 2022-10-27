package l2mv.gameserver.model.entity.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.network.serverpackets.ExCubeGameAddPlayer;
import l2mv.gameserver.network.serverpackets.ExCubeGameChangePoints;
import l2mv.gameserver.network.serverpackets.ExCubeGameEnd;
import l2mv.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import l2mv.gameserver.network.serverpackets.ExPartyPetWindowAdd;
import l2mv.gameserver.network.serverpackets.ExPartyPetWindowDelete;
import l2mv.gameserver.network.serverpackets.PartySmallWindowAll;
import l2mv.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.utils.Debug;
import l2mv.gameserver.utils.Location;

public final class BattleObservationManager
{
	private final ScheduledFuture<?> showScoreThread;

	private BattleObservationManager()
	{
		if (ConfigHolder.getBool("TournamentObserversShowScore"))
		{
			final long delay = ConfigHolder.getLong("TournamentObserversScorePacketDelay");
			showScoreThread = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new ShowScoresThread(), delay, delay);
		}
		else
		{
			showScoreThread = null;
		}
	}

	public static void initialize()
	{
		getInstance();
	}

	public static boolean tryObserveBattle(BattleRecord battle, Player player)
	{
		final BattleInstance battleInstance = battle.getBattleInstance();
		if (battleInstance == null)
		{
			player.sendCustomMessage("Tournament.TryObserveFail.BattleOver", new Object[0]);
			return false;
		}
		if (!canObserveBattle(battleInstance, player, true))
		{
			return false;
		}
		final Location loc = Location.findAroundPosition(battleInstance.getMap().getObserversLocation(), ConfigHolder.getInt("TournamentObserversRadius"), player.getGeoIndex());
		if (player.enterObserverMode(loc, battleInstance.getReflection()))
		{
			onStartObservation(battleInstance, player);
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "tryObserveBattle", battle, battleInstance, player, loc);
			}
			return true;
		}
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "tryObserveBattle", "Failed", battle, battleInstance, player, loc);
		}
		return false;
	}

	private static void onStartObservation(BattleInstance battleInstance, Player player)
	{
		battleInstance.addObserver(player);
		player.addListener(battleInstance.getObservationEndListener());
		if (ConfigHolder.getBool("TournamentObserversSeeFightersStatus"))
		{
			player.leaveParty();
			showFightersStatus(battleInstance, player);
		}
		final String msgSender = StringHolder.getNotNull(player, "Tournament.MsgToObserver.Sender", new Object[0]);
		player.sendPacket(new Say2(0, ChatType.ALL, msgSender, StringHolder.getNotNull(player, "Tournament.MsgToObserver.FullDetails", new Object[0])));
		player.sendPacket(new Say2(0, ChatType.ALL, msgSender, StringHolder.getNotNull(player, "Tournament.MsgToObserver.ChatInfo", new Object[0])));
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onStartObservation", battleInstance, player, msgSender);
		}
	}

	public static void onLeaveObservation(BattleInstance battle, Player player)
	{
		battle.removeObserver(player);
		battle.addPastObserver(player);
		player.removeListener(battle.getObservationEndListener());
		hideFightersStatus(player);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "onLeaveObservation", battle, player);
		}
	}

	public static void teleportBackObservers(BattleInstance battle)
	{
		for (Player observer : battle.getObserversCopy(false))
		{
			if (observer.isOnline())
			{
				observer.leaveObserverMode();
			}
			else
			{
				onLeaveObservation(battle, observer);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "teleportBackObservers", battle, observer, observer.isOnline());
			}
		}
	}

	private static boolean canObserveBattle(BattleInstance battle, Player player, boolean sendErrorMsg)
	{
		if (!ConfigHolder.getBool("TournamentAllowObserve"))
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.Disabled", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", battle, player, sendErrorMsg, "TournamentAllowObserve");
			}
			return false;
		}
		if (battle.getObserversForIterate().contains(player) || battle.getAllFightersForIterate().contains(player) || player.isInObserverMode())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.AlreadyObserving", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", battle, player, sendErrorMsg, battle.getObserversForIterate().contains(player), battle.getAllFightersForIterate().contains(player), player.isInObserverMode());
			}
			return false;
		}
		if (battle.isBattleOver())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.BattleOver", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "isBattleOver", battle, player, sendErrorMsg);
			}
			return false;
		}
		if (player.isBlocked())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.Blocked", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "isBlocked", battle, player, sendErrorMsg);
			}
			return false;
		}
		if (player.isInJail())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.Jailed", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "isInJail", battle, player, sendErrorMsg);
			}
			return false;
		}
		if (Olympiad.isRegistered(player) || player.isInOlympiadMode())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.Olympiad", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "Olympiad", battle, player, sendErrorMsg, Olympiad.isRegistered(player), player.isInOlympiadMode());
			}
			return false;
		}
		if (player.isInFightClub() || player.isRegisteredInFightClub())
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.FightClub", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "FightClub", battle, player, sendErrorMsg, player.isInFightClub(), player.isRegisteredInFightClub());
			}
			return false;
		}
		if (!player.getReflection().equals(ReflectionManager.DEFAULT))
		{
			if (sendErrorMsg)
			{
				player.sendCustomMessage("Tournament.TryObserveFail.AnotherInstance", new Object[0]);
			}
			if (Debug.TOURNAMENT.isActive())
			{
				Debug.TOURNAMENT.debug(ActiveBattleManager.class, "canObserveBattle", "Reflection", battle, player, sendErrorMsg, player.getReflection());
			}
			return false;
		}
		return true;
	}

	public static void broadcastFighterStatusUpdate(BattleInstance battleInstance, Playable fighter)
	{
		final IStaticPacket packet = fighter.getPartyStatusUpdatePacket();
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(packet);
		}
	}

	public static void broadcastObserversSpawnedSummon(BattleInstance battleInstance, Summon summon)
	{
		final IStaticPacket packet = new ExPartyPetWindowAdd(summon);
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(packet);
		}
	}

	public static void broadcastObserversUnspawnedSummon(BattleInstance battleInstance, Summon summon)
	{
		final IStaticPacket packet = new ExPartyPetWindowDelete(summon);
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(packet);
		}
	}

	private static void showFightersStatus(BattleInstance battle, Player toPlayer)
	{
		final List<PartySmallWindowAll.PartySmallWindowMemberInfo> list = new ArrayList<PartySmallWindowAll.PartySmallWindowMemberInfo>(4);
		list.add(new PartySmallWindowAll.PartySmallWindowMemberInfo("Team 1: ", 100, 0, 0, 0, false));
		list.add(new PartySmallWindowAll.PartySmallWindowMemberInfo("Vampir", 100, 0, 0, 0, true));
		list.add(new PartySmallWindowAll.PartySmallWindowMemberInfo("Team 2: ", 100, 0, 0, 0, false));
		list.add(new PartySmallWindowAll.PartySmallWindowMemberInfo("Tester", 100, 0, 0, 0, true));
		final Team[] teams = battle.getBattleRecord().getTeams();
		final List<PartySmallWindowAll.PartySmallWindowMemberInfo> members = new ArrayList<PartySmallWindowAll.PartySmallWindowMemberInfo>(teams.length + teams.length * ConfigHolder.getInt("TournamentPlayersInTeam"));
		for (Team team : teams)
		{
			final int teamIndex = battle.getBattleRecord().getTeamIndex(team);
			members.add(new PartySmallWindowAll.PartySmallWindowMemberInfo("Team " + (teamIndex + 1), 100, 1, 0, 0, false));
			for (Player fighter : battle.getFightersForIterate(team))
			{
				members.add(new PartySmallWindowAll.PartySmallWindowMemberInfo(fighter));
			}
		}
		toPlayer.sendPacket(new PartySmallWindowAll(0, 0, members));
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "showFightersStatus", battle, toPlayer);
		}
	}

	private static void hideFightersStatus(Player toPlayer)
	{
		toPlayer.sendPacket(PartySmallWindowDeleteAll.STATIC);
		if (Debug.TOURNAMENT.isActive())
		{
			Debug.TOURNAMENT.debug(ActiveBattleManager.class, "hideFightersStatus", toPlayer);
		}
	}

	public static void onBattleOver(BattleInstance battleInstance)
	{
		showEndBattleScore(battleInstance);
	}

	private static void showScores(BattleInstance battleInstance)
	{
		final IStaticPacket packet = new ExCubeGameChangePoints(0, battleInstance.getTeam2Wins(), battleInstance.getTeam1Wins());
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(packet);
		}
		for (Player pastObserver : battleInstance.getPastObserversForIterate())
		{
			pastObserver.sendPacket(packet);
		}
	}

	private static void showEndBattleScore(BattleInstance battleInstance)
	{
		showScores(battleInstance);
		final BattleRecord record = battleInstance.getBattleRecord();
		final boolean redTeamWon = record.getWinnerId() == record.getTeam1Id();
		final IStaticPacket resultPacket = new ExCubeGameEnd(redTeamWon);
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(resultPacket);
		}
		for (Player pastObserver : battleInstance.getPastObserversForIterate())
		{
			pastObserver.sendPacket(resultPacket);
		}
	}

	private static void addFightersToEndScore(BattleInstance battleInstance)
	{
		final BattleRecord record = battleInstance.getBattleRecord();
		final List<Player> redTeamFighters = battleInstance.getFightersForIterate(record.getTeam1());
		final List<Player> blueTeamFighters = battleInstance.getFightersForIterate(record.getTeam2());
		final List<IStaticPacket> playersInEndScorePackets = new ArrayList<IStaticPacket>(redTeamFighters.size() + blueTeamFighters.size());
		for (Player redTeamFighter : redTeamFighters)
		{
			playersInEndScorePackets.add(new ExCubeGameAddPlayer(redTeamFighter, true));
			playersInEndScorePackets.add(new ExCubeGameExtendedChangePoints(0, battleInstance.getTeam2Wins(), battleInstance.getTeam1Wins(), true, redTeamFighter, battleInstance.getTeam1Wins()));
		}
		for (Player blueTeamFighter : blueTeamFighters)
		{
			playersInEndScorePackets.add(new ExCubeGameAddPlayer(blueTeamFighter, false));
			playersInEndScorePackets.add(new ExCubeGameExtendedChangePoints(0, battleInstance.getTeam1Wins(), battleInstance.getTeam1Wins(), false, blueTeamFighter, battleInstance.getTeam2Wins()));
		}
		for (Player observer : battleInstance.getObserversForIterate())
		{
			observer.sendPacket(playersInEndScorePackets);
		}
		for (Player pastObserver : battleInstance.getPastObserversForIterate())
		{
			pastObserver.sendPacket(playersInEndScorePackets);
		}
	}

	private static BattleObservationManager getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final BattleObservationManager instance = new BattleObservationManager();
	}

	private static class ShowScoresThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (ConfigHolder.getBool("TournamentObserversShowScore"))
			{
				for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate())
				{
					if (record.isNowLive() && !record.isPastBattle())
					{
						showScores(record.getBattleInstance());
					}
				}
			}
		}
	}
}

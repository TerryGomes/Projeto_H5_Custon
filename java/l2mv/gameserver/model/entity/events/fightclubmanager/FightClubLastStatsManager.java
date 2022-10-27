package l2mv.gameserver.model.entity.events.fightclubmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;

public class FightClubLastStatsManager
{
	public static enum FightClubStatType
	{
		KILL_PLAYER("Kill Player");

		private final String _name;

		private FightClubStatType(String name)
		{
			_name = name;
		}

		public String getName()
		{
			return _name;
		}
	}

	private final List<FightClubLastPlayerStats> _allStats = new CopyOnWriteArrayList<>();

	public void updateStat(Player player, FightClubStatType type, int score)
	{
		FightClubLastPlayerStats myStat = getMyStat(player);

		if (myStat == null)
		{
			myStat = new FightClubLastPlayerStats(player, type.getName(), score);
			_allStats.add(myStat);
		}
		else
		{
			myStat.setScore(score);
		}
	}

	public FightClubLastPlayerStats getMyStat(Player player)
	{
		for (FightClubLastPlayerStats stat : _allStats)
		{
			if (stat.isMyStat(player))
			{
				return stat;
			}
		}

		return null;
	}

	public List<FightClubLastPlayerStats> getStats(boolean sortByScore)
	{
		List<FightClubLastPlayerStats> listToSort = new ArrayList<>();
		listToSort.addAll(_allStats);
		if (sortByScore)
		{
			Comparator<FightClubLastPlayerStats> statsComparator = new SortRanking();
			Collections.sort(listToSort, statsComparator);
		}

		return listToSort;
	}

	public void onAllEventInstancesOver(AbstractFightClub lastEvent)
	{
		if (Config.FIGHT_CLUB_ANNOUNCE_TOP_KILLER)
		{
			List<FightClubLastPlayerStats> stats = getStats(true);
			if (!stats.isEmpty())
			{
				FightClubLastPlayerStats bestStat = stats.get(0);

				Say2 packet = new Say2(0, ChatType.CRITICAL_ANNOUNCE, lastEvent.getShortName(), bestStat.getPlayerName() + " had most kills in " + lastEvent.getName() + " Event!");
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					player.sendPacket(packet);
				}
			}
		}
	}

	public void clearStats()
	{
		_allStats.clear();
	}

	private static class SortRanking implements Comparator<FightClubLastPlayerStats>, Serializable
	{
		private static final long serialVersionUID = 7691414259610932752L;

		@Override
		public int compare(FightClubLastPlayerStats o1, FightClubLastPlayerStats o2)
		{
			return Integer.compare(o2.getScore(), o1.getScore());
		}
	}

	private static class FightClubLastStatsManagerHolder
	{
		private static final FightClubLastStatsManager _instance = new FightClubLastStatsManager();
	}

	public static FightClubLastStatsManager getInstance()
	{
		return FightClubLastStatsManagerHolder._instance;
	}
}

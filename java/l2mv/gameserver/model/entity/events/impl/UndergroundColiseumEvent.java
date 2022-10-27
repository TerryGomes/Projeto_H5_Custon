package l2mv.gameserver.model.entity.events.impl;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import l2mv.commons.collections.MultiValueSet;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.UndergroundColiseumHistoryDAO;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.utils.Util;

/**
 * @author VISTALL
 * @date 0:46/16.06.2011
 */
public class UndergroundColiseumEvent extends GlobalEvent
{
	private class Timer extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			List<Player> leaders = getObjects(REGISTERED_LEADERS);

			Player leader1 = Util.safeGet(leaders, 0);
			Player leader2 = Util.safeGet(leaders, 1);

			if (leader1 == null || leader2 == null)
			{
				return;
			}

			if (!isValid(leader1, leaders) || !isValid(leader2, leaders))
			{
				ExShowScreenMessage p = new ExShowScreenMessage(NpcString.THE_MATCH_IS_AUTOMATICALLY_CANCELED_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_ADMISSION_MANAGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER);

				broadCast(leader1, p);
				broadCast(leader2, p);
				return;
			}

			UndergroundColiseumBattleEvent battleEvent = new UndergroundColiseumBattleEvent(UndergroundColiseumEvent.this, leader1, leader2);
			battleEvent.reCalcNextTime(false);
		}

		private void broadCast(Player player, IStaticPacket packet)
		{
			if (player.getParty() == null)
			{
				player.sendPacket(packet);
			}
			else
			{
				player.getParty().sendPacket(packet);
			}
		}
	}

	// private static final SchedulingPattern DATE_PATTERN = new SchedulingPattern("0 21 * * fri,sat,sun");

	public static final String REGISTERED_LEADERS = "registered_leaders";
	public static final String MANAGER = "manager";
	public static final String DOORS = "doors";
	public static final String TOWERS = "towers";
	public static final String ZONES = "zones";
	public static final String BOXES = "boxes";
	public static final String BLUE_TELEPORT_LOCS = "blue_teleport_locs";
	public static final String RED_TELEPORT_LOCS = "red_teleport_locs";
	public static final String HISTORY = "history";

	public static final int REGISTER_COUNT = 5;
	public static final int PARTY_SIZE = Config.UNDERGROUND_COLISEUM_MEMBER_COUNT;
	private long _startTime;

	private final int _minLevel;
	private final int _maxLevel;
	private final int _circleStart;

	private Future<?> _timerTask;

	public UndergroundColiseumEvent(MultiValueSet<String> set)
	{
		super(set);
		_minLevel = set.getInteger("min_level");
		_maxLevel = set.getInteger("max_level");
		_circleStart = set.getInteger("circle_count", Calendar.FRIDAY);
	}

	@Override
	public void initEvent()
	{
		if (Config.DEV_UNDERGROUND_COLISEUM)
		{
			super.initEvent();

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startTimeMillis());

			if (calendar.get(Calendar.DAY_OF_WEEK) == _circleStart)
			{
				UndergroundColiseumHistoryDAO.getInstance().delete(getId());
			}
			else
			{
				addObjects(HISTORY, UndergroundColiseumHistoryDAO.getInstance().select(getId()));
			}
		}
	}

	public void register(Player player)
	{
		if (!isValid(player, Collections.<Player>emptyList()))
		{
			return;
		}

		List<Player> leaders = getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);
		if (leaders.size() >= REGISTER_COUNT)
		{
			return;
		}

		leaders.add(player);
	}

	public void addToHistory(final String name)
	{
		List<Pair<String, Integer>> history = getObjects(UndergroundColiseumEvent.HISTORY);

		Pair<String, Integer> winner = null;
		for (Pair<String, Integer> pair : history)
		{
			if (pair.getKey().equals(name))
			{
				winner = pair;
				break;
			}
		}

		if (winner == null)
		{
			winner = new MutablePair<String, Integer>(name, 1);
			addObject(UndergroundColiseumEvent.HISTORY, winner);

			UndergroundColiseumHistoryDAO.getInstance().insert(getId(), winner);
		}
		else
		{
			winner.setValue(winner.getValue() + 1);

			UndergroundColiseumHistoryDAO.getInstance().update(getId(), winner);
		}
	}

	public Pair<String, Integer> getTopWinner()
	{
		List<Pair<String, Integer>> history = getObjects(UndergroundColiseumEvent.HISTORY);

		int max = Integer.MIN_VALUE;
		Pair<String, Integer> pair = null;
		for (Pair<String, Integer> temp : history)
		{
			if (temp.getValue() > max)
			{
				pair = temp;
				max = pair.getValue();
			}
		}

		return pair;
	}

	@Override
	public void startEvent()
	{
		startTimer();
	}

	@Override
	public void stopEvent()
	{
		removeObjects(REGISTERED_LEADERS);

		stopTimer();
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		// _startTime = DATE_PATTERN.next(System.currentTimeMillis());
		_startTime = System.currentTimeMillis();

		registerActions();
	}

	@Override
	protected long startTimeMillis()
	{
		return _startTime;
	}

	@Override
	public boolean isInProgress()
	{
		return true;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public void startTimer()
	{
		_timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Timer(), 5000L, 5000L);
	}

	public void stopTimer()
	{
		if (_timerTask != null)
		{
			_timerTask.cancel(false);
			_timerTask = null;
		}
	}

	public boolean isValid(final Player leaderPlayer, final List<Player> players)
	{
		if (leaderPlayer == null)
		{
			return false;
		}

		boolean fail = false;
		Party party = leaderPlayer.getParty();
		if (party == null || party.size() < PARTY_SIZE)
		{
			fail = true;
		}
		else
		{
			for (Player member : party)
			{
				if (member.getLevel() < _minLevel || member.getLevel() > _maxLevel)
				{
					fail = true;
					break;
				}
			}
		}

		if (fail)
		{
			players.remove(leaderPlayer);
		}

		return true;
	}
}

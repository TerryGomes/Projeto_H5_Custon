package l2mv.gameserver.model.entity.tournament;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Language;

public class StartFightThread extends RunnableImpl
{
	private static final long[] STOP_SECONDS_FOR_MSG = new long[]
	{
		300L,
		240L,
		180L,
		120L,
		60L,
		30L,
		15L,
		0L
	};

	private final long _remainingTimeSeconds;
	private final BattleInstance _battle;
	private final boolean _announce;

	private StartFightThread(long remainingTimeSeconds, BattleInstance battle, boolean announce)
	{
		_remainingTimeSeconds = remainingTimeSeconds;
		_battle = battle;
		_announce = announce;
	}

	@Override
	public void runImpl()
	{
		final long secondsToAwait = getAwaitTimeSeconds();
		if (secondsToAwait <= 0L)
		{
			ActiveBattleManager.onStartFightThreadOver(_battle);
		}
		else
		{
			if (_announce)
			{
				Map<Language, String> msgToShow;
				if (_remainingTimeSeconds > 60L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightStartDate.MoreThanMinute", TimeUnit.SECONDS.toMinutes(_remainingTimeSeconds));
				}
				else if (_remainingTimeSeconds == 60L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightStartDate.Minute", new Object[0]);
				}
				else if (_remainingTimeSeconds > 1L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightStartDate.MoreThanSecond", _remainingTimeSeconds);
				}
				else
				{
					if (_remainingTimeSeconds != 1L)
					{
						throw new AssertionError("In " + StartFightThread.class.getSimpleName() + " remainingTimeSeconds == " + _remainingTimeSeconds);
					}
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightStartDate.Second", new Object[0]);
				}
				ActiveBattleManager.showScreenMsgAll(_battle, msgToShow);
			}
			final ScheduledFuture<?> thread = ThreadPoolManager.getInstance().schedule(new StartFightThread(_remainingTimeSeconds - secondsToAwait, _battle, true), TimeUnit.SECONDS.toMillis(secondsToAwait));
			_battle.setStartFightThread(thread);
		}
	}

	private long getAwaitTimeSeconds()
	{
		for (long stopTime : StartFightThread.STOP_SECONDS_FOR_MSG)
		{
			if (stopTime < _remainingTimeSeconds)
			{
				return _remainingTimeSeconds - stopTime;
			}
		}
		return 0L;
	}

	public static void scheduleStartFight(BattleInstance battle)
	{
		final boolean firstFight = battle.getFightIndex() == 0;
		long secondsToStart;
		if (firstFight)
		{
			secondsToStart = ConfigHolder.getLong("TournamentFirstFightPreparation");
		}
		else
		{
			secondsToStart = ConfigHolder.getLong("TournamentNextFightsPreparation");
		}
		ThreadPoolManager.getInstance().execute(new StartFightThread(secondsToStart, battle, false));
	}
}

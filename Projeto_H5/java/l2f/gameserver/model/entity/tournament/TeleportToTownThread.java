package l2f.gameserver.model.entity.tournament;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.utils.ChatUtil;
import l2f.gameserver.utils.Language;

public class TeleportToTownThread extends RunnableImpl
{
	private static final long[] STOP_SECONDS_FOR_MSG = new long[]
	{
		180L,
		120L,
		60L,
		30L,
		15L,
		10L,
		5L,
		0L
	};

	private final long _remainingTimeSeconds;
	private final BattleInstance _battle;

	private TeleportToTownThread(long remainingTimeSeconds, BattleInstance battle)
	{
		_remainingTimeSeconds = remainingTimeSeconds;
		_battle = battle;
	}

	@Override
	public void runImpl()
	{
		final long secondsToAwait = getAwaitTimeSeconds();
		if (secondsToAwait <= 0L)
		{
			ActiveBattleManager.teleportBackToTown(_battle);
			BattleObservationManager.teleportBackObservers(_battle);
			ActiveBattleManager.cleanBattleRecord(_battle.getBattleRecord());
			BattleScheduleManager.getInstance().checkRoundOver();
		}
		else
		{
			Map<Language, String> msgToShow;
			if (_remainingTimeSeconds > 60L)
			{
				msgToShow = ChatUtil.getMessagePerLang("Tournament.TeleportToTownDate.MoreThanMinute", TimeUnit.SECONDS.toMinutes(_remainingTimeSeconds));
			}
			else if (_remainingTimeSeconds == 60L)
			{
				msgToShow = ChatUtil.getMessagePerLang("Tournament.TeleportToTownDate.Minute", new Object[0]);
			}
			else if (_remainingTimeSeconds > 1L)
			{
				msgToShow = ChatUtil.getMessagePerLang("Tournament.TeleportToTownDate.MoreThanSecond", _remainingTimeSeconds);
			}
			else
			{
				if (_remainingTimeSeconds != 1L)
				{
					throw new AssertionError("In " + StartFightThread.class.getSimpleName() + " remainingTimeSeconds == " + _remainingTimeSeconds);
				}
				msgToShow = ChatUtil.getMessagePerLang("Tournament.TeleportToTownDate.Second", new Object[0]);
			}
			ActiveBattleManager.showSystemMsgAll(_battle, msgToShow);
			ThreadPoolManager.getInstance().schedule(new TeleportToTownThread(_remainingTimeSeconds - secondsToAwait, _battle), TimeUnit.SECONDS.toMillis(secondsToAwait));
		}
	}

	private long getAwaitTimeSeconds()
	{
		for (long stopTime : TeleportToTownThread.STOP_SECONDS_FOR_MSG)
		{
			if (stopTime < _remainingTimeSeconds)
			{
				return _remainingTimeSeconds - stopTime;
			}
		}
		return 0L;
	}

	public static void scheduleTeleportToTown(BattleInstance battle)
	{
		ThreadPoolManager.getInstance().execute(new TeleportToTownThread(ConfigHolder.getLong("TournamentBackSeconds"), battle));
	}
}

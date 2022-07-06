package l2mv.gameserver.model.entity.tournament;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Language;

public class EndFightByTimeThread extends RunnableImpl
{
	private final long _remainingTimeSeconds;
	private final BattleInstance _battle;
	private final boolean _announce;

	private EndFightByTimeThread(long remainingTimeSeconds, BattleInstance battle, boolean announce)
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
			ActiveBattleManager.endFight(_battle, ActiveBattleManager.FightEndType.TIME);
		}
		else
		{
			if (_announce)
			{
				Map<Language, String> msgToShow;
				if (_remainingTimeSeconds > 60L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightEndByTimeDate.MoreThanMinute", TimeUnit.SECONDS.toMinutes(_remainingTimeSeconds));
				}
				else if (_remainingTimeSeconds == 60L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightEndByTimeDate.Minute", new Object[0]);
				}
				else if (_remainingTimeSeconds > 1L)
				{
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightEndByTimeDate.MoreThanSecond", _remainingTimeSeconds);
				}
				else
				{
					if (_remainingTimeSeconds != 1L)
					{
						throw new AssertionError("In " + EndFightByTimeThread.class.getSimpleName() + " remainingTimeSeconds == " + _remainingTimeSeconds);
					}
					msgToShow = ChatUtil.getMessagePerLang("Tournament.FightEndByTimeDate.Second", new Object[0]);
				}
				ActiveBattleManager.showScreenMsgAll(_battle, msgToShow);
			}
			final ScheduledFuture<?> thread = ThreadPoolManager.getInstance().schedule(new EndFightByTimeThread(_remainingTimeSeconds - secondsToAwait, _battle, true), TimeUnit.SECONDS.toMillis(secondsToAwait));
			_battle.setStopFightThread(thread);
		}
	}

	private long getAwaitTimeSeconds()
	{
		for (long stopTime : ConfigHolder.getLongArray("TournamentFightEndByTimeAnnounceSeconds"))
		{
			if (stopTime < _remainingTimeSeconds)
			{
				return _remainingTimeSeconds - stopTime;
			}
		}
		return 0L;
	}

	public static Map<Language, String> getWonFightMessageToShow(Team winnerTeam)
	{
		final List<Player> onlinePlayers = winnerTeam.getOnlinePlayers();
		final String[] playerNicknames = new String[onlinePlayers.size()];
		for (int i = 0; i < playerNicknames.length; ++i)
		{
			playerNicknames[i] = onlinePlayers.get(i).getName();
		}
		switch (onlinePlayers.size())
		{
		case 1:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByTime9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Fight.ByTime with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}

	public static void scheduleBattleEnd(BattleInstance battle)
	{
		final long secondsToEnd = ConfigHolder.getLong("TournamentMaxFightTimeForResult");
		ThreadPoolManager.getInstance().execute(new EndFightByTimeThread(secondsToEnd, battle, false));
	}
}

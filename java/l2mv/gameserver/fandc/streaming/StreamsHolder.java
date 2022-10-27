package l2mv.gameserver.fandc.streaming;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.listener.game.OnConfigsReloaded;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.Log;

public class StreamsHolder implements OnConfigsReloaded
{
	private final List<Stream> _allActiveStreams = new ArrayList<Stream>();
	private final Object _streamsListLock = new Object();
	private long _nextTotalRewardTimeClearDate = -1L;
	private int _minRequiredViewers = -1;

	private StreamsHolder()
	{
		calculateMinRequiredViewers();
		GameServer.getInstance().addListener(this);
	}

	public void onStreamActive(String channelName, String gameName, String streamTitle, int viewersCount, long currentDate, long delayBetweenChecks)
	{
		checkClearTotalRewardedTime();
		synchronized (_streamsListLock)
		{
			for (Stream stream : _allActiveStreams)
			{
				if (stream.getChannelName().equals(channelName))
				{
					if (!stream.getStreamGameName().equals(gameName))
					{
						stream.setStreamGameName(gameName, isGameNameCorrect(gameName));
					}
					if (!stream.getStreamTitle().equals(streamTitle))
					{
						stream.setStreamTitle(streamTitle, isTitleCorrect(streamTitle));
					}
					stream.setViewersCount(viewersCount);

					if (stream.isNowPunished(currentDate))
					{
						Log.logStream("Unable to increase reward for Stream: " + stream + "! Punished until date: " + stream.getPunishedUntilDate());
					}
					else if (isStreamActive(stream))
					{
						stream.setLastActiveDate(currentDate);
						final Player activeStreamPlayer = stream.getStreamingPlayer();
						if (activeStreamPlayer != null && !isPlayerActive(activeStreamPlayer, currentDate))
						{
							Log.logStream("Streaming Player: " + activeStreamPlayer.toString() + " is AFK Streaming: " + stream);
						}
						else if (activeStreamPlayer != null && activeStreamPlayer.isOnline() || ConfigHolder.getBool("StreamIncreaseRewardWhileOffline"))
						{
							final long actualRewardedSeconds = stream.incNotRewardedSeconds(TimeUnit.MILLISECONDS.toSeconds(delayBetweenChecks), true, true);
							if (actualRewardedSeconds > 0L)
							{
								Log.logStream("Increasing reward for Stream: " + stream + " by " + actualRewardedSeconds + " seconds!");
							}
							else
							{
								Log.logStream("Unable to increase reward for Stream: " + stream + "! Streamer reached MAX time!");
							}
						}
						else
						{
							Log.logStream("Unable to increase reward for Stream: " + stream + "! Player is offline/afk!");
						}
					}
					else
					{
						Log.logStream("Unable to increase reward for Stream: " + stream + "! It isn't active!");
					}
					return;
				}
			}

			final Stream stream2 = new Stream(channelName, gameName, isGameNameCorrect(gameName), streamTitle, isTitleCorrect(streamTitle), viewersCount, currentDate);
			if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
			{
				StreamDatabaseHandler.onStreamCreated(stream2);
			}

			Log.logStream("New Stream has been found: " + stream2 + "!");
			_allActiveStreams.add(stream2);
		}
	}

	public List<Stream> getAllActiveStreamsCopy()
	{
		synchronized (_streamsListLock)
		{
			return new ArrayList<Stream>(_allActiveStreams);
		}
	}

	@Nullable
	public Stream getMyStream(Player player)
	{
		final int playerObjectId = player.getObjectId();
		synchronized (_streamsListLock)
		{
			for (Stream stream : _allActiveStreams)
			{
				if (stream.getAttachedPlayerId() == playerObjectId)
				{
					return stream;
				}
			}
		}
		return null;
	}

	public boolean isAwaitingForApproval(Player player)
	{
		final int playerObjectId = player.getObjectId();
		synchronized (_streamsListLock)
		{
			for (Stream stream : _allActiveStreams)
			{
				if (stream.getIdsToApprove().contains(playerObjectId))
				{
					return true;
				}
			}
		}
		return false;
	}

	public Stream getStreamByChannelName(String channelName)
	{
		synchronized (_streamsListLock)
		{
			for (Stream stream : _allActiveStreams)
			{
				if (stream.getChannelName().equalsIgnoreCase(channelName))
				{
					return stream;
				}
			}
		}
		return null;
	}

	protected void checkClearTotalRewardedTime()
	{
		final long currentDate = System.currentTimeMillis();
		if (_nextTotalRewardTimeClearDate <= 0L)
		{
			setupTotalRewardClearTimer(currentDate);
		}
		else if (_nextTotalRewardTimeClearDate < currentDate)
		{
			clearTotalRewardedTimes();
			setupTotalRewardClearTimer(currentDate);
		}
	}

	private void clearTotalRewardedTimes()
	{
		for (Stream stream : _allActiveStreams)
		{
			stream.setTotalRewardedSecondsToday(0L);
		}
		StreamDatabaseHandler.resetTotalRewardedTimes();
	}

	public static boolean isPlayerActive(Player player)
	{
		return isPlayerActive(player, System.currentTimeMillis());
	}

	public static boolean isPlayerActive(Player player, long currentTime)
	{
		return ConfigHolder.getBool("AllowStreamingAFKSystem") && player.getLastNotAfkTime() + TimeUnit.SECONDS.toMillis(ConfigHolder.getLong("StreamingAFKSystemSecondsToAFK")) >= currentTime;
	}

	public boolean isStreamActive(Stream stream)
	{
		return stream.isStreamGameNameCorrect() && stream.isTitleCorrect() && stream.getViewersCount() >= _minRequiredViewers;
	}

	public static boolean isGameNameCorrect(String gameName)
	{
		final String correctGameName = ConfigHolder.getString("TwitchCorrectStreamGame");
		return correctGameName.isEmpty() || gameName.equals(correctGameName);
	}

	public static boolean isTitleCorrect(CharSequence streamTitle)
	{
		final String[][] possibilities = ConfigHolder.getMultiStringArray("TwitchCorrectStreamTitlesContainArray");
		if (possibilities.length == 0)
		{
			return true;
		}

		for (String[] stringsToContain : ConfigHolder.getMultiStringArray("TwitchCorrectStreamTitlesContainArray"))
		{
			if (containsIgnoreCaseAll(streamTitle, stringsToContain))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean containsIgnoreCaseAll(CharSequence text, String... stringsToContain)
	{
		for (String stringToContain : stringsToContain)
		{
			if (!StringUtils.containsIgnoreCase(text, stringToContain))
			{
				return false;
			}
		}
		return true;
	}

	private void setupTotalRewardClearTimer(long currentDate)
	{
		final Calendar c = Calendar.getInstance();
		c.set(11, ConfigHolder.getInt("StreamingTotalRewardedTimeClearHour"));
		c.set(12, 0);
		c.set(13, 0);
		while (c.getTimeInMillis() < currentDate)
		{
			c.add(6, 1);
			Log.logStream("Adding 1 Day to Total Reward Clear Time!");
		}
		_nextTotalRewardTimeClearDate = c.getTimeInMillis();
	}

	public int getMinRequiredViewers()
	{
		return _minRequiredViewers;
	}

	@Override
	public void onConfigsReloaded()
	{
		calculateMinRequiredViewers();
	}

	private void calculateMinRequiredViewers()
	{
		int lowestValue = Integer.MAX_VALUE;
		for (StatsSet reward : ConfigHolder.getStatsSetList("StreamingRewards", "minViewers", Integer.class, "itemId", Integer.class, "itemCount", Long.class))
		{
			if (reward.getInteger("minViewers") < lowestValue)
			{
				lowestValue = reward.getInteger("minViewers");
			}
		}
		_minRequiredViewers = lowestValue;
	}

	public static StreamsHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static final class SingletonHolder
	{
		private static final StreamsHolder instance = new StreamsHolder();
	}
}

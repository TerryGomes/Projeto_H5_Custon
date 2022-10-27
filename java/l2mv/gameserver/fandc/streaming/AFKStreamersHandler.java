package l2mv.gameserver.fandc.streaming;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.Say2;

public class AFKStreamersHandler
{
	private static final long MIN_CHECK_DELAY = 1000L;

	private final Map<Stream, Long> lastAFKMessages = new HashMap<Stream, Long>();

	public AFKStreamersHandler()
	{
		final long delay = Math.max(TimeUnit.SECONDS.toMillis(ConfigHolder.getLong("StreamingAFKSystemDelayBetweenMsgs")), MIN_CHECK_DELAY);
		ThreadPoolManager.getInstance().scheduleAtFixedDelay(new CheckAFKStreamers(this), delay, delay);
	}

	private Map<Stream, Long> getLastAFKMessages()
	{
		return lastAFKMessages;
	}

	public static AFKStreamersHandler getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final AFKStreamersHandler instance = new AFKStreamersHandler();
	}

	private static class CheckAFKStreamers extends RunnableImpl
	{
		private final AFKStreamersHandler _handler;

		private CheckAFKStreamers(AFKStreamersHandler handler)
		{
			_handler = handler;
		}

		@Override
		public void runImpl()
		{
			if (!ConfigHolder.getBool("AllowStreamingAFKSystem") || ConfigHolder.getInt("StreamingAFKSystemDelayBetweenMsgs") <= 0)
			{
				return;
			}

			final long currentDate = System.currentTimeMillis();
			final long minDateToAfk = currentDate - TimeUnit.SECONDS.toMillis(ConfigHolder.getLong("StreamingAFKSystemSecondsToAFK"));
			for (Stream stream : StreamsHolder.getInstance().getAllActiveStreamsCopy())
			{
				final Player player = stream.getStreamingPlayer();
				if (player != null && StreamsHolder.getInstance().isStreamActive(stream) && player.getLastNotAfkTime() < minDateToAfk)
				{
					if (_handler.getLastAFKMessages().containsKey(stream))
					{
						if (_handler.getLastAFKMessages().get(stream) >= currentDate)
						{
							continue;
						}
						sendAFKMessage(player);
						_handler.getLastAFKMessages().put(stream, currentDate + ConfigHolder.getLong("StreamingAFKSystemDelayBetweenMsgs"));
					}
					else
					{
						sendAFKMessage(player);
						_handler.getLastAFKMessages().put(stream, currentDate + ConfigHolder.getLong("StreamingAFKSystemDelayBetweenMsgs"));
					}
				}
			}
		}

		private static void sendAFKMessage(Player player)
		{
			player.sendPacket(new Say2(0, ConfigHolder.getChatType("StreamingAFKSystemMsgType"), StringHolder.getNotNull(player, "Twitch.Stream", new Object[0]), StringHolder.getNotNull(player, "Twitch.AFKMessage", new Object[0])));
		}
	}
}

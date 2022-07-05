package l2f.gameserver.handler.admincommands.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import l2f.gameserver.fandc.streaming.Stream;
import l2f.gameserver.fandc.streaming.StreamDatabaseHandler;
import l2f.gameserver.fandc.streaming.StreamsHolder;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;

public class AdminStream implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_punish_stream, admin_active_streams
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_punish_stream:
		{
			final String channelName = wordList[1];
			final int hours = Integer.parseInt(wordList[2]);
			final Stream stream = StreamsHolder.getInstance().getStreamByChannelName(channelName);
			if (stream == null)
			{
				activeChar.sendMessage("Stream with such name was not found!");
				return false;
			}
			punishStream(stream, hours);
			break;
		}
		case admin_active_streams:
		{
			final List<Stream> activeStreams = StreamsHolder.getInstance().getAllActiveStreamsCopy();
			final long currentDate = System.currentTimeMillis();
			final boolean rewardWhilePlayerOffline = ConfigHolder.getBool("StreamIncreaseRewardWhileOffline");
			final long maxSecondsPerDay = ConfigHolder.getLong("StreamingMaxRewardedSecondsPerDay");
			for (Stream activeStream : activeStreams)
			{
				if (StreamsHolder.getInstance().isStreamActive(activeStream) && !activeStream.isNowPunished(currentDate) && activeStream.getTotalRewardedSecondsToday() < maxSecondsPerDay)
				{
					final Player activeStreamPlayer = activeStream.getStreamingPlayer();
					if ((activeStreamPlayer == null || activeStreamPlayer.isOnline() || !StreamsHolder.isPlayerActive(activeStreamPlayer, currentDate)) && !rewardWhilePlayerOffline)
					{
						continue;
					}
					if (activeStreamPlayer == null)
					{
						activeChar.sendMessage("Active Stream: " + activeStream.getChannelName());
					}
					else
					{
						activeChar.sendMessage("Active Stream: " + activeStream.getChannelName() + " Player: " + activeStreamPlayer.toString());
					}
				}
			}
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	private static void punishStream(Stream stream, long hours)
	{
		stream.setPunishedUntilDate(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hours));
		StreamDatabaseHandler.updateStream(stream);
		final Player activeStreamer = stream.getStreamingPlayer();
		if (activeStreamer != null)
		{
			activeStreamer.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, StringHolder.getNotNull(activeStreamer, "Twitch.Stream", new Object[0]), StringHolder.getNotNull(activeStreamer, "Twitch.PunishedForHours", new Object[]
			{
				hours
			})));
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}

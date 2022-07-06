/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.handler.voicecommands.impl;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.fandc.streaming.Stream;
import l2mv.gameserver.fandc.streaming.StreamDatabaseHandler;
import l2mv.gameserver.fandc.streaming.StreamsHolder;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

public class StreamVoice extends Functions implements IVoicedCommandHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(StreamVoice.class);

	private static final SimpleDateFormat END_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private static final String[] VOICED_COMMANDS =
	{
		"stream"
	};

	private boolean useStreamBypass(Player player, String bypass, Object... params)
	{
		return useVoicedCommand("stream", player, bypass + (params.length > 0 ? " " : "") + Util.joinArrayWithCharacter(params, " "));
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if (!ConfigHolder.getBool("AllowStreamPanel") || !ConfigHolder.getBool("AllowStreamingSystem"))
		{
			return false;
		}

		try
		{
			// Menu principal
			if (args == null || args.isEmpty())
			{
				args = "main";
			}

			final StringTokenizer st = new StringTokenizer(args);

			switch (st.nextToken())
			{
			case "main":
			{
				if (StreamsHolder.getInstance().getMyStream(activeChar) != null)
				{
					return useStreamBypass(activeChar, "mainStreamer");
				}

				if (ConfigHolder.getBool("StreamConnectionNeedsApproval") && StreamsHolder.getInstance().isAwaitingForApproval(activeChar))
				{
					return useStreamBypass(activeChar, "mainAwaitingApproval");
				}

				return useStreamBypass(activeChar, "mainNotStreamer");
			}
			case "mainStreamer":
			{
				checkReward(activeChar);
				showMainStreamerPage(activeChar);
				return true;
			}
			case "mainNotStreamer":
			{
				showMainNotStreamerPage(activeChar);
				return true;
			}
			case "mainAwaitingApproval":
			{
				showMainAwaitingApprovalPage(activeChar);
				return true;
			}
			case "tryConnectToStream":
			{
				final String streamName = st.nextToken();
				tryConnectToStream(activeChar, streamName);
				return true;
			}
			case "errorConnectToStream":
			{
				final String errorMsg = Util.getAllTokens(st);
				showErrorConnectToStreamPage(activeChar, errorMsg);
				return true;
			}
			case "transferStream":
			{
				showTransferStreamPage(activeChar);
				return true;
			}
			case "finalizeTransferStream":
			{
				final String newCharName = st.nextToken();
				tryTransferChannel(activeChar, newCharName);
				return true;
			}
			case "errorTransferStream":
			{
				final String errorMsg = Util.getAllTokens(st);
				showErrorTransferStreamPage(activeChar, errorMsg);
				return true;
			}
			case "disconnectFromStream":
			{
				askDisconnectFromStream(activeChar);
				return true;
			}
			}
			return true;
		}
		catch (Exception e)
		{

		}

		return false;
	}

	private void showMainStreamerPage(Player player)
	{
		final Stream myStream = StreamsHolder.getInstance().getMyStream(player);

		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/mainStreamer.htm");

		// Stream data
		html.replace("%streamTitle%", myStream.getStreamTitle());
		html.replace("%streamChannelName%", myStream.getChannelName());
		html.replace("%streamGameName%", myStream.getStreamGameName());
		html.replace("%streamViewersCount%", myStream.getViewersCount());

		// Stream reward
		final StringBuilder rewards = new StringBuilder();
		if (!myStream.isTitleCorrect())
		{
			rewards.append("<font color=d50000>Stream is currently NOT being rewarded! Reason: Wrong Title.</font>");
		}
		else if (!myStream.isStreamGameNameCorrect())
		{
			rewards.append("<font color=d50000>Stream is currently NOT being rewarded! Reason: Wrong Game Name.</font>");
		}
		else if (myStream.getViewersCount() < StreamsHolder.getInstance().getMinRequiredViewers())
		{
			rewards.append("<font color=d50000>Stream is currently NOT being rewarded! Reason: Not enough Viewers. Min Viewers: ${StreamsHolder.minRequiredViewers}</font>");
		}
		else if (!StreamsHolder.isPlayerActive(player))
		{
			rewards.append("<font color=d50000>AFK Streamers are not getting rewarded!</font>");
		}
		else if (myStream.isNowPunished())
		{
			rewards.append("<font color=d50000>You have been punished! Contact Game Administrators for more info.<br1>" + "End Date: " + END_DATE_FORMAT.format(myStream.getPunishedUntilDate()) + "</font>");
		}
		else if (myStream.getTotalRewardedSecondsToday() >= ConfigHolder.getLong("StreamingMaxRewardedSecondsPerDay"))
		{
			rewards.append("<font color=d50000>You have reached MAX Streaming Time for today!</font>");
		}
		else
		{
			rewards.append("In " + Math.ceil((ConfigHolder.getLong("StreamingSecondsToReward") - myStream.getNotRewardedSeconds()) / 60) + " minutes You will receive:</center>");
			rewards.append("<br1>");
			rewards.append("<font color=d9b330>");
			rewards.append("<table width=260>");
			rewards.append("<tr>");
			rewards.append("<td width=130 align=center><font color=d4cdbe>Since 0 Viewers:</font</td>");
			rewards.append("<td width=130 align=center><font color=d4cdbe>Since 3 Viewers:</font></td>");
			rewards.append("</tr>");
			rewards.append("<tr>");
			rewards.append("<td align=center>2 Festival Adena!</td>");
			rewards.append("<td align=center>6 Festival Adena!</td>");
			rewards.append("</tr>");
			rewards.append("<tr>");
			rewards.append("<td align=center><br></td>");
			rewards.append("<td align=center></td>");
			rewards.append("</tr>");
			rewards.append("</table>");
			rewards.append("</font>");
		}

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);
		html.replace("%streamRewards%", rewards.toString());

		player.sendPacket(html);
	}

	private void showMainNotStreamerPage(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/mainNotStreamer.htm");

		// Stream reward
		final StringBuilder rewardTime = new StringBuilder();

		final long secondsForReward = ConfigHolder.getLong("StreamingSecondsToReward");
		final int hours = (int) Math.floor(secondsForReward / 3600);
		if (hours > 1)
		{
			rewardTime.append(hours + " ");
			if (secondsForReward - (hours * 3600) == 0)
			{
				rewardTime.append("Hours");
			}
			else
			{
				rewardTime.append(" and " + Math.floor(secondsForReward - (hours * 3600) / 60) + " Minutes");
			}
		}
		else if (hours == 1)
		{
			rewardTime.append("Hour");
		}
		else if (secondsForReward / 60 > 1)
		{
			rewardTime.append(Math.floor(secondsForReward / 60) + " Minutes");
		}
		else if ((secondsForReward / 60) == 1)
		{
			rewardTime.append("Minute ");
		}
		else
		{
			rewardTime.append(secondsForReward + " seconds");
		}

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);
		html.replace("%rewardTime%", rewardTime.toString());

		player.sendPacket(html);
	}

	private void showMainAwaitingApprovalPage(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/mainAwaitingApproval.htm");

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);

		player.sendPacket(html);
	}

	private void showErrorConnectToStreamPage(Player player, String errorMsg)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/errorConnectToStream.htm");

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);
		html.replace("%errorMsg%", errorMsg);

		player.sendPacket(html);
	}

	private void showTransferStreamPage(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/transferStream.htm");

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);

		player.sendPacket(html);
	}

	private void showErrorTransferStreamPage(Player player, String errorMsg)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/stream/errorTransferStream.htm");

		// Replacements
		html.replace("%serverName%", Config.SERVER_NAME);
		html.replace("%errorMsg%", errorMsg);

		player.sendPacket(html);
	}

	private void tryConnectToStream(Player player, String streamName)
	{
		final Stream stream = StreamsHolder.getInstance().getStreamByChannelName(streamName);
		if (stream == null)
		{
			String errorMsg;
			if (ConfigHolder.getBool("TwitchActiveStreamsURLWithGameName"))
			{
				errorMsg = StringHolder.getNotNull(player, "Twitch.CantConnect.NotExistOfflineOrWrongGame", streamName, ConfigHolder.getString("TwitchCorrectStreamGame"));
			}
			else
			{
				errorMsg = StringHolder.getNotNull(player, "Twitch.CantConnect.NotExistOrOffline", streamName);
			}
			useStreamBypass(player, "errorConnectToStream", errorMsg);
			Log.logStream("Player " + player + " tried to connect to Stream: " + streamName + ". Such Stream doesn't exist!");
		}
		else if (stream.getAttachedPlayerId() > 0)
		{
			String errorMsg;
			if (stream.getAttachedPlayerServer().equalsIgnoreCase(Config.SERVER_SUB_NAME))
			{
				errorMsg = StringHolder.getNotNull(player, "Twitch.CantConnect.AttachedSameServer", new Object[0]);
			}
			else
			{
				errorMsg = StringHolder.getNotNull(player, "Twitch.CantConnect.AttachedOtherServer", ConfigHolder.getString("ServerName"));
			}
			useStreamBypass(player, "errorConnectToStream", errorMsg);
			Log.logStream("Player " + player + " tried to connect to Stream: " + streamName + ". It is attached already to " + stream.getAttachedPlayerId() + " on SubServer: " + stream.getAttachedPlayerServer() + "!");
		}
		else
		{
			if (ConfigHolder.getBool("StreamConnectionNeedsApproval"))
			{
				stream.addIdToApprove(player.getObjectId());
				if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
				{
					StreamDatabaseHandler.updateStream(stream);
				}
				player.sendMessage(StringHolder.getNotNull(player, "Twitch.ConnectSuccessWaitApproval", new Object[0]));
				Log.logStream("Player " + player + " attached to Stream: " + stream.getChannelName() + ". Waiting for Approval!");
			}
			else
			{
				stream.setAttachedPlayerId(player.getObjectId(), Config.SERVER_SUB_NAME);
				if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
				{
					StreamDatabaseHandler.updateStream(stream);
				}
				player.sendMessage(StringHolder.getNotNull(player, "Twitch.ConnectSuccess", new Object[0]));
				Log.logStream("Player correctly " + player + " attached to Stream: " + stream.getChannelName() + ".");
			}
			useStreamBypass(player, "mainStreamer");
		}
	}

	private void tryTransferChannel(Player player, String newCharName)
	{
		final Stream stream = StreamsHolder.getInstance().getMyStream(player);
		if (stream == null)
		{
			useStreamBypass(player, "errorTransferStream", StringHolder.getNotNull(player, "Twitch.CantTransfer.NoStream", new Object[0]));
			Log.logStream("Player " + player + " WITHOUT stream tried to transfer it to other character(" + newCharName + ")!");
			return;
		}

		if (newCharName.isEmpty())
		{
			useStreamBypass(player, "errorTransferStream", StringHolder.getNotNull(player, "Twitch.CantTransfer.CharNameNotFilled", new Object[0]));
			Log.logStream("Player " + player + " tried to transfer " + stream + " to Character with Empty Name!");
			return;
		}

		final Player onlineNewChar = GameObjectsStorage.getPlayer(newCharName);
		if (onlineNewChar == null)
		{
			final int newCharId = CharacterDAO.getInstance().getObjectIdByName(newCharName);
			if (newCharId <= 0)
			{
				useStreamBypass(player, "errorTransferStream", StringHolder.getNotNull(player, "Twitch.CantTransfer.NewCharNotExists", new Object[0]));
				Log.logStream("Player " + player + " tried to transfer " + stream + " to Non Existing Char with Name: " + newCharName + "!");
			}
			else
			{
				stream.setAttachedPlayerId(newCharId, Config.SERVER_SUB_NAME);
				if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
				{
					StreamDatabaseHandler.updateStream(stream);
				}
				final String exactName = CharacterDAO.getNameByObjectId(newCharId);
				player.sendMessage(StringHolder.getNotNull(player, "Twitch.TransferredToOffline", exactName));
				Log.logStream("Player " + player + " transferred " + stream + " to New Offline Character: " + exactName);
			}
		}
		else
		{
			stream.setAttachedPlayerId(onlineNewChar.getObjectId(), Config.SERVER_SUB_NAME);
			if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
			{
				StreamDatabaseHandler.updateStream(stream);
			}
			onlineNewChar.sendMessage(StringHolder.getNotNull(player, "Twitch.TransferredToYou", stream.getChannelName()));
			player.sendMessage(StringHolder.getNotNull(player, "Twitch.TransferredToOnline", onlineNewChar.getName()));
			Log.logStream("Player " + player + " transferred " + stream + " to New Online Character: " + onlineNewChar.getName());
		}
	}

	private static void askDisconnectFromStream(Player player)
	{
		final ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString(StringHolder.getNotNull(player, "Twitch.AskDisconnect", new Object[0]));
		player.ask(packet, new DisconnectStreamAnswer(player));
	}

	private static void checkReward(Player player)
	{
		if (!ConfigHolder.getBool("AllowRewardingStreamers"))
		{
			return;
		}

		if (ConfigHolder.getLong("StreamingSecondsToReward") < 60L)
		{
			LOG.error("Config StreamingSecondsToReward has too low value!!!");
			return;
		}

		final Stream stream = StreamsHolder.getInstance().getMyStream(player);
		if (stream != null)
		{
			boolean rewarded = false;
			while (stream.getNotRewardedSeconds() >= ConfigHolder.getLong("StreamingSecondsToReward"))
			{
				int highestViewersReached = -1;
				for (StatsSet reward : ConfigHolder.getStatsSetList("StreamingRewards", "minViewers", Integer.class, "itemId", Integer.class, "itemCount", Long.class))
				{
					final int minViewers = reward.getInteger("minViewers");
					if (stream.getViewersCount() >= minViewers && minViewers > highestViewersReached)
					{
						highestViewersReached = minViewers;
					}
				}
				if (highestViewersReached >= 0)
				{
					for (StatsSet reward : ConfigHolder.getStatsSetList("StreamingRewards", "minViewers", Integer.class, "itemId", Integer.class, "itemCount", Long.class))
					{
						if (highestViewersReached == reward.getInteger("minViewers"))
						{
							ItemFunctions.addItem(player, reward.getInteger("itemId"), reward.getLong("itemCount"), true, "StreamingReward");
						}
					}
				}
				stream.setNotRewardedSeconds(stream.getNotRewardedSeconds() - ConfigHolder.getLong("StreamingSecondsToReward"));
				rewarded = true;
				Log.logStream("Player correctly " + player + " received reward for Streaming Channel " + stream.getChannelName() + " with " + stream.getViewersCount() + " viewers.");
			}
			if (rewarded)
			{
				player.sendMessage(StringHolder.getNotNull(player, "Twitch.Rewarded", new Object[0]));
				StreamDatabaseHandler.updateStream(stream);
			}
		}
	}

	private static class DisconnectStreamAnswer implements OnAnswerListener
	{
		private final Player _player;

		private DisconnectStreamAnswer(Player player)
		{
			_player = player;
		}

		@Override
		public void sayYes()
		{
			final Stream stream = StreamsHolder.getInstance().getMyStream(_player);
			if (stream == null)
			{
				_player.sendMessage(StringHolder.getNotNull(_player, "Twitch.CantDisconnect.NoStream", new Object[0]));
				Log.logStream("Player " + _player + " tried to Disconnect from NON Existing Stream Channel!");
			}
			else
			{
				stream.setAttachedPlayerId(-1, "");
				if (ConfigHolder.getBool("StreamConnectionSavedInDB"))
				{
					StreamDatabaseHandler.updateStream(stream);
				}
				_player.sendMessage(StringHolder.getNotNull(_player, "Twitch.DisconnectSuccess", new Object[0]));
				Log.logStream("Player " + _player + " disconnected from " + stream + "!");
			}
		}

		@Override
		public void sayNo()
		{
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}

package l2f.gameserver.fandc.streaming;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.utils.Log;

public final class StreamDatabaseHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(StreamDatabaseHandler.class);

	private static final String IDS_TO_APPROVE_REGEX = ";";

	public static void onStreamCreated(Stream stream)
	{
		try (Connection con = StreamDatabaseFactory.getStreamDatabaseConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("SELECT * FROM streams WHERE channel_name=?"))
			{
				statement.setString(1, stream.getChannelName());
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
					{
						final int attachedPlayerId = rset.getInt("connected_player_id");
						if (attachedPlayerId > 0)
						{
							stream.setAttachedPlayerId(attachedPlayerId, rset.getString("connected_player_server"));
						}
						final List<Integer> idsToApprove = parseIdsToApprove(rset.getString("ids_awaiting_approval"));
						if (!idsToApprove.isEmpty())
						{
							stream.addIdsToApprove(idsToApprove);
						}
						stream.setNotRewardedSeconds(rset.getLong("not_rewarded_seconds"));
						stream.setTotalRewardedSecondsToday(rset.getLong("total_rewarded_seconds_today"));
						stream.setPunishedUntilDate(rset.getLong("punished_until_date"));
						return;
					}
				}
			}
			catch (SQLException e)
			{
				LOG.error("Error while searching for " + stream + " in streams Table!", e);
			}

			try (PreparedStatement statement = con.prepareStatement("INSERT INTO streams VALUES(?,?,?,?,?,?,?)"))
			{
				statement.setString(1, stream.getChannelName());
				statement.setInt(2, stream.getAttachedPlayerId());
				statement.setString(3, stream.getAttachedPlayerId() > 0 ? Config.SERVER_SUB_NAME : "");
				statement.setString(4, createIdsToApproveString(stream.getIdsToApprove()));
				statement.setInt(5, 0);
				statement.setInt(6, 0);
				statement.setInt(7, -1);
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				LOG.error("Error while inserting " + stream + " to streams Table!", e);
			}
		}
		catch (SQLException e2)
		{
			LOG.error("Error while connecting to Database with streams Table!", e2);
		}
	}

	public static void updateStream(Stream stream)
	{
		try (Connection con = StreamDatabaseFactory.getStreamDatabaseConnection();
					PreparedStatement statement = con.prepareStatement(
								"UPDATE streams SET connected_player_id=?, connected_player_server=?, ids_awaiting_approval = ?, not_rewarded_seconds = ?, total_rewarded_seconds_today = ?, punished_until_date = ? WHERE channel_name=?"))
		{
			statement.setInt(1, stream.getAttachedPlayerId());
			statement.setString(2, stream.getAttachedPlayerServer());
			statement.setString(3, createIdsToApproveString(stream.getIdsToApprove()));
			statement.setLong(4, stream.getNotRewardedSeconds());
			statement.setLong(5, stream.getTotalRewardedSecondsToday());
			statement.setLong(6, stream.getPunishedUntilDate());
			statement.setString(7, stream.getChannelName());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while updating " + stream + "!", e);
		}
	}

	public static void reloadStreams()
	{
		Log.logStream("Reloading Streams!");
		try (Connection con = StreamDatabaseFactory.getStreamDatabaseConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM streams"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final String channelName = rset.getString("channel_name");
				final Stream stream = StreamsHolder.getInstance().getStreamByChannelName(channelName);
				if (stream != null)
				{
					stream.setAttachedPlayerId(rset.getInt("connected_player_id"), rset.getString("connected_player_server"));
					stream.getIdsToApprove().clear();
					stream.addIdsToApprove(parseIdsToApprove(rset.getString("ids_awaiting_approval")));
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while reloading Streams!", e);
		}
	}

	public static void resetTotalRewardedTimes()
	{
		Log.logStream("RESETTING REWARD TIMES!");
		try (Connection con = StreamDatabaseFactory.getStreamDatabaseConnection(); PreparedStatement statement = con.prepareStatement("UPDATE streams SET total_rewarded_seconds_today = 0"))
		{
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while resetting Total Rewarded Times of Streams!", e);
		}
	}

	public static void saveRewardTimes()
	{
		try (Connection con = StreamDatabaseFactory.getStreamDatabaseConnection();
					PreparedStatement statement = con.prepareStatement("UPDATE streams SET not_rewarded_seconds = ?, total_rewarded_seconds_today = ? WHERE channel_name=?"))
		{
			for (Stream stream : StreamsHolder.getInstance().getAllActiveStreamsCopy())
			{
				if (stream.getAttachedPlayerId() > 0 && stream.getAttachedPlayerServer().equals(Config.SERVER_SUB_NAME))
				{
					statement.setLong(1, stream.getNotRewardedSeconds());
					statement.setLong(2, stream.getTotalRewardedSecondsToday());
					statement.setString(3, stream.getChannelName());
					statement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while saving Streams to Database!", e);
		}
	}

	private static String createIdsToApproveString(List<Integer> idsToApprove)
	{
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < idsToApprove.size(); ++i)
		{
			if (i > 0)
			{
				builder.append(";");
			}
			final Integer id = idsToApprove.get(i);
			builder.append(id);
		}
		return builder.toString();
	}

	private static List<Integer> parseIdsToApprove(String textToParse)
	{
		final String[] idsString = textToParse.split(IDS_TO_APPROVE_REGEX);
		if (idsString.length == 1 && idsString[0].isEmpty())
		{
			return new ArrayList<Integer>(0);
		}

		final List<Integer> ids = new ArrayList<Integer>(idsString.length);
		for (String anIdsString : idsString)
		{
			ids.add(Integer.parseInt(anIdsString));
		}
		return ids;
	}
}

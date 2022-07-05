package l2f.gameserver.model.entity.CCPHelpers.itemLogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.BatchStatement;

public class ItemLogList
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemLogList.class);
	public static final SingleItemLog[] EMPTY_ITEM_LOGS = new SingleItemLog[0];
	private final Map<Integer, List<ItemActionLog>> _logLists = new ConcurrentHashMap<>();

	public List<ItemActionLog> getLogs(Player player)
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS)
		{
			return new ArrayList<>();
		}
		List<ItemActionLog> list = _logLists.get(Integer.valueOf(player.getObjectId()));
		if (list == null)
		{
			return new ArrayList<>();
		}
		return list;
	}

	public void addLogs(ItemActionLog logs)
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS)
		{
			return;
		}

		Integer playerObjectId = Integer.valueOf(logs.getPlayerObjectId());
		List<ItemActionLog> list;
		if (_logLists.containsKey(playerObjectId))
		{
			list = _logLists.get(playerObjectId);
		}
		else
		{
			list = new CopyOnWriteArrayList<>();
			_logLists.put(playerObjectId, list);
		}

		list.add(logs);
	}

	public void fillReceiver(int itemObjectId, String playerName)
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS)
		{
			return;
		}
		for (List<ItemActionLog> logList : _logLists.values())
		{
			for (ItemActionLog log : logList)
			{
				if (!log.getActionType().isReceiverKnown())
				{
					for (SingleItemLog item : log.getItemsLost())
					{
						if ((item.getItemObjectId() != itemObjectId) || ((item.getReceiverName() != null) && (!item.getReceiverName().isEmpty())))
						{
							continue;
						}
						item.setReceiverName(playerName);
						return;
					}
				}
			}
		}
	}

	public void loadAllLogs()
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS || !Config.PLAYER_ITEM_LOGS_SAVED_IN_DB)
		{
			return;
		}

		final long logsSince = System.currentTimeMillis() - Config.PLAYER_ITEM_LOGS_MAX_TIME;
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			int minId = -1;
			try (PreparedStatement statement = con.prepareStatement("SELECT log_id FROM logs WHERE time > ? ORDER BY log_id ASC LIMIT 1"))
			{
				statement.setLong(1, logsSince);
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
					{
						minId = rset.getInt("log_id");
					}
				}
			}
			try (PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM logs WHERE log_id < ?"))
			{
				deleteStatement.setInt(1, minId);
				deleteStatement.executeUpdate();
			}
			try (PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM logs_items WHERE log_id < ?"))
			{
				deleteStatement.setInt(1, minId);
				deleteStatement.executeUpdate();
			}
			ItemLogHandler.getInstance().loadLastActionId(con);
			final Map<Integer, ItemActionLog> logsById = new HashMap<Integer, ItemActionLog>();
			try (PreparedStatement statement2 = con.prepareStatement("SELECT * FROM logs"); final ResultSet rset2 = statement2.executeQuery())
			{
				while (rset2.next())
				{
					final int logId = rset2.getInt("log_id");
					final int playerObjectId = rset2.getInt("player_object_id");
					final ItemActionType actionType = ItemActionType.valueOf(rset2.getString("action_type"));
					final long time = rset2.getLong("time");
					List<ItemActionLog> logs = _logLists.get(playerObjectId);
					if (logs == null)
					{
						logs = new CopyOnWriteArrayList<ItemActionLog>();
						_logLists.put(playerObjectId, logs);
					}
					final ItemActionLog log = new ItemActionLog(logId, playerObjectId, actionType, time, ItemLogList.EMPTY_ITEM_LOGS, ItemLogList.EMPTY_ITEM_LOGS, true);
					logs.add(log);
					logsById.put(logId, log);
				}
			}
			try (PreparedStatement statement2 = con.prepareStatement("SELECT * FROM logs_items"); final ResultSet rset2 = statement2.executeQuery())
			{
				while (rset2.next())
				{
					final ItemActionLog log2 = logsById.get(rset2.getInt("log_id"));
					if (log2 != null)
					{
						final int itemObjectId = rset2.getInt("item_object_id");
						final int itemTemplateId = rset2.getInt("item_template_id");
						final long itemCount = rset2.getLong("item_count");
						final int itemEnchantLevel = rset2.getInt("item_enchant_level");
						final String receiverName = rset2.getString("receiver_name");
						final SingleItemLog itemLog = new SingleItemLog(itemTemplateId, itemCount, itemEnchantLevel, itemObjectId, receiverName);
						log2.addItemLog(itemLog, rset2.getInt("lost") == 1);
					}
				}
			}
		}
		catch (final SQLException e)
		{
			LOG.error("Error while getting min Log Id:", e);
		}
	}

	public void saveAllLogs()
	{
		if (!Config.ENABLE_PLAYER_ITEM_LOGS || !Config.PLAYER_ITEM_LOGS_SAVED_IN_DB)
		{
			return;
		}

		LOG.info("Saving Logs");
		try (Connection con = DatabaseFactory.getInstance().getConnection(); final PreparedStatement statement = BatchStatement.createPreparedStatement(con, "INSERT INTO `logs` VALUES (?, ?, ?, ?);"))
		{
			for (List<ItemActionLog> list : _logLists.values())
			{
				for (ItemActionLog log : list)
				{
					if (!log.isSavedInDatabase())
					{
						statement.setInt(1, log.getActionId());
						statement.setInt(2, log.getPlayerObjectId());
						statement.setString(3, log.getActionType().toString());
						statement.setLong(4, log.getTime());
						statement.addBatch();
					}
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (final SQLException e)
		{
			LOG.error("Failed to save all Item Logs: ", e);
		}

		LOG.info("Saving Logs_Items");
		try (Connection con = DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = BatchStatement.createPreparedStatement(con, "INSERT INTO `logs_items` VALUES (?, ?, ?, ?, ?, ?, ?);"))
		{
			for (List<ItemActionLog> list : _logLists.values())
			{
				for (ItemActionLog log : list)
				{
					if (!log.isSavedInDatabase())
					{
						for (int i = 0; i < 2; ++i)
						{
							final boolean isLostItem = i == 1;
							final SingleItemLog[] array;
							final SingleItemLog[] items = array = isLostItem ? log.getItemsLost() : log.getItemsReceived();
							for (SingleItemLog item : array)
							{
								statement.setInt(1, log.getActionId());
								statement.setInt(2, item.getItemObjectId());
								statement.setInt(3, item.getItemTemplateId());
								statement.setLong(4, item.getItemCount());
								statement.setInt(5, item.getItemEnchantLevel());
								statement.setInt(6, isLostItem ? 1 : 0);
								statement.setString(7, item.getReceiverName() == null ? "" : item.getReceiverName());
								statement.addBatch();
							}
						}
					}
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Failed to save all Single Item Logs: ", e);
		}
		LOG.info("Logs Saved!");
	}

	public static ItemLogList getInstance()
	{
		return ItemLogListHolder.instance;
	}

	private static class ItemLogListHolder
	{
		private static final ItemLogList instance = new ItemLogList();
	}
}
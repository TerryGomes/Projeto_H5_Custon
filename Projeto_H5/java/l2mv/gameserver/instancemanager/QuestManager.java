package l2mv.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.quest.Quest;

public class QuestManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestManager.class);
	public static final int TUTORIAL_QUEST_ID = 255;

	private static Map<String, Quest> _questsByName = new ConcurrentHashMap<String, Quest>();
	private static Map<Integer, Quest> _questsById = new ConcurrentHashMap<Integer, Quest>();

	public static Quest getQuest(String name)
	{
		return _questsByName.get(name);
	}

	public static Quest getQuest(Class<?> quest)
	{
		return getQuest(quest.getSimpleName());
	}

	public static Quest getQuest(int questId)
	{
		return _questsById.get(questId);
	}

	public static Quest getQuest2(String nameOrId)
	{
		if (_questsByName.containsKey(nameOrId))
		{
			return _questsByName.get(nameOrId);
		}
		try
		{
			int questId = Integer.valueOf(nameOrId);
			return _questsById.get(questId);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static void addQuest(Quest newQuest)
	{
		_questsByName.put(newQuest.getName(), newQuest);
		_questsById.put(newQuest.getQuestIntId(), newQuest);
	}

	public static Collection<Quest> getQuests()
	{
		return _questsByName.values();
	}

	public static void updateQuestNames()
	{
		Connection con = null;
		PreparedStatement statement = null;
		PreparedStatement repairQuestData = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			repairQuestData = con.prepareStatement("UPDATE character_quests SET name=? WHERE name=?");
			statement = con.prepareStatement("SELECT name FROM character_quests");
			rset = statement.executeQuery();
			while (rset.next())
			{
				String questId = rset.getString("name");

				if (questId.startsWith("Q"))
				{
					int id = Integer.parseInt(questId.split("_")[0].replace("Q", ""));
					String newname = QuestManager.getQuest(id).getName();
					if (newname == null)
					{
						continue;
					}
					repairQuestData.setString(1, newname);
					repairQuestData.setString(2, questId);
					repairQuestData.executeUpdate();
					questId = newname;
				}
			}

			DbUtils.close(statement, rset);
		}
		catch (final Exception e)
		{
			LOGGER.error("could not insert char quest:", e);
		}
		finally
		{
			DbUtils.closeQuietly(repairQuestData);
			DbUtils.closeQuietly(con, statement, rset);
		}
	}
}
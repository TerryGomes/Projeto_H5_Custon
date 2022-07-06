package l2mv.gameserver.instancemanager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.gameserver.model.quest.Quest;

public class QuestManager
{
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
}
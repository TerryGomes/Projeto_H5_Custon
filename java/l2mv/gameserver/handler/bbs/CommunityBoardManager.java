package l2mv.gameserver.handler.bbs;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.multverso.dailyquests.DailyQuestHandler;
import l2mv.gameserver.templates.StatsSet;

public class CommunityBoardManager
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoardManager.class);
	private static final CommunityBoardManager _instance = new CommunityBoardManager();

	private final Map<String, ICommunityBoardHandler> _handlers = new HashMap<String, ICommunityBoardHandler>();
	private final StatsSet _properties = new StatsSet();

	public static CommunityBoardManager getInstance()
	{
		return _instance;
	}

	public CommunityBoardManager()
	{
		registerHandler(new DailyQuestHandler());
	}

	public void registerHandler(ICommunityBoardHandler commHandler)
	{
		for (String bypass : commHandler.getBypassCommands())
		{
			if (_handlers.containsKey(bypass))
			{
				_log.warn("CommunityBoard: dublicate bypass registered! First handler: " + _handlers.get(bypass).getClass().getSimpleName() + " second: " + commHandler.getClass().getSimpleName());
			}

			_handlers.put(bypass, commHandler);
		}
	}

	public void removeHandler(ICommunityBoardHandler handler)
	{
		for (String bypass : handler.getBypassCommands())
		{
			_handlers.remove(bypass);
		}
		_log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
	}

	public ICommunityBoardHandler getCommunityHandler(String bypass)
	{
		if (!Config.COMMUNITYBOARD_ENABLED || _handlers.isEmpty())
		{
			return null;
		}

		for (Map.Entry<String, ICommunityBoardHandler> entry : _handlers.entrySet())
		{
			if (bypass.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	public void setProperty(String name, String val)
	{
		_properties.set(name, val);
	}

	public void setProperty(String name, int val)
	{
		_properties.set(name, val);
	}

	public int getIntProperty(String name)
	{
		return _properties.getInteger(name, 0);
	}
}

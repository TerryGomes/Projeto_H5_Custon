package l2mv.gameserver.model.entity.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;

public class GameEventManager
{
	private static final Logger _log = LoggerFactory.getLogger(GameEventManager.class);
	private static GameEventManager _instance;
	private HashMap<String, GameEvent> _events;
	private ScheduledFuture<?> event_sched;
	private GameEvent event;

	public GameEventManager()
	{
		_events = new HashMap<String, GameEvent>();
		event_sched = null;
		event = null;
	}

	public static GameEventManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new GameEventManager();
		}
		return _instance;
	}

	public void registerEvent(GameEvent evt)
	{
		_events.put(evt.getName(), evt);
		nextEvent();
	}

	public boolean nextEvent()
	{
		long min = 0;
		long time = 0;
		GameEvent nextEv = null;
		for (GameEvent ev : _events.values())
		{
			time = ev.getNextTime();
			if (min > time || min == 0)
			{
				min = time;
			}
		}
		for (GameEvent ev : _events.values())
		{
			time = ev.getNextTime();
			if (min == time)
			{
				nextEv = ev;
				break;
			}
		}

		if (event_sched != null)
		{
			event_sched.cancel(true);
		}

		event = nextEv;

		if (event == null)
		{
			_log.info("Event load: error");
			return false;
		}

		_log.info("Event " + event.getName() + " started in " + Long.toString((time - System.currentTimeMillis() / 1000) / 60) + " mins.");
		event_sched = ThreadPoolManager.getInstance().schedule(new EventStart(), time * 1000 - System.currentTimeMillis());
		return true;
	}

	public GameEvent findEvent(String name)
	{
		return _events.get(name);
	}

	public Collection<GameEvent> getAllEvents()
	{
		return _events.values();
	}

	public GameEvent participantOf(Player player)
	{
		for (GameEvent evt : getAllEvents())
		{
			if (evt.isParticipant(player))
			{
				return evt;
			}
		}
		return null;
	}

	private class EventStart implements Runnable
	{
		private EventStart()
		{
		}

		@Override
		public void run()
		{
			if (event != null)
			{
				event.start();
			}
			_log.info("Event " + event.getName() + " has been started.");
		}
	}
}
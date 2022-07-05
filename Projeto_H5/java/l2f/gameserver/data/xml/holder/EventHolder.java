package l2f.gameserver.data.xml.holder;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.GlobalEvent;

public final class EventHolder extends AbstractHolder
{
	private static final EventHolder _instance = new EventHolder();
	private final IntObjectMap<GlobalEvent> _events = new TreeIntObjectMap<GlobalEvent>();
	public static int FIGHT_CLUB_EVENTS = 0;

	public static EventHolder getInstance()
	{
		return _instance;
	}

	public void addEvent(EventType type, GlobalEvent event)
	{
		_events.put(type.step() + event.getId(), event);

		if (type == EventType.FIGHT_CLUB_EVENT)
		{
			FIGHT_CLUB_EVENTS++;
		}
	}

	@SuppressWarnings("unchecked")
	public <E extends GlobalEvent> E getEvent(EventType type, int id)
	{
		return (E) _events.get(type.step() + id);
	}

	public void findEvent(Player player)
	{
		for (GlobalEvent event : _events.values())
		{
			if (event.isParticle(player))
			{
				player.addEvent(event);
			}
		}
	}

	public void callInit()
	{
		for (GlobalEvent event : _events.values())
		{
			event.initEvent();
		}
	}

	@Override
	public int size()
	{
		return _events.size();
	}

	@Override
	public void clear()
	{
		FIGHT_CLUB_EVENTS = 0;
		_events.clear();
	}
}

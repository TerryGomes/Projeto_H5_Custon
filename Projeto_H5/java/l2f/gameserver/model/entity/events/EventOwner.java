package l2f.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class EventOwner implements Serializable
{
	private Set<GlobalEvent> events = new CopyOnWriteArraySet<>();

	@SuppressWarnings("unchecked")
	public <E extends GlobalEvent> E getEvent(Class<E> eventClass)
	{
		for (GlobalEvent e : events)
		{
			if ((e.getClass() == eventClass) || eventClass.isAssignableFrom(e.getClass())) // FIXME [VISTALL] какойто другой способ определить
			{
				return (E) e;
			}
		}

		return null;
	}

	public void addEvent(GlobalEvent event)
	{
		events.add(event);
	}

	public void removeEvent(GlobalEvent event)
	{
		events.remove(event);
	}

	public Set<GlobalEvent> getEvents()
	{
		return events;
	}
}

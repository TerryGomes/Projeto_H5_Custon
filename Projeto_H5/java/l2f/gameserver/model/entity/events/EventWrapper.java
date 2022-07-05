package l2f.gameserver.model.entity.events;

import l2f.gameserver.taskmanager.actionrunner.ActionWrapper;

public class EventWrapper extends ActionWrapper
{
	private final GlobalEvent _event;
	private final int _time;

	public EventWrapper(String name, GlobalEvent event, int time)
	{
		super(name);
		_event = event;
		_time = time;
	}

	@Override
	public void runImpl0() throws Exception
	{
		_event.timeActions(_time);
	}
}

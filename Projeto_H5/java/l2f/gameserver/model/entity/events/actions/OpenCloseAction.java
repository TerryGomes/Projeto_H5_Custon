package l2f.gameserver.model.entity.events.actions;

import l2f.gameserver.model.entity.events.EventAction;
import l2f.gameserver.model.entity.events.GlobalEvent;

public class OpenCloseAction implements EventAction
{
	private final boolean _open;
	private final String _name;

	public OpenCloseAction(boolean open, String name)
	{
		_open = open;
		_name = name;
	}

	@Override
	public void call(GlobalEvent event)
	{
		event.doorAction(_name, _open);
	}
}

package l2f.gameserver.model.entity.events.actions;

import l2f.gameserver.model.entity.events.EventAction;
import l2f.gameserver.model.entity.events.GlobalEvent;

public class SpawnDespawnAction implements EventAction
{
	private final boolean _spawn;
	private final String _name;

	public SpawnDespawnAction(String name, boolean spawn)
	{
		_spawn = spawn;
		_name = name;
	}

	@Override
	public void call(GlobalEvent event)
	{
		event.spawnAction(_name, _spawn);
	}
}

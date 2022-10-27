package l2mv.gameserver.model.entity.events.objects;

import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.NpcUtils;

public class SpawnSimpleObject implements SpawnableObject
{
	private final int _npcId;
	private final Location _loc;
	private NpcInstance _npc;

	public SpawnSimpleObject(int npcId, Location loc)
	{
		_npcId = npcId;
		_loc = loc;
	}

	@Override
	public void spawnObject(GlobalEvent event)
	{
		_npc = NpcUtils.spawnSingle(_npcId, _loc, event.getReflection());
		if (_npc != null)
		{
			_npc.addEvent(event);
		}
	}

	@Override
	public void despawnObject(GlobalEvent event)
	{
		if (_npc != null)
		{
			_npc.removeEvent(event);
			_npc.deleteMe();
			_npc = null;
		}
	}

	@Override
	public void refreshObject(GlobalEvent event)
	{

	}
}

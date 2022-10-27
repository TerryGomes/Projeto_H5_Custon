package l2mv.gameserver.model.entity.events.objects;

import l2mv.gameserver.data.xml.holder.StaticObjectHolder;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.instances.StaticObjectInstance;

public class StaticObjectObject implements SpawnableObject
{
	private final int _uid;
	private StaticObjectInstance _instance;

	public StaticObjectObject(int id)
	{
		_uid = id;
	}

	@Override
	public void spawnObject(GlobalEvent event)
	{
		_instance = StaticObjectHolder.getInstance().getObject(_uid);
	}

	@Override
	public void despawnObject(GlobalEvent event)
	{
		//
	}

	@Override
	public void refreshObject(GlobalEvent event)
	{
		if (!event.isInProgress())
		{
			_instance.removeEvent(event);
		}
		else
		{
			_instance.addEvent(event);
		}
	}

	public void setMeshIndex(int id)
	{
		_instance.setMeshIndex(id);
		_instance.broadcastInfo(false);
	}

	public int getUId()
	{
		return _uid;
	}
}

package l2mv.gameserver.model.entity.events.objects;

import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.instances.DoorInstance;

public class DoorObject implements SpawnableObject, InitableObject
{
	private final int _id;
	private DoorInstance _door;

	private boolean _weak;

	public DoorObject(int id)
	{
		_id = id;
	}

	@Override
	public void initObject(GlobalEvent e)
	{
		_door = e.getReflection().getDoor(_id);
	}

	@Override
	public void spawnObject(GlobalEvent event)
	{
		refreshObject(event);
	}

	@Override
	public void despawnObject(GlobalEvent event)
	{
		final Reflection ref = event.getReflection();
		if (ref == ReflectionManager.DEFAULT)
		{
			refreshObject(event);
		}
	}

	@Override
	public void refreshObject(GlobalEvent event)
	{
		if (!event.isInProgress())
		{
			_door.removeEvent(event);
		}
		else
		{
			_door.addEvent(event);
		}

		if (_door.getCurrentHp() <= 0)
		{
			_door.decayMe();
			_door.spawnMe();
		}

		_door.setCurrentHp(_door.getMaxHp() * (isWeak() ? 0.5 : 1.), true);
		close(event);
	}

	public int getUId()
	{
		return _door.getDoorId();
	}

	public int getUpgradeValue()
	{
		return _door.getUpgradeHp();
	}

	public void setUpgradeValue(GlobalEvent event, int val)
	{
		_door.setUpgradeHp(val);
		refreshObject(event);
	}

	public void open(GlobalEvent e)
	{
		_door.openMe(null, !e.isInProgress());
	}

	public void close(GlobalEvent e)
	{
		_door.closeMe(null, !e.isInProgress());
	}

	public DoorInstance getDoor()
	{
		return _door;
	}

	public boolean isWeak()
	{
		return _weak;
	}

	public void setWeak(boolean weak)
	{
		_weak = weak;
	}
}

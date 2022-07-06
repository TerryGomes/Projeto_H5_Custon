package l2mv.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2mv.gameserver.model.entity.events.GlobalEvent;

public interface SpawnableObject extends Serializable
{
	void spawnObject(GlobalEvent event);

	void despawnObject(GlobalEvent event);

	void refreshObject(GlobalEvent event);
}

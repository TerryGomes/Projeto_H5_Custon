package l2f.gameserver.listener.actor.npc;

import l2f.gameserver.listener.NpcListener;
import l2f.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}

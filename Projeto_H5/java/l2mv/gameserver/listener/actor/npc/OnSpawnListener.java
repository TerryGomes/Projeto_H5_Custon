package l2mv.gameserver.listener.actor.npc;

import l2mv.gameserver.listener.NpcListener;
import l2mv.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}

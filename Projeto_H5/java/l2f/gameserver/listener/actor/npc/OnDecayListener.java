package l2f.gameserver.listener.actor.npc;

import l2f.gameserver.listener.NpcListener;
import l2f.gameserver.model.instances.NpcInstance;

public interface OnDecayListener extends NpcListener
{
	public void onDecay(NpcInstance actor);
}

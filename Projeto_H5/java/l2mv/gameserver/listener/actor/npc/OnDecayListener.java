package l2mv.gameserver.listener.actor.npc;

import l2mv.gameserver.listener.NpcListener;
import l2mv.gameserver.model.instances.NpcInstance;

public interface OnDecayListener extends NpcListener
{
	public void onDecay(NpcInstance actor);
}

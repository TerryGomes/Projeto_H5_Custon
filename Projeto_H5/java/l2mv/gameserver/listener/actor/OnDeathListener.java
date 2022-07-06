package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;

public interface OnDeathListener extends CharListener
{
	public void onDeath(Creature actor, Creature killer);
}

package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

public interface OnDeathListener extends CharListener
{
	public void onDeath(Creature actor, Creature killer);
}

package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

public interface OnAttackHitListener extends CharListener
{
	public void onAttackHit(Creature actor, Creature attacker);
}

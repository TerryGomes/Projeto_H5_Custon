package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;

public interface OnAttackHitListener extends CharListener
{
	public void onAttackHit(Creature actor, Creature attacker);
}

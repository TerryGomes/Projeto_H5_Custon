package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;

public interface OnAttackListener extends CharListener
{
	public void onAttack(Creature actor, Creature target);
}

package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill);
}

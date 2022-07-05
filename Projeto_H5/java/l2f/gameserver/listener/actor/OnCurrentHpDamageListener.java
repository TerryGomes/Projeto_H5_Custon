package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill);
}

package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;

public interface OnMagicHitListener extends CharListener
{
	public void onMagicHit(Creature actor, Skill skill, Creature caster);
}

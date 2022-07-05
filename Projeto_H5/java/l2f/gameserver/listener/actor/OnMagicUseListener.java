package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt);
}

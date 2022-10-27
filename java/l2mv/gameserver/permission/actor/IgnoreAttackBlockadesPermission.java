package l2mv.gameserver.permission.actor;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.permission.CharPermission;

public interface IgnoreAttackBlockadesPermission extends CharPermission
{
	boolean canIgnoreAttackBlockades(Creature p0, Creature p1, Skill p2, boolean p3);
}

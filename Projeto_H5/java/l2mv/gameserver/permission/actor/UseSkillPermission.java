package l2mv.gameserver.permission.actor;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.permission.CharPermission;

public interface UseSkillPermission extends CharPermission
{
	boolean canUseSkill(Creature p0, Creature p1, Skill p2);

	void sendPermissionDeniedError(Creature p0, Creature p1, Skill p2);
}

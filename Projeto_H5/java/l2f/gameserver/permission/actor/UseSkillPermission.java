package l2f.gameserver.permission.actor;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.permission.CharPermission;

public interface UseSkillPermission extends CharPermission
{
	boolean canUseSkill(Creature p0, Creature p1, Skill p2);

	void sendPermissionDeniedError(Creature p0, Creature p1, Skill p2);
}

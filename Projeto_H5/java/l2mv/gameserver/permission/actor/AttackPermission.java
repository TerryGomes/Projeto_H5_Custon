package l2mv.gameserver.permission.actor;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.permission.CharPermission;

public interface AttackPermission extends CharPermission
{
	boolean canAttack(Creature p0, Creature p1, Skill p2, boolean p3);

	default void sendPermissionDeniedError(Creature actor, Creature target, Skill skill, boolean force)
	{
		actor.sendPacket(getPermissionDeniedError(actor, target, skill, force));
	}

	IStaticPacket getPermissionDeniedError(Creature p0, Creature p1, Skill p2, boolean p3);
}

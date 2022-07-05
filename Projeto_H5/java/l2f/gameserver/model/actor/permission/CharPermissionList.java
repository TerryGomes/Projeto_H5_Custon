package l2f.gameserver.model.actor.permission;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.permission.Permission;
import l2f.commons.permission.PermissionList;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.permission.actor.AttackPermission;
import l2f.gameserver.permission.actor.IgnoreAttackBlockadesPermission;
import l2f.gameserver.permission.actor.UseSkillPermission;

public class CharPermissionList extends PermissionList<Creature>
{
	protected static final PermissionList<Creature> global;
	protected final Creature actor;

	public CharPermissionList(Creature actor)
	{
		super();
		this.actor = actor;
	}

	public Creature getActor()
	{
		return actor;
	}

	public static boolean addGlobal(Permission<Creature> permission)
	{
		return CharPermissionList.global.add(permission);
	}

	public static boolean removeGlobal(Permission<Creature> permission)
	{
		return CharPermissionList.global.remove(permission);
	}

	protected <E> List<E> getPermissions(Class<E> type)
	{
		final List<E> list = new ArrayList<E>(CharPermissionList.global.size() + actor.getPermissions().size());
		if (!CharPermissionList.global.getPermissions().isEmpty())
		{
			for (Permission<Creature> permission : CharPermissionList.global.getPermissions())
			{
				if (type.isInstance(permission))
				{
					list.add((E) permission);
				}
			}
		}
		if (!this.getPermissions().isEmpty())
		{
			for (Permission<Creature> permission : this.getPermissions())
			{
				if (type.isInstance(permission))
				{
					list.add((E) permission);
				}
			}
		}
		return list;
	}

	public boolean canAttack(Creature target, Skill skill, boolean force, boolean sendDeniedError)
	{
		for (AttackPermission permission : this.getPermissions(AttackPermission.class))
		{
			if (!permission.canAttack(getActor(), target, skill, force))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), target, skill, force);
				}
				return false;
			}
		}
		return true;
	}

	public IStaticPacket getAttackPermissionDeniedError(Creature target, Skill skill, boolean force)
	{
		for (AttackPermission permission : this.getPermissions(AttackPermission.class))
		{
			if (!permission.canAttack(getActor(), target, skill, force))
			{
				return permission.getPermissionDeniedError(getActor(), target, skill, force);
			}
		}
		return null;
	}

	public boolean canIgnoreAttackBlockades(Creature target, Skill skill, boolean force)
	{
		for (IgnoreAttackBlockadesPermission permission : this.getPermissions(IgnoreAttackBlockadesPermission.class))
		{
			if (permission.canIgnoreAttackBlockades(getActor(), target, skill, force))
			{
				return true;
			}
		}
		return false;
	}

	public boolean canUseSkill(Creature target, Skill skill, boolean sendDeniedError)
	{
		for (UseSkillPermission permission : this.getPermissions(UseSkillPermission.class))
		{
			if (!permission.canUseSkill(getActor(), target, skill))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), target, skill);
				}
				return false;
			}
		}
		return true;
	}

	static
	{
		global = new PermissionList<Creature>();
	}
}

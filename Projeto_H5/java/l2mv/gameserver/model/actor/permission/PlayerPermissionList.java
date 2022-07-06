package l2mv.gameserver.model.actor.permission;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.permission.actor.player.AttributeItemPermission;
import l2mv.gameserver.permission.actor.player.EnchantItemPermission;
import l2mv.gameserver.permission.actor.player.JoinFightClubPermission;
import l2mv.gameserver.permission.actor.player.LogOutPermission;
import l2mv.gameserver.permission.actor.player.ResurrectPermission;

public class PlayerPermissionList extends PlayablePermissionList
{
	public PlayerPermissionList(Player actor)
	{
		super(actor);
	}

	@Override
	public Player getActor()
	{
		return (Player) actor;
	}

	public boolean canResurrect(Creature target, boolean force, boolean isSalvation, boolean sendDeniedError)
	{
		for (ResurrectPermission permission : this.getPermissions(ResurrectPermission.class))
		{
			if (!permission.canResurrect(getActor(), target, force, isSalvation))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), target, force, isSalvation);
				}
				return false;
			}
		}
		return true;
	}

	public boolean canEnchantItem(ItemInstance item, ItemInstance enchant, ItemInstance catalyst, boolean sendDeniedError)
	{
		for (EnchantItemPermission permission : this.getPermissions(EnchantItemPermission.class))
		{
			if (!permission.canEnchantItem(getActor(), item, enchant, catalyst))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), item, enchant, catalyst);
				}
				return false;
			}
		}
		return true;
	}

	public boolean canAttributeItem(ItemInstance item, ItemInstance stone, boolean sendDeniedError)
	{
		for (AttributeItemPermission permission : this.getPermissions(AttributeItemPermission.class))
		{
			if (!permission.canAttributeItem(getActor(), item, stone))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), item, stone);
				}
				return false;
			}
		}
		return true;
	}

	public boolean canLogOut(boolean isRestart, boolean sendDeniedError)
	{
		for (LogOutPermission permission : this.getPermissions(LogOutPermission.class))
		{
			if (!permission.canLogOut(getActor(), isRestart))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), isRestart);
				}
				return false;
			}
		}
		return true;
	}

	public boolean canJoinFightClub(boolean sendDeniedError)
	{
		for (JoinFightClubPermission permission : this.getPermissions(JoinFightClubPermission.class))
		{
			if (!permission.joinSignFightClub(getActor()))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor());
				}
				return false;
			}
		}
		return true;
	}

	public String getJoinFightClubError()
	{
		for (JoinFightClubPermission permission : this.getPermissions(JoinFightClubPermission.class))
		{
			if (!permission.joinSignFightClub(getActor()))
			{
				return permission.getPermissionDeniedError(getActor());
			}
		}
		return null;
	}
}

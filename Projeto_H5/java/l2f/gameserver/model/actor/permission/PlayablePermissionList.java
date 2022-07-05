package l2f.gameserver.model.actor.permission;

import l2f.gameserver.model.Playable;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.permission.actor.player.LoseItemPermission;
import l2f.gameserver.permission.actor.player.UseItemPermission;

public class PlayablePermissionList extends CharPermissionList
{
	public PlayablePermissionList(Playable actor)
	{
		super(actor);
	}

	@Override
	public Playable getActor()
	{
		return (Playable) actor;
	}

	public boolean canUseItem(ItemInstance item, boolean ctrlPressed, boolean sendDeniedError)
	{
		for (UseItemPermission permission : this.getPermissions(UseItemPermission.class))
		{
			if (!permission.canUseItem(getActor(), item, ctrlPressed))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), item, ctrlPressed);
				}
				return false;
			}
		}
		return true;
	}

	public boolean canLoseItem(ItemInstance item, boolean sendDeniedError)
	{
		for (LoseItemPermission permission : this.getPermissions(LoseItemPermission.class))
		{
			if (!permission.canLoseItem(getActor(), item))
			{
				if (sendDeniedError)
				{
					permission.sendPermissionDeniedError(getActor(), item);
				}
				return false;
			}
		}
		return true;
	}

	public LoseItemPermission getLoseItemDeniedPermission(ItemInstance item)
	{
		for (LoseItemPermission permission : this.getPermissions(LoseItemPermission.class))
		{
			if (!permission.canLoseItem(getActor(), item))
			{
				return permission;
			}
		}
		return null;
	}
}

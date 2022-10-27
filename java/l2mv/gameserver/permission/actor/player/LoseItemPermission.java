package l2mv.gameserver.permission.actor.player;

import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.permission.PlayablePermission;

public interface LoseItemPermission extends PlayablePermission
{
	boolean canLoseItem(Playable p0, ItemInstance p1);

	default void sendPermissionDeniedError(Playable actor, ItemInstance item)
	{
		actor.sendMessage(getPermissionDeniedError(actor, item));
	}

	String getPermissionDeniedError(Playable p0, ItemInstance p1);
}

package l2f.gameserver.permission.actor.player;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.permission.PlayablePermission;

public interface AttributeItemPermission extends PlayablePermission
{
	boolean canAttributeItem(Player p0, ItemInstance p1, ItemInstance p2);

	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2);
}

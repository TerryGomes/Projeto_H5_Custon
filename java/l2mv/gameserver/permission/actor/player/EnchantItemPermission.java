package l2mv.gameserver.permission.actor.player;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.permission.PlayerPermission;

public interface EnchantItemPermission extends PlayerPermission
{
	boolean canEnchantItem(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);

	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);
}

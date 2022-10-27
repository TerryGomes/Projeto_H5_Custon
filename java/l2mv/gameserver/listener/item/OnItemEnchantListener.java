package l2mv.gameserver.listener.item;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;

public interface OnItemEnchantListener extends PlayerListener
{
	public void onEnchantFinish(Player player, ItemInstance item, boolean succeed);
}

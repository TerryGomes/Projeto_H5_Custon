package l2f.gameserver.listener.item;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;

public interface OnItemEnchantListener extends PlayerListener
{
	public void onEnchantFinish(Player player, ItemInstance item, boolean succeed);
}

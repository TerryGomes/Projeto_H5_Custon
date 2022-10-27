package l2mv.gameserver.listener.inventory;

import l2mv.commons.listener.Listener;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	public void onEquip(int slot, ItemInstance item, Playable actor);

	public void onUnequip(int slot, ItemInstance item, Playable actor);
}

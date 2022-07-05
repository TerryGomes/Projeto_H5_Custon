package l2f.gameserver.listener.inventory;

import l2f.commons.listener.Listener;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	public void onEquip(int slot, ItemInstance item, Playable actor);

	public void onUnequip(int slot, ItemInstance item, Playable actor);
}

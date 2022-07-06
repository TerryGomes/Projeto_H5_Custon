package l2mv.gameserver.model.items.attachment;

import l2mv.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment
{
	boolean canPickUp(Player player);

	void pickUp(Player player);
}

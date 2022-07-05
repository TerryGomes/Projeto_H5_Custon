package l2f.gameserver.model.items.attachment;

import l2f.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment
{
	boolean canPickUp(Player player);

	void pickUp(Player player);
}

package l2f.gameserver.model.entity.tournament.permission;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.permission.actor.player.AttributeItemPermission;

public class TournamentAttributeItemPermission implements AttributeItemPermission
{
	@Override
	public boolean canAttributeItem(Player actor, ItemInstance item, ItemInstance stone)
	{
		return ConfigHolder.getBool("TournamentAllowMakingAttribute");
	}

	@Override
	public void sendPermissionDeniedError(Player actor, ItemInstance item, ItemInstance stone)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.AttributeItem", new Object[0]);
	}
}

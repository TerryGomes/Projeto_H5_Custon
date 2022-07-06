package l2mv.gameserver.model.entity.tournament.permission;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.permission.actor.player.AttributeItemPermission;

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

package l2f.gameserver.model.entity.tournament.permission;

import l2f.gameserver.data.StringHolder;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.permission.actor.player.LoseItemPermission;

public class TournamentLoseItemPermission implements LoseItemPermission
{
	@Override
	public boolean canLoseItem(Playable actor, ItemInstance item)
	{
		return false;
	}

	@Override
	public String getPermissionDeniedError(Playable actor, ItemInstance item)
	{
		return StringHolder.getNotNull(actor.getPlayer(), "Tournament.NotAllowed.LoseItem", new Object[0]);
	}
}

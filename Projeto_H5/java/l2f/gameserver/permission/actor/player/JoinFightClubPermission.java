package l2f.gameserver.permission.actor.player;

import l2f.gameserver.model.Player;
import l2f.gameserver.permission.PlayerPermission;

public interface JoinFightClubPermission extends PlayerPermission
{
	boolean joinSignFightClub(Player p0);

	default void sendPermissionDeniedError(Player actor)
	{
		actor.sendMessage(getPermissionDeniedError(actor));
	}

	String getPermissionDeniedError(Player p0);
}

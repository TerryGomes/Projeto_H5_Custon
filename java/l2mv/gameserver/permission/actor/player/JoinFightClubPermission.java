package l2mv.gameserver.permission.actor.player;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.permission.PlayerPermission;

public interface JoinFightClubPermission extends PlayerPermission
{
	boolean joinSignFightClub(Player p0);

	default void sendPermissionDeniedError(Player actor)
	{
		actor.sendMessage(getPermissionDeniedError(actor));
	}

	String getPermissionDeniedError(Player p0);
}

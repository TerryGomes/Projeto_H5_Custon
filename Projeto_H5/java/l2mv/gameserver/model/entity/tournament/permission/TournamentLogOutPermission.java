package l2mv.gameserver.model.entity.tournament.permission;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.permission.actor.player.LogOutPermission;

public class TournamentLogOutPermission implements LogOutPermission
{
	@Override
	public boolean canLogOut(Player actor, boolean isRestart)
	{
		return false;
	}

	@Override
	public void sendPermissionDeniedError(Player actor, boolean isRestart)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.LogOut", new Object[0]);
	}
}

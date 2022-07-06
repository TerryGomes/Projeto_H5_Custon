package l2mv.gameserver.model.entity.tournament.permission;

import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.permission.actor.player.JoinFightClubPermission;

public class TournamentJoinFightClubPermission implements JoinFightClubPermission
{
	@Override
	public boolean joinSignFightClub(Player actor)
	{
		return false;
	}

	@Override
	public String getPermissionDeniedError(Player actor)
	{
		return StringHolder.getNotNull(actor, "Tournament.NotAllowed.JoinFightClub", new Object[0]);
	}
}

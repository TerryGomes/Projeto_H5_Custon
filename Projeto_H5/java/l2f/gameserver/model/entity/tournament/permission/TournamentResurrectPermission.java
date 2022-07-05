package l2f.gameserver.model.entity.tournament.permission;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.permission.actor.player.ResurrectPermission;

public class TournamentResurrectPermission implements ResurrectPermission
{
	@Override
	public boolean canResurrect(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		return false;
	}

	@Override
	public void sendPermissionDeniedError(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.Resurrect", new Object[0]);
	}
}

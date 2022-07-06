package l2mv.gameserver.model.entity.tournament.permission;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.permission.actor.IgnoreAttackBlockadesPermission;

public class TournamentIgnoreAttackBlockadesPermission implements IgnoreAttackBlockadesPermission
{
	@Override
	public boolean canIgnoreAttackBlockades(Creature actor, Creature target, Skill skill, boolean force)
	{
		return true;
	}
}

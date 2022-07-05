package l2f.gameserver.model.entity.tournament.permission;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.permission.actor.IgnoreAttackBlockadesPermission;

public class TournamentIgnoreAttackBlockadesPermission implements IgnoreAttackBlockadesPermission
{
	@Override
	public boolean canIgnoreAttackBlockades(Creature actor, Creature target, Skill skill, boolean force)
	{
		return true;
	}
}

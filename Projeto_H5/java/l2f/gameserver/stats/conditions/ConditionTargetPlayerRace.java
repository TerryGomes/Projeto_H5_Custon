package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.stats.Env;

public class ConditionTargetPlayerRace extends Condition
{
	private final Race _race;

	public ConditionTargetPlayerRace(String race)
	{
		_race = Race.valueOf(race.toLowerCase());
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		return target != null && target.isPlayer() && _race == ((Player) target).getRace();
	}
}
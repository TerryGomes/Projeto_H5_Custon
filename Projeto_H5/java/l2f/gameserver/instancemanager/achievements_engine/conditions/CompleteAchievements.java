package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class CompleteAchievements extends Condition
{
	public CompleteAchievements(Object value)
	{
		super(value);
		setName("Complete Achievements");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		if (player.getCompletedAchievements().size() >= val)
		{
			return true;
		}

		return false;
	}
}
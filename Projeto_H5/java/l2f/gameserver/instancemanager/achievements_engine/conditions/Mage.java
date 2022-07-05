package l2f.gameserver.instancemanager.achievements_engine.conditions;

import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class Mage extends Condition
{
	public Mage(Object value)
	{
		super(value);
		setName("Be Mage");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		if (player.isMageClass())
		{
			return true;
		}

		return false;
	}
}
package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.Player;
import l2f.gameserver.stats.Env;

public class ConditionPlayerClassId extends Condition
{
	private final int[] _classIds;

	public ConditionPlayerClassId(String[] ids)
	{
		_classIds = new int[ids.length];
		for (int i = 0; i < ids.length; i++)
		{
			_classIds[i] = Integer.parseInt(ids[i]);
		}
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if (!env.character.isPlayer())
		{
			return false;
		}

		int playerClassId = ((Player) env.character).getActiveClassId();
		for (int id : _classIds)
		{
			if (playerClassId == id)
			{
				return true;
			}
		}

		return false;
	}
}
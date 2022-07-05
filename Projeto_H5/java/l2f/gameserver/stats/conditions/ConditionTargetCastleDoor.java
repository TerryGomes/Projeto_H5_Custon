package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.stats.Env;

public class ConditionTargetCastleDoor extends Condition
{
	private final boolean _isCastleDoor;

	public ConditionTargetCastleDoor(boolean isCastleDoor)
	{
		_isCastleDoor = isCastleDoor;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target instanceof DoorInstance == _isCastleDoor;
	}
}

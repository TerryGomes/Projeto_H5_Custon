package l2f.gameserver.stats.conditions;

import l2f.gameserver.GameTimeController;
import l2f.gameserver.stats.Env;

public class ConditionGameTime extends Condition
{
	public enum CheckGameTime
	{
		NIGHT
	}

	private final CheckGameTime _check;

	private final boolean _required;

	public ConditionGameTime(CheckGameTime check, boolean required)
	{
		_check = check;
		_required = required;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		switch (_check)
		{
		case NIGHT:
			return GameTimeController.getInstance().isNowNight() == _required;
		}
		return !_required;
	}
}

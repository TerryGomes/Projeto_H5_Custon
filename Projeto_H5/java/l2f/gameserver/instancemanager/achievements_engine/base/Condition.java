package l2f.gameserver.instancemanager.achievements_engine.base;

import l2f.gameserver.model.Player;

public abstract class Condition
{
	private final Object _value;
	private String _name;

	public Condition(Object value)
	{
		_value = value;
	}

	public abstract boolean meetConditionRequirements(Player player);

	public Object getValue()
	{
		return _value;
	}

	public void setName(String s)
	{
		_name = s;
	}

	public String getName()
	{
		return _name;
	}
}
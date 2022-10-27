package l2mv.gameserver.stats.funcs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.conditions.Condition;

public final class FuncTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(FuncTemplate.class);

	public static final FuncTemplate[] EMPTY_ARRAY = new FuncTemplate[0];

	public Condition _applyCond;
	public Class<?> _func;
	public Constructor<?> _constructor;
	public Stats _stat;
	public int _order;
	public double _value;

	public FuncTemplate(Condition applyCond, String func, Stats stat, int order, double value)
	{
		_applyCond = applyCond;
		_stat = stat;
		_order = order;
		_value = value;

		try
		{
			_func = Class.forName("l2mv.gameserver.stats.funcs.Func" + func);

			_constructor = _func.getConstructor(new Class[]
			{
				Stats.class, // stats to update
				Integer.TYPE, // order of execution
				Object.class, // owner
				Double.TYPE // value for function
			});
		}
		catch (ClassNotFoundException | SecurityException | NoSuchMethodException e)
		{
			_log.error("Error while creating FuncTemplate ", e);
		}
	}

	public Func getFunc(Object owner)
	{
		try
		{
			Func f = (Func) _constructor.newInstance(_stat, _order, owner, _value);
			if (_applyCond != null)
			{
				f.setCondition(_applyCond);
			}
			return f;
		}
		catch (IllegalAccessException | InstantiationException | InvocationTargetException e)
		{
			_log.error("Error while getting Function!", e);
			return null;
		}
	}
}
package l2f.gameserver.stats.funcs;

import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.stats.conditions.Condition;

public abstract class Func implements Comparable<Func>
{
	public static final Func[] EMPTY_FUNC_ARRAY = new Func[0];
	public final Stats stat;
	public final int order;
	public final Object owner;
	public final double value;
	protected Condition cond;

	public Func(Stats stat, int order, Object owner)
	{
		this(stat, order, owner, 0.);
	}

	public Func(Stats stat, int order, Object owner, double value)
	{
		this.stat = stat;
		this.order = order;
		this.owner = owner;
		this.value = value;
	}

	public void setCondition(Condition cond)
	{
		this.cond = cond;
	}

	/**
	 * Для отладки
	 */
	public Condition getCondition()
	{
		return cond;
	}

	public abstract void calc(Env env);

	@Override
	public int compareTo(Func f) throws NullPointerException
	{
		return order - f.order;
	}
}
package l2mv.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IMMOExecutor<T extends MMOClient>
{
	public void execute(Runnable r);
}
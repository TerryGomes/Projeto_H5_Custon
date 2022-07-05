package l2f.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IMMOExecutor<T extends MMOClient>
{
	public void execute(Runnable r);
}
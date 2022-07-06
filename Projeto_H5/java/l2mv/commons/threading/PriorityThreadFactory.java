package l2mv.commons.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory
{
	private int _prio;
	private String _name;
	private AtomicInteger _threadNumber = new AtomicInteger(1);
	private ThreadGroup _group;

	public PriorityThreadFactory(String name, int prio)
	{
		_prio = prio;
		_name = name;
		_group = new ThreadGroup(_name);
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(_group, r);
		t.setName(_name + "-" + _threadNumber.getAndIncrement());
		t.setPriority(_prio);
		return t;
	}

	public ThreadGroup getGroup()
	{
		return _group;
	}
}
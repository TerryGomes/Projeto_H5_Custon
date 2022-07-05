package l2f.gameserver.utils;

import java.util.LinkedList;

public class MaxElementsList<E> extends LinkedList<E>
{
	private final int MAX;

	public MaxElementsList(int maxElements)
	{
		MAX = maxElements;
	}

	/**
	 * If size will reach MAX, removing first Element and adding new as Last
	 */
	@Override
	public boolean add(E e)
	{
		if (size() + 1 > MAX)
		{
			removeFirst();
		}
		super.addLast(e);
		return true;
	}
}

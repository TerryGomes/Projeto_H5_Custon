package l2mv.commons.collections;

import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class EmptyIterator<E> implements Iterator<E>
{
	private static Iterator INSTANCE = new EmptyIterator();

	@SuppressWarnings("unchecked")
	public static <E> Iterator<E> getInstance()
	{
		return INSTANCE;
	}

	private EmptyIterator()
	{
	}

	@Override
	public boolean hasNext()
	{
		return false;
	}

	@Override
	public E next()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}

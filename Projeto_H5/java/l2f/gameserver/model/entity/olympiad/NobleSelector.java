package l2f.gameserver.model.entity.olympiad;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import l2f.commons.util.Rnd;

public class NobleSelector<E>
{
	private class Node<T>
	{
		private final T value;
		private final int points;
		private int weight = 0;

		public Node(T value, int points)
		{
			this.value = value;
			this.points = points;
		}
	}

	private final List<Node<E>> _nodes;
	private Node<E> _current = null;
	private int _totalWeight = 0;

	public NobleSelector()
	{
		_nodes = new ArrayList<Node<E>>();
	}

	public NobleSelector(int initialCapacity)
	{
		_nodes = new ArrayList<Node<E>>(initialCapacity);
	}

	public final void add(E value, int points)
	{
		_nodes.add(new Node<E>(value, points));
	}

	public final int size()
	{
		return _nodes.size();
	}

	public final void reset()
	{
		_current = null;
	}

	public final E select()
	{
		final Node<E> result = selectImpl();
		return result != null ? result.value : null;
	}

	public final int testSelect()
	{
		final Node<E> result = selectImpl();
		return result != null ? result.points : -1;
	}

	private final Node<E> selectImpl()
	{
		if (_current == null)
		{
			return init();
		}

		Node<E> n;
		int random = Rnd.get(_totalWeight);
		for (int i = _nodes.size(); --i >= 0;)
		{
			n = _nodes.get(i);
			random -= n.weight;
			if (random < 0)
			{
				if (_nodes.remove(i) != n)
				{
					throw new ConcurrentModificationException();
				}
				else
				{
					return n;
				}
			}
		}
		return null;
	}

	private final Node<E> init()
	{
		final int size = _nodes.size();
		if (size < 2)
		{
			return null;
		}

		_current = _nodes.remove(Rnd.get(size));
		_totalWeight = 0;
		for (Node<E> n : _nodes)
		{
			_totalWeight += getWeight(n, _current);
		}

		if (size != _nodes.size() + 1)
		{
			throw new ConcurrentModificationException();
		}

		return _current;
	}

	private final int getWeight(Node<E> n, Node<E> base)
	{
		final int delta = Math.abs(n.points - base.points);

		if (delta < 20)
		{
			n.weight = 8;
		}
		else if (delta < 40)
		{
			n.weight = 6;
		}
		else if (delta < 40)
		{
			n.weight = 4;
		}
		else if (delta < 60)
		{
			n.weight = 2;
		}
		else
		{
			n.weight = 1;
		}

		if ((n.points > 50) == (base.points > 50))
		{
			n.weight *= 10;
		}

		return n.weight;
	}
}
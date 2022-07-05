package l2f.commons.math.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2f.commons.util.Rnd;

public class RndSelector<E>
{
	private class RndNode<T> implements Comparable<RndNode<T>>
	{
		private final T value;
		private final int weight;

		public RndNode(T value, int weight)
		{
			this.value = value;
			this.weight = weight;
		}

		@Override
		public int compareTo(RndNode<T> o)
		{
			return this.weight - weight;
		}
	}

	private int totalWeight = 0;
	private final List<RndNode<E>> nodes;

	public RndSelector()
	{
		nodes = new ArrayList<RndNode<E>>();
	}

	public RndSelector(int initialCapacity)
	{
		nodes = new ArrayList<RndNode<E>>(initialCapacity);
	}

	public void add(E value, int weight)
	{
		if (value == null || weight <= 0)
		{
			return;
		}
		totalWeight += weight;
		nodes.add(new RndNode<E>(value, weight));
	}

	/**
	 * ?????? ???? ?? ????????? ??? null, null ???????? ?????? ???? ????? ????? ???? ????????? ?????? maxWeight
	 */
	public E chance(int maxWeight)
	{
		if (maxWeight <= 0)
		{
			return null;
		}

		Collections.sort(nodes);

		int r = Rnd.get(maxWeight);
		int weight = 0;
		for (int i = 0; i < nodes.size(); i++)
		{
			if ((weight += nodes.get(i).weight) > r)
			{
				return nodes.get(i).value;
			}
		}
		return null;
	}

	/**
	 * ?????? ???? ?? ????????? ??? null, null ???????? ?????? ???? ????? ????? ???? ????????? ?????? 100
	 */
	public E chance()
	{
		return chance(100);
	}

	/**
	 * ?????? ???? ?? ?????????
	 */
	public E select()
	{
		return chance(totalWeight);
	}

	public void clear()
	{
		totalWeight = 0;
		nodes.clear();
	}
}
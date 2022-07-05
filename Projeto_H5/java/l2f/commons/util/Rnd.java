package l2f.commons.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;

public class Rnd
{
	private Rnd()
	{
	}

	private static final Random random = new Random();

	private static final ThreadLocal<RandomGenerator> rnd = new ThreadLocalGeneratorHolder();

	private static AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

	static final class ThreadLocalGeneratorHolder extends ThreadLocal<RandomGenerator>
	{
		@Override
		public RandomGenerator initialValue()
		{
			return new MersenneTwister(seedUniquifier.getAndIncrement() + System.nanoTime());
		}
	}

	private static RandomGenerator rnd()
	{
		return rnd.get();
	}

	public static double get() // get random number from 0 to 1
	{
		return rnd().nextDouble();
	}

	/**
	 * Gets a random number from 0(inclusive) to n(exclusive)
	 *
	 * @param n The superior limit (exclusive)
	 * @return A number from 0 to n-1
	 */
	public static int get(int n)
	{
		return rnd().nextInt(n);
	}

	public static long get(long n)
	{
		return (long) (rnd().nextDouble() * n);
	}

	public static int get(int min, int max) // get random number from min to max (not max-1 !)
	{
		return min + get(max - min + 1);
	}

	public static long get(long min, long max) // get random number from min to max (not max-1 !)
	{
		return min + get(max - min + 1);
	}

	public static int nextInt()
	{
		return rnd().nextInt();
	}

	public static double nextDouble()
	{
		return rnd().nextDouble();
	}

	public static double nextGaussian()
	{
		return rnd().nextGaussian();
	}

	public static boolean nextBoolean()
	{
		return rnd().nextBoolean();
	}

	/**
	 * ??????????? ??? ???????? ??????.<br>
	 * ????????????? ? ????????????? ?????? Rnd.get()
	 * @param chance ? ????????? ?? 0 ?? 100
	 * @return true ? ?????? ????????? ?????????.
	 * <li>???? chance <= 0, ?????? false
	 * <li>???? chance >= 100, ?????? true
	 */
	public static boolean chance(int chance)
	{
		return chance >= 1 && (chance > 99 || rnd().nextInt(99) + 1 <= chance);
	}

	/**
	 * ??????????? ??? ???????? ??????.<br>
	 * ????????????? ? ????????????? ?????? Rnd.get() ???? ????? ????? ????????? ?????
	 * @param chance ? ????????? ?? 0 ?? 100
	 * @return true ? ?????? ????????? ?????????.
	 * <li>???? chance <= 0, ?????? false
	 * <li>???? chance >= 100, ?????? true
	 */
	public static boolean chance(double chance)
	{
		return rnd().nextDouble() <= chance / 100.;
	}

	public static <E> E get(E[] list)
	{
		if (list.length == 0)
		{
			return null;
		}

		return list[get(list.length)];
	}

	public static int get(int[] list)
	{
		if (list.length == 0)
		{
			return 0;
		}

		return list[get(list.length)];
	}

	public static <E> E get(List<E> list)
	{
		if (list.size() == 0)
		{
			return null;
		}

		return list.get(get(list.size()));
	}

	public static byte[] nextBytes(byte[] array)
	{
		random.nextBytes(array);
		return array;
	}

	public static int nextInt(int n)
	{
		if (n < 0)
		{
			return random.nextInt(-n) * -1;
		}
		if (n == 0)
		{
			return 0;
		}
		return random.nextInt(n);
	}
}
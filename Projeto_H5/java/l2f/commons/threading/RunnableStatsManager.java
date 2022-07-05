/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.commons.threading;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public final class RunnableStatsManager
{
	/**
	 * Field _instance.
	 */
	private static final RunnableStatsManager _instance = new RunnableStatsManager();

	/**
	 * Method getInstance.
	 * @return RunnableStatsManager
	 */
	public static final RunnableStatsManager getInstance()
	{
		return _instance;
	}

	/**
	 * Field classStats.
	 */
	final Map<Class<?>, ClassStat> classStats = new HashMap<>();
	/**
	 * Field lock.
	 */
	private final Lock lock = new ReentrantLock();

	/**
	 * @author Mobius
	 */
	private class ClassStat
	{
		/**
		 * Field clazz.
		 */
		final Class<?> clazz;
		/**
		 * Field runCount.
		 */
		long runCount = 0;
		/**
		 * Field runTime.
		 */
		long runTime = 0;
		/**
		 * Field minTime.
		 */
		long minTime = Long.MAX_VALUE;
		/**
		 * Field maxTime.
		 */
		long maxTime = Long.MIN_VALUE;

		/**
		 * Constructor for ClassStat.
		 * @param cl Class<?>
		 */
		ClassStat(Class<?> cl)
		{
			clazz = cl;
			classStats.put(cl, this);
		}
	}

	/**
	 * Method handleStats.
	 * @param cl Class<?>
	 * @param runTime long
	 */
	public void handleStats(Class<?> cl, long runTime)
	{
		try
		{
			lock.lock();
			ClassStat stat = classStats.get(cl);
			if (stat == null)
			{
				stat = new ClassStat(cl);
			}
			stat.runCount++;
			stat.runTime += runTime;
			if (stat.minTime > runTime)
			{
				stat.minTime = runTime;
			}
			if (stat.maxTime < runTime)
			{
				stat.maxTime = runTime;
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Method getSortedClassStats.
	 * @return List<ClassStat>
	 */
	private List<ClassStat> getSortedClassStats()
	{
		List<ClassStat> result = Collections.emptyList();
		try
		{
			lock.lock();
			result = Arrays.asList(classStats.values().toArray(new ClassStat[classStats.size()]));
		}
		finally
		{
			lock.unlock();
		}
		Collections.sort(result, new Comparator<ClassStat>()
		{
			@Override
			public int compare(ClassStat c1, ClassStat c2)
			{
				if (c1.maxTime < c2.maxTime)
				{
					return 1;
				}
				if (c1.maxTime == c2.maxTime)
				{
					return 0;
				}
				return -1;
			}
		});
		return result;
	}

	/**
	 * Method getStats.
	 * @return CharSequence
	 */
	public CharSequence getStats()
	{
		StringBuilder list = new StringBuilder();
		List<ClassStat> stats = getSortedClassStats();
		for (ClassStat stat : stats)
		{
			list.append(stat.clazz.getName()).append(":\n");
			list.append("\tRun: ............ ").append(stat.runCount).append('\n');
			list.append("\tTime: ........... ").append(stat.runTime).append('\n');
			list.append("\tMin: ............ ").append(stat.minTime).append('\n');
			list.append("\tMax: ............ ").append(stat.maxTime).append('\n');
			list.append("\tAverage: ........ ").append(stat.runTime / stat.runCount).append('\n');
		}
		return list;
	}
}
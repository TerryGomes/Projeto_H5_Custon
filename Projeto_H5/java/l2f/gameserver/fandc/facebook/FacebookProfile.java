package l2f.gameserver.fandc.facebook;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import l2f.gameserver.ConfigHolder;

public class FacebookProfile
{
	private final String id;
	private final String name;
	private long lastCompletedTaskDate;
	private final TObjectIntHashMap<FacebookActionType> positivePoints;
	private final TObjectIntHashMap<FacebookActionType> negativePoints;

	public FacebookProfile(String id, String name)
	{
		this.id = id;
		this.name = name;
		positivePoints = new TObjectIntHashMap<>(0);
		negativePoints = new TObjectIntHashMap<>(0);
	}

	public FacebookProfile(String id, String name, long lastCompletedTaskDate, TObjectIntHashMap<FacebookActionType> positivePoints, TObjectIntHashMap<FacebookActionType> negativePoints)
	{
		this.id = id;
		this.name = name;
		this.lastCompletedTaskDate = lastCompletedTaskDate;
		this.positivePoints = positivePoints;
		this.negativePoints = negativePoints;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setLastCompletedTaskDate(long lastCompletedTaskDate)
	{
		this.lastCompletedTaskDate = lastCompletedTaskDate;
	}

	public long getLastCompletedTaskDate()
	{
		return lastCompletedTaskDate;
	}

	public void addPositivePoint(FacebookActionType type, boolean saveInDatabase)
	{
		final int points = positivePoints.get(type);
		if (points == Constants.DEFAULT_INT_NO_ENTRY_VALUE)
		{
			positivePoints.put(type, 1);
		}
		else
		{
			positivePoints.put(type, points + 1);
		}
		if (saveInDatabase)
		{
			FacebookDatabaseHandler.replaceFacebookProfile(this);
		}
	}

	public int getPositivePoints(FacebookActionType type)
	{
		final int value = positivePoints.get(type);
		if (value == Constants.DEFAULT_INT_NO_ENTRY_VALUE)
		{
			return 0;
		}
		return value;
	}

	public TObjectIntHashMap<FacebookActionType> getPositivePointsForIterate()
	{
		return positivePoints;
	}

	public void addNegativePoint(FacebookActionType type, boolean saveInDatabase)
	{
		final int points = negativePoints.get(type);
		if (points == Constants.DEFAULT_INT_NO_ENTRY_VALUE)
		{
			negativePoints.put(type, 1);
		}
		else
		{
			negativePoints.put(type, points + 1);
		}
		if (saveInDatabase)
		{
			FacebookDatabaseHandler.replaceFacebookProfile(this);
		}
	}

	public void removeNegativePoint(FacebookActionType type, boolean saveInDatabase)
	{
		final int points = negativePoints.get(type);
		if (points == Constants.DEFAULT_INT_NO_ENTRY_VALUE)
		{
			return;
		}

		if (points == 1)
		{
			negativePoints.remove(type);
		}
		else
		{
			negativePoints.put(type, points - 1);
		}
		if (saveInDatabase)
		{
			FacebookDatabaseHandler.replaceFacebookProfile(this);
		}
	}

	public int getNegativePoints(FacebookActionType type)
	{
		final int value = negativePoints.get(type);
		if (value == Constants.DEFAULT_INT_NO_ENTRY_VALUE)
		{
			return 0;
		}
		return value;
	}

	public boolean hasNegativePoints()
	{
		return !negativePoints.isEmpty();
	}

	public Set<FacebookActionType> getNegativePointTypesForIterate()
	{
		return negativePoints.keySet();
	}

	public TObjectIntHashMap<FacebookActionType> getNegativePointsForIterate()
	{
		return negativePoints;
	}

	public long getDelayEndDate()
	{
		if (lastCompletedTaskDate < 0L)
		{
			return -1L;
		}

		final long endDate = lastCompletedTaskDate + ConfigHolder.getMillis("FacebookDelayBetweenTasks", TimeUnit.SECONDS);
		if (endDate < System.currentTimeMillis())
		{
			return -1L;
		}
		return endDate;
	}

	public boolean hasTaskDelay()
	{
		return lastCompletedTaskDate >= 0L && lastCompletedTaskDate + ConfigHolder.getMillis("FacebookDelayBetweenTasks", TimeUnit.SECONDS) > System.currentTimeMillis();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FacebookProfile && id.equals(((FacebookProfile) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public String toString()
	{
		return "FacebookProfile{id='" + id + '\'' + ", name='" + name + '\'' + ", lastCompletedTaskDate=" + lastCompletedTaskDate + ", positivePoints=" + positivePoints + ", negativePoints=" + negativePoints
					+ '}';
	}
}

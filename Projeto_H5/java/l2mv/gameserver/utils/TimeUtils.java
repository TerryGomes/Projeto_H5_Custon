package l2mv.gameserver.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeUtils
{
	public static final long SECOND_IN_MILLIS = 1000L;
	public static final long MINUTE_IN_MILLIS = 60000L;
	public static final long HOUR_IN_MILLIS = 3600000L;
	public static final long DAY_IN_MILLIS = 86400000L;
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");
	private static final TimeUnit[] TIME_UNITS_DESCENDING = new TimeUnit[]
	{
		TimeUnit.DAYS,
		TimeUnit.HOURS,
		TimeUnit.MINUTES,
		TimeUnit.SECONDS,
		TimeUnit.MILLISECONDS
	};

	public static String toSimpleFormat(Calendar cal)
	{
		return SIMPLE_FORMAT.format(cal.getTime());
	}

	public static String toSimpleFormat(long cal)
	{
		return SIMPLE_FORMAT.format(cal);
	}

	public static String timeLeftToEpoch(long epochTimeMillis)
	{
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(epochTimeMillis - System.currentTimeMillis());
		return minutesToFullString((int) minutes);
	}

	public static String convertDateToString(long time)
	{
		Date dt = new Date(time);
		String stringDate = SIMPLE_FORMAT.format(dt);
		return stringDate;
	}

	public static String minutesToFullString(long period)
	{
		return minutesToFullString((int) period);
	}

	public static String minutesToFullString(int period)
	{
		StringBuilder sb = new StringBuilder();

		// парсим дни
		if (period > 1440) // больше 1 суток
		{
			sb.append((period - (period % 1440)) / 1440).append(" D.");
			period = period % 1440;
		}

		// парсим часы
		if (period > 60) // остаток более 1 часа
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append((period - (period % 60)) / 60).append(" H.");

			period = period % 60;
		}

		// парсим остаток
		if (period > 0) // есть остаток
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append(period).append(" Min.");
		}
		if (sb.length() < 1)
		{
			sb.append("less than 1 minute.");
		}

		return sb.toString();
	}

	public static long getMilisecondsToNextDay(List<Integer> days, int hourOfTheEvent)
	{
		return getMilisecondsToNextDay(days, hourOfTheEvent, 5);
	}

	public static long getMilisecondsToNextDay(List<Integer> days, int hourOfTheEvent, int minuteOfTheEvent)
	{
		int[] hours = new int[days.size()];
		for (int i = 0; i < hours.length; i++)
		{
			hours[i] = days.get(i).intValue();
		}
		return getMilisecondsToNextDay(hours, hourOfTheEvent, minuteOfTheEvent);
	}

	/**
	 * Getting Time in Milliseconds to the closest day.
	 * If every day already passed, it's getting closest day of next month
	 * Event Time: Millisecond: 0, Second: 0, Minute: 0, Hour: hourOfTheEvent
	 * @param days Array of specific days in the month
	 * @param hourOfTheEvent hour of the day, when clock will stop
	 * @param minuteOfTheEvent
	 * @return Time in milliseconds to that day
	 */
	public static long getMilisecondsToNextDay(int[] days, int hourOfTheEvent, int minuteOfTheEvent)
	{
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(Calendar.SECOND, 0);
		tempCalendar.set(Calendar.MILLISECOND, 0);
		tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfTheEvent);
		tempCalendar.set(Calendar.MINUTE, minuteOfTheEvent);

		final long currentTime = System.currentTimeMillis();
		Calendar eventCalendar = Calendar.getInstance();

		boolean found = false;
		long smallest = Long.MAX_VALUE;// In case, we need to make it in next month

		for (int day : days)
		{
			tempCalendar.set(Calendar.DAY_OF_MONTH, day);
			long timeInMillis = tempCalendar.getTimeInMillis();

			// If time is smaller than current
			if (timeInMillis <= currentTime)
			{
				if (timeInMillis < smallest)
				{
					smallest = timeInMillis;
				}
				continue;
			}

			// If event time wasn't chosen yet or its smaller than current Event Time
			if (!found || timeInMillis < eventCalendar.getTimeInMillis())
			{
				found = true;
				eventCalendar.setTimeInMillis(timeInMillis);
			}
		}

		if (!found)
		{
			eventCalendar.setTimeInMillis(smallest);// Smallest time + One Month
			eventCalendar.add(Calendar.MONTH, 1);
		}
		return eventCalendar.getTimeInMillis() - currentTime;
	}

	public static long addDay(int count)
	{
		long DAY = count * 60 * 60 * 24 * 1000L;
		return DAY;
	}

	public static long addHours(int count)
	{
		long HOUR = count * 60 * 60 * 1000L;
		return HOUR;
	}

	public static long addMinutes(int count)
	{
		long MINUTE = count * 60 * 1000L;
		return MINUTE;
	}

	public static long addSecond(int count)
	{
		long SECONDS = count * 1000L;
		return SECONDS;
	}

	public static String formatTime(int time)
	{
		return formatTime(time, true);
	}

	public static String formatTime(int time, boolean cut)
	{
		int days = 0;
		int hours = 0;
		int minutes = 0;

		days = time / 86400;
		hours = (time - days * 24 * 3600) / 3600;
		minutes = (time - days * 24 * 3600 - hours * 3600) / 60;

		String result;

		if (days >= 1)
		{
			if ((hours < 1) || (cut))
			{
				result = days + " " + Util.declension(days, DeclensionKey.DAYS);
			}
			else
			{
				result = days + " " + Util.declension(days, DeclensionKey.DAYS) + " " + hours + " " + Util.declension(hours, DeclensionKey.HOUR);
			}
		}
		else if (hours >= 1)
		{
			if ((minutes < 1) || (cut))
			{
				result = hours + " " + Util.declension(hours, DeclensionKey.HOUR);
			}
			else
			{
				result = hours + " " + Util.declension(hours, DeclensionKey.HOUR) + " " + minutes + " " + Util.declension(minutes, DeclensionKey.MINUTES);
			}
		}
		else
		{
			result = minutes + " " + Util.declension(minutes, DeclensionKey.MINUTES);
		}
		return result;
	}

	public static Map<TimeUnit, Long> getDelayTillTimeUnits(long millisToEvent, boolean decreaseByUnit, boolean allowZeroValues)
	{
		final Map<TimeUnit, Long> result = new EnumMap<TimeUnit, Long>(TimeUnit.class);
		for (TimeUnit timeUnit : TIME_UNITS_DESCENDING)
		{
			if (millisToEvent >= timeUnit.toMillis(1L))
			{
				final long valueInUnit = (long) Math.floor(millisToEvent / timeUnit.toMillis(1L));
				if (decreaseByUnit)
				{
					millisToEvent -= valueInUnit * timeUnit.toMillis(1L);
				}
				result.put(timeUnit, valueInUnit);
			}
			else if (allowZeroValues)
			{
				result.put(timeUnit, 0L);
			}
		}
		return result;
	}
}

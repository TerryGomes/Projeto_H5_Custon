/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * This file is part of Vote Rewarding System.
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.fandc.votingengine;

import java.util.Calendar;

/**
 * @author UnAfraid
 */
public class VotingUtil
{
	public static int findIndexOfNonDigit(String string)
	{
		final char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isDigit(chars[i]))
			{
				continue;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Parses patterns like:
	 * <ul>
	 * <li>1min or 10mins</li>
	 * <li>1day or 10days</li>
	 * <li>1week or 4weeks</li>
	 * <li>1month or 12months</li>
	 * <li>1year or 5years</li>
	 * </ul>
	 *
	 * @param datePattern
	 * @return the time converted by the date pattern given.
	 */
	public static long parseTimeString(String datePattern)
	{
		final int index = findIndexOfNonDigit(datePattern);
		if (index == -1)
		{
			throw new IllegalStateException("Incorrect time format given: " + datePattern);
		}
		try
		{
			final int val = Integer.parseInt(datePattern.substring(0, index));
			final String type = datePattern.substring(index);
			final Calendar cal = Calendar.getInstance();
			switch (type.toLowerCase())
			{
			case "sec":
			case "secs":
			{
				cal.add(Calendar.SECOND, val);
				break;
			}
			case "min":
			case "mins":
			{
				cal.add(Calendar.MINUTE, val);
				break;
			}
			case "hour":
			case "hours":
			{
				cal.add(Calendar.HOUR, val);
				break;
			}
			case "day":
			case "days":
			{
				cal.add(Calendar.DAY_OF_MONTH, val);
				break;
			}
			case "week":
			case "weeks":
			{
				cal.add(Calendar.WEEK_OF_MONTH, val);
				break;
			}
			case "month":
			case "months":
			{
				cal.add(Calendar.MONTH, val);
				break;
			}
			case "year":
			case "years":
			{
				cal.add(Calendar.YEAR, val);
				break;
			}
			default:
			{
				throw new IllegalStateException("Incorrect format: " + type + " !!");
			}
			}
			return cal.getTimeInMillis() - System.currentTimeMillis();
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Incorrect time format given: " + datePattern + " val: " + datePattern.substring(0, index));
		}
	}
}

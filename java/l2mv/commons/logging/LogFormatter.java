package l2mv.commons.logging;

import java.util.Calendar;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter
{
	String newline = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record)
	{
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(record.getMillis());
		String text = "[" + getDate(date, Calendar.MONTH) + ".";
		text += getDate(date, Calendar.DAY_OF_MONTH) + ".";
		text += getDate(date, Calendar.YEAR);
		text += " ";
		text += getDate(date, Calendar.HOUR_OF_DAY) + ":";
		text += getDate(date, Calendar.MINUTE) + ":";
		text += getDate(date, Calendar.SECOND) + "]";
		text += " " + record.getMessage();
		text += newline;
		return text;
	}

	private String getDate(Calendar c, int i)
	{
		int intResult = c.get(i);
		if (i == Calendar.MONTH)
		{
			intResult++;
		}
		String result = String.valueOf(intResult);
		if (result.length() == 4)
		{
			result = result.substring(2);
		}
		if (result.length() == 1)
		{
			result = "0" + result;
		}
		return result;
	}
}

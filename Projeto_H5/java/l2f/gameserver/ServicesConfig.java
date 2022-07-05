package l2f.gameserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.configuration.ExProperties;

public class ServicesConfig
{
	private static final Logger _log = LoggerFactory.getLogger(ServicesConfig.class);

	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private static ConcurrentHashMap<String, String> properties = new ConcurrentHashMap();

	public static void load()
	{
		File _content = new File(Config.DATAPACK_ROOT, "config/services");
		if (!_content.exists())
		{
			_log.warn("./config/services not exists! Config not loaded!");
		}
		else
		{
			loadFile(_content.listFiles());
		}
		_log.info("Config services: Loaded " + properties.size() + " properties.");
	}

	public static void reload()
	{
		properties.clear();
		load();
	}

	private static void loadFile(File[] _content)
	{
		for (File _file : _content)
		{
			if (_file.isHidden())
			{
				continue;
			}
			if ((_file.isDirectory()) && (!_file.getName().contains("default")))
			{
				loadFile(_file.listFiles());
			}
			else if ((_file.getName().endsWith(".ini")) || (_file.getName().endsWith(".properties")))
			{
				loadProperties(_file);
			}
		}
	}

	private static void loadProperties(File _file)
	{
		ExProperties p = new ExProperties();
		try
		{
			p.load(_file);
			for (String name : p.stringPropertyNames())
			{
				if (properties.get(name) != null)
				{
					properties.replace(name, p.getProperty(name).trim());
				}
				else if (p.getProperty(name) == null)
				{
					_log.info("Null property for key " + name);
				}
				else
				{
					properties.put(name, p.getProperty(name).trim());
				}
			}
			p.clear();
		}
		catch (IOException e)
		{
			_log.error("Error loading config : " + _file.getName() + '!', e);
		}
	}

	public static String get(String name, String defaultValue)
	{
		String val;
		try
		{
			val = properties.get(name);
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static boolean get(String name, boolean defaultValue)
	{
		boolean val;
		try
		{
			val = ExProperties.parseBoolean(properties.get(name));
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static int get(String name, int defaultValue)
	{
		int val;
		try
		{
			val = Integer.parseInt(properties.get(name));
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static long get(String name, long defaultValue)
	{
		long val;
		try
		{
			val = Long.parseLong(properties.get(name));
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static double get(String name, double defaultValue)
	{
		double val;
		try
		{
			val = Double.parseDouble(properties.get(name));
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static String[] get(String name, String[] defaultValue)
	{
		String[] val;
		try
		{
			val = properties.get(name).split("[\\s,;]+");
		}
		catch (Exception e)
		{
			val = defaultValue;
		}
		return val;
	}

	public static boolean[] get(String name, boolean[] defaultValue)
	{
		boolean[] val = defaultValue;

		String[] values = get(name, new String[0]);
		val = new boolean[values.length];
		for (int i = 0; i < val.length; i++)
		{
			val[i] = ExProperties.parseBoolean(values[i]);
		}
		return val;
	}

	public static int[] get(String name, int[] defaultValue)
	{
		int[] val = defaultValue;

		String[] values = get(name, new String[0]);
		val = new int[values.length];
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Integer.parseInt(values[i]);
		}
		return val;
	}

	public static long[] get(String name, long[] defaultValue)
	{
		long[] val = defaultValue;

		String[] values = get(name, new String[0]);
		val = new long[values.length];
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Long.parseLong(values[i]);
		}
		return val;
	}

	public static double[] get(String name, double[] defaultValue)
	{
		double[] val = defaultValue;

		String[] values = get(name, new String[0]);
		val = new double[values.length];
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Double.parseDouble(values[i]);
		}
		return val;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> get(String name, HashMap<String, String> defaultValue)
	{
		@SuppressWarnings("rawtypes")
		Map values = defaultValue;

		String[] propertySplit = get(name, new String[0]);
		for (String element : propertySplit)
		{
			String[] vals = element.split(":");
			if (vals.length != 2)
			{
				continue;
			}
			try
			{
				values.put(vals[0], vals[1]);
			}
			catch (Exception e)
			{
				//
			}
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, Integer> getII(String name, HashMap<Integer, Integer> defaultValue)
	{
		@SuppressWarnings("rawtypes")
		Map values = defaultValue;

		String[] propertySplit = get(name, new String[0]);
		for (String element : propertySplit)
		{
			String[] vals = element.split(":");
			if (vals.length != 2)
			{
				continue;
			}
			try
			{
				values.put(Integer.valueOf(Integer.parseInt(vals[0])), Integer.valueOf(Integer.parseInt(vals[1])));
			}
			catch (NumberFormatException e)
			{
				//
			}
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, Double> getID(String name, HashMap<Integer, Double> defaultValue)
	{
		@SuppressWarnings("rawtypes")
		Map values = defaultValue;

		String[] propertySplit = get(name, new String[0]);
		for (String element : propertySplit)
		{
			String[] vals = element.split(":");
			if (vals.length != 2)
			{
				continue;
			}
			try
			{
				values.put(Integer.valueOf(Integer.parseInt(vals[0])), Double.valueOf(Double.parseDouble(vals[1])));
			}
			catch (NumberFormatException e)
			{
				//
			}
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, Long> getIL(String name, HashMap<Integer, Long> defaultValue)
	{
		@SuppressWarnings("rawtypes")
		Map values = defaultValue;

		String[] propertySplit = get(name, new String[0]);
		for (String element : propertySplit)
		{
			String[] vals = element.split(":");
			if (vals.length != 2)
			{
				continue;
			}
			try
			{
				values.put(Integer.valueOf(Integer.parseInt(vals[0])), Long.valueOf(Long.parseLong(vals[1])));
			}
			catch (NumberFormatException e)
			{
				//
			}
		}
		return values;
	}
}
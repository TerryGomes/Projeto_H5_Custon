package l2mv.gameserver.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.Location;

public class ConfigParser
{
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	private static final String COMMENT_START_TEXT = "//!";
	public static final String[] EMPTY_ARRAY = new String[0];
	public static final String[][] EMPTY_MULTI_ARRAY = new String[0][0];

	public static Map<String, String> load()
	{
		final Map<String, String> configs = new HashMap<String, String>();
		loadFilesFromFolder(new File("config/"), configs);
		LOG.info("Loaded " + configs.size() + " Configs!");
		return configs;
	}

	public static Object parseObject(String value, Class type)
	{
		if (type == Integer.class || type == Integer.TYPE)
		{
			return Integer.parseInt(value);
		}
		if (type == Long.class || type == Long.TYPE)
		{
			return Long.parseLong(value);
		}
		if (type == Double.class || type == Double.TYPE)
		{
			return Double.parseDouble(value);
		}
		if (type == Boolean.class || type == Boolean.TYPE)
		{
			return Boolean.parseBoolean(value);
		}
		if (type == String.class)
		{
			return value;
		}
		if (type == File.class)
		{
			return new File(value);
		}
		if (type == Pattern.class)
		{
			return Pattern.compile(value);
		}

		if (type == Location.class)
		{
			final int[] array = (int[]) parseObject(value, int[].class);
			if (array.length == 3)
			{
				return new Location(array[0], array[1], array[2]);
			}
			if (array.length == 4)
			{
				return new Location(array[0], array[1], array[2], array[3]);
			}
			return new Location();
		}
		else
		{
			if (type == AbnormalEffect.class)
			{
				try
				{
					return AbnormalEffect.getByName(value);
				}
				catch (NoSuchElementException ex)
				{
				}
			}

			if (Enum.class.isAssignableFrom(type))
			{
				return Enum.valueOf(type, value);
			}

			if (!type.isArray())
			{
				LOG.error("Config type " + type + " not Handled!");
				return null;
			}

			if (type.getComponentType().isArray())
			{
				if (type == String[][].class)
				{
					return parseDoubleArrayToString(value);
				}
				return parseDoubleArray(value, (Class<Object>) type);
			}
			else
			{
				if (type == String[].class)
				{
					return parseArrayToString(value);
				}
				return parseArray(value, (Class<Object>) type);
			}
		}
	}

	public static <E, T> Map<E, T> parseMap(String config, Class<E> firstType, Class<T> secondType)
	{
		final Map<E, T> map = new HashMap<E, T>();
		if (config.trim().isEmpty())
		{
			return map;
		}

		final String[] arrayRecordNoSplit = config.split(ConfigHolder.ARRAY_SPLITS[0]);
		for (String element : arrayRecordNoSplit)
		{
			final String[] recordNoSplit = element.trim().split(ConfigHolder.ARRAY_SPLITS[1]);
			if (recordNoSplit.length < 2)
			{
				throw new AssertionError();
			}

			final E firstValue = (E) parseObject(recordNoSplit[0].trim(), firstType);
			final T secondValue = (T) parseObject(recordNoSplit[1].trim(), secondType);
			map.put(firstValue, secondValue);
		}
		return map;
	}

	@Nullable
	public static <E, T> Map.Entry<E, T> parseMapEntry(String config, Class<E> firstType, Class<T> secondType)
	{
		if (config.trim().isEmpty())
		{
			return null;
		}

		final String[] arrayRecord = config.split(";");
		final E firstValue = (E) parseObject(arrayRecord[0].trim(), firstType);
		final T secondValue = (T) parseObject(arrayRecord[1].trim(), secondType);
		return new AbstractMap.SimpleEntry<E, T>(firstValue, secondValue);
	}

	@Nullable
	public static StatsSet parseStatsSet(String config, Object... keyAndClasses)
	{
		if (config.trim().isEmpty())
		{
			return null;
		}

		final String[] arrayRecord = config.split(";");
		final StatsSet statsSet = new StatsSet();
		for (int i = 0; i < arrayRecord.length; ++i)
		{
			final int keyIndex = i * 2;
			if (!(keyAndClasses[keyIndex] instanceof String) || !(keyAndClasses[keyIndex + 1] instanceof Class))
			{
				throw new AssertionError("Wrong wanted types for StatsSet with Config: " + config);
			}
			statsSet.put((String) keyAndClasses[keyIndex], parseObject(arrayRecord[i].trim(), (Class) keyAndClasses[keyIndex + 1]));
		}
		return statsSet;
	}

	@Nullable
	public static List<StatsSet> parseStatsSetList(String config, Object... keyAndClasses)
	{
		if (config.trim().isEmpty())
		{
			return null;
		}

		final String[] arrayRecordNoSplit = config.split(ConfigHolder.ARRAY_SPLITS[0]);
		final List<StatsSet> list = new ArrayList<StatsSet>(arrayRecordNoSplit.length);
		for (String recordNoSplit : arrayRecordNoSplit)
		{
			final String[] splitSingleRecord = recordNoSplit.trim().split(ConfigHolder.ARRAY_SPLITS[1]);
			final StatsSet statsSet = new StatsSet();
			for (int i = 0; i < splitSingleRecord.length; ++i)
			{
				final int keyIndex = i * 2;
				if (!(keyAndClasses[keyIndex] instanceof String) || !(keyAndClasses[keyIndex + 1] instanceof Class))
				{
					throw new AssertionError("Wrong wanted types for StatsSet with Config: " + config);
				}
				statsSet.put((String) keyAndClasses[keyIndex], parseObject(splitSingleRecord[i].trim(), (Class) keyAndClasses[keyIndex + 1]));
			}
			list.add(statsSet);
		}
		return list;
	}

	private static <E> Object parseArray(String text, Class<E> type)
	{
		final String[] stringArray = parseArrayToString(text);
		final Class<?> primitiveType = type.getComponentType();
		final Object finalArray = Array.newInstance(primitiveType, stringArray.length);
		for (int i = 0; i < stringArray.length; ++i)
		{
			final Object obj = parseObject(stringArray[i], primitiveType);
			Array.set(finalArray, i, obj);
		}
		return finalArray;
	}

	private static <E> Object parseDoubleArray(String text, Class<E> type)
	{
		final String[][] stringDoubleArray = parseDoubleArrayToString(text);
		final Class<?> splitType = type.getComponentType();
		final Class<?> primitiveType = splitType.getComponentType();
		final Object finalArray = Array.newInstance(splitType, stringDoubleArray.length);
		for (int i = 0; i < stringDoubleArray.length; ++i)
		{
			final String[] arraySplit = stringDoubleArray[i];
			final Object finalArraySplit = Array.newInstance(primitiveType, arraySplit.length);
			for (int x = 0; x < arraySplit.length; ++x)
			{
				final String object = arraySplit[x];
				Array.set(finalArraySplit, x, parseObject(object, primitiveType));
			}
			Array.set(finalArray, i, finalArraySplit);
		}
		return finalArray;
	}

	public static String[][] parseDoubleArrayToString(String text)
	{
		if (text.trim().isEmpty())
		{
			return ConfigParser.EMPTY_MULTI_ARRAY;
		}

		final String[] arrayRecordNoSplit = text.split(ConfigHolder.ARRAY_SPLITS[0]);
		final String[][] finalArray = new String[arrayRecordNoSplit.length][];
		for (int i = 0; i < arrayRecordNoSplit.length; ++i)
		{
			final String[] recordNoSplit = arrayRecordNoSplit[i].trim().split(ConfigHolder.ARRAY_SPLITS[1]);
			finalArray[i] = new String[recordNoSplit.length];
			for (int x = 0; x < recordNoSplit.length; ++x)
			{
				final String value = recordNoSplit[x].trim();
				finalArray[i][x] = value;
			}
		}
		return finalArray;
	}

	private static String[] parseArrayToString(String text)
	{
		if (text.trim().isEmpty())
		{
			return ConfigParser.EMPTY_ARRAY;
		}

		final String[] arrayRecordNoSplit = text.split(ConfigHolder.ARRAY_SPLITS[0]);
		final String[] finalArray = new String[arrayRecordNoSplit.length];
		for (int i = 0; i < arrayRecordNoSplit.length; ++i)
		{
			final String value = arrayRecordNoSplit[i].trim();
			finalArray[i] = value;
		}
		return finalArray;
	}

	private static void loadFilesFromFolder(File folder, Map<String, String> configs)
	{
		final File[] files = folder.listFiles();
		if (files == null)
		{
			return;
		}
		for (File fileEntry : files)
		{
			if (fileEntry.isDirectory())
			{
				loadFilesFromFolder(fileEntry, configs);
			}
			else if (fileEntry.getPath().endsWith(".properties"))
			{
				loadFile(fileEntry, configs);
			}
		}
	}

	private static void loadFile(File file, Map<String, String> configs)
	{
		try (LineNumberReader reader = new LineNumberReader(new FileReader(file)))
		{
			TempConfigData currentConfigData = new TempConfigData();
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#") || line.isEmpty())
				{
					if (currentConfigData.getName().isEmpty())
					{
						continue;
					}

					configs.put(currentConfigData.getName(), currentConfigData.getValue().toString());
					currentConfigData = new TempConfigData();
				}
				else
				{
					String valueFromLine;
					if (currentConfigData.getName().isEmpty())
					{
						final String[] split = line.split("=", 2);
						if (split.length < 2)
						{
							LOG.error("Error on line: " + line + "; file: " + file.getName());
							continue;
						}
						currentConfigData.setName(split[0].trim().toLowerCase());
						if (configs.containsKey(currentConfigData.getName()))
						{
							LOG.warn("Config " + currentConfigData.getName() + " exists twice!");
						}
						valueFromLine = split[1].trim();
					}
					else
					{
						valueFromLine = line;
					}

					final boolean restInNextLine = valueFromLine.endsWith("\\");
					if (valueFromLine.contains("//!"))
					{
						valueFromLine = valueFromLine.substring(0, valueFromLine.indexOf("//!"));
					}
					else if (restInNextLine)
					{
						valueFromLine = valueFromLine.substring(0, valueFromLine.length() - 1);
					}
					if (restInNextLine)
					{
						currentConfigData.getValue().append(valueFromLine);
					}
					else
					{
						currentConfigData.getValue().append(valueFromLine);
						final String value = replaceSpecialChars(currentConfigData.getValue().toString());
						configs.put(currentConfigData.getName(), value);
						currentConfigData = new TempConfigData();
					}
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("Exception in StringHolder", e);
		}
	}

	private static String replaceSpecialChars(String s)
	{
		final String replacedString = s.replace("\\\\", "\\");
		return replacedString;
	}

	private static final class TempConfigData
	{
		private String name = "";
		private final StringBuilder value = new StringBuilder();

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public StringBuilder getValue()
		{
			return value;
		}
	}
}

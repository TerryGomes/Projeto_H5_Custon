package l2mv.gameserver;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.annotations.NotNull;
import l2mv.commons.annotations.Nullable;
import l2mv.commons.data.xml.AbstractHolder;
import l2mv.commons.net.nio.impl.SelectorConfig;
import l2mv.gameserver.data.ConfigParser;
import l2mv.gameserver.network.loginservercon.ServerType;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.DynamicConfig;
import l2mv.gameserver.utils.Location;

public final class ConfigHolder extends AbstractHolder
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfigHolder.class);
	public static final String FIRST_SPLIT = ";";
	public static final String SECOND_SPLIT = ",";
	public static final String[] ARRAY_SPLITS = new String[]
	{
		";",
		","
	};
	public static final String FILE_EXTENSION = ".properties";

	private final SelectorConfig selectorConfig = new SelectorConfig();
	private final Object configsLock = new Object();
	private final Map<String, String> configs = new HashMap<String, String>();
	private final Map<String, DynamicConfig> convertedConfigs = new HashMap<String, DynamicConfig>();

	public boolean checkExists(String name, Class<?> type)
	{
		final String smallName = name.toLowerCase();
		if (convertedConfigs.get(smallName) != null)
		{
			return true;
		}
		try
		{
			loadObject(smallName, type, false);
			return true;
		}
		catch (AssertionError e)
		{
			return false;
		}
	}

	public static boolean checkIsEmpty(String name)
	{
		final String lowerName = name.toLowerCase();
		return !getInstance().configs.containsKey(lowerName) || getInstance().configs.get(lowerName).isEmpty();
	}

	public static int getInt(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, Integer.class);
		return value.getInt();
	}

	public static long getLong(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, Long.class);
		return value.getLong();
	}

	public static double getDouble(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, Double.class);
		return value.getDouble();
	}

	public static boolean getBool(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, Boolean.class);
		return value.getBool();
	}

	@NotNull
	public static String getString(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, String.class);
		final String string = value.getString();
		if (string == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getString"));
		}
		return string;
	}

	public static long getTimeDuration(String name, TimeUnit wantedUnit, TimeUnit configUnit)
	{
		return wantedUnit.convert(getLong(name), configUnit);
	}

	public static long getMillis(String name, TimeUnit configUnit)
	{
		return TimeUnit.MILLISECONDS.convert(getLong(name), configUnit);
	}

	@NotNull
	public static File getFilePath(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, File.class);
		final File filePath = value.getFilePath();
		if (filePath == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getFilePath"));
		}
		return filePath;
	}

	@NotNull
	public static Pattern getPattern(@NotNull final String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "name", "l2mv/gameserver/ConfigHolder", "getPattern"));
		}
		final DynamicConfig value = getInstance().getOrLoad(name, Pattern.class);
		final Pattern pattern = value.getPattern();
		if (pattern == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getPattern"));
		}
		return pattern;
	}

	@NotNull
	public static Location getLocation(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, Location.class);
		final Location location = value.getLocation();
		if (location == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getLocation"));
		}
		return location;
	}

	@NotNull
	public static ChatType getChatType(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, ChatType.class);
		final ChatType chatType = value.getClassType(ChatType.class);
		if (chatType == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getChatType"));
		}
		return chatType;
	}

	@NotNull
	public static <E> E getEnum(String name, Class<E> type)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, type);
		final E classType = value.getClassType(type);
		if (classType == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getEnum"));
		}
		return classType;
	}

	@NotNull
	public static ServerType getServerType(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, ServerType.class);
		final ServerType serverType = value.getClassType(ServerType.class);
		if (serverType == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getServerType"));
		}
		return serverType;
	}

	@NotNull
	public static AbnormalEffect getAbnormalEffect(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, AbnormalEffect.class);
		final AbnormalEffect abnormalEffect = value.getClassType(AbnormalEffect.class);
		if (abnormalEffect == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getAbnormalEffect"));
		}
		return abnormalEffect;
	}

	@NotNull
	public static int[][] getMultiIntArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, int[][].class);
		final int[][] multiIntArray = value.getMultiIntArray();
		if (multiIntArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMultiIntArray"));
		}
		return multiIntArray;
	}

	@NotNull
	public static long[][] getMultiLongArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, long[][].class);
		final long[][] multiLongArray = value.getMultiLongArray();
		if (multiLongArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMultiLongArray"));
		}
		return multiLongArray;
	}

	@NotNull
	public static double[][] getMultiDoubleArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, double[][].class);
		final double[][] multiDoubleArray = value.getMultiDoubleArray();
		if (multiDoubleArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMultiDoubleArray"));
		}
		return multiDoubleArray;
	}

	@NotNull
	public static String[][] getMultiStringArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, String[][].class);
		final String[][] multiStringArray = value.getMultiStringArray();
		if (multiStringArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMultiStringArray"));
		}
		return multiStringArray;
	}

	@NotNull
	public static <E> E[][] getMultiArray(String name, Class<E> primaryType)
	{
		if (primaryType.isPrimitive())
		{
			throw new AssertionError("getMultiArray(" + name + ", " + primaryType + ") shouldn't be used with Primitives!");
		}
		final Class<?> array = Array.newInstance(Array.newInstance(primaryType, 0).getClass(), 0).getClass();
		final DynamicConfig value = getInstance().getOrLoad(name, array);
		final E[][] array2 = value.getClassType((Class<E[][]>) array);
		if (array2 == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMultiArray"));
		}
		return array2;
	}

	@NotNull
	public static int[] getIntArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, int[].class);
		final int[] intArray = value.getIntArray();
		if (intArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getIntArray"));
		}
		return intArray;
	}

	@NotNull
	public static long[] getLongArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, long[].class);
		final long[] longArray = value.getLongArray();
		if (longArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getLongArray"));
		}
		return longArray;
	}

	@NotNull
	public static double[] getDoubleArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, double[].class);
		final double[] doubleArray = value.getDoubleArray();
		if (doubleArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getDoubleArray"));
		}
		return doubleArray;
	}

	@NotNull
	public static boolean[] getBooleanArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, boolean[].class);
		final boolean[] booleanArray = value.getBooleanArray();
		if (booleanArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getBooleanArray"));
		}
		return booleanArray;
	}

	@NotNull
	public static String[] getStringArray(String name)
	{
		final DynamicConfig value = getInstance().getOrLoad(name, String[].class);
		final String[] stringArray = value.getStringArray();
		if (stringArray == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getStringArray"));
		}
		return stringArray;
	}

	@NotNull
	public static <E> E[] getArray(String name, Class<E> primaryType)
	{
		if (primaryType.isPrimitive())
		{
			throw new AssertionError("getArray(" + name + ", " + primaryType + ") shouldn't be used with Primitives!");
		}
		final Class<?> array = Array.newInstance(primaryType, 0).getClass();
		final DynamicConfig value = getInstance().getOrLoad(name, array);
		final E[] array2 = value.getClassType((Class<E[]>) array);
		if (array2 == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getArray"));
		}
		return array2;
	}

	public static long[] getTimeDurationArray(String name, TimeUnit wantedUnit, TimeUnit configUnit)
	{
		final long[] configArray = getLongArray(name);
		if (configUnit == wantedUnit)
		{
			return configArray;
		}
		final long[] arrayCopy = new long[configArray.length];
		for (int i = 0; i < configArray.length; ++i)
		{
			arrayCopy[i] = wantedUnit.convert(configArray[i], configUnit);
		}
		return arrayCopy;
	}

	@NotNull
	public static <E, T> Map<E, T> getMap(String name, Class<E> firstType, Class<T> secondType)
	{
		final DynamicConfig value = getInstance().getOrLoadMap(name, firstType, secondType);
		final Map<E, T> map = value.getClassType(Map.class);
		if (map == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMap"));
		}
		return map;
	}

	@NotNull
	public static <E, T> Map.Entry<E, T> getMapEntry(String name, Class<E> firstType, Class<T> secondType)
	{
		final DynamicConfig value = getInstance().getOrLoadMapEntry(name, firstType, secondType);
		final Map.Entry<E, T> entry = value.getClassType(Map.Entry.class);
		if (entry == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getMapEntry"));
		}
		return entry;
	}

	@NotNull
	public static StatsSet getStatsSet(String name, Object... keysAndClasses)
	{
		final DynamicConfig value = getInstance().getOrLoadStatsSet(name, keysAndClasses);
		final StatsSet set = value.getClassType(StatsSet.class);
		if (set == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getStatsSet"));
		}
		return set;
	}

	@NotNull
	public static List<StatsSet> getStatsSetList(String name, Object... keysAndClasses)
	{
		final DynamicConfig value = getInstance().getOrLoadStatsSetList(name, keysAndClasses);
		final List<StatsSet> list = value.getClassType(List.class);
		if (list == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getStatsSetList"));
		}
		return list;
	}

	@NotNull
	public SelectorConfig getSelectorConfig()
	{
		final SelectorConfig selectorConfig = this.selectorConfig;
		if (selectorConfig == null)
		{
			throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getSelectorConfig"));
		}
		return selectorConfig;
	}

	@NotNull
	private DynamicConfig getOrLoad(String name, Class<?> type)
	{
		final String smallName = name.toLowerCase();
		final DynamicConfig convertedConfig = convertedConfigs.get(smallName);
		if (convertedConfig != null)
		{
			final DynamicConfig dynamicConfig = convertedConfig;
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoad"));
			}
			return dynamicConfig;
		}
		else
		{
			final DynamicConfig obj = loadObject(smallName, type, true);
			convertedConfigs.put(smallName, obj);
			final DynamicConfig dynamicConfig2 = obj;
			if (dynamicConfig2 == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoad"));
			}
			return dynamicConfig2;
		}
	}

	@NotNull
	private DynamicConfig getOrLoadMap(String name, Class<?> firstType, @SuppressWarnings("rawtypes") final Class<?> secondType)
	{
		final String smallName = name.toLowerCase();
		final DynamicConfig convertedConfig = convertedConfigs.get(smallName);
		if (convertedConfig != null)
		{
			final DynamicConfig dynamicConfig = convertedConfig;
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadMap"));
			}
			return dynamicConfig;
		}
		else
		{
			final DynamicConfig obj = loadMap(smallName, firstType, secondType, true);
			convertedConfigs.put(smallName, obj);
			final DynamicConfig dynamicConfig2 = obj;
			if (dynamicConfig2 == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadMap"));
			}
			return dynamicConfig2;
		}
	}

	@NotNull
	private DynamicConfig getOrLoadMapEntry(String name, Class<?> firstType, Class<?> secondType)
	{
		final String smallName = name.toLowerCase();
		final DynamicConfig convertedConfig = convertedConfigs.get(smallName);
		if (convertedConfig != null)
		{
			final DynamicConfig dynamicConfig = convertedConfig;
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadMapEntry"));
			}
			return dynamicConfig;
		}
		else
		{
			final DynamicConfig obj = loadMapEntry(smallName, firstType, secondType, true);
			convertedConfigs.put(smallName, obj);
			final DynamicConfig dynamicConfig2 = obj;
			if (dynamicConfig2 == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadMapEntry"));
			}
			return dynamicConfig2;
		}
	}

	@NotNull
	private DynamicConfig getOrLoadStatsSet(String name, Object... keysAndClasses)
	{
		final String smallName = name.toLowerCase();
		final DynamicConfig convertedConfig = convertedConfigs.get(smallName);
		if (convertedConfig != null)
		{
			final DynamicConfig dynamicConfig = convertedConfig;
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadStatsSet"));
			}
			return dynamicConfig;
		}
		else
		{
			final DynamicConfig obj = loadStatsSet(smallName, true, keysAndClasses);
			convertedConfigs.put(smallName, obj);
			final DynamicConfig dynamicConfig2 = obj;
			if (dynamicConfig2 == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "getOrLoadStatsSet"));
			}
			return dynamicConfig2;
		}
	}

	@Nullable
	private DynamicConfig getOrLoadStatsSetList(String name, Object... keysAndClasses)
	{
		final String smallName = name.toLowerCase();
		final DynamicConfig convertedConfig = convertedConfigs.get(smallName);
		if (convertedConfig != null)
		{
			return convertedConfig;
		}
		final DynamicConfig obj = loadStatsSetList(smallName, true, keysAndClasses);
		convertedConfigs.put(smallName, obj);
		return obj;
	}

	@NotNull
	private DynamicConfig loadObject(String name, Class<?> type, boolean logError)
	{
		final String value = getConfigValue(name, logError);
		try
		{
			final Object object = ConfigParser.parseObject(value, type);
			final DynamicConfig dynamicConfig = new DynamicConfig(object, name);
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "loadObject"));
			}
			return dynamicConfig;
		}
		catch (NumberFormatException e)
		{
			if (logError)
			{
				ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to " + type + '!');
			}
			throw new AssertionError();
		}
	}

	@NotNull
	private DynamicConfig loadMap(String name, Class<?> firstType, Class<?> secondType, boolean logError)
	{
		final String value = getConfigValue(name, logError);
		try
		{
			final Object object = ConfigParser.parseMap(value, (Class<Object>) firstType, (Class<Object>) secondType);
			final DynamicConfig dynamicConfig = new DynamicConfig(object, name);
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "loadMap"));
			}
			return dynamicConfig;
		}
		catch (NumberFormatException e)
		{
			if (logError)
			{
				ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to Map<" + firstType + ", " + secondType + ">!");
			}
			throw new AssertionError();
		}
	}

	@NotNull
	private DynamicConfig loadMapEntry(String name, Class<?> firstType, Class<?> secondType, boolean logError)
	{
		final String value = getConfigValue(name, logError);
		try
		{
			final Object object = ConfigParser.parseMapEntry(value, (Class<Object>) firstType, (Class<Object>) secondType);
			if (object == null)
			{
				if (logError)
				{
					ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to Map.Entry<" + firstType + ", " + secondType + ">!");
				}
				throw new AssertionError();
			}
			final DynamicConfig dynamicConfig = new DynamicConfig(object, name);
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "loadMapEntry"));
			}
			return dynamicConfig;
		}
		catch (NumberFormatException e)
		{
			if (logError)
			{
				ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to Map.Entry<" + firstType + ", " + secondType + ">!");
			}
			throw new AssertionError();
		}
	}

	@NotNull
	private DynamicConfig loadStatsSet(String name, boolean logError, Object... keysAndClasses)
	{
		final String value = getConfigValue(name, logError);
		try
		{
			final Object object = ConfigParser.parseStatsSet(value, keysAndClasses);
			if (object == null)
			{
				if (logError)
				{
					ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to StatsSet " + Arrays.toString(keysAndClasses) + "!");
				}
				throw new AssertionError();
			}
			final DynamicConfig dynamicConfig = new DynamicConfig(object, name);
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "loadStatsSet"));
			}
			return dynamicConfig;
		}
		catch (NumberFormatException e)
		{
			if (logError)
			{
				ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to StatsSet " + Arrays.toString(keysAndClasses) + "!");
			}
			throw new AssertionError();
		}
	}

	@NotNull
	private DynamicConfig loadStatsSetList(String name, boolean logError, Object... keysAndClasses)
	{
		final String value = getConfigValue(name, logError);
		try
		{
			final Object object = ConfigParser.parseStatsSetList(value, keysAndClasses);
			if (object == null)
			{
				if (logError)
				{
					ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to StatsSet List " + Arrays.toString(keysAndClasses) + "!");
				}
				throw new AssertionError();
			}
			final DynamicConfig dynamicConfig = new DynamicConfig(object, name);
			if (dynamicConfig == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2mv/gameserver/ConfigHolder", "loadStatsSetList"));
			}
			return dynamicConfig;
		}
		catch (NumberFormatException e)
		{
			if (logError)
			{
				ConfigHolder.LOG.error("Couldn't parse Config " + name + " with Value: " + value + " to StatsSet List " + Arrays.toString(keysAndClasses) + "!");
			}
			throw new AssertionError();
		}
	}

	private String getConfigValue(String name, boolean logError)
	{
		synchronized (configsLock)
		{
			if (!configs.containsKey(name))
			{
				if (logError)
				{
					ConfigHolder.LOG.error("Couldn't find Config " + name + '!');
				}
				throw new AssertionError();
			}
			return configs.get(name);
		}
	}

	public void reload()
	{
		final Map<String, String> newConfigs = ConfigParser.load();
		synchronized (configsLock)
		{
			configs.clear();
			configs.putAll(newConfigs);
		}
		convertedConfigs.clear();
		GameServer.getInstance().getListeners().onConfigsReloaded();
	}

	@Override
	public void log()
	{
		synchronized (configsLock)
		{
			this.info("Loaded " + configs.size() + " Configs!");
		}
	}

	@Override
	public int size()
	{
		synchronized (configsLock)
		{
			return configs.size();
		}
	}

	@Override
	public void clear()
	{
		synchronized (configsLock)
		{
			configs.clear();
		}
		convertedConfigs.clear();
	}

	public static ConfigHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ConfigHolder instance = new ConfigHolder();
	}
}

package scriptconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Util;

public class ScriptConfig extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(ScriptConfig.class);

	private static final String dir = (Config.DATAPACK_ROOT + "/config/ScripsConfig");
	private static ConcurrentHashMap<String, String> properties;

	@Override
	public void onLoad()
	{
		properties = new ConcurrentHashMap<String, String>();
		LoadConfig();
		_log.info("Loaded Service: ScripsConfig");
	}

	@Override
	public void onReload()
	{
		onLoad();
	}

	@Override
	public void onShutdown()
	{
	}

	public static void LoadConfig()
	{
		File files = new File(dir);
		if (!files.exists())
		{
			_log.warn("WARNING! " + dir + " not exists! Config not loaded!");
		}
		else
		{
			parseFiles(files.listFiles());
		}
	}

	private static void parseFiles(File[] files)
	{
		for (File f : files)
		{
			if (f.isHidden())
			{
				continue;
			}
			if (f.isDirectory() && !f.getName().contains("defaults"))
			{
				parseFiles(f.listFiles());
			}
			else if (f.getName().endsWith(".ini"))
			{
				try
				{
					InputStream is = new FileInputStream(f);
					Properties p = new Properties();
					p.load(is);
					loadProperties(p);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void loadProperties(Properties p)
	{
		for (String name : p.stringPropertyNames())
		{
			if (properties.get(name) != null)
			{
				properties.replace(name, p.getProperty(name).trim());
				_log.info("Duplicate properties name \"" + name + "\" replaced with new value.");
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

	public static String get(String name)
	{
		if (properties.get(name) == null)
		{
			_log.warn("ConfigSystem: Null value for key: " + name);
		}
		return properties.get(name);
	}

	public static float getFloat(String name)
	{
		return getFloat(name, Float.MAX_VALUE);
	}

	public static boolean getBoolean(String name)
	{
		return getBoolean(name, false);
	}

	public static int getInt(String name)
	{
		return getInt(name, Integer.MAX_VALUE);
	}

	public static int[] getIntArray(String name)
	{
		return getIntArray(name, new int[0]);
	}

	public static int getIntHex(String name)
	{
		return getIntHex(name, Integer.decode("0xFFFFFF"));
	}

	public static byte getByte(String name)
	{
		return getByte(name, Byte.MAX_VALUE);
	}

	public static long getLong(String name)
	{
		return getLong(name, Long.MAX_VALUE);
	}

	public static double getDouble(String name)
	{
		return getDouble(name, Double.MAX_VALUE);
	}

	public static String get(String name, String def)
	{
		return get(name) == null ? def : get(name);
	}

	public static float getFloat(String name, float def)
	{
		return Float.parseFloat(get(name, String.valueOf(def)));
	}

	public static boolean getBoolean(String name, boolean def)
	{
		return Boolean.parseBoolean(get(name, String.valueOf(def)));
	}

	public static int getInt(String name, int def)
	{
		return Integer.parseInt(get(name, String.valueOf(def)));
	}

	public static int[] getIntArray(String name, int[] def)
	{
		return get(name, null) == null ? def : Util.parseCommaSeparatedIntegerArray(get(name, null));
	}

	public static int getIntHex(String name, int def)
	{
		if (!get(name, String.valueOf(def)).trim().startsWith("0x"))
		{
			return Integer.decode("0x" + get(name, String.valueOf(def)));
		}
		else
		{
			return Integer.decode(get(name, String.valueOf(def)));
		}
	}

	public static byte getByte(String name, byte def)
	{
		return Byte.parseByte(get(name, String.valueOf(def)));
	}

	public static double getDouble(String name, double def)
	{
		return Double.parseDouble(get(name, String.valueOf(def)));
	}

	public static long getLong(String name, long def)
	{
		return Long.parseLong(get(name, String.valueOf(def)));
	}

	public static void set(String name, String param)
	{
		properties.replace(name, param);
	}

	public static void set(String name, Object obj)
	{
		set(name, String.valueOf(obj));
	}
}

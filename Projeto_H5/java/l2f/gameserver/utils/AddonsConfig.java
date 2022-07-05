package l2f.gameserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntIntHashMap;
import l2f.gameserver.Config;
import l2f.gameserver.model.quest.Quest;

public class AddonsConfig
{
	private static final Logger log = LoggerFactory.getLogger(AddonsConfig.class);
	private static final String dir = Config.DATAPACK_ROOT + "/config/Addons";
	private static ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<Integer, Double> questRewardRates = new ConcurrentHashMap<Integer, Double>();
	private static ConcurrentHashMap<Integer, Double> questDropRates = new ConcurrentHashMap<Integer, Double>();

	public static void load()
	{
		File files = new File(dir);
		if (!files.exists())
		{
			log.warn("WARNING! " + dir + " not exists! Config not loaded!");
		}
		else
		{
			properties = new ConcurrentHashMap<>();
			questRewardRates = new ConcurrentHashMap<>();
			questDropRates = new ConcurrentHashMap<>();
			parseFiles(files.listFiles());
		}
	}

	public static void reload()
	{
		synchronized (properties)
		{
			synchronized (questRewardRates)
			{
				synchronized (questDropRates)
				{
					properties = new ConcurrentHashMap<String, String>();
					questRewardRates = new ConcurrentHashMap<Integer, Double>();
					questDropRates = new ConcurrentHashMap<Integer, Double>();
					load();
				}
			}
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
			if (f.getName().startsWith("quest_reward_rates"))
			{
				try
				{
					InputStream is = new FileInputStream(f);
					Properties p = new Properties();
					p.load(is);
					loadQuestRewardRates(p);
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
			else if (f.getName().startsWith("quest_drop_rates"))
			{
				try
				{
					InputStream is = new FileInputStream(f);
					Properties p = new Properties();
					p.load(is);
					loadQuestDropRates(p);
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

	private static void loadQuestRewardRates(Properties p)
	{
		for (String name : p.stringPropertyNames())
		{
			int id;
			try
			{
				id = Integer.parseInt(name);
			}
			catch (NumberFormatException nfe)
			{
				continue;
			}
			if (questRewardRates.get(id) != null)
			{
				questRewardRates.replace(id, Double.parseDouble(p.getProperty(name).trim()));
				log.info("Duplicate quest Reward id \"" + name + "\"");
			}
			else if (p.getProperty(name) == null)
			{
				log.info("Null property for quest id " + name);
			}
			else
			{
				questRewardRates.put(id, Double.parseDouble(p.getProperty(name).trim()));
			}
		}
		p.clear();
	}

	private static void loadQuestDropRates(Properties p)
	{
		for (String name : p.stringPropertyNames())
		{
			int id;
			try
			{
				id = Integer.parseInt(name);
			}
			catch (NumberFormatException nfe)
			{
				continue;
			}
			if (questDropRates.get(id) != null)
			{
				questDropRates.replace(id, Double.parseDouble(p.getProperty(name).trim()));
				log.info("Duplicate quest Drop id \"" + name + "\"");
			}
			else if (p.getProperty(name) == null)
			{
				log.info("Null property for quest id " + name);
			}
			else
			{
				questDropRates.put(id, Double.parseDouble(p.getProperty(name).trim()));
			}
		}
		p.clear();
	}

	private static void loadProperties(Properties p)
	{
		for (String name : p.stringPropertyNames())
		{
			if (properties.get(name) != null)
			{
				properties.replace(name, p.getProperty(name).trim());
				log.info("Duplicate properties name \"" + name + "\" replaced with new value.");
			}
			else if (p.getProperty(name) == null)
			{
				log.info("Null property for key " + name);
			}
			else
			{
				properties.put(name, p.getProperty(name).trim());
			}
		}
		p.clear();
	}

	public static double getQuestRewardRates(Quest q)
	{
		return questRewardRates.containsKey(q.getQuestIntId()) ? questRewardRates.get(q.getQuestIntId()) : 1.0;
	}

	public static double getQuestDropRates(Quest q)
	{
		return questDropRates.containsKey(q.getQuestIntId()) ? questDropRates.get(q.getQuestIntId()) : 1.0;
	}

	public static String get(String name)
	{
		if (properties.get(name) == null)
		{
			log.warn("AddonsConfig: Null value for key: " + name);
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

	public static TIntIntHashMap SKILL_DURATION_LIST;
	public static TIntIntHashMap SKILL_REUSE_LIST;

	public static void loadSkillDurationList()
	{
		if (getBoolean("EnableModifySkillDuration"))
		{
			String[] propertySplit = get("SkillDurationList").split(";");
			SKILL_DURATION_LIST = new TIntIntHashMap(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					log.warn(concat("[SkillDurationList]: invalid config property -> SkillDurationList \"", skill, "\""));
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							log.warn(concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
						}
					}
				}
			}
		}
	}

	public static void loadSkillReuseList()
	{
		if (getBoolean("EnableModifySkillReuse"))
		{
			String[] propertySplit = get("SkillReuseList").split(";");
			SKILL_REUSE_LIST = new TIntIntHashMap(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					log.warn(concat("[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\""));
				}
				else
				{
					try
					{
						SKILL_REUSE_LIST.put(Integer.valueOf(skillSplit[0]), Integer.valueOf(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							log.warn(concat("[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
						}
					}
				}
			}
		}
	}

	public static String concat(String... strings)
	{
		final StringBuilder sbString = new StringBuilder(getLength(strings));
		for (String string : strings)
		{
			sbString.append(string);
		}
		return sbString.toString();
	}

	private static int getLength(String[] strings)
	{
		int length = 0;
		for (String string : strings)
		{
			length += string.length();
		}
		return length;
	}
}

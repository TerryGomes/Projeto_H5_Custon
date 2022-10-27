package l2mv.gameserver.tables;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gnu.trove.list.array.TDoubleArrayList;
//import l2mv.commons.crypt.CryptUtil;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Options;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.triggers.TriggerInfo;
import l2mv.gameserver.stats.triggers.TriggerType;
import l2mv.gameserver.templates.item.ItemTemplate;

public class AugmentationData
{
	private static final Logger LOG = LoggerFactory.getLogger(AugmentationData.class);

	private static AugmentationData _Instance;

	public static AugmentationData getInstance()
	{
		if (_Instance == null)
		{
			_Instance = new AugmentationData();
		}
		return _Instance;
	}

	// stats
	@SuppressWarnings("unused")
	private static final int STAT_START = 1;
	@SuppressWarnings("unused")
	private static final int STAT_END = 14560;
	private static final int STAT_BLOCKSIZE = 3640;
	// private static final int STAT_NUMBEROF_BLOCKS = 4;
	private static final int STAT_SUBBLOCKSIZE = 91;
	// private static final int STAT_NUMBEROF_SUBBLOCKS = 40;
	private static final int STAT_NUM = 13;

	private static final byte[] STATS1_MAP = new byte[STAT_SUBBLOCKSIZE];
	private static final byte[] STATS2_MAP = new byte[STAT_SUBBLOCKSIZE];

	// skills
	private static final int BLUE_START = 14561;
	// private static final int PURPLE_START = 14578;
	// private static final int RED_START = 14685;
	private static final int SKILLS_BLOCKSIZE = 178;

	// basestats
	private static final int BASESTAT_STR = 16341;
	@SuppressWarnings("unused")
	private static final int BASESTAT_CON = 16342;
	@SuppressWarnings("unused")
	private static final int BASESTAT_INT = 16343;
	private static final int BASESTAT_MEN = 16344;

	// accessory
	private static final int ACC_START = 16669;
	private static final int ACC_BLOCKS_NUM = 10;
	private static final int ACC_STAT_SUBBLOCKSIZE = 21;
	private static final int ACC_STAT_NUM = 6;

	private static final int ACC_RING_START = ACC_START;
	private static final int ACC_RING_SKILLS = 18;
	private static final int ACC_RING_BLOCKSIZE = ACC_RING_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;
	private static final int ACC_RING_END = ACC_RING_START + ACC_BLOCKS_NUM * ACC_RING_BLOCKSIZE - 1;

	private static final int ACC_EAR_START = ACC_RING_END + 1;
	private static final int ACC_EAR_SKILLS = 18;
	private static final int ACC_EAR_BLOCKSIZE = ACC_EAR_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;
	private static final int ACC_EAR_END = ACC_EAR_START + ACC_BLOCKS_NUM * ACC_EAR_BLOCKSIZE - 1;

	private static final int ACC_NECK_START = ACC_EAR_END + 1;
	private static final int ACC_NECK_SKILLS = 24;
	private static final int ACC_NECK_BLOCKSIZE = ACC_NECK_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;

	@SuppressWarnings("unused")
	private static final int ACC_END = ACC_NECK_START + ACC_BLOCKS_NUM * ACC_NECK_BLOCKSIZE;

	private static final byte[] ACC_STATS1_MAP = new byte[ACC_STAT_SUBBLOCKSIZE];
	private static final byte[] ACC_STATS2_MAP = new byte[ACC_STAT_SUBBLOCKSIZE];

	private final List<?>[] _augStats = new ArrayList[4];
	private final List<?>[] _augAccStats = new ArrayList[4];

	private final List<?>[] _blueSkills = new ArrayList[10];
	private final List<?>[] _purpleSkills = new ArrayList[10];
	private final List<?>[] _redSkills = new ArrayList[10];
	private final List<?>[] _yellowSkills = new ArrayList[10];

	private final Map<Integer, Options> _allSkills = new HashMap<>();

	public AugmentationData()
	{
		LOG.info("Initializing AugmentationData.");

		_augStats[0] = new ArrayList<augmentationStat>();
		_augStats[1] = new ArrayList<augmentationStat>();
		_augStats[2] = new ArrayList<augmentationStat>();
		_augStats[3] = new ArrayList<augmentationStat>();

		_augAccStats[0] = new ArrayList<augmentationStat>();
		_augAccStats[1] = new ArrayList<augmentationStat>();
		_augAccStats[2] = new ArrayList<augmentationStat>();
		_augAccStats[3] = new ArrayList<augmentationStat>();

		// Lookup tables structure: STAT1 represent first stat, STAT2 - second.
		// If both values are the same - use solo stat, if different - combined.
		int idx;
		// weapon augmentation block: solo values first
		// 00-00, 01-01 ... 11-11,12-12
		for (idx = 0; idx < STAT_NUM; idx++)
		{
			// solo stats
			STATS1_MAP[idx] = (byte) idx;
			STATS2_MAP[idx] = (byte) idx;
		}
		// combined values next.
		// 00-01,00-02,00-03 ... 00-11,00-12;
		// 01-02,01-03 ... 01-11,01-12;
		// ...
		// 09-10,09-11,09-12;
		// 10-11,10-12;
		// 11-12
		for (int i = 0; i < STAT_NUM; i++)
		{
			for (int j = i + 1; j < STAT_NUM; idx++, j++)
			{
				// combined stats
				STATS1_MAP[idx] = (byte) i;
				STATS2_MAP[idx] = (byte) j;
			}
		}
		idx = 0;
		// accessory augmentation block, structure is different:
		// 00-00,00-01,00-02,00-03,00-04,00-05
		// 01-01,01-02,01-03,01-04,01-05
		// 02-02,02-03,02-04,02-05
		// 03-03,03-04,03-05
		// 04-04 \
		// 05-05 - order is changed here
		// 04-05 /
		// First values always solo, next are combined, except last 3 values
		for (int i = 0; i < ACC_STAT_NUM - 2; i++)
		{
			for (int j = i; j < ACC_STAT_NUM; idx++, j++)
			{
				ACC_STATS1_MAP[idx] = (byte) i;
				ACC_STATS2_MAP[idx] = (byte) j;
			}
		}
		ACC_STATS1_MAP[idx] = 4;
		ACC_STATS2_MAP[idx++] = 4;
		ACC_STATS1_MAP[idx] = 5;
		ACC_STATS2_MAP[idx++] = 5;
		ACC_STATS1_MAP[idx] = 4;
		ACC_STATS2_MAP[idx] = 5;

		for (int i = 0; i < 10; i++)
		{
			_blueSkills[i] = new ArrayList<Integer>();
			_purpleSkills[i] = new ArrayList<Integer>();
			_redSkills[i] = new ArrayList<Integer>();
			_yellowSkills[i] = new ArrayList<Integer>();
		}

		load();

		// Use size*4: since theres 4 blocks of stat-data with equivalent size
		LOG.info("AugmentationData: Loaded: " + _augStats[0].size() * 4 + " augmentation stats.");
		LOG.info("AugmentationData: Loaded: " + _augAccStats[0].size() * 4 + " accessory augmentation stats.");
		for (int i = 0; i < 10; i++)
		{
			LOG.info("AugmentationData: Loaded: " + _blueSkills[i].size() + " blue, " + _purpleSkills[i].size() + " purple and " + _redSkills[i].size() + " red skills for lifeStoneLevel " + i);
		}
	}

	public class augmentationStat
	{
		private final Stats _stat;
		private final int _singleSize;
		private final int _combinedSize;
		private final double _singleValues[];
		private final double _combinedValues[];

		public augmentationStat(Stats stat, double sValues[], double cValues[])
		{
			_stat = stat;
			_singleSize = sValues.length;
			_singleValues = sValues;
			_combinedSize = cValues.length;
			_combinedValues = cValues;
		}

		public int getSingleStatSize()
		{
			return _singleSize;
		}

		public int getCombinedStatSize()
		{
			return _combinedSize;
		}

		public double getSingleStatValue(int i)
		{
			if (i >= _singleSize || i < 0)
			{
				return _singleValues[_singleSize - 1];
			}
			return _singleValues[i];
		}

		public double getCombinedStatValue(int i)
		{
			if (i >= _combinedSize || i < 0)
			{
				return _combinedValues[_combinedSize - 1];
			}
			return _combinedValues[i];
		}

		public Stats getStat()
		{
			return _stat;
		}
	}


	@SuppressWarnings("unchecked")
	private final void load()
	{
		// Load the skillmap
		// Note: the skillmap data is only used when generating new augmentations
		// the client expects a different id in order to display the skill in the
		// items description...
		try
		{
			int badAugmantData = 0;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT, "data/stats/augmentation/augmentation_skillmap.xml");
//			FileInputStream stream = new FileInputStream(file);
//			InputStream output;
//			if ((byte) stream.read() == 0x00)
//			{
//				byte[] bytes = new byte[0];
//				output = new ByteArrayInputStream(bytes);
//				output = CryptUtil.decrypt(stream, output);
//			}
//			else
//			{
//				output = new FileInputStream(file);
//			}
//			Document doc = factory.newDocumentBuilder().parse(output);
			
			Document doc = factory.newDocumentBuilder().parse(file);
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("augmentation".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int skillId = 0, augmentationId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int skillLvL = 0;
							String type = "blue";

							TriggerType t = null;
							double chance = 0;
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								attrs = cd.getAttributes();
								if ("skillId".equalsIgnoreCase(cd.getNodeName()))
								{
									skillId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								}
								else if ("skillLevel".equalsIgnoreCase(cd.getNodeName()))
								{
									skillLvL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								}
								else if ("type".equalsIgnoreCase(cd.getNodeName()))
								{
									type = attrs.getNamedItem("val").getNodeValue();
								}
								else if ("trigger_type".equalsIgnoreCase(cd.getNodeName()))
								{
									t = TriggerType.valueOf(attrs.getNamedItem("val").getNodeValue());
								}
								else if ("trigger_chance".equalsIgnoreCase(cd.getNodeName()))
								{
									chance = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
								}
							}

							if ((skillId == 0) || (skillLvL == 0))
							{
								badAugmantData++;
								continue;
							}

							int k = (augmentationId - BLUE_START) / SKILLS_BLOCKSIZE;
							if (type.equalsIgnoreCase("blue"))
							{
								((List<Integer>) _blueSkills[k]).add(augmentationId);
							}
							else if (type.equalsIgnoreCase("purple"))
							{
								((List<Integer>) _purpleSkills[k]).add(augmentationId);
							}
							else if (type.equalsIgnoreCase("red"))
							{
								((List<Integer>) _redSkills[k]).add(augmentationId);
							}

							_allSkills.put(augmentationId, new Options(augmentationId, type, skillId, skillLvL, t, chance));
						}
					}
				}
			}

			if (badAugmantData != 0)
			{
				LOG.info("AugmentationData: " + badAugmantData + " bad skill(s) were skipped.");
			}
		}
		catch (FileNotFoundException e)
		{
			LOG.error("FileNotFoundException parsing augmentation_skillmap.xml.", e);
			return;
		}
		catch (NumberFormatException e)
		{
			LOG.error("NumberFormatException parsing augmentation_skillmap.xml.", e);
			return;
		}
		catch (DOMException e)
		{
			LOG.error("DOMException parsing augmentation_skillmap.xml.", e);
			return;
		}
		catch (IllegalArgumentException e)
		{
			LOG.error("IllegalArgumentException parsing augmentation_skillmap.xml.", e);
			return;
		}
		catch (IOException | ParserConfigurationException | SAXException | RuntimeException e)
		{
			LOG.error("Error parsing augmentation_skillmap.xml.", e);
			return;
		}

		// Load the stats from xml
		for (int i = 1; i < 5; i++)
		{
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setIgnoringComments(true);

				File file = new File(Config.DATAPACK_ROOT, "data/stats/augmentation/augmentation_stats" + i + ".xml");

				
				Document doc = factory.newDocumentBuilder().parse(file);
				//				FileInputStream stream = new FileInputStream(file);
				//				InputStream output;
//				if ((byte) stream.read() == 0x00)
//				{
//					byte[] bytes = new byte[0];
//					output = new ByteArrayInputStream(bytes);
//					output = CryptUtil.decrypt(stream, output);
//				}
//				else
//				{
//					output = new FileInputStream(file);
//				}
//				Document doc = factory.newDocumentBuilder().parse(output);

				for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("list".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("stat".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								String statName = attrs.getNamedItem("name").getNodeValue();
								double soloValues[] = null, combinedValues[] = null;

								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									if ("table".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();
										String tableName = attrs.getNamedItem("name").getNodeValue();

										StringTokenizer data = new StringTokenizer(cd.getFirstChild().getNodeValue());
										TDoubleArrayList array = new TDoubleArrayList();
										while (data.hasMoreTokens())
										{
											array.add(Double.parseDouble(data.nextToken()));
										}

										if (tableName.equalsIgnoreCase("#soloValues"))
										{
											soloValues = new double[array.size()];
											int x = 0;
											for (double value : array.toArray())
											{
												soloValues[x++] = value;
											}
										}
										else
										{
											combinedValues = new double[array.size()];
											int x = 0;
											for (double value : array.toArray())
											{
												combinedValues[x++] = value;
											}
										}
									}
								}

								// store this stat
								((List<augmentationStat>) _augStats[(i - 1)]).add(new augmentationStat(Stats.valueOfXml(statName), soloValues, combinedValues));
							}
						}
					}
				}
			}
			catch (DOMException | FileNotFoundException | NumberFormatException | ParserConfigurationException | SAXException e)
			{
				LOG.error("Error parsing augmentation_stats" + i + ".xml.", e);
				return;
			}
			catch (IOException e)
			{
				LOG.error("IOException while parsing augmentation_stats" + i + ".xml.", e);
				return;
			}

			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setIgnoringComments(true);

				File file = new File(Config.DATAPACK_ROOT, "data/stats/augmentation/augmentation_jewel_stats" + i + ".xml");
//				FileInputStream stream = new FileInputStream(file);
//				InputStream output;
//				if ((byte) stream.read() == 0x00)
//				{
//					byte[] bytes = new byte[0];
//					output = new ByteArrayInputStream(bytes);
//					output = CryptUtil.decrypt(stream, output);
//				}
//				else
//				{
//					output = new FileInputStream(file);
//				}
//				Document doc = factory.newDocumentBuilder().parse(output);
				Document doc = factory.newDocumentBuilder().parse(file);
				for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("list".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("stat".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								String statName = attrs.getNamedItem("name").getNodeValue();
								double soloValues[] = null, combinedValues[] = null;

								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									if ("table".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();
										String tableName = attrs.getNamedItem("name").getNodeValue();

										StringTokenizer data = new StringTokenizer(cd.getFirstChild().getNodeValue());
										TDoubleArrayList array = new TDoubleArrayList();
										while (data.hasMoreTokens())
										{
											array.add(Double.parseDouble(data.nextToken()));
										}

										if (tableName.equalsIgnoreCase("#soloValues"))
										{
											soloValues = new double[array.size()];
											int x = 0;
											for (double value : array.toArray())
											{
												soloValues[x++] = value;
											}
										}
										else
										{
											combinedValues = new double[array.size()];
											int x = 0;
											for (double value : array.toArray())
											{
												combinedValues[x++] = value;
											}
										}
									}
								}

								// store this stat
								((List<augmentationStat>) _augAccStats[(i - 1)]).add(new augmentationStat(Stats.valueOfXml(statName), soloValues, combinedValues));
							}
						}
					}
				}
			}
			catch (DOMException | FileNotFoundException | NumberFormatException | ParserConfigurationException | SAXException e)
			{
				LOG.error("Error parsing jewel augmentation_stats" + i + ".xml.", e);
				return;
			}
			catch (IOException e)
			{
				LOG.error("IOException parsing jewel augmentation_stats" + i + ".xml.", e);
				return;
			}
		}
	}

	public int generateRandomAugmentation(int lifeStoneLevel, int lifeStoneGrade, int bodyPart)
	{
		switch (bodyPart)
		{
		case ItemTemplate.SLOT_L_FINGER:
		case ItemTemplate.SLOT_R_FINGER:
		case ItemTemplate.SLOT_L_FINGER | ItemTemplate.SLOT_R_FINGER:
		case ItemTemplate.SLOT_L_EAR:
		case ItemTemplate.SLOT_R_EAR:
		case ItemTemplate.SLOT_L_EAR | ItemTemplate.SLOT_R_EAR:
		case ItemTemplate.SLOT_NECK:
			return generateRandomAccessoryAugmentation(lifeStoneLevel, bodyPart);
		default:
			return generateRandomWeaponAugmentation(lifeStoneLevel, lifeStoneGrade);
		}
	}

	private int generateRandomWeaponAugmentation(int lifeStoneLevel, int lifeStoneGrade)
	{
		// Note that stat12 stands for stat 1 AND 2 (same for stat34 ;p )
		// this is because a value can contain up to 2 stat modifications
		// (there are two int values packed in one integer value, meaning 4 stat modifications at max)
		// for more info take a look at getAugStatsById(...)

		// Note: lifeStoneGrade: (0 means low grade, 3 top grade)
		// First: determine whether we will add a skill/baseStatModifier or not
		// because this determine which color could be the result
		int stat12 = 0;
		int stat34 = 0;
		boolean generateSkill = false;
		boolean generateGlow = false;

		// lifestonelevel is used for stat Id and skill level, but here the max level is 9
		lifeStoneLevel = Math.min(lifeStoneLevel, 9);

		switch (lifeStoneGrade)
		{
		case 0:
			generateSkill = Rnd.chance(Config.AUGMENTATION_NG_SKILL_CHANCE);
			generateGlow = Rnd.chance(Config.AUGMENTATION_NG_GLOW_CHANCE);
			break;
		case 1:
			generateSkill = Rnd.chance(Config.AUGMENTATION_MID_SKILL_CHANCE);
			generateGlow = Rnd.chance(Config.AUGMENTATION_MID_GLOW_CHANCE);
			break;
		case 2:
			generateSkill = Rnd.chance(Config.AUGMENTATION_HIGH_SKILL_CHANCE);
			generateGlow = Rnd.chance(Config.AUGMENTATION_HIGH_GLOW_CHANCE);
			break;
		case 3:
			generateSkill = Rnd.chance(Config.AUGMENTATION_TOP_SKILL_CHANCE);
			generateGlow = Rnd.chance(Config.AUGMENTATION_TOP_GLOW_CHANCE);
			break;
		}

		if (!generateSkill && Rnd.get(1, 100) <= Config.AUGMENTATION_BASESTAT_CHANCE)
		{
			stat34 = Rnd.get(BASESTAT_STR, BASESTAT_MEN);
		}

		// Second: decide which grade the augmentation result is going to have:
		// 0:yellow, 1:blue, 2:purple, 3:red
		// The chances used here are most likely custom,
		// whats known is: you cant have yellow with skill(or baseStatModifier)
		// noGrade stone can not have glow, mid only with skill, high has a chance(custom), top allways glow
		int resultColor = Rnd.get(0, 100);
		if (stat34 == 0 && !generateSkill)
		{
			if (resultColor <= 15 * lifeStoneGrade + 40)
			{
				resultColor = 1;
			}
			else
			{
				resultColor = 0;
			}
		}
		else if (resultColor <= 10 * lifeStoneGrade + 5 || stat34 != 0)
		{
			resultColor = 3;
		}
		else if (resultColor <= 10 * lifeStoneGrade + 10)
		{
			resultColor = 1;
		}
		else
		{
			resultColor = 2;
		}

		// generate a skill if neccessary
		if (generateSkill)
		{
			switch (resultColor)
			{
			case 1: // blue skill
				stat34 = (Integer) _blueSkills[lifeStoneLevel].get(Rnd.get(0, _blueSkills[lifeStoneLevel].size() - 1));
				break;
			case 2: // purple skill
				stat34 = (Integer) _purpleSkills[lifeStoneLevel].get(Rnd.get(0, _purpleSkills[lifeStoneLevel].size() - 1));
				break;
			case 3: // red skill
				stat34 = (Integer) _redSkills[lifeStoneLevel].get(Rnd.get(0, _redSkills[lifeStoneLevel].size() - 1));
				break;
			}
		}

		// Third: Calculate the subblock offset for the choosen color,
		// and the level of the lifeStone
		// from large number of retail augmentations:
		// no skill part
		// Id for stat12:
		// A:1-910 B:911-1820 C:1821-2730 D:2731-3640 E:3641-4550 F:4551-5460 G:5461-6370 H:6371-7280
		// Id for stat34(this defines the color):
		// I:7281-8190(yellow) K:8191-9100(blue) L:10921-11830(yellow) M:11831-12740(blue)
		// you can combine I-K with A-D and L-M with E-H
		// using C-D or G-H Id you will get a glow effect
		// there seems no correlation in which grade use which Id except for the glowing restriction
		// skill part
		// Id for stat12:
		// same for no skill part
		// A same as E, B same as F, C same as G, D same as H
		// A - no glow, no grade LS
		// B - weak glow, mid grade LS?
		// C - glow, high grade LS?
		// D - strong glow, top grade LS?

		// is neither a skill nor basestat used for stat34? then generate a normal stat
		int offset;
		if (stat34 == 0)
		{
			int temp = Rnd.get(2, 3);
			int colorOffset = resultColor * 10 * STAT_SUBBLOCKSIZE + temp * STAT_BLOCKSIZE + 1;
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + colorOffset;

			stat34 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);
			if (generateGlow && lifeStoneGrade >= 2)
			{
				offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + (temp - 2) * STAT_BLOCKSIZE + lifeStoneGrade * 10 * STAT_SUBBLOCKSIZE + 1;
			}
			else
			{
				offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + (temp - 2) * STAT_BLOCKSIZE + Rnd.get(0, 1) * 10 * STAT_SUBBLOCKSIZE + 1;
			}
		}
		else if (!generateGlow)
		{
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + Rnd.get(0, 1) * STAT_BLOCKSIZE + 1;
		}
		else
		{
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + Rnd.get(0, 1) * STAT_BLOCKSIZE + (lifeStoneGrade + resultColor) / 2 * 10 * STAT_SUBBLOCKSIZE + 1;
		}

		stat12 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);

		return ((stat34 << 16) + stat12);
	}

	private int generateRandomAccessoryAugmentation(int lifeStoneLevel, int bodyPart)
	{
		int stat12 = 0;
		int stat34 = 0;
		int base = 0;
		int skillsLength = 0;

		lifeStoneLevel = Math.min(lifeStoneLevel, 9);

		switch (bodyPart)
		{
		case ItemTemplate.SLOT_L_FINGER:
		case ItemTemplate.SLOT_R_FINGER:
		case ItemTemplate.SLOT_L_FINGER | ItemTemplate.SLOT_R_FINGER:
			base = ACC_RING_START + ACC_RING_BLOCKSIZE * lifeStoneLevel;
			skillsLength = ACC_RING_SKILLS;
			break;
		case ItemTemplate.SLOT_L_EAR:
		case ItemTemplate.SLOT_R_EAR:
		case ItemTemplate.SLOT_L_EAR | ItemTemplate.SLOT_R_EAR:
			base = ACC_EAR_START + ACC_EAR_BLOCKSIZE * lifeStoneLevel;
			skillsLength = ACC_EAR_SKILLS;
			break;
		case ItemTemplate.SLOT_NECK:
			base = ACC_NECK_START + ACC_NECK_BLOCKSIZE * lifeStoneLevel;
			skillsLength = ACC_NECK_SKILLS;
			break;
		default:
			return 0;
		}

		int resultColor = Rnd.get(0, 3);
		TriggerInfo triggerInfo = null;

		// first augmentation (stats only)
		stat12 = Rnd.get(ACC_STAT_SUBBLOCKSIZE);

		if (Rnd.get(1, 100) <= Config.AUGMENTATION_ACC_SKILL_CHANCE)
		{
			// second augmentation (skill)
			stat34 = base + Rnd.get(skillsLength);
			if (_allSkills.containsKey(stat34))
			{
				triggerInfo = _allSkills.get(stat34).getTrigger();
			}
		}

		if (triggerInfo == null)
		{
			// second augmentation (stats)
			// calculating any different from stat12 value inside sub-block
			// starting from next and wrapping over using remainder
			stat34 = (stat12 + 1 + Rnd.get(ACC_STAT_SUBBLOCKSIZE - 1)) % ACC_STAT_SUBBLOCKSIZE;
			// this is a stats - skipping skills
			stat34 = base + skillsLength + ACC_STAT_SUBBLOCKSIZE * resultColor + stat34;
		}

		// stat12 has stats only
		stat12 = base + skillsLength + ACC_STAT_SUBBLOCKSIZE * resultColor + stat12;

		return ((stat34 << 16) + stat12);
	}

	/**
	 * @return Generates a random secondary augmentation using always the max values
	 */
	public int generateRandomSecondaryAugmentation()
	{
		final int lifeStoneLevel = 9;
		final int lifeStoneGrade = 3;
		final int resultColor = Rnd.get(0, 3);
		final int offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + Rnd.get(0, 1) * STAT_BLOCKSIZE + (lifeStoneGrade + resultColor) / 2 * 10 * STAT_SUBBLOCKSIZE + 1;

		return Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);
	}
}

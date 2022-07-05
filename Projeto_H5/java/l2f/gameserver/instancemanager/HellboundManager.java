package l2f.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import l2f.commons.geometry.Polygon;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Territory;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * Manages 11 stages of Hellbound Island and all it's events
 * @author pchayka
 */

public class HellboundManager
{
	private static final Logger _log = LoggerFactory.getLogger(HellboundManager.class);
	private static ArrayList<HellboundSpawn> _list;
	private static List<SimpleSpawner> _spawnList;
	private static HellboundManager _instance;
	private static int _initialStage;
	private static final long _taskDelay = 2 * 60 * 1000L; // 30min
	DeathListener _deathListener = new DeathListener();

	public static HellboundManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new HellboundManager();
		}
		return _instance;
	}

	public HellboundManager()
	{
		getHellboundSpawn();
		spawnHellbound();
		doorHandler();
		_initialStage = getHellboundLevel();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new StageCheckTask(), _taskDelay, _taskDelay);
		_log.info("Hellbound Manager: Loaded");
	}

	public static long getConfidence()
	{
		return ServerVariables.getLong("HellboundConfidence", 0);
	}

	public static void addConfidence(long value)
	{
		ServerVariables.set("HellboundConfidence", Math.round(getConfidence() + value * Config.RATE_HELLBOUND_CONFIDENCE));
	}

	public static void reduceConfidence(long value)
	{
		long i = getConfidence() - value;
		if (i < 1)
		{
			i = 1;
		}
		ServerVariables.set("HellboundConfidence", i);
	}

	public static void setConfidence(long value)
	{
		ServerVariables.set("HellboundConfidence", value);
	}

	public static int getHellboundLevel()
	{
		if (Config.HELLBOUND_LEVEL <= getHellboundLevelS())
		{
			return getHellboundLevelS();
		}
		return Config.HELLBOUND_LEVEL;
	}

	public static int getHellboundLevelS()
	{
		long confidence = ServerVariables.getLong("HellboundConfidence", 0);
		boolean judesBoxes = ServerVariables.getBool("HB_judesBoxes", false);
		boolean bernardBoxes = ServerVariables.getBool("HB_bernardBoxes", false);
		boolean derekKilled = ServerVariables.getBool("HB_derekKilled", false);
		boolean captainKilled = ServerVariables.getBool("HB_captainKilled", false);

		if (confidence < 1)
		{
			return 0;
		}
		else if (confidence >= 1 && confidence < 300000)
		{
			return 1;
		}
		else if (confidence >= 300000 && confidence < 600000)
		{
			return 2;
		}
		else if (confidence >= 600000 && confidence < 1000000)
		{
			return 3;
		}
		else if (confidence >= 1000000 && confidence < 1200000)
		{
			if (derekKilled && judesBoxes && bernardBoxes)
			{
				return 5;
			}
			else if (!derekKilled && judesBoxes && bernardBoxes)
			{
				return 4;
			}
			else if (!derekKilled && (!judesBoxes || !bernardBoxes))
			{
				return 3;
			}
		}
		else if (confidence >= 1200000 && confidence < 1500000)
		{
			return 6;
		}
		else if (confidence >= 1500000 && confidence < 1800000)
		{
			return 7;
		}
		else if (confidence >= 1800000 && confidence < 2100000)
		{
			if (captainKilled)
			{
				return 9;
			}
			else
			{
				return 8;
			}
		}
		else if (confidence >= 2100000 && confidence < 2200000)
		{
			return 10;
		}
		else if (confidence >= 2200000)
		{
			return 11;
		}

		return 0;
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if (killer == null || !cha.isMonster() || !killer.isPlayable())
			{
				return;
			}

			switch (getHellboundLevel())
			{
			case 0:
				break;
			case 1:
			{
				switch (cha.getNpcId())
				{
				case 22320: // Junior Watchman
				case 22321: // Junior Summoner
				case 22324: // Blind Huntsman
				case 22325: // Blind Watchman
					addConfidence(1);
					break;
				case 22327: // Arcane Scout
				case 22328: // Arcane Guardian
				case 22329: // Arcane Watchman
					addConfidence(3); // confirmed
					break;
				case 22322: // Subjugated Native
				case 22323: // Charmed Native
				case 32299: // Quarry Slave
					reduceConfidence(10);
					break;
				}
				break;
			}
			case 2:
			{
				switch (cha.getNpcId())
				{
				case 18463: // Remnant Diabolist
				case 18464: // Remnant Diviner
					addConfidence(5);
					break;
				case 22322: // Subjugated Native
				case 22323: // Charmed Native
				case 32299: // Quarry Slave
					reduceConfidence(10);
					break;
				}
				break;
			}
			case 3:
			{
				switch (cha.getNpcId())
				{
				case 22342: // Darion's Enforcer
				case 22343: // Darion's Executioner
					addConfidence(3);
					break;
				case 22341: // Keltas
					addConfidence(100);
					break;
				case 22322: // Subjugated Native
				case 22323: // Charmed Native
				case 32299: // Quarry Slave
					reduceConfidence(10);
					break;
				}
				break;
			}
			case 4:
			{
				switch (cha.getNpcId())
				{
				case 18465: // Derek
					addConfidence(10000);
					ServerVariables.set("HB_derekKilled", true);
					break;
				case 22322: // Subjugated Native
				case 22323: // Charmed Native
				case 32299: // Quarry Slave
					reduceConfidence(10);
					break;
				}
				break;
			}
			case 5:
			{
				switch (cha.getNpcId())
				{
				case 22448: // Leodas
					reduceConfidence(50);
					break;
				}
				break;
			}
			case 6:
			{
				switch (cha.getNpcId())
				{
				case 22326: // Hellinark
					addConfidence(500);
					break;
				case 18484: // Naia Failan
					addConfidence(5);
					break;
				}
				break;
			}
			case 8:
			{
				switch (cha.getNpcId())
				{
				case 18466: // Outpost Captain
					addConfidence(10000);
					ServerVariables.set("HB_captainKilled", true);
					break;
				}
				break;
			}
			default:
				break;
			}
		}
	}

	private void spawnHellbound()
	{
		SimpleSpawner spawnDat;
		NpcTemplate template;

		for (HellboundSpawn hbsi : _list)
		{
			if (ArrayUtils.contains(hbsi.getStages(), getHellboundLevel()))
			{
				try
				{
					template = NpcHolder.getInstance().getTemplate(hbsi.getNpcId());
					for (int i = 0; i < hbsi.getAmount(); i++)
					{
						spawnDat = new SimpleSpawner(template);
						spawnDat.setAmount(1);
						if (hbsi.getLoc() != null)
						{
							spawnDat.setLoc(hbsi.getLoc());
						}
						if (hbsi.getSpawnTerritory() != null)
						{
							spawnDat.setTerritory(hbsi.getSpawnTerritory());
						}
						spawnDat.setReflection(ReflectionManager.DEFAULT);
						spawnDat.setRespawnDelay(hbsi.getRespawn(), hbsi.getRespawnRnd());
						spawnDat.setRespawnTime(0);
						spawnDat.doSpawn(true);
						spawnDat.getLastSpawn().addListener(_deathListener);
						spawnDat.startRespawn();
						_spawnList.add(spawnDat);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while Spawning Hellbound! ", e);
				}
			}
		}
		_log.info("HellboundManager: Spawned " + _spawnList.size() + " mobs and NPCs according to the current Hellbound stage");
	}

	private void getHellboundSpawn()
	{
		_list = new ArrayList<HellboundSpawn>();
		_spawnList = new ArrayList<SimpleSpawner>();

		try
		{
			File file = new File(Config.DATAPACK_ROOT + "/data/hellbound_spawnlist.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			Document doc1 = factory.newDocumentBuilder().parse(file);

			int counter = 0;
			for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n1.getNodeName()))
				{
					for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
					{
						if ("data".equalsIgnoreCase(d1.getNodeName()))
						{
							counter++;
							int npcId = Integer.parseInt(d1.getAttributes().getNamedItem("npc_id").getNodeValue());
							Location spawnLoc = null;
							if (d1.getAttributes().getNamedItem("loc") != null)
							{
								spawnLoc = Location.parseLoc(d1.getAttributes().getNamedItem("loc").getNodeValue());
							}
							int count = 1;
							if (d1.getAttributes().getNamedItem("count") != null)
							{
								count = Integer.parseInt(d1.getAttributes().getNamedItem("count").getNodeValue());
							}
							int respawn = 60;
							if (d1.getAttributes().getNamedItem("respawn") != null)
							{
								respawn = Integer.parseInt(d1.getAttributes().getNamedItem("respawn").getNodeValue());
							}
							int respawnRnd = 0;
							if (d1.getAttributes().getNamedItem("respawn_rnd") != null)
							{
								respawnRnd = Integer.parseInt(d1.getAttributes().getNamedItem("respawn_rnd").getNodeValue());
							}

							Node att = d1.getAttributes().getNamedItem("stage");
							StringTokenizer st = new StringTokenizer(att.getNodeValue(), ";");
							int tokenCount = st.countTokens();
							int[] stages = new int[tokenCount];
							for (int i = 0; i < tokenCount; i++)
							{
								Integer value = Integer.decode(st.nextToken().trim());
								stages[i] = value;
							}

							Territory territory = null;
							for (Node s1 = d1.getFirstChild(); s1 != null; s1 = s1.getNextSibling())
							{
								if ("territory".equalsIgnoreCase(s1.getNodeName()))
								{

									Polygon poly = new Polygon();
									for (Node s2 = s1.getFirstChild(); s2 != null; s2 = s2.getNextSibling())
									{
										if ("add".equalsIgnoreCase(s2.getNodeName()))
										{
											int x = Integer.parseInt(s2.getAttributes().getNamedItem("x").getNodeValue());
											int y = Integer.parseInt(s2.getAttributes().getNamedItem("y").getNodeValue());
											int minZ = Integer.parseInt(s2.getAttributes().getNamedItem("zmin").getNodeValue());
											int maxZ = Integer.parseInt(s2.getAttributes().getNamedItem("zmax").getNodeValue());
											poly.add(x, y).setZmin(minZ).setZmax(maxZ);
										}
									}

									territory = new Territory().add(poly);

									if (!poly.validate())
									{
										_log.error("HellboundManager: Invalid spawn territory : " + poly + '!');
										continue;
									}
								}
							}

							if (spawnLoc == null && territory == null)
							{
								_log.error("HellboundManager: no spawn data for npc id : " + npcId + '!');
								continue;
							}

							HellboundSpawn hbs = new HellboundSpawn(npcId, spawnLoc, count, territory, respawn, respawnRnd, stages);
							_list.add(hbs);
						}
					}
				}
			}

			_log.info("HellboundManager: Loaded " + counter + " spawn entries.");
		}
		catch (NumberFormatException | DOMException | ParserConfigurationException | SAXException e)
		{
			_log.warn("HellboundManager: Spawn table could not be initialized.", e);
		}
		catch (IOException | IllegalArgumentException e)
		{
			_log.warn("HellboundManager: IOException or IllegalArgumentException.", e);
		}
	}

	private void despawnHellbound()
	{
		for (SimpleSpawner spawnToDelete : _spawnList)
		{
			spawnToDelete.deleteAll();
		}

		_spawnList.clear();
	}

	private class StageCheckTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if (_initialStage != getHellboundLevel())
			{
				despawnHellbound();
				spawnHellbound();
				doorHandler();
				_initialStage = getHellboundLevel();
			}
		}
	}

	public class HellboundSpawn
	{
		private final int _npcId;
		private final Location _loc;
		private final int _amount;
		private final Territory _spawnTerritory;
		private final int _respawn;
		private final int _respawnRnd;
		private final int[] _stages;

		public HellboundSpawn(int npcId, Location loc, int amount, Territory spawnTerritory, int respawn, int respawnRnd, int[] stages)
		{
			_npcId = npcId;
			_loc = loc;
			_amount = amount;
			_spawnTerritory = spawnTerritory;
			_respawn = respawn;
			_respawnRnd = respawnRnd;
			_stages = stages;
		}

		public int getNpcId()
		{
			return _npcId;
		}

		public Location getLoc()
		{
			return _loc;
		}

		public int getAmount()
		{
			return _amount;
		}

		public Territory getSpawnTerritory()
		{
			return _spawnTerritory;
		}

		public int getRespawn()
		{
			return _respawn;
		}

		public int getRespawnRnd()
		{
			return _respawnRnd;
		}

		public int[] getStages()
		{
			return _stages;
		}
	}

	private static void doorHandler()
	{
		final int NativeHell_native0131 = 19250001; // Kief room
		final int NativeHell_native0132 = 19250002;
		final int NativeHell_native0133 = 19250003; // Another room
		final int NativeHell_native0134 = 19250004;

		final int sdoor_trans_mesh00 = 20250002;
		final int Hell_gate_door = 20250001;

		final int[] _doors =
		{
			NativeHell_native0131,
			NativeHell_native0132,
			NativeHell_native0133,
			NativeHell_native0134,
			sdoor_trans_mesh00,
			Hell_gate_door
		};

		for (int i = 0; i < _doors.length; i++)
		{
			ReflectionUtils.getDoor(_doors[i]).closeMe();
		}

		switch (getHellboundLevel())
		{
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			break;
		case 6:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			break;
		case 7:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
			break;
		case 8:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
			break;
		case 9:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
			ReflectionUtils.getDoor(Hell_gate_door).openMe();
			break;
		case 10:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
			ReflectionUtils.getDoor(Hell_gate_door).openMe();
			break;
		case 11:
			ReflectionUtils.getDoor(NativeHell_native0131).openMe();
			ReflectionUtils.getDoor(NativeHell_native0132).openMe();
			ReflectionUtils.getDoor(sdoor_trans_mesh00).openMe();
			ReflectionUtils.getDoor(Hell_gate_door).openMe();
			break;
		default:
			break;
		}
	}
}
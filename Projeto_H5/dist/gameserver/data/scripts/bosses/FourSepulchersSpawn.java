package bosses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.util.Rnd;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;
import npc.model.SepulcherMonsterInstance;
import npc.model.SepulcherNpcInstance;
import npc.model.SepulcherRaidInstance;

public class FourSepulchersSpawn extends Functions implements ScriptFile
{
	private static final Logger LOG = LoggerFactory.getLogger(FourSepulchersSpawn.class);

	public static Map<Integer, NpcLocation> _shadowSpawns = new HashMap<Integer, NpcLocation>();
	public static Map<Integer, NpcLocation> _mysteriousBoxSpawns = new HashMap<Integer, NpcLocation>();
	public static Map<Integer, List<NpcLocation>> _dukeFinalMobs = new HashMap<Integer, List<NpcLocation>>();
	public static Map<Integer, List<NpcLocation>> _emperorsGraveNpcs = new HashMap<Integer, List<NpcLocation>>();
	public static Map<Integer, List<NpcLocation>> _magicalMonsters = new HashMap<Integer, List<NpcLocation>>();
	public static Map<Integer, List<NpcLocation>> _physicalMonsters = new HashMap<Integer, List<NpcLocation>>();
	public static Map<Integer, Location> _startHallSpawns = new HashMap<Integer, Location>();
	public static Map<Integer, Boolean> _hallInUse = new HashMap<Integer, Boolean>();
	public static List<GateKeeper> _GateKeepers = new ArrayList<GateKeeper>();
	public static Map<Integer, Integer> _keyBoxNpc = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> _victim = new HashMap<Integer, Integer>();
	public static Map<Integer, Boolean> _archonSpawned = new HashMap<Integer, Boolean>();

	public static Map<Integer, List<SepulcherMonsterInstance>> _dukeMobs = new HashMap<Integer, List<SepulcherMonsterInstance>>();
	public static Map<Integer, List<SepulcherMonsterInstance>> _viscountMobs = new HashMap<Integer, List<SepulcherMonsterInstance>>();

	public static List<SepulcherNpcInstance> _managers;

	public static List<NpcInstance> _allMobs = new ArrayList<NpcInstance>();

	public static class NpcLocation extends Location
	{
		public int npcId;

		public NpcLocation()
		{
		}

		public NpcLocation(int x, int y, int z, int heading, int npcId)
		{
			super(x, y, z, heading);
			this.npcId = npcId;
		}
	}

	private static Location[] _startHallSpawn =
	{
		new Location(181632, -85587, -7218),
		new Location(179963, -88978, -7218),
		new Location(173217, -86132, -7218),
		new Location(175608, -82296, -7218)
	};

	private static NpcLocation[][] _shadowSpawnLoc =
	{
		{
			// x, y, z, heading, npcId
			new NpcLocation(191231, -85574, -7216, 33380, 25339),
			new NpcLocation(189534, -88969, -7216, 32768, 25349),
			new NpcLocation(173195, -76560, -7215, 49277, 25346),
			new NpcLocation(175591, -72744, -7215, 49317, 25342)
		},
		{
			new NpcLocation(191231, -85574, -7216, 33380, 25342),
			new NpcLocation(189534, -88969, -7216, 32768, 25339),
			new NpcLocation(173195, -76560, -7215, 49277, 25349),
			new NpcLocation(175591, -72744, -7215, 49317, 25346)
		},
		{
			new NpcLocation(191231, -85574, -7216, 33380, 25346),
			new NpcLocation(189534, -88969, -7216, 32768, 25342),
			new NpcLocation(173195, -76560, -7215, 49277, 25339),
			new NpcLocation(175591, -72744, -7215, 49317, 25349)
		},
		{
			new NpcLocation(191231, -85574, -7216, 33380, 25349),
			new NpcLocation(189534, -88969, -7216, 32768, 25346),
			new NpcLocation(173195, -76560, -7215, 49277, 25342),
			new NpcLocation(175591, -72744, -7215, 49317, 25339)
		}
	};

	public static void init()
	{
		initFixedInfo();
		loadMysteriousBox();
		loadPhysicalMonsters();
		loadMagicalMonsters();
		initLocationShadowSpawns();
		loadDukeMonsters();
		loadEmperorsGraveMonsters();
		spawnManagers();
	}

	private static void initFixedInfo()
	{
		_startHallSpawns.put(31921, _startHallSpawn[0]);
		_startHallSpawns.put(31922, _startHallSpawn[1]);
		_startHallSpawns.put(31923, _startHallSpawn[2]);
		_startHallSpawns.put(31924, _startHallSpawn[3]);

		_hallInUse.put(31921, false);
		_hallInUse.put(31922, false);
		_hallInUse.put(31923, false);
		_hallInUse.put(31924, false);

		_GateKeepers.add(new GateKeeper(31925, 182727, -85493, -7200, -32584, 25150012));
		_GateKeepers.add(new GateKeeper(31926, 184547, -85479, -7200, -32584, 25150013));
		_GateKeepers.add(new GateKeeper(31927, 186349, -85473, -7200, -32584, 25150014));
		_GateKeepers.add(new GateKeeper(31928, 188154, -85463, -7200, -32584, 25150015));
		_GateKeepers.add(new GateKeeper(31929, 189947, -85466, -7200, -32584, 25150016));

		_GateKeepers.add(new GateKeeper(31930, 181030, -88868, -7200, -33272, 25150002));
		_GateKeepers.add(new GateKeeper(31931, 182809, -88856, -7200, -33272, 25150003));
		_GateKeepers.add(new GateKeeper(31932, 184626, -88859, -7200, -33272, 25150004));
		_GateKeepers.add(new GateKeeper(31933, 186438, -88858, -7200, -33272, 25150005));
		_GateKeepers.add(new GateKeeper(31934, 188236, -88854, -7200, -33272, 25150006));

		_GateKeepers.add(new GateKeeper(31935, 173102, -85105, -7200, -16248, 25150032));
		_GateKeepers.add(new GateKeeper(31936, 173101, -83280, -7200, -16248, 25150033));
		_GateKeepers.add(new GateKeeper(31937, 173103, -81479, -7200, -16248, 25150034));
		_GateKeepers.add(new GateKeeper(31938, 173086, -79698, -7200, -16248, 25150035));
		_GateKeepers.add(new GateKeeper(31939, 173083, -77896, -7200, -16248, 25150036));

		_GateKeepers.add(new GateKeeper(31940, 175497, -81265, -7200, -16248, 25150022));
		_GateKeepers.add(new GateKeeper(31941, 175495, -79468, -7200, -16248, 25150023));
		_GateKeepers.add(new GateKeeper(31942, 175488, -77652, -7200, -16248, 25150024));
		_GateKeepers.add(new GateKeeper(31943, 175489, -75856, -7200, -16248, 25150025));
		_GateKeepers.add(new GateKeeper(31944, 175478, -74049, -7200, -16248, 25150026));

		_keyBoxNpc.put(18120, 31455);
		_keyBoxNpc.put(18121, 31455);
		_keyBoxNpc.put(18122, 31455);
		_keyBoxNpc.put(18123, 31455);
		_keyBoxNpc.put(18124, 31456);
		_keyBoxNpc.put(18125, 31456);
		_keyBoxNpc.put(18126, 31456);
		_keyBoxNpc.put(18127, 31456);
		_keyBoxNpc.put(18128, 31457);
		_keyBoxNpc.put(18129, 31457);
		_keyBoxNpc.put(18130, 31457);
		_keyBoxNpc.put(18131, 31457);
		_keyBoxNpc.put(18149, 31458);
		_keyBoxNpc.put(18150, 31459);
		_keyBoxNpc.put(18151, 31459);
		_keyBoxNpc.put(18152, 31459);
		_keyBoxNpc.put(18153, 31459);
		_keyBoxNpc.put(18154, 31460);
		_keyBoxNpc.put(18155, 31460);
		_keyBoxNpc.put(18156, 31460);
		_keyBoxNpc.put(18157, 31460);
		_keyBoxNpc.put(18158, 31461);
		_keyBoxNpc.put(18159, 31461);
		_keyBoxNpc.put(18160, 31461);
		_keyBoxNpc.put(18161, 31461);
		_keyBoxNpc.put(18162, 31462);
		_keyBoxNpc.put(18163, 31462);
		_keyBoxNpc.put(18164, 31462);
		_keyBoxNpc.put(18165, 31462);
		_keyBoxNpc.put(18183, 31463);
		_keyBoxNpc.put(18184, 31464);
		_keyBoxNpc.put(18212, 31465);
		_keyBoxNpc.put(18213, 31465);
		_keyBoxNpc.put(18214, 31465);
		_keyBoxNpc.put(18215, 31465);
		_keyBoxNpc.put(18216, 31466);
		_keyBoxNpc.put(18217, 31466);
		_keyBoxNpc.put(18218, 31466);
		_keyBoxNpc.put(18219, 31466);

		_victim.put(18150, 18158);
		_victim.put(18151, 18159);
		_victim.put(18152, 18160);
		_victim.put(18153, 18161);
		_victim.put(18154, 18162);
		_victim.put(18155, 18163);
		_victim.put(18156, 18164);
		_victim.put(18157, 18165);
	}

	private static void initLocationShadowSpawns()
	{
		// int locNo = Rnd.get(4);
		int locNo = 0;
		int[] gateKeeper =
		{
			31929,
			31934,
			31939,
			31944
		};

		_shadowSpawns.clear();
		for (int i = 0; i <= 3; i++)
		{
			NpcLocation loc = new NpcLocation();
			loc.set(_shadowSpawnLoc[locNo][i]);
			loc.npcId = _shadowSpawnLoc[locNo][i].npcId;
			_shadowSpawns.put(gateKeeper[i], loc);
		}
	}

	private static void loadEmperorsGraveMonsters()
	{
		_emperorsGraveNpcs.clear();
		int count = loadSpawn(_emperorsGraveNpcs, 6);
		LOG.info("FourSepulchersManager: loaded " + count + " Emperor's grave NPC spawns.");
	}

	private static void loadDukeMonsters()
	{
		_dukeFinalMobs.clear();
		_archonSpawned.clear();
		int count = loadSpawn(_dukeFinalMobs, 5);
		for (Integer npcId : _dukeFinalMobs.keySet())
		{
			_archonSpawned.put(npcId, false);
		}
		LOG.info("FourSepulchersManager: loaded " + count + " Church of duke monsters spawns.");
	}

	private static void loadMagicalMonsters()
	{
		_magicalMonsters.clear();
		int count = loadSpawn(_magicalMonsters, 2);
		LOG.info("FourSepulchersManager: loaded " + count + " magical monsters spawns.");
	}

	private static void loadPhysicalMonsters()
	{
		_physicalMonsters.clear();
		int count = loadSpawn(_physicalMonsters, 1);
		LOG.info("FourSepulchersManager: loaded " + count + " physical monsters spawns.");
	}

	private static int loadSpawn(Map<Integer, List<NpcLocation>> table, int type)
	{
		int count = 0;
		Connection con = null;
		PreparedStatement statement1 = null;
		ResultSet rset1 = null;
		PreparedStatement statement2 = null;
		ResultSet rset2 = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement1 = con.prepareStatement("SELECT DISTINCT key_npc_id FROM four_sepulchers_spawnlist WHERE spawntype = ? ORDER BY key_npc_id");
			statement1.setInt(1, type);
			rset1 = statement1.executeQuery();
			statement2 = con.prepareStatement(
						"SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist WHERE key_npc_id = ? AND spawntype = ? ORDER BY id");
			while (rset1.next())
			{
				int keyNpcId = rset1.getInt("key_npc_id");
				statement2.setInt(1, keyNpcId);
				statement2.setInt(2, type);
				rset2 = statement2.executeQuery();

				List<NpcLocation> locations = new ArrayList<NpcLocation>();
				while (rset2.next())
				{
					locations.add(new NpcLocation(rset2.getInt("locx"), rset2.getInt("locy"), rset2.getInt("locz"), rset2.getInt("heading"), rset2.getInt("npc_templateid")));
					count++;
				}

				DbUtils.close(rset2);
				table.put(keyNpcId, locations);
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while Loading Four Sepulchers Spawns", e);
		}
		finally
		{
			DbUtils.closeQuietly(statement2, rset2);
			DbUtils.closeQuietly(con, statement1, rset1);
		}

		return count;
	}

	private static void loadMysteriousBox()
	{
		_mysteriousBoxSpawns.clear();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, key_npc_id FROM four_sepulchers_spawnlist WHERE spawntype = 0 ORDER BY id");
			rset = statement.executeQuery();

			while (rset.next())
			{
				_mysteriousBoxSpawns.put(rset.getInt("key_npc_id"), new NpcLocation(rset.getInt("locx"), rset.getInt("locy"), rset.getInt("locz"), rset.getInt("heading"), rset.getInt("npc_templateid")));
			}

			LOG.info("FourSepulchersManager: Loaded " + _mysteriousBoxSpawns.size() + " Mysterious-Box spawns.");
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Mystierios Box from Four Sepulchers Spawns", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	private static void spawnManagers()
	{
		_managers = new ArrayList<SepulcherNpcInstance>();
		for (int i = 31921; i <= 31924; i++)
		{
			try
			{
				NpcTemplate template = NpcHolder.getInstance().getTemplate(i);
				Location loc = null;
				switch (i)
				{
				case 31921: // conquerors
					loc = new Location(181061, -85595, -7200, -32584);
					break;
				case 31922: // emperors
					loc = new Location(179292, -88981, -7200, -33272);
					break;
				case 31923: // sages
					loc = new Location(173202, -87004, -7200, -16248);
					break;
				case 31924: // judges
					loc = new Location(175606, -82853, -7200, -16248);
					break;
				}
				SepulcherNpcInstance npc = new SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
				npc.setSpawnedLoc(loc);
				npc.spawnMe(loc);
				_managers.add(npc);
				LOG.info("FourSepulchersManager: Spawned " + template.name);
			}
			catch (RuntimeException e)
			{
				LOG.error("Error while Spawning Four Sepulcher Managers", e);
			}
		}
	}

	static void closeAllDoors()
	{
		for (GateKeeper gk : _GateKeepers)
		{
			try
			{
				gk.door.closeMe();
			}
			catch (RuntimeException e)
			{
				LOG.error("Error while closing Four Sepulchers Spawn", e);
			}
		}
	}

	public static void deleteAllMobs()
	{
		for (NpcInstance mob : _allMobs)
		{
			mob.deleteMe();
		}
		_allMobs.clear();
	}

	public static void spawnShadow(int npcId)
	{
		if (!FourSepulchersManager.isAttackTime())
		{
			return;
		}
		NpcLocation loc = _shadowSpawns.get(npcId);
		if (loc == null)
		{
			return;
		}
		NpcTemplate template = NpcHolder.getInstance().getTemplate(loc.npcId);
		SepulcherRaidInstance mob = new SepulcherRaidInstance(IdFactory.getInstance().getNextId(), template);
		mob.setSpawnedLoc(loc);
		mob.spawnMe(loc);
		mob.mysteriousBoxId = npcId;
		_allMobs.add(mob);
	}

	public static void locationShadowSpawns()
	{
		int locNo = Rnd.get(4);
		int[] gateKeeper =
		{
			31929,
			31934,
			31939,
			31944
		};
		for (int i = 0; i <= 3; i++)
		{
			Location loc = _shadowSpawns.get(gateKeeper[i]);
			// Обновляем только координаты, не npcId.
			loc.x = _shadowSpawnLoc[locNo][i].x;
			loc.y = _shadowSpawnLoc[locNo][i].y;
			loc.z = _shadowSpawnLoc[locNo][i].z;
			loc.h = _shadowSpawnLoc[locNo][i].h;
		}
	}

	public static void spawnEmperorsGraveNpc(int npcId)
	{
		if (!FourSepulchersManager.isAttackTime())
		{
			return;
		}
		List<NpcLocation> monsterList = _emperorsGraveNpcs.get(npcId);
		if (monsterList != null)
		{
			for (NpcLocation loc : monsterList)
			{
				NpcTemplate template = NpcHolder.getInstance().getTemplate(loc.npcId);
				NpcInstance npc = null;
				if (template.isInstanceOf(SepulcherMonsterInstance.class))
				{
					npc = new SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
				}
				else
				{
					npc = new SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
				}
				npc.setSpawnedLoc(loc);
				npc.spawnMe(loc);
				_allMobs.add(npc);
			}
		}
	}

	public static void spawnArchonOfHalisha(int npcId)
	{
		if (!FourSepulchersManager.isAttackTime() || _archonSpawned.get(npcId))
		{
			return;
		}
		List<NpcLocation> monsterList = _dukeFinalMobs.get(npcId);
		if (monsterList == null)
		{
			return;
		}
		for (NpcLocation loc : monsterList)
		{
			NpcTemplate template = NpcHolder.getInstance().getTemplate(loc.npcId);
			SepulcherMonsterInstance mob = new SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
			mob.setSpawnedLoc(loc);
			mob.spawnMe(loc);
			mob.mysteriousBoxId = npcId;
			_allMobs.add(mob);
		}
		_archonSpawned.put(npcId, true);
	}

	public static void spawnExecutionerOfHalisha(NpcInstance npc)
	{
		if (!FourSepulchersManager.isAttackTime())
		{
			return;
		}
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_victim.get(npc.getNpcId()));
		SepulcherMonsterInstance npc2 = new SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
		npc2.setSpawnedLoc(npc.getLoc());
		npc2.spawnMe(npc.getLoc());
		_allMobs.add(npc2);
	}

	public static void spawnKeyBox(NpcInstance npc)
	{
		if (!FourSepulchersManager.isAttackTime())
		{
			return;
		}
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_keyBoxNpc.get(npc.getNpcId()));
		SepulcherNpcInstance npc2 = new SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
		npc2.setSpawnedLoc(npc.getLoc());
		npc2.spawnMe(npc.getLoc());
		_allMobs.add(npc2);
	}

	public static void spawnMonster(int npcId)
	{
		if (!FourSepulchersManager.isAttackTime())
		{
			return;
		}

		List<NpcLocation> monsterList;
		List<SepulcherMonsterInstance> mobs = new ArrayList<SepulcherMonsterInstance>();

		if (Rnd.get(2) == 0)
		{
			monsterList = _physicalMonsters.get(npcId);
		}
		else
		{
			monsterList = _magicalMonsters.get(npcId);
		}

		if (monsterList != null)
		{
			boolean spawnKeyBoxMob = false;
			boolean spawnedKeyBoxMob = false;

			for (NpcLocation loc : monsterList)
			{
				if (spawnedKeyBoxMob)
				{
					spawnKeyBoxMob = false;
				}
				else
				{
					switch (npcId)
					{
					case 31469:
					case 31474:
					case 31479:
					case 31484:
						if (Rnd.chance(2))
						{
							spawnKeyBoxMob = true;
							spawnedKeyBoxMob = true;
						}
						break;
					}
				}

				NpcTemplate template = NpcHolder.getInstance().getTemplate(spawnKeyBoxMob ? 18149 : loc.npcId);
				SepulcherMonsterInstance mob = new SepulcherMonsterInstance(IdFactory.getInstance().getNextId(), template);
				mob.setSpawnedLoc(loc);
				mob.spawnMe(loc);
				mob.mysteriousBoxId = npcId;
				switch (npcId)
				{
				case 31469:
				case 31474:
				case 31479:
				case 31484:
				case 31472:
				case 31477:
				case 31482:
				case 31487:
					mobs.add(mob);
					break;
				}
				_allMobs.add(mob);
			}

			switch (npcId)
			{
			case 31469:
			case 31474:
			case 31479:
			case 31484:
				_viscountMobs.put(npcId, mobs);
				break;

			case 31472:
			case 31477:
			case 31482:
			case 31487:
				_dukeMobs.put(npcId, mobs);
				break;
			}
		}
	}

	public static void spawnMysteriousBox(int npcId)
	{
		if (!FourSepulchersManager.isAttackTime() || (_mysteriousBoxSpawns.get(npcId) == null))
		{
			return;
		}
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_mysteriousBoxSpawns.get(npcId).npcId);
		SepulcherNpcInstance npc = new SepulcherNpcInstance(IdFactory.getInstance().getNextId(), template);
		npc.setSpawnedLoc(_mysteriousBoxSpawns.get(npcId));
		npc.spawnMe(npc.getSpawnedLoc());
		_allMobs.add(npc);
	}

	public static synchronized boolean isDukeMobsAnnihilated(int npcId)
	{
		List<SepulcherMonsterInstance> mobs = _dukeMobs.get(npcId);
		if (mobs == null)
		{
			return true;
		}
		for (SepulcherMonsterInstance mob : mobs)
		{
			if (!mob.isDead())
			{
				return false;
			}
		}
		return true;
	}

	public static synchronized boolean isViscountMobsAnnihilated(int npcId)
	{
		List<SepulcherMonsterInstance> mobs = _viscountMobs.get(npcId);
		if (mobs == null)
		{
			return true;
		}
		for (SepulcherMonsterInstance mob : mobs)
		{
			if (!mob.isDead())
			{
				return false;
			}
		}
		return true;
	}

	public static boolean isShadowAlive(int id)
	{
		NpcLocation loc = _shadowSpawns.get(id);
		if (loc == null)
		{
			return true;
		}
		for (NpcInstance n : _allMobs)
		{
			if (n.getNpcId() == loc.npcId && !n.isDead())
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isKeyBoxMobSpawned()
	{
		for (NpcInstance n : _allMobs)
		{
			if (n.getNpcId() == 18149)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	public static class GateKeeper extends Location
	{
		public final DoorInstance door;
		public final NpcTemplate template;

		public GateKeeper(int npcId, int _x, int _y, int _z, int _h, int doorId)
		{
			super(_x, _y, _z, _h);
			door = ReflectionUtils.getDoor(doorId);
			template = NpcHolder.getInstance().getTemplate(npcId);
			if (template == null)
			{
				LOG.warn("FourGoblets::Sepulcher::RoomLock npc_template " + npcId + " undefined");
			}
			if (door == null)
			{
				LOG.warn("FourGoblets::Sepulcher::RoomLock door id " + doorId + " undefined");
			}
		}
	}
}
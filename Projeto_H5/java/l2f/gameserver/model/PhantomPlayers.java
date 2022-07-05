package l2f.gameserver.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.PhantomPlayerAI;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.data.xml.holder.ArmorSetsHolder;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2f.gameserver.data.xml.holder.SkillAcquireHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.CursedWeaponsManager;
import l2f.gameserver.model.actor.instances.player.Bonus;
import l2f.gameserver.model.base.AcquireType;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.base.InvisibleType;
import l2f.gameserver.model.base.MultiSellEntry;
import l2f.gameserver.model.base.MultiSellIngredient;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.base.RestartType;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.item.CreateItem;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.item.WeaponTemplate;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Util;

public class PhantomPlayers
{
	private static final Logger _log = Logger.getLogger(PhantomPlayers.class.getName());
	private static List<Integer> _phantoms;
	private static List<Integer> _phantomsWithClan;
	private static List<String> _phantomNames;
	private static List<String> _phantomTitles;
	private static List<PhantomSpawner> _phantomSpawners;

	public static void init()
	{
		_log.info("Loading phantom players...");
		stopSpawners();

		try
		{
			File file = new File("config/phantom/player_names.txt");
			_phantomNames = FileUtils.readLines(file);
		}
		catch (IOException e)
		{
			_log.warn("PhantomPlayers: Unable to load phantom player names.", e);
			_phantomNames = Collections.emptyList();
		}

		try
		{
			File file = new File("config/phantom/player_titles.txt");
			_phantomTitles = FileUtils.readLines(file);
		}
		catch (IOException e)
		{
			_log.warn("PhantomPlayers: Unable to load phantom Titles.", e);
			_phantomTitles = Collections.emptyList();
		}

		_phantoms = new ArrayList<Integer>();
		_phantomsWithClan = new ArrayList<Integer>();
		_phantomSpawners = new ArrayList<PhantomSpawner>();

		_log.info("Loaded " + _phantomNames.size() + " possible phantom names.");
		_log.info("Loaded " + _phantomTitles.size() + " possible phantom titles.");

		cacheFantoms();

		_log.info("Loaded " + _phantoms.size() + " phantom players from database with a maximum of " + Config.PHANTOM_MAX_PLAYERS + " phantoms.");
		_log.info("Scheduled spawner with " + Config.PHANTOM_SPAWN_DELAY + " seconds delay.");
	}

	private static void cacheFantoms()
	{
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			con.setTransactionIsolation(1);
			st = con.prepareStatement("SELECT * FROM characters WHERE account_name=?"); // Yes, and FUCK YOU! This is the way we will mark phantoms
			st.setString(1, Config.PHANTOM_PLAYERS_ACCOUNT);
			rs = st.executeQuery();
			while (rs.next())
			{
				int objId = rs.getInt("obj_Id");
				int clanid = rs.getInt("clanid");
				String name = rs.getString("char_name");

				_phantoms.add(objId);

				if (_phantomNames.contains(name))
				{
					_phantomNames.remove(name);
				}

				if (clanid > 0)
				{
					_phantomsWithClan.add(objId);
				}
			}

			st.close();
			rs.close();
			con.close();
			_log.info("PhantomPlayers: Cached " + _phantoms.size() + " players.");
			_log.info("PhantomPlayers: Free names " + _phantomNames.size() + ".");

		}
		catch (Exception e)
		{
			_log.warn("PhantomPlayers: could not load chars from DB: " + e);
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, st, rs);
		}
	}

	public static Player createNewPhantom()
	{
		if (_phantomNames == null)
		{
			return null;
		}

		for (int i = 0; i < 25; i++) // 25 tries to make a phantom player. Enough tries to avoid name duplicate and other stuff.
		{
			String name = Rnd.get(_phantomNames);
			Player player = createNewPhantom(name, -1);
			if (player != null)
			{
				return player;
			}
		}

		return null;
	}

	public static Player createNewPhantomWithLevel(int level)
	{
		if (_phantomNames == null)
		{
			return null;
		}

		for (int i = 0; i < 25; i++) // 25 tries to make a phantom player. Enough tries to avoid name duplicate and other stuff.
		{
			String name = Rnd.get(_phantomNames);
			Player player = createNewPhantom(name, level);
			if (player != null)
			{
				return player;
			}
		}

		return null;
	}

	public static Player createNewPhantom(String name, int level)
	{
		Race race = Rnd.get(Race.values());
		boolean mage = Rnd.nextBoolean();
		if (name.toLowerCase().contains("darkelf") || name.toLowerCase().contains("dance"))
		{
			race = Race.darkelf;
		}
		else if (name.toLowerCase().contains("elf"))
		{
			race = Race.elf;
		}
		else if (name.toLowerCase().contains("human"))
		{
			race = Race.human;
		}
		else if (name.toLowerCase().contains("orc"))
		{
			race = Race.orc;
		}
		else if (name.toLowerCase().contains("dwarf") || name.toLowerCase().contains("spoil"))
		{
			race = Race.dwarf;
		}
		else if (name.toLowerCase().contains("kamael"))
		{
			race = Race.kamael;
		}

		if (name.toLowerCase().contains("mage") || name.toLowerCase().contains("summon") || name.toLowerCase().contains("summoner") || name.toLowerCase().contains("buff")
					|| name.toLowerCase().contains("buffer"))
		{
			mage = true;
		}

		return createNewPhantom(name, race, mage, level);
	}

	public static Player createNewPhantom(String name, Race race, boolean mage, int level)
	{
		boolean female = Rnd.nextBoolean();
		List<Integer> classes = new ArrayList<Integer>();
		for (ClassId c : ClassId.values())
		{
			// Check if class is banned.
			if (Util.contains(Config.PHANTOM_BANNED_CLASSID, c.getId()))
			{
				continue;
			}

			if (c.getLevel() == 4)
			{
				classes.add(c.getId());
			}
		}

		int classId = Rnd.get(classes);

		if (classId == -1)
		{
			switch (race)
			{
			case human:
				if (mage)
				{
					classId = ClassId.fighter.getId();
				}
				else
				{
					classId = ClassId.mage.getId();
				}
				break;
			case elf:
				if (mage)
				{
					classId = ClassId.elvenFighter.getId();
				}
				else
				{
					classId = ClassId.elvenMage.getId();
				}
				break;
			case darkelf:
				if (mage)
				{
					classId = ClassId.darkFighter.getId();
				}
				else
				{
					classId = ClassId.darkMage.getId();
				}
				break;
			case orc:
				if (mage)
				{
					classId = ClassId.orcFighter.getId();
				}
				else
				{
					classId = ClassId.orcMage.getId();
				}
				break;
			case dwarf:
				classId = ClassId.dwarvenFighter.getId();
				break;
			case kamael:
				if (mage)
				{
					classId = ClassId.femaleSoldier.getId();
					female = true;
				}
				else
				{
					classId = ClassId.maleSoldier.getId();
					female = false;
				}
			}
		}

		return createNewPhantom(name, level, classId, mage, female, race, "-1");
	}

	public static Player createNewPhantom(String name, int level, int classId, boolean isMage, boolean isMale, Race race, String title)
	{
		if (_phantomNames == null)
		{
			return null;
		}

		if (name.equals("-1"))
		{
			name = Rnd.get(_phantomNames);
		}

		if (classId == -1)
		{
			if (level == -1)
			{
				level = 85;
			}

			List<Integer> classes = new ArrayList<Integer>();
			for (ClassId c : ClassId.values())
			{
				if (Util.contains(Config.PHANTOM_BANNED_CLASSID, c.getId()) || (level <= 20 && c.getLevel() != 1))
				{
					continue;
				}

				if (level >= 20 && level < 40 && c.getLevel() != 2)
				{
					continue;
				}

				if (level >= 40 && level < 76 && c.getLevel() != 3)
				{
					continue;
				}

				if (level >= 76 && c.getLevel() != 4)
				{
					continue;
				}

				classes.add(c.getId());
			}

			classId = Rnd.get(classes);
		}

		try
		{
			int count = 0;
			while (!Util.isMatchingRegexp(name, Config.CHAR_NAME_TEMPLATE) && count++ < 25)
			{
				if (_phantomNames.contains(name))
				{
					_phantomNames.remove(name);
				}

				name = Rnd.get(_phantomNames);
			}

			if (!Util.isMatchingRegexp(name, Config.CHAR_NAME_TEMPLATE))
			{
				return null;
			}

			count = 0;
			while (CharacterDAO.getInstance().getObjectIdByName(name) > 0 && count++ < 25)
			{
				if (_phantomNames.contains(name))
				{
					_phantomNames.remove(name);
				}

				name = Rnd.get(_phantomNames);
			}

			if (CharacterDAO.getInstance().getObjectIdByName(name) > 0)
			{
				return null;
			}

			int hairStyle = Rnd.get(3);
			int hairColor = Rnd.get(3);
			int face = Rnd.get(3);

			Player newChar = Player.create(classId, isMale ? 0 : 1, Config.PHANTOM_PLAYERS_ACCOUNT, name, hairStyle, hairColor, face);
			if (newChar == null)
			{
				return null;
			}

			newChar.setCreateTime(1337); // Thats out special number :)
			try
			{
				switch (ClassId.VALUES[classId].getLevel())
				{
				case 2:
					newChar.addExpAndSp(Experience.getExpForLevel(20), 0);
					break;
				case 3:
					newChar.addExpAndSp(Experience.getExpForLevel(40), 0);
					break;
				case 4:
					newChar.addExpAndSp(Experience.getExpForLevel(75), 0);
				}
			}
			catch (ArrayIndexOutOfBoundsException | NullPointerException e)
			{
				_log.warn("PhantomPlayers: Failed to set appropreate level for classId " + classId, e);
			}

			try (Connection con = DatabaseFactory.getInstance().getConnection())
			{
				Player.restoreCharSubClasses(newChar, con);
			}
			catch (SQLException e)
			{
				_log.error("Error while restoring Subclasses on phantom player ", e);
			}

			if (Config.STARTING_ADENA > 0)
			{
				newChar.addAdena(Config.STARTING_ADENA, "Phantom");
			}
			if (level > 0)
			{
				newChar.addExpAndSp(Experience.LEVEL[level] - newChar.getExp(), 0, 0, 0, false, false);
			}
			else
			{
				newChar.addExpAndSp(Experience.LEVEL[85] - newChar.getExp(), 0, 0, 0, false, false);
			}

			if (title.equals("-1"))
			{
				if (Config.ALLOW_PHANTOM_CUSTOM_TITLES && Rnd.chance(Config.PHANTOM_CHANCE_SET_NOBLE_TITLE) && newChar.getLevel() >= 76)
				{
					String titlealt = Rnd.get(_phantomTitles);

					newChar.setNoble(true, false);
					newChar.setTitle(titlealt);

					newChar.updatePledgeClass();
					newChar.updateNobleSkills();
					newChar.sendPacket(new SkillList(newChar));
					newChar.broadcastUserInfo(true);
				}
				else if (Config.CHAR_TITLE)
				{
					newChar.setTitle(Config.ADD_CHAR_TITLE);
				}
				else
				{
					newChar.setTitle("");
				}
			}
			else
			{
				newChar.setTitle(title);
			}

			if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && Config.SERVICES_RATE_CREATE_PA != 0 && newChar.getBonus() == null)
			{
				newChar.getBonus().setBonusExpire((int) (System.currentTimeMillis() / 1000L * (60 * 60 * 24 * Config.SERVICES_RATE_CREATE_PA)));
				newChar.stopBonusTask();
				newChar.startBonusTask();
			}

			for (CreateItem i : newChar.getTemplate().getItems())
			{
				ItemInstance item = ItemFunctions.createItem(i.getItemId());
				newChar.getInventory().addItem(item, "Phantom");

				if (i.isEquipable() && item.isEquipable() && (newChar.getActiveWeaponItem() == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON))
				{
					newChar.getInventory().equipItem(item);
				}
			}

			ClassId nclassId = ClassId.VALUES[classId];
			if (Config.ALLOW_START_ITEMS)
			{
				if (nclassId.isMage())
				{
					for (int i = 0; i < Config.START_ITEMS_MAGE.length; i++)
					{
						ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_MAGE[i]);
						item.setCount(Config.START_ITEMS_MAGE_COUNT[i]);
						newChar.getInventory().addItem(item, "Phantom");
					}
				}
				else
				{
					for (int i = 0; i < Config.START_ITEMS_FITHER.length; i++)
					{
						ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_FITHER[i]);
						item.setCount(Config.START_ITEMS_FITHER_COUNT[i]);
						newChar.getInventory().addItem(item, "Phantom");
					}
				}
			}

			for (SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(newChar, AcquireType.NORMAL))
			{
				newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);
			}

			newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
			newChar.setCurrentCp(0); // retail
			newChar.setOnlineStatus(false);

			newChar.store(false);
			newChar.getInventory().store();
			newChar.deleteMe();

			_phantoms.add(Integer.valueOf(newChar.getObjectId()));

			if (_phantomNames.contains(name))
			{
				_phantomNames.remove(name);
			}

			return newChar;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static PhantomSpawner spawnPhantoms(int numSpawns, int delayInMilis, boolean generateNew, int grade, Location loc)
	{
		PhantomSpawner spawner = new PhantomSpawner(numSpawns, delayInMilis, grade, generateNew).setLocation(loc);
		ThreadPoolManager.getInstance().execute(spawner);
		_phantomSpawners.add(spawner);
		return spawner;
	}

	public static PhantomSpawner spawnPhantoms(int numSpawns, int delayInMilis, boolean generateNew, Location loc)
	{
		PhantomSpawner spawner = new PhantomSpawner(numSpawns, delayInMilis, generateNew).setLocation(loc);
		ThreadPoolManager.getInstance().execute(spawner);
		_phantomSpawners.add(spawner);
		return spawner;
	}

	public static int getSpawnedPhantomsCount()
	{
		int count = 0;
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isPhantom())
			{
				count++;
			}
		}

		return count;
	}

	/**
	 * Gets the next unspawned phantom.
	 * @return random free (unspawned) phantom object id or -1 if all are taken.
	 */
	private static int getUnspawnedPhantomObjId()
	{
		List<Integer> _unspawnedPhantoms = new ArrayList<Integer>();
		_unspawnedPhantoms.addAll(_phantoms);
		_unspawnedPhantoms.addAll(_phantomsWithClan);

		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (_unspawnedPhantoms.contains(Integer.valueOf(player.getObjectId())))
			{
				_unspawnedPhantoms.remove(Integer.valueOf(player.getObjectId()));
			}
		}

		if (!_unspawnedPhantoms.isEmpty())
		{
			return Rnd.get(_unspawnedPhantoms);
		}

		return -1;
	}

	/**
	 * Gets the next unspawned phantom.
	 * @return random free (unspawned) phantom object id or -1 if all are taken.
	 */
	public static int getRandomPhantomWithClan()
	{
		List<Integer> _unspawnedPhantoms = new ArrayList<Integer>();
		_unspawnedPhantoms.addAll(_phantomsWithClan);

		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (_unspawnedPhantoms.contains(Integer.valueOf(player.getObjectId())))
			{
				_unspawnedPhantoms.remove(Integer.valueOf(player.getObjectId()));
			}
		}

		if (!_unspawnedPhantoms.isEmpty())
		{
			return Rnd.get(_unspawnedPhantoms);
		}

		return -1;
	}

	public static class PhantomSpawn implements Runnable
	{
		private final int _objId;
		private Location _loc;
		private int _grade = -1;
		private boolean _hasAI = false;
		private boolean _farming = false;
		private int _despawnTime;
		private boolean _disableRespawn = false;

		public PhantomSpawn()
		{
			_objId = getUnspawnedPhantomObjId();
			_hasAI = true;
			_farming = false;
		}

		public PhantomSpawn(int objId, boolean hasAI, int grade, boolean farming, int despawnTime, boolean disableRespawn)
		{
			_objId = objId;
			_loc = null;
			_hasAI = hasAI;
			_grade = grade;
			_farming = farming;
			_despawnTime = despawnTime;
			_disableRespawn = disableRespawn;
		}

		public PhantomSpawn setLocation(Location loc)
		{
			_loc = loc;
			return this;
		}

		@Override
		public void run()
		{
			Player player = World.getPlayer(_objId);
			if (player == null)
			{
				player = Player.restore(_objId);
			}
			if (player == null)
			{
				return;
			}

			// player.setClient(new GameClient(null));

			player.setOfflineMode(false);
			player.setIsOnline(true);
			player.updateOnlineStatus();

			player.setOnlineStatus(true);
			player.setInvisibleType(InvisibleType.NONE);
			player.setNonAggroTime(Long.MAX_VALUE);
			player.spawnMe();
			player.setReflection(0);
			// player.setLoc(_loc);
			// player.teleToLocation(_loc, 0);

			player.getListeners().onEnter();

			// Backup to set default name color every time on login.
			if (player.getNameColor() != 0xFFFFFF && (player.getKarma() == 0 || player.getRecomHave() == 0) && !player.isGM())
			{
				player.setNameColor(0xFFFFFF);
			}

			if (player.getTitleColor() != Player.DEFAULT_TITLE_COLOR && !player.isGM())
			{
				player.setTitleColor(Player.DEFAULT_TITLE_COLOR);
			}

			// Restore after nocarrier title, title color.
			if (player.getVar("NoCarrierTitle") != null)
			{

				player.setTitle(player.getVar("NoCarrierTitle"));

				if (player.getVar("NoCarrierTitleColor") != null)
				{
					player.setTitleColor(Integer.parseInt(player.getVar("NoCarrierTitleColor")));
				}

				player.broadcastCharInfo();

				player.unsetVar("NoCarrierTitle");
				player.unsetVar("NoCarrierTitleColor");
			}

			if (player.isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().showUsageTime(player, player.getCursedWeaponEquippedId());
			}

			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());

			player.setIsPhantom(true, _hasAI);

			if (player.getAI().isPhantomPlayerAI() && _hasAI)
			{
				if (_farming)
				{
					((PhantomPlayerAI) player.getAI()).startFarming();
				}
				else
				{
					((PhantomPlayerAI) player.getAI()).startRoamingInTown();
				}

				// ((PhantomPlayerAI) player.getAI()).startAITask();
				// ((PhantomPlayerAI) player.getAI()).activateShots();
			}

			if (_grade != 0)
			{
				PhantomPlayers.equipArmor(player, _grade);
				PhantomPlayers.equipJewels(player, _grade);
				PhantomPlayers.equipWeapon(player, _grade);
			}

			player.setRunning();
			player.standUp();
			player.startTimers();

			player.broadcastCharInfo();
			player.setHeading(Rnd.get(65535));
			if (player.isDead())
			{
				player.teleToLocation(Location.getRestartLocation(player, RestartType.TO_VILLAGE));
				player.doRevive(100);
			}
			// else
			// player.teleToLocation(_loc == null ? player.getLoc() : _loc);
			if (_loc != null)
			{
				player.setLoc(_loc);
			}

			player.sendChanges();
			ThreadPoolManager.getInstance().schedule(new PhantomDespawn(player.getObjectId(), _hasAI, _disableRespawn, _grade), (_despawnTime == 0 ? Config.PHANTOM_MAX_LIFETIME : _despawnTime) * 60000);
		}
	}

	/**
	 * Checks if the given player is in the world, then logs out when he is out of combat.
	 */
	private static class PhantomDespawn implements Runnable
	{
		private final int _objId;
		private final boolean _force;
		private final boolean _useBsoe;
		private final boolean _disableRespawn;
		private boolean _hasAI = true;
		private final int _grade;

		public PhantomDespawn(int objId, boolean hasAI, boolean disableRespawn, int grade)
		{
			_objId = objId;
			_force = false;
			_useBsoe = false;
			_disableRespawn = disableRespawn;
			_hasAI = hasAI;
			_grade = grade;

		}

		public PhantomDespawn(int objId, boolean force, boolean useBsoe, boolean disablerespwn)
		{
			_objId = objId;
			_force = force;
			_useBsoe = useBsoe;
			_disableRespawn = disablerespwn;
			_grade = -1;
		}

		@Override
		public void run()
		{
			Player phantom = GameObjectsStorage.getPlayer(_objId);
			if (phantom == null)
			{
				return;
			}

			if (!_force)
			{
				// Continue when phantom is out of combat.
				if (phantom.isInCombat())
				{
					ThreadPoolManager.getInstance().schedule(this, 1000);
				}

				// When phantom is out of combat, stop moving.
				if (phantom.isMoving())
				{
					phantom.stopMove();
				}
			}

			if (_useBsoe)
			{
				if (phantom.getAI().isPhantomPlayerAI())
				{
					((PhantomPlayerAI) phantom.getAI()).castSOE(1); // BSOE
				}
			}

			phantom.getAI().stopAITask();
			phantom.setOnlineStatus(false);
			phantom.store(false);
			phantom.kick();

			if (!Config.DISABLE_PHANTOM_RESPAWN && !_disableRespawn)
			{
				PhantomSpawner spawner = new PhantomSpawner(_hasAI, _grade);
				ThreadPoolManager.getInstance().execute(spawner);
				// _phantomSpawners.add(spawner);
			}
		}
	}

	public static class PhantomSpawner implements Runnable
	{
		private final int _numSpawns;
		private final int _delayInMilis;
		private final boolean _generateNewPhantoms;
		private int _curSpawns = 0;
		private Location _loc = null;
		private ScheduledFuture<?> _task = null;
		private boolean _hasAi = true;
		private int _grade = -1;

		public PhantomSpawner()
		{
			_numSpawns = Config.PHANTOM_SPAWN_MAX;
			_delayInMilis = Config.PHANTOM_SPAWN_DELAY;
			_generateNewPhantoms = true;
		}

		public PhantomSpawner(boolean hasAi, int grade)
		{
			_numSpawns = 1;
			_delayInMilis = Config.PHANTOM_SPAWN_DELAY;
			_generateNewPhantoms = true;
			_hasAi = hasAi;
			_grade = grade;
		}

		public PhantomSpawner(int numSpawns)
		{
			_numSpawns = numSpawns;
			_delayInMilis = Config.PHANTOM_SPAWN_DELAY;
			_generateNewPhantoms = true;
		}

		public PhantomSpawner(int numSpawns, int delayInMilis)
		{
			_numSpawns = numSpawns;
			_delayInMilis = delayInMilis;
			_generateNewPhantoms = true;
		}

		public PhantomSpawner(int numSpawns, int delayInMilis, boolean generateNewPhantoms)
		{
			_numSpawns = numSpawns;
			_delayInMilis = delayInMilis;
			_generateNewPhantoms = generateNewPhantoms;
		}

		public PhantomSpawner(int numSpawns, int delayInMilis, int grade, boolean generateNewPhantoms)
		{
			_numSpawns = numSpawns;
			_delayInMilis = delayInMilis;
			_generateNewPhantoms = generateNewPhantoms;
			_grade = grade;
		}

		public PhantomSpawner setLocation(Location loc)
		{
			_loc = loc;
			return this;
		}

		@Override
		public void run()
		{
			if (_numSpawns == 0)
			{
				return;
			}

			_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					if (_curSpawns >= _numSpawns)
					{
						cancel();
						return;
					}

					// Do not spawn more than max phantoms.
					if (GameObjectsStorage.getAllPlayersStream().filter(Player::isPhantom).count() >= Config.PHANTOM_MAX_PLAYERS)
					{
						_log.info("PhantomsEngine: Error! Reached maximum allowed spawns of: " + Config.PHANTOM_MAX_PLAYERS + " players.");
						cancel();
						return;
					}

					int objId = getUnspawnedPhantomObjId();
					if (objId < 0)
					{
						if (_generateNewPhantoms)
						{
							try
							{
								Player phantom = createNewPhantom();
								if (_loc != null)
								{
									phantom.setLoc(_loc);
									// phantom.teleToLocation(_loc, 0);
								}
								objId = phantom.getObjectId();
								_log.info("Spawning phantom " + phantom + " through spawner. Cur/Max " + _curSpawns + "/" + _numSpawns);
							}
							catch (Exception e)
							{
								_log.error("ERROR: Spawning phantom  through spawner. Cur/Max " + _curSpawns + "/" + _numSpawns, e);
							}
						}
						else
						{
							return;
						}
					}

					ThreadPoolManager.getInstance().execute(new PhantomSpawn(objId, _hasAi, _grade, false, 0, false).setLocation(_loc));
					_curSpawns++;
				}
			}, 0, _delayInMilis);
		}

		public void cancel()
		{
			if (_task != null)
			{
				_task.cancel(true);
			}
		}
	}

	public static void stopSpawners()
	{
		if (_phantomSpawners != null)
		{
			for (PhantomSpawner thread : _phantomSpawners)
			{
				if (thread != null)
				{
					thread.cancel();
				}
			}
		}
	}

	public static void terminatePhantoms(boolean force, boolean disablerespawn)
	{
		stopSpawners();

		for (int objId : _phantoms)
		{
			new PhantomDespawn(objId, force, false, disablerespawn).run();
		}
	}

	public static void terminatePhantom(int objId, boolean disableFromReenter, boolean disablerespawn)
	{
		if (disableFromReenter && _phantoms != null)
		{
			_phantoms.remove(Integer.valueOf(objId));
		}

		new PhantomDespawn(objId, true, false, disablerespawn).run();
	}

	/**
	 * Equips the appropreate armor for the level. If not found, it will create one.
	 * @param player
	 * @param grade
	 */
	public static void equipArmor(Player player, int grade)
	{
		int[] setsArr = getAppropriateArmorSetIds(player, grade);
		if (setsArr.length == 0)
		{
			return;
		}

		// TODO: Lame... will fix later
		List<Integer> sets = new ArrayList<Integer>();
		for (int set : setsArr)
		{
			if (!Util.contains(Config.PHANTOM_BANNED_SETID, set))
			{
				sets.add(Integer.valueOf(set));
			}
		}

		// First search if there is already a set that can be equipped.
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item.isEquipable())
			{
				ArmorSet set = ArmorSetsHolder.getInstance().getArmorSet(item.getItemId());

				// A set is found, but not appropreate.
				// Set isnt full... sucks. Do not equip
				if ((set == null) || !sets.contains(Integer.valueOf(set.getSetById())) || !set.containAll(player))
				{
					continue;
				}

				// Set found. Equip it and do not create a new set.
				set.equipSet(player);
				return;
			}
		}

		// No set found, then give one.
		int chosenSet = Rnd.get(sets);

		ArmorSet set = null;
		for (ArmorSet armorset : ArmorSetsHolder.getInstance().getAllSets())
		{
			if (armorset.getSetById() == chosenSet)
			{
				set = armorset;
				break;
			}
		}

		if (set != null)
		{
			set.createDummySet(player);
			set.equipSet(player);
		}
	}

	public static int[] getAppropriateArmorSetIds(Player player, int gradeGear)
	{
		byte mastery = 1; // Light
		if (player.getSkillLevel(163) > 0)
		{ // Spellcraft
			mastery = 0;
		}
		else if (player.getSkillLevel(231) > 0)
		{ // Warrior Heavy Armor Mastery
			mastery = 2;
		}
		else if (player.getSkillLevel(232) > 0)
		{ // Tank Heavy Armor Mastery
			mastery = 2;
		}
		else if (player.getSkillLevel(227) > 0) // Warrior Light Armor Mastery
		{
			mastery = 1;
			/*
			 * else if (player.getSkillLevel(233) > 0) // Archer Light Armor Mastery mastery = 1; else if (player.getSkillLevel(465) > 0) // Kamael Light Armor Mastery mastery = 1;
			 */
		}

		int grade = 0;

		if (gradeGear > 0)
		{
			grade = gradeGear;
		}
		else
		{
			grade = Rnd.get(Config.PHANTOM_MAX_ARMOR_GRADE);
		}

		switch (grade)
		{
		case 0: // No-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					2
				}; // Robe
			case 1:
				return new int[]
				{
					1
				}; // Light
			case 2:
				return new int[]
				{
					1
				}; // Heavy
			}
			break;
		case 1: // D-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					8,
					9
				}; // Robe
			case 1:
				return new int[]
				{
					6,
					7
				}; // Light
			case 2:
				return new int[]
				{
					4,
					5
				}; // Heavy
			}
			break;
		case 2: // C-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					32, /* 33, 34 */
				}; // Robe
			case 1:
				return new int[]
				{
					28,
					29,
					30,
					31
				}; // Light
			case 2:
				return new int[]
				{ /* 25, 26, */
					27
				}; // Heavy
			}
			break;
		case 3: // B-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{ /* 46, */
					49
					/* , 52, 55 */ }; // Robe
			case 1:
				return new int[]
				{
					45,
					48, /* 51, */
					54
				}; // Light
			case 2:
				return new int[]
				{
					44,
					47,
					50,
					53
				}; // Heavy
			}
			break;
		case 4: // A-grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					67
					/* , 70, 73, 76 */ }; // Robe
			case 1:
				return new int[]
				{
					66, /* 69, */
					72,
					75
				}; // Light
			case 2:
				return new int[]
				{
					65,
					68,
					71,
					74
				}; // Heavy
			}
			break;
		case 5: // S-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					97,
					98
				}; // Robe
			case 1:
				return new int[]
				{
					96,
					108
				}; // Light
			case 2:
				return new int[]
				{
					95,
					103
				}; // Heavy
			}
			break;
		case 6: // S80-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					131,
					136,
					137
				}; // Robe
			case 1:
				return new int[]
				{
					130,
					134,
					135
				}; // Light
			case 2:
				return new int[]
				{
					129,
					132,
					133
				}; // Heavy
			}
			break;
		case 7: // S84-Grade
			switch (mastery)
			{
			case 0:
				return new int[]
				{
					136,
					137
				}; // Robe
			case 1:
				return new int[]
				{
					134,
					135
				}; // Light
			case 2:
				return new int[]
				{
					132,
					133
				}; // Heavy
			}
			break;
		}

		return new int[] {};
	}

	public static void equipWeapon(Player player, int gradeGear)
	{
		WeaponType weaponType = getAppropreateWeaponType(player);

		// Find existing weapons to equip.
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item.isWeapon() && ((WeaponTemplate) item.getTemplate()).getItemType() == weaponType)
			{
				// Item grade is lesser than S, but still the best one there
				if (item.getTemplate().getItemGrade().ordinal() < 5 && item.getTemplate().getItemGrade().ordinal() == player.getGrade())
				{
					if (!item.isEquipped())
					{
						player.getInventory().equipItem(item);
					}
					return;
				}
				// S grade or above and compatitable - equip.
				else if (item.getTemplate().getItemGrade().externalOrdinal == 5 && item.getTemplate().getItemGrade().ordinal() <= player.getGrade())
				{
					if (!item.isEquipped())
					{
						player.getInventory().equipItem(item);
					}
					return;
				}
			}
		}

		// No weapons found? Why not get one?
		// Pff, I'm getting too lazy for doing this. But, come on, I'm not going to gather all the weapon IDs... this way is just fine :P
		MultiSellListContainer[] weaponsInShops = new MultiSellListContainer[]
		{
			MultiSellHolder.getInstance().getList(9009), // D Weapons
			MultiSellHolder.getInstance().getList(9007), // C Weapons
			MultiSellHolder.getInstance().getList(9005), // B Weapons
			MultiSellHolder.getInstance().getList(9002), // A Weapons
			MultiSellHolder.getInstance().getList(9026), // S Weapons
			MultiSellHolder.getInstance().getList(9038) // S84 Weapons
		};

		List<Integer> weaplist = new ArrayList<Integer>();

		for (MultiSellListContainer list : weaponsInShops)
		{
			if (list == null)
			{
				continue;
			}

			for (MultiSellEntry entry : list.getEntries())
			{
				if (entry == null)
				{
					continue;
				}

				for (MultiSellIngredient ingr : entry.getProduction())
				{
					ItemTemplate item = ItemHolder.getInstance().getTemplate(ingr.getItemId());
					// Dont pick forbidden weapons.
					if (item == null || !item.isWeapon() || (item.getItemGrade().ordinal() > Config.PHANTOM_MAX_WEAPON_GRADE))
					{
						continue;
					}

					if (gradeGear > 0)
					{
						if (item.getItemGrade().ordinal() != gradeGear)
						{
							continue;
						}
					}

					WeaponTemplate wpn = (WeaponTemplate) item;
					if (player.getClassId().isMage() && weaponType == WeaponType.ETC && (wpn.getItemType() == WeaponType.SWORD || wpn.getItemType() == WeaponType.BLUNT))
					{// Mages
						if (wpn.getCrystalType().externalOrdinal < 2 && gradeGear < 2) // No and D grade
						{
							weaplist.add(wpn.getItemId());
						}

						// C and above - Acumen, PLEASE!
						for (Skill skill : wpn.getAttachedSkills())
						{
							if (skill.getId() == 3047) // Special Ability: Acumen
							{
								weaplist.add(wpn.getItemId());
								break;
							}
						}
					}
					else if (weaponType != WeaponType.ETC && wpn.getItemType() == weaponType)
					{// Fighters
						if (wpn.getCrystalType().externalOrdinal < 2 && gradeGear < 2) // No and D grade
						{
							weaplist.add(wpn.getItemId());
						}

						// C and above - SA, PLEASE!
						for (Skill skill : wpn.getAttachedSkills())
						{
							if (skill.getId() == 3023 || skill.getId() == 3042 || skill.getId() == 3043 || skill.getId() == 3066 || skill.getId() == 3542 || skill.getId() == 3572) // Special
																																													// Ability:
																																													// Critical
																																													// Damage
							{
								weaplist.add(wpn.getItemId());
								break;
							}
							else if (skill.getId() == 3010 || skill.getId() == 3011 || skill.getId() == 3044 || skill.getId() == 3565 || skill.getId() == 3566 || skill.getId() == 3567) // Special
																																															// Ability:
																																															// Focus
							{
								weaplist.add(wpn.getItemId());
								break;
							}
							else if (skill.getId() == 3013 || skill.getId() == 3011) // Special Ability: Health
							{
								weaplist.add(wpn.getItemId());
								break;
							}
							else if (wpn.getItemType() == weaponType && weaponType == WeaponType.DUAL)
							{
								weaplist.add(wpn.getItemId());
								break;
							}
						}
					}
				}
			}
		}

		// Did we find any weapon from the shop?
		if (weaplist.isEmpty() || weaplist.size() == 0)
		{
			return;
		}

		List<Integer> newWeaps = new ArrayList<Integer>();
		if (gradeGear < 0)
		{
			for (int weapitem : weaplist)
			{
				ItemTemplate tmp = ItemHolder.getInstance().getTemplate(weapitem);

				ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
				if (chest != null && chest.getCrystalType().externalOrdinal == tmp.getCrystalType().externalOrdinal)
				{
					newWeaps.add(tmp.getItemId());
				}
				else if (chest != null && chest.getCrystalType().externalOrdinal == 5 && tmp.getCrystalType().externalOrdinal >= 5)
				{
					newWeaps.add(tmp.getItemId());
				}
			}

			if (newWeaps.isEmpty() || newWeaps.size() == 0)
			{
				return;
			}
		}

		// Finally, a weapon found, create and equip it.
		ItemInstance item = ItemFunctions.createItem(Rnd.get(gradeGear > 0 ? weaplist : newWeaps));
		player.getInventory().equipItem(item);

		if (Config.CHANCE_TO_ENCHANT_WEAP > 0)
		{
			if (Rnd.chance(Config.CHANCE_TO_ENCHANT_WEAP))
			{
				item.setEnchantLevel(Rnd.get(3, Config.MAX_ENCH_PHANTOM_WEAP));
			}
		}

		player.sendPacket(new InventoryUpdate().addModifiedItem(item));
		// if (player.getAI() != null && player.getAI().isPhantomPlayerAI())
		// ((PhantomPlayerAI) player.getAI()).activateShots();
	}

	public static WeaponType getAppropreateWeaponType(Player player)
	{
		ClassId clas = player.getClassId().getLevel() == 4 ? player.getClassId().getParent(player.getSex()) : player.getClassId();
		switch (clas)
		{
		case fighter:
		case warrior:
		case knight:
		case paladin:
		case darkAvenger:
		case elvenFighter:
		case elvenKnight:
		case templeKnight:
		case swordSinger:
		case darkFighter:
		case palusKnight:
		case shillienKnight:
		case maleSoldier:
		case femaleSoldier:
			return WeaponType.SWORD;
		case gladiator:
		case bladedancer:
			return WeaponType.DUAL;
		case warlord:
			return WeaponType.POLE;
		case rogue:
		case treasureHunter:
		case elvenScout:
		case plainsWalker:
		case assassin:
		case abyssWalker:
		case bountyHunter:
		case warsmith:
			return WeaponType.DAGGER;
		case hawkeye:
		case silverRanger:
		case phantomRanger:
			return WeaponType.BOW;
		case orcRaider:
		case destroyer:
			return WeaponType.BIGSWORD;
		case orcMonk:
		case tyrant:
		case orcFighter:
			return WeaponType.DUALFIST;
		case dwarvenFighter:
		case scavenger:
		case artisan:
			return WeaponType.BLUNT;
		case trooper:
		case berserker:
			return WeaponType.ANCIENTSWORD;
		case warder:
		case arbalester:
			return WeaponType.CROSSBOW;
		case maleSoulbreaker:
		case femaleSoulbreaker:
		case inspector:
			return WeaponType.RAPIER;
		default:
			return WeaponType.ETC; // Mages...
		}
	}

	public static void equipJewels(Player player, int gradeGear)
	{
		int[] jewels = null;

		int grade = 0;

		if (gradeGear > 0)
		{
			grade = gradeGear;
		}
		else
		{
			grade = Rnd.get(Config.PHANTOM_MAX_JEWEL_GRADE);
		}

		switch (grade)
		{
		case 0:
			jewels = new int[]
			{
				882,
				882,
				851,
				851,
				914
			}; // D-grade shareniq TOP
			break;
		case 1:
			jewels = new int[]
			{
				888,
				888,
				857,
				857,
				919
			}; // C-grade Blessed set TOP
			break;
		case 2:
			jewels = new int[]
			{
				895,
				895,
				864,
				864,
				926
			}; // B-grade Black ore set TOP
			break;
		case 3:
			jewels = new int[]
			{
				893,
				893,
				862,
				862,
				924
			}; // A-grade Majestic set TOP
			break;
		case 4:
			jewels = new int[]
			{
				889,
				889,
				858,
				858,
				920
			}; // S-grade Tateossian set TOP
			break;
		case 5:
			jewels = new int[]
			{
				9457,
				9457,
				9455,
				9455,
				9456
			}; // Dynasty set TOP
			break;
		case 6:
			jewels = new int[]
			{
				15723,
				15723,
				15724,
				15724,
				15725
			}; // S82 Moirai set TOP
			break;
		case 7:
			jewels = new int[]
			{
				14165,
				14165,
				14163,
				14163,
				14164
			}; // S84 Vesper set TOP
		}

		if (jewels == null)
		{
			return;
		}

		for (int jewel : jewels)
		{
			ItemInstance item = player.getInventory().getItemByItemId(jewel);
			if (item == null)
			{
				item = ItemFunctions.createItem(jewel); // No jewel found? Lets make one.
			}

			if (!item.isEquipped())
			{
				player.getInventory().equipItem(item);
			}
		}
	}
}
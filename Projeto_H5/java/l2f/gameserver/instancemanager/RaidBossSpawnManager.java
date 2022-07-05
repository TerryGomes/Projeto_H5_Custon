package l2f.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.instances.RaidBossInstance;
import l2f.gameserver.model.instances.ReflectionBossInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.RadarControl;
import l2f.gameserver.network.serverpackets.ShowMiniMap;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.tables.GmListTable;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class RaidBossSpawnManager
{
	private static final Logger _log = LoggerFactory.getLogger(RaidBossSpawnManager.class);

	private static RaidBossSpawnManager _instance;

	protected static Map<Integer, Spawner> _spawntable = new ConcurrentHashMap<Integer, Spawner>();
	protected static Map<Integer, StatsSet> _storedInfo;
	protected static Map<Integer, Map<Integer, Integer>> _points;

	public static enum Status
	{
		ALIVE, DEAD, UNDEFINED
	}

	private RaidBossSpawnManager()
	{
		_instance = this;
		if (!Config.DONTLOADSPAWN)
		{
			reloadBosses();
		}
	}

	public void reloadBosses()
	{
		loadStatus();
		restorePointsTable();
		calculateRanking();
	}

	public static RaidBossSpawnManager getInstance()
	{
		if (_instance == null)
		{
			new RaidBossSpawnManager();
		}
		return _instance;
	}

	private void loadStatus()
	{
		_storedInfo = new ConcurrentHashMap<Integer, StatsSet>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); ResultSet rset = con.createStatement().executeQuery("SELECT * FROM `raidboss_status`"))
		{
			while (rset.next())
			{
				int id = rset.getInt("id");
				StatsSet info = new StatsSet();
				info.set("current_hp", rset.getDouble("current_hp"));
				info.set("current_mp", rset.getDouble("current_mp"));
				info.set("respawn_delay", rset.getInt("respawn_delay"));
				info.set("date_of_death", rset.getInt("date_of_death"));
				info.set("last_killer", rset.getString("last_killer"));
				_storedInfo.put(id, info);
			}
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt load raidboss statuses", e);
		}
		_log.info("RaidBossSpawnManager: Loaded " + _storedInfo.size() + " Statuses");
	}

	public void setRaidBossDied(int id, String killer)
	{
		Spawner spawner = _spawntable.get(id);
		if (spawner == null)
		{
			return;
		}

		StatsSet info = _storedInfo.get(id);
		if (info == null)
		{
			_storedInfo.put(id, info = new StatsSet());
		}

		NpcInstance raidboss = spawner.getFirstSpawned();
		if (raidboss instanceof ReflectionBossInstance)
		{
			return;
		}

		info.set("current_hp", 0);
		info.set("current_mp", 0);
		info.set("respawn_delay", spawner.getRespawnTime());
		info.set("date_of_death", (long) (System.currentTimeMillis() / 1000L));
		info.set("last_killer", killer);

		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("REPLACE INTO `raidboss_status` (id, current_hp, current_mp, respawn_delay, date_of_death, last_killer) VALUES (?,?,?,?,?,?)"))
		{
			statement.setInt(1, id);
			statement.setDouble(2, info.getDouble("current_hp"));
			statement.setDouble(3, info.getDouble("current_mp"));
			statement.setInt(4, info.getInteger("respawn_delay"));
			statement.setInt(5, info.getInteger("date_of_death"));
			statement.setString(6, info.getString("last_killer"));
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt update raidboss_status 1 table", e);
		}
	}

	private void setRaidBossAlive(int id)
	{
		Spawner spawner = _spawntable.get(id);
		if (spawner == null)
		{
			return;
		}

		StatsSet info = _storedInfo.get(id);
		if (info == null)
		{
			_storedInfo.put(id, info = new StatsSet());
		}

		NpcInstance raidboss = spawner.getFirstSpawned();
		if (raidboss instanceof ReflectionBossInstance)
		{
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("REPLACE INTO `raidboss_status` (id, current_hp, current_mp, respawn_delay, date_of_death) VALUES (?,?,?,?,?)"))
		{
			statement.setInt(1, id);
			statement.setDouble(2, info.getDouble("current_hp"));
			statement.setDouble(3, info.getDouble("current_mp"));
			statement.setInt(4, info.getInteger("respawn_delay", 0));
			statement.setLong(5, info.getLong("date_of_death", 0L));
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt update raidboss_status 2 table", e);
		}
	}

	public void addNewSpawn(int npcId, Spawner spawnDat)
	{
		if (_spawntable.containsKey(npcId))
		{
			return;
		}

		_spawntable.put(npcId, spawnDat);

		StatsSet info = _storedInfo.get(npcId);
		if (info != null)
		{
			spawnDat.setRespawnTime(info.getInteger("respawn_delay", 0));
		}
	}

	public void onBossSpawned(RaidBossInstance raidboss)
	{
		int bossId = raidboss.getNpcId();
		if (!_spawntable.containsKey(bossId))
		{
			return;
		}

		StatsSet info = _storedInfo.get(bossId);
		if (info == null)
		{
			info = new StatsSet();

			info.set("current_hp", raidboss.getMaxHp());
			info.set("current_mp", raidboss.getMaxMp());
			info.set("respawn_delay", 0);
			info.set("date_of_death", 0);
			info.set("last_killer", "");
			_storedInfo.put(bossId, info);
		}
		else if (info.getInteger("current_hp", 0) <= 1)
		{
			info.set("current_hp", raidboss.getMaxHp());
			info.set("current_mp", raidboss.getMaxMp());
		}

		raidboss.setCurrentHp(info.getDouble("current_hp"), false);
		raidboss.setCurrentMp(info.getDouble("current_mp"));
		setRaidBossAlive(bossId);

		GmListTable.broadcastMessageToGMs("Spawning RaidBoss " + raidboss.getName());
	}

	public void onBossDespawned(RaidBossInstance raidboss, boolean isDead, String killer)
	{
		if (isDead)
		{
			setRaidBossDied(raidboss.getNpcId(), killer);
		}
		else
		{
			setRaidBossAlive(raidboss.getNpcId());
		}
	}

	public Status getRaidBossStatusId(int bossId)
	{
		Spawner spawner = _spawntable.get(bossId);
		if (spawner == null)
		{
			return Status.UNDEFINED;
		}

		NpcInstance npc = spawner.getFirstSpawned();
		return npc == null ? Status.DEAD : Status.ALIVE;
	}

	public boolean isDefined(int bossId)
	{
		return _spawntable.containsKey(bossId);
	}

	/**
	 * Using RadarControl to show location of bossId
	 * Works only for ids that exists in RaidBossSpawnManager
	 * @param player to get location
	 * @param bossId Id of the target
	 */
	public static void showBossLocation(Player player, int bossId)
	{
		switch (getInstance().getRaidBossStatusId(bossId))
		{
		case ALIVE:
		case DEAD:
			Spawner spawn = getInstance().getSpawnTable().get(bossId);

			Location loc = spawn.getCurrentSpawnRange().getRandomLoc(spawn.getReflection().getGeoIndex());

			/* player.sendPacket(new RadarControl(2, 2, loc), new RadarControl(0, 1, loc)); */
			final Player _player = player;
			final Location loc1 = loc;
			new java.util.Timer().schedule(new java.util.TimerTask()
			{
				@Override
				public void run()
				{
					_player.sendPacket(new RadarControl(2, 2, loc1));
					_player.sendPacket(new RadarControl(0, 1, loc1));
				}
			}, 500);

			player.sendPacket(new ShowMiniMap(player, 0));
			break;
		case UNDEFINED:
			player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2AdventurerInstance.BossNotInGame", player).addNumber(bossId));
			break;
		}
	}

	public Map<Integer, Spawner> getSpawnTable()
	{
		return _spawntable;
	}

	public Map<Integer, StatsSet> getAllBosses()
	{
		return _storedInfo;
	}

	// ----------- Points & Ranking -----------

	public static final Integer KEY_RANK = new Integer(-1);
	public static final Integer KEY_TOTAL_POINTS = new Integer(0);

	private Lock pointsLock = new ReentrantLock();

	private void restorePointsTable()
	{
		pointsLock.lock();
		_points = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();

		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT owner_id, boss_id, points FROM `raidboss_points` ORDER BY owner_id ASC");
					ResultSet rset = statement.executeQuery())
		{
			int currentOwner = 0;
			Map<Integer, Integer> score = null;
			while (rset.next())
			{
				if (currentOwner != rset.getInt("owner_id"))
				{
					currentOwner = rset.getInt("owner_id");
					score = new HashMap<Integer, Integer>();
					_points.put(currentOwner, score);
				}

				assert score != null;
				int bossId = rset.getInt("boss_id");
				NpcTemplate template = NpcHolder.getInstance().getTemplate(bossId);
				if (bossId != KEY_RANK && bossId != KEY_TOTAL_POINTS && template != null && template.rewardRp > 0)
				{
					score.put(bossId, rset.getInt("points"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt load raidboss points", e);
		}
		pointsLock.unlock();
	}

	public void updatePointsDb()
	{
		pointsLock.lock();
		if (!mysql.set("TRUNCATE `raidboss_points`"))
		{
			_log.warn("RaidBossSpawnManager: Couldnt empty raidboss_points table");
		}

		if (_points.isEmpty())
		{
			pointsLock.unlock();
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO `raidboss_points` (owner_id, boss_id, points) VALUES(?,?,?)"))
		{
			boolean addedBatch = false;
			for (Map.Entry<Integer, Map<Integer, Integer>> pointEntry : _points.entrySet())
			{
				Map<Integer, Integer> tmpPoint = pointEntry.getValue();
				if (tmpPoint == null || tmpPoint.isEmpty())
				{
					continue;
				}

				for (Map.Entry<Integer, Integer> pointListEntry : tmpPoint.entrySet())
				{
					if (KEY_RANK.equals(pointListEntry.getKey()) || KEY_TOTAL_POINTS.equals(pointListEntry.getKey()) || pointListEntry.getValue() == null || pointListEntry.getValue() == 0)
					{
						continue;
					}

					statement.setInt(1, pointEntry.getKey());
					statement.setInt(2, pointListEntry.getKey());
					statement.setInt(3, pointListEntry.getValue());
					statement.addBatch();

					addedBatch = true;
				}
			}

			if (addedBatch)
			{
				statement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt update raidboss_points table", e);
		}
		pointsLock.unlock();
	}

	public void addPoints(int ownerId, int bossId, int points)
	{
		if (points <= 0 || ownerId <= 0 || bossId <= 0)
		{
			return;
		}

		pointsLock.lock();
		// ???? ????? ?????? ? ??????? ????????
		Map<Integer, Integer> pointsTable = _points.get(ownerId);

		// ?? ?????? ?????????
		if (pointsTable == null)
		{
			pointsTable = new HashMap<Integer, Integer>();
			_points.put(ownerId, pointsTable);
		}

		// ??? ??????? ?????? ????????? ????? ??????
		if (pointsTable.isEmpty())
		{
			pointsTable.put(bossId, points);
		}
		else
		// ???? ?????? ???? ??????
		{
			Integer currentPoins = pointsTable.get(bossId);
			pointsTable.put(bossId, currentPoins == null ? points : currentPoins + points);
		}
		pointsLock.unlock();
	}

	public TreeMap<Integer, Integer> calculateRanking()
	{
		// ??????? PlayerId - Rank ??? ??????????? ???????????
		TreeMap<Integer, Integer> tmpRanking = new TreeMap<Integer, Integer>();

		pointsLock.lock();

		// ????? ???????????? ??????? ? ??????????? ? ??????? ? ?????????? ?? ???????
		for (Map.Entry<Integer, Map<Integer, Integer>> point : _points.entrySet())
		{
			// ???????? ??????? ??? <BossId - Points>
			Map<Integer, Integer> tmpPoint = point.getValue();

			// ???? ? ????? ??? ??? ?? ?????, ?? ?? ?????????????
			tmpPoint.remove(KEY_RANK);
			tmpPoint.remove(KEY_TOTAL_POINTS);
			int totalPoints = 0;

			// ???????? ??? ????? ??? ??????
			for (Entry<Integer, Integer> e : tmpPoint.entrySet())
			{
				totalPoints += e.getValue();
			}

			// ????? ??? ????? ?????????
			if (totalPoints != 0)
			{
				// ?????? ? ???? ?????
				tmpPoint.put(KEY_TOTAL_POINTS, totalPoints);
				// ? ??? ?????????? ???? ?????
				tmpRanking.put(totalPoints, point.getKey());
			}
		}

		// ?????????? ??????? ?????? ? ??????? ?? ? ????? ????????
		int ranking = 1;
		for (Entry<Integer, Integer> entry : tmpRanking.descendingMap().entrySet())
		{
			// ???? ??????????????? ?????? ?? ???????? ???????
			Map<Integer, Integer> tmpPoint = _points.get(entry.getValue());

			// ? ????????? ???? ????
			tmpPoint.put(KEY_RANK, ranking);
			ranking++;
		}

		pointsLock.unlock();

		return tmpRanking;
	}

	/*
	 * Rank 1 = 2,500 Clan Reputation Points
	 * Rank 2 = 1,800 Clan Reputation Points
	 * Rank 3 = 1,400 Clan Reputation Points
	 * Rank 4 = 1,200 Clan Reputation Points
	 * Rank 5 = 900 Clan Reputation Points
	 * Rank 6 = 700 Clan Reputation Points
	 * Rank 7 = 600 Clan Reputation Points
	 * Rank 8 = 400 Clan Reputation Points
	 * Rank 9 = 300 Clan Reputation Points
	 * Rank 10 = 200 Clan Reputation Points
	 * Rank 11~50 = 50 Clan Reputation Points
	 * Rank 51~100 = 25 Clan Reputation Points
	 */
	public void distributeRewards()
	{
		pointsLock.lock();
		TreeMap<Integer, Integer> ranking = calculateRanking();
		Iterator<Integer> e = ranking.descendingMap().values().iterator();
		int counter = 1;
		while (e.hasNext() && counter <= 100)
		{
			int reward = 0;
			int playerId = e.next();
			switch (counter)
			{
			case 1:
				reward = 2500;
				break;
			case 2:
				reward = 1800;
				break;
			case 3:
				reward = 1400;
				break;
			case 4:
				reward = 1200;
				break;
			case 5:
				reward = 900;
				break;
			case 6:
				reward = 700;
				break;
			case 7:
				reward = 600;
				break;
			case 8:
				reward = 400;
				break;
			case 9:
				reward = 300;
				break;
			case 10:
				reward = 200;
				break;
			default:
				if (counter <= 50)
				{
					reward = 50;
				}
				else if (counter <= 100)
				{
					reward = 25;
				}
				break;
			}
			Player player = GameObjectsStorage.getPlayer(playerId);
			Clan clan = null;
			if (player != null)
			{
				clan = player.getClan();
			}
			else
			{
				clan = ClanTable.getInstance().getClan(mysql.simple_get_int("clanid", "characters", "obj_Id=" + playerId));
			}
			if (clan != null)
			{
				clan.incReputation(reward, true, "RaidPoints");
			}
			counter++;
		}
		_points.clear();
		updatePointsDb();
		pointsLock.unlock();
	}

	public Map<Integer, Map<Integer, Integer>> getPoints()
	{
		return _points;
	}

	public Map<Integer, Integer> getPointsForOwnerId(int ownerId)
	{
		return _points.get(ownerId);
	}
}
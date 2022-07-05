package l2f.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.GameTimeController;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.data.xml.holder.SpawnHolder;
import l2f.gameserver.listener.game.OnDayNightChangeListener;
import l2f.gameserver.listener.game.OnSSPeriodListener;
import l2f.gameserver.model.HardSpawner;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.templates.spawn.PeriodOfDay;
import l2f.gameserver.templates.spawn.SpawnTemplate;
import l2f.gameserver.utils.Location;

public class SpawnManager
{
	private class Listeners implements OnDayNightChangeListener, OnSSPeriodListener
	{
		@Override
		public void onDay()
		{
			despawn(PeriodOfDay.NIGHT.name());
			spawn(PeriodOfDay.DAY.name());
		}

		@Override
		public void onNight()
		{
			despawn(PeriodOfDay.DAY.name());
			spawn(PeriodOfDay.NIGHT.name());
		}

		@Override
		public void onPeriodChange(int mode)
		{
			switch (mode)
			{
			case 0: // all spawns
				despawn(DAWN_GROUP);
				despawn(DUSK_GROUP);
				spawn(DAWN_GROUP);
				spawn(DUSK_GROUP);
				break;
			case 1: // dusk spawns
				despawn(DAWN_GROUP);
				despawn(DUSK_GROUP);
				spawn(DUSK_GROUP);
				spawn(DUSK_GROUP2);
				break;
			case 2: // dawn spawns
				despawn(DAWN_GROUP);
				despawn(DUSK_GROUP);
				spawn(DAWN_GROUP);
				spawn(DAWN_GROUP2);
				break;
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);

	private static SpawnManager _instance = new SpawnManager();

	private static final String DAWN_GROUP = "dawn_spawn";
	private static final String DUSK_GROUP = "dusk_spawn";
	private static final String DAWN_GROUP2 = "dawn_spawn2";
	private static final String DUSK_GROUP2 = "dusk_spawn2";

	private final Map<String, List<Spawner>> _spawns = new ConcurrentHashMap<String, List<Spawner>>();
	private final Listeners _listeners = new Listeners();

	private final Map<Integer, Integer> spawnCountByNpcId = new HashMap<>();
	private final Map<Integer, List<Location>> spawnLocationsByNpcId = new HashMap<>();

	public static SpawnManager getInstance()
	{
		return _instance;
	}

	private SpawnManager()
	{
		for (Map.Entry<String, List<SpawnTemplate>> entry : SpawnHolder.getInstance().getSpawns().entrySet())
		{
			fillSpawn(entry.getKey(), entry.getValue());
		}

		GameTimeController.getInstance().addListener(_listeners);
		SevenSigns.getInstance().addListener(_listeners);
	}

	public List<Spawner> fillSpawn(String group, List<SpawnTemplate> templateList)
	{
		if (Config.DONTLOADSPAWN)
		{
			return Collections.emptyList();
		}

		List<Spawner> spawnerList = _spawns.get(group);
		if (spawnerList == null)
		{
			_spawns.put(group, spawnerList = new ArrayList<Spawner>(templateList.size()));
		}

		for (SpawnTemplate template : templateList)
		{
			HardSpawner spawner = new HardSpawner(template);
			spawnerList.add(spawner);

			NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(spawner.getCurrentNpcId());

			int toAdd;
			if ((Config.RATE_MOB_SPAWN > 1) && (npcTemplate.getInstanceClass() == MonsterInstance.class) && (npcTemplate.level >= Config.RATE_MOB_SPAWN_MIN_LEVEL) && (npcTemplate.level <= Config.RATE_MOB_SPAWN_MAX_LEVEL))
			{
				toAdd = template.getCount() * Config.RATE_MOB_SPAWN;
				spawner.setAmount(toAdd);
			}
			else
			{
				toAdd = template.getCount();
				spawner.setAmount(toAdd);
			}

			if (Config.ALLOW_DROP_CALCULATOR)
			{
				int currentCount = spawnCountByNpcId.containsKey(npcTemplate.getNpcId()) ? spawnCountByNpcId.get(npcTemplate.getNpcId()).intValue() : 0;
				spawnCountByNpcId.put(npcTemplate.getNpcId(), currentCount + toAdd);
			}

			spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
			spawner.setReflection(ReflectionManager.DEFAULT);
			spawner.setRespawnTime(0);

			if (Config.ALLOW_DROP_CALCULATOR)
			{
				Location spawnLoc = spawner.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
				if (!spawnLocationsByNpcId.containsKey(npcTemplate.getNpcId()))
				{
					spawnLocationsByNpcId.put(npcTemplate.getNpcId(), new ArrayList<Location>());
				}
				spawnLocationsByNpcId.get(npcTemplate.getNpcId()).add(spawnLoc);
			}

			if (npcTemplate.isRaid && group.equals(PeriodOfDay.NONE.name()))
			{
				RaidBossSpawnManager.getInstance().addNewSpawn(npcTemplate.getNpcId(), spawner);
			}
		}

		return spawnerList;
	}

	public void spawnAll()
	{
		spawn(PeriodOfDay.NONE.name());
		if (Config.ALLOW_EVENT_GATEKEEPER)
		{
			spawn("event_gatekeeper");
		}
		if (!Config.ALLOW_CLASS_MASTERS_LIST.isEmpty())
		{
			spawn("class_master");
		}
		if (Config.SPAWN_NPC_BUFFER)
		{
			spawn("npc_buffer");
		}
		if (Config.SPAWN_scrubwoman)
		{
			spawn("scrubwoman");
		}
		if (Config.SPAWN_CITIES_TREE)
		{
			spawn("cities_tree");
		}
		if (Config.ALLOW_UPDATE_ANNOUNCER)
		{
			spawn("update_announcer");
		}
	}

	public void spawn(String group)
	{
		List<Spawner> spawnerList = _spawns.get(group);
		if (spawnerList == null)
		{
			return;
		}

		int npcSpawnCount = 0;

		for (Spawner spawner : spawnerList)
		{
			npcSpawnCount += spawner.init();

			if (((npcSpawnCount % 1000) == 0) && (npcSpawnCount != 0))
			{
				_log.info("SpawnManager: spawned " + npcSpawnCount + " npc for group: " + group);
			}
		}
		_log.info("SpawnManager: spawned " + npcSpawnCount + " npc; spawns: " + spawnerList.size() + "; group: " + group);
	}

	public void despawn(String group)
	{
		List<Spawner> spawnerList = _spawns.get(group);
		if (spawnerList == null)
		{
			return;
		}

		for (Spawner spawner : spawnerList)
		{
			spawner.deleteAll();
		}
	}

	public List<Spawner> getSpawners(String group)
	{
		List<Spawner> list = _spawns.get(group);
		return list == null ? Collections.<Spawner>emptyList() : list;
	}

	public int getSpawnedCountByNpc(int npcId)
	{
		if (!spawnCountByNpcId.containsKey(npcId))
		{
			return 0;
		}
		return spawnCountByNpcId.get(npcId).intValue();
	}

	public List<Location> getRandomSpawnsByNpc(int npcId)
	{
		return spawnLocationsByNpcId.get(npcId);
	}

	public void reloadAll()
	{
		for (List<Spawner> spawnerList : _spawns.values())
		{
			for (Spawner spawner : spawnerList)
			{
				spawner.deleteAll();
			}
		}

		RaidBossSpawnManager.getInstance().reloadBosses();

		spawnAll();

		// FIXME [VISTALL] come up with another way to
		int mode = 0;
		if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION)
		{
			mode = SevenSigns.getInstance().getCabalHighestScore();
		}

		_listeners.onPeriodChange(mode);

		if (GameTimeController.getInstance().isNowNight())
		{
			_listeners.onNight();
		}
		else
		{
			_listeners.onDay();
		}
	}

	public List<NpcInstance> getAllSpawned(String group)
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();
		for (Spawner spawner : getSpawners(group))
		{
			result.addAll(spawner.getAllSpawned());
		}
		return result;
	}
}
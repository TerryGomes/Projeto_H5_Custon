package l2mv.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.text.StrTable;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance;

//TODO [G1ta0] submit the bredyatinu to hell
public class GameObjectsStorage
{
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(GameObjectsStorage.class);

	private static final int STORAGE_PLAYERS = 0x00;
	private static final int STORAGE_SUMMONS = 0x01;
	private static final int STORAGE_NPCS = 0x02;
	/** ......................................... */
	private static final int STORAGE_OTHER = 0x1E;
	private static final int STORAGE_NONE = 0x1F;

	@SuppressWarnings("rawtypes")
	private static final GameObjectArray[] storages = new GameObjectArray[STORAGE_NONE];

	static
	{
		storages[STORAGE_PLAYERS] = new GameObjectArray<Player>("PLAYERS", Config.MAXIMUM_ONLINE_USERS, 1);
		storages[STORAGE_SUMMONS] = new GameObjectArray<Playable>("SUMMONS", Config.MAXIMUM_ONLINE_USERS, 1);
		storages[STORAGE_NPCS] = new GameObjectArray<NpcInstance>("NPCS", 60000 * Config.RATE_MOB_SPAWN, 5000);
		storages[STORAGE_OTHER] = new GameObjectArray<GameObject>("OTHER", 2000, 1000);
	}

	@SuppressWarnings("unchecked")
	private static GameObjectArray<Player> getStoragePlayers()
	{
		return storages[STORAGE_PLAYERS];
	}

	@SuppressWarnings(
	{
		"unchecked",
		"unused"
	})
	private static GameObjectArray<Playable> getStorageSummons()
	{
		return storages[STORAGE_SUMMONS];
	}

	@SuppressWarnings("unchecked")
	private static GameObjectArray<NpcInstance> getStorageNpcs()
	{
		return storages[STORAGE_NPCS];
	}

	private static int selectStorageID(GameObject o)
	{
		if (o.isNpc())
		{
			return STORAGE_NPCS;
		}

		if (o.isPlayable())
		{
			return o.isPlayer() ? STORAGE_PLAYERS : STORAGE_SUMMONS;
		}

		return STORAGE_OTHER;
	}

	public static GameObject get(long storedId)
	{
		int STORAGE_ID;
		if (storedId == 0 || (STORAGE_ID = getStorageID(storedId)) == STORAGE_NONE)
		{
			return null;
		}
		GameObject result = storages[STORAGE_ID].get(getStoredIndex(storedId));
		return result != null && result.getObjectId() == getStoredObjectId(storedId) ? result : null;
	}

	public static GameObject get(Long storedId)
	{
		int STORAGE_ID;
		if (storedId == null || storedId == 0 || (STORAGE_ID = getStorageID(storedId)) == STORAGE_NONE)
		{
			return null;
		}
		GameObject result = storages[STORAGE_ID].get(getStoredIndex(storedId));
		return result != null && result.getObjectId() == getStoredObjectId(storedId) ? result : null;
	}

	public static boolean isStored(long storedId)
	{
		int STORAGE_ID;
		if (storedId == 0 || (STORAGE_ID = getStorageID(storedId)) == STORAGE_NONE)
		{
			return false;
		}
		GameObject o = storages[STORAGE_ID].get(getStoredIndex(storedId));
		return o != null && o.getObjectId() == getStoredObjectId(storedId);
	}

	public static NpcInstance getAsNpc(long storedId)
	{
		return (NpcInstance) get(storedId);
	}

	public static NpcInstance getAsNpc(Long storedId)
	{
		return (NpcInstance) get(storedId);
	}

	public static Player getAsPlayer(long storedId)
	{
		return (Player) get(storedId);
	}

	public static Playable getAsPlayable(long storedId)
	{
		return (Playable) get(storedId);
	}

	public static Creature getAsCharacter(long storedId)
	{
		return (Creature) get(storedId);
	}

	public static MonsterInstance getAsMonster(long storedId)
	{
		return (MonsterInstance) get(storedId);
	}

	public static PetInstance getAsPet(long storedId)
	{
		return (PetInstance) get(storedId);
	}

	public static ItemInstance getAsItem(long storedId)
	{
		return (ItemInstance) get(storedId);
	}

	public static boolean contains(long storedId)
	{
		return get(storedId) != null;
	}

	public static Player getPlayer(String name)
	{
		return getStoragePlayers().findByName(name);
	}

	public static Player getPlayer(int objId)
	{
		return getStoragePlayers().findByObjectId(objId);
	}

	/**
		* A copy of the list of players from the store suitable for manipulation over her
		* To iterate mostly better use getAllPlayersForIterate()
	 * @return
	 */
	public static List<Player> getAllPlayers()
	{
		return getStoragePlayers().getAll();
	}

	/**
	 * Only be used for enumeration types for(L2Player player : getAllPlayersForIterate()) ...
	 * @return
	 */
	public static Iterable<Player> getAllPlayersForIterate()
	{
		return getStoragePlayers();
	}

	public static Stream<Player> getAllPlayersStream()
	{
		return getStoragePlayers().stream();
	}

	public static List<Player> getAllGMs()
	{
		final List<Player> gms = new ArrayList<>();
		for (Player player : getStoragePlayers())
		{
			if (player.isGM())
			{
				gms.add(player);
			}
		}
		return gms;
	}

	/**
	 * Returns online from off traders
	 * @return
	 */
	public static int getAllPlayersCount()
	{
		return getStoragePlayers().getRealSize() + Config.ONLINE_PLUS;
	}

	public static int getAllTradablePlayersCount()
	{
		int count = 0;
		for (Player player : getStoragePlayers())
		{
			if (player.isInOfflineMode())
			{
				count++;
			}
		}
		return count;
	}

	public static int getAllStorePlayersCount()
	{
		int count = 0;
		for (Player player : getStoragePlayers())
		{
			if (player.isInStoreMode())
			{
				count++;
			}
		}
		return count;
	}

	public static int getAllObjectsCount()
	{
		int result = 0;
		for (GameObjectArray<?> storage : storages)
		{
			if (storage != null)
			{
				result += storage.getRealSize();
			}
		}
		return result;
	}

	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	public static List<GameObject> getAllObjects()
	{
		List<GameObject> result = new ArrayList<GameObject>(getAllObjectsCount());
		for (GameObjectArray storage : storages)
		{
			if (storage != null)
			{
				storage.getAll(result);
			}
		}
		return result;
	}

	public static GameObject findObject(int objId)
	{
		GameObject result = null;
		for (GameObjectArray<?> storage : storages)
		{
			if (storage != null)
			{
				if ((result = storage.findByObjectId(objId)) != null)
				{
					return result;
				}
			}
		}
		return null;
	}

	private static long offline_refresh = 0;
	private static int offline_count = 0;

	public static int getAllOfflineCount()
	{
		if (!Config.SERVICES_OFFLINE_TRADE_ALLOW)
		{
			return 0;
		}

		long now = System.currentTimeMillis();
		if (now > offline_refresh)
		{
			offline_refresh = now + 10000;
			offline_count = 0;
			for (Player player : getStoragePlayers())
			{
				if (player.isInOfflineMode())
				{
					offline_count++;
				}
			}
		}

		return offline_count;
	}

	public static List<NpcInstance> getAllNpcs()
	{
		return getStorageNpcs().getAll();
	}

	/**
	 * использовать только для перебора типа for(L2Player player : getAllPlayersForIterate()) ...
	 * @return
	 */
	public static Iterable<NpcInstance> getAllNpcsForIterate()
	{
		return getStorageNpcs();
	}

	public static List<Player> getAllPlayersCopy()
	{
		return getStoragePlayers().getAll();
	}

	public static NpcInstance getByNpcId(int npc_id)
	{
		NpcInstance result = null;
		for (NpcInstance temp : getStorageNpcs())
		{
			if (npc_id == temp.getNpcId())
			{
				if (!temp.isDead())
				{
					return temp;
				}
				result = temp;
			}
		}
		return result;
	}

	public static List<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive)
	{
		return getAllByNpcId(npc_id, justAlive, false);
	}

	public static List<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive, boolean visible)
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();
		for (NpcInstance temp : getStorageNpcs())
		{
			if (temp.getTemplate() != null && npc_id == temp.getTemplate().getNpcId() && (!justAlive || !temp.isDead()) && (!visible || temp.isVisible()))
			{
				result.add(temp);
			}
		}
		return result;
	}

	public static List<NpcInstance> getAllByNpcId(int[] npc_ids, boolean justAlive)
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();
		for (NpcInstance temp : getStorageNpcs())
		{
			if (!justAlive || !temp.isDead())
			{
				for (int npc_id : npc_ids)
				{
					if (npc_id == temp.getNpcId())
					{
						result.add(temp);
					}
				}
			}
		}
		return result;
	}

	public static NpcInstance getNpc(String s)
	{
		List<NpcInstance> npcs = getStorageNpcs().findAllByName(s);
		if (npcs.size() == 0)
		{
			return null;
		}
		for (NpcInstance temp : npcs)
		{
			if (!temp.isDead())
			{
				return temp;
			}
		}
		if (npcs.size() > 0)
		{
			return npcs.remove(npcs.size() - 1);
		}

		return null;
	}

	public static NpcInstance getNpc(int objId)
	{
		return getStorageNpcs().findByObjectId(objId);
	}

	/**
	 * кладет объект в хранилище и возвращает уникальный индентификатор по которому его можно будет найти в хранилище
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static long put(GameObject o)
	{
		int STORAGE_ID = selectStorageID(o);
		return o.getObjectId() & 0xFFFFFFFFL | (STORAGE_ID & 0x1FL) << 32 | (storages[STORAGE_ID].add(o) & 0xFFFFFFFFL) << 37;
	}

	public static long putDummy(GameObject o)
	{
		return objIdNoStore(o.getObjectId());
	}

	/**
	 * генерирует уникальный индентификатор по которому будет ясно что объект вне хранилища но можно будет получить objectId
	 * @param objId
	 * @return
	 */
	public static long objIdNoStore(int objId)
	{
		return objId & 0xFFFFFFFFL | (STORAGE_NONE & 0x1FL) << 32;
	}

	/**
	 * пересчитывает StoredId, необходимо при изменении ObjectId
	 * @param o
	 * @return
	 */
	public static long refreshId(Creature o)
	{
		return o.getObjectId() & 0xFFFFFFFFL | o.getStoredId() >> 32 << 32;
	}

	public static GameObject remove(long storedId)
	{
		int STORAGE_ID = getStorageID(storedId);
		return STORAGE_ID == STORAGE_NONE ? null : storages[STORAGE_ID].remove(getStoredIndex(storedId), getStoredObjectId(storedId));
	}

	private static int getStorageID(long storedId)
	{
		return (int) (storedId >> 32) & 0x1F;
	}

	private static int getStoredIndex(long storedId)
	{
		return (int) (storedId >> 37);
	}

	public static int getStoredObjectId(long storedId)
	{
		return (int) storedId;
	}

	public static StrTable getStats()
	{
		StrTable table = new StrTable("L2 Objects Storage Stats");

		GameObjectArray<?> storage;
		for (int i = 0; i < storages.length; i++)
		{
			if ((storage = storages[i]) == null)
			{
				continue;
			}

			synchronized (storage)
			{
				table.set(i, "Name", storage.name);
				table.set(i, "Size / Real", storage.size() + " / " + storage.getRealSize());
				table.set(i, "Capacity / init", storage.capacity() + " / " + storage.initCapacity);
			}
		}

		return table;
	}
}
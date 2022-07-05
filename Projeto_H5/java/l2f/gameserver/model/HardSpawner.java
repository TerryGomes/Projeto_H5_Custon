package l2f.gameserver.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.spawn.SpawnNpcInfo;
import l2f.gameserver.templates.spawn.SpawnRange;
import l2f.gameserver.templates.spawn.SpawnTemplate;

public class HardSpawner extends Spawner
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3566741484655685267L;
	private final SpawnTemplate _template;
	private int _pointIndex;
	private int _npcIndex;

	private List<NpcInstance> _reSpawned = new CopyOnWriteArrayList<NpcInstance>();

	public HardSpawner(SpawnTemplate template)
	{
		_template = template;
		_spawned = new CopyOnWriteArrayList<NpcInstance>();
	}

	@Override
	public void decreaseCount(NpcInstance oldNpc)
	{
		oldNpc.setSpawn(null); // [VISTALL] нужно убирать спавн что бы не вызвать зацикливания, и остановки спавна
		oldNpc.deleteMe();

		_spawned.remove(oldNpc);

		SpawnNpcInfo npcInfo = getNextNpcInfo();

		NpcInstance npc = npcInfo.getTemplate().getNewInstance();
		npc.setSpawn(this);

		_reSpawned.add(npc);

		decreaseCount0(npcInfo.getTemplate(), npc, oldNpc.getDeadTime());
	}

	@Override
	public NpcInstance doSpawn(boolean spawn)
	{
		SpawnNpcInfo npcInfo = getNextNpcInfo();

		return doSpawn0(npcInfo.getTemplate(), spawn, npcInfo.getParameters());
	}

	@Override
	protected NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set)
	{
		_reSpawned.remove(mob);

		SpawnRange range = _template.getSpawnRange(getNextRangeId());
		mob.setSpawnRange(range);
		return initNpc0(mob, range.getRandomLoc(getReflection().getGeoIndex()), spawn, set);
	}

	@Override
	public int getCurrentNpcId()
	{
		SpawnNpcInfo npcInfo = _template.getNpcId(_npcIndex);
		return npcInfo.getTemplate().npcId;
	}

	@Override
	public SpawnRange getCurrentSpawnRange()
	{
		return _template.getSpawnRange(_pointIndex);
	}

	@Override
	public void respawnNpc(NpcInstance oldNpc)
	{
		initNpc(oldNpc, true, StatsSet.EMPTY);
	}

	@Override
	public void deleteAll()
	{
		super.deleteAll();

		for (NpcInstance npc : _reSpawned)
		{
			npc.setSpawn(null);
			npc.deleteMe();
		}

		_reSpawned.clear();
	}

	private synchronized SpawnNpcInfo getNextNpcInfo()
	{
		int old = _npcIndex++;
		if (_npcIndex >= _template.getNpcSize())
		{
			_npcIndex = 0;
		}

		SpawnNpcInfo npcInfo = _template.getNpcId(old);
		if (npcInfo.getMax() > 0)
		{
			int count = 0;
			for (NpcInstance npc : _spawned)
			{
				if (npc.getNpcId() == npcInfo.getTemplate().getNpcId())
				{
					count++;
				}
			}

			if (count >= npcInfo.getMax())
			{
				return getNextNpcInfo();
			}
		}
		return npcInfo;
	}

	private synchronized int getNextRangeId()
	{
		int old = _pointIndex++;
		if (_pointIndex >= _template.getSpawnRangeSize())
		{
			_pointIndex = 0;
		}
		return old;
	}

	@Override
	public HardSpawner clone()
	{
		HardSpawner spawnDat = new HardSpawner(_template);
		spawnDat.setAmount(_maximumCount);
		spawnDat.setRespawnDelay(_respawnDelay, _respawnDelayRandom);
		spawnDat.setRespawnTime(0);
		return spawnDat;
	}
}

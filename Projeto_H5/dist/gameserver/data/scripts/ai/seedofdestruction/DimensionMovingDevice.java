package ai.seedofdestruction;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;

/**
 * AI Dimension Moving Device в Seed of Destruction:
 * Trap spawn cast with a delay of 3 seconds through 5 seconds after spawn in the following sequence:
 * Dragon Steed Troop Commander
 * White Dragon Leader
 * Dragon Steed Troop Healer (not off-like)
 * Dragon Steed Troop Magic Leader
 * Dragon Steed Troop Javelin Thrower
 *
 * @author claww
 */
public class DimensionMovingDevice extends DefaultAI
{
	private static final int MOBS_WAVE_DELAY = 120 * 1000; // 2 мин между волнами мобов
	private static final int MOBS_WAVE_DELAY2 = 20 * 1000;
	private long spawnTime = 0;
	private long spawnTime2 = 0;
	private int count = 0;

	private static final int[] MOBS =
	{
		22538, // Dragon Steed Troop Commander
		22540, // White Dragon Leader
		22547, // Dragon Steed Troop Healer
		22542, // Dragon Steed Troop Magic Leader
		22548 // Dragon Steed Troop Javelin Thrower
	};

	private List<NpcInstance> _npcs = new ArrayList<NpcInstance>();

	public DimensionMovingDevice(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		spawnTime = 0;
		_npcs.clear();
		super.onEvtDead(killer);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (spawnTime + MOBS_WAVE_DELAY < System.currentTimeMillis())
		{
			if (_npcs.size() < 100)
			{
				for (int id : MOBS)
				{
					NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(id, actor.getLoc(), 0);
					_npcs.add(mob);
				}
			}

			spawnTime = System.currentTimeMillis();
			return true;
		}

		if (spawnTime + MOBS_WAVE_DELAY < System.currentTimeMillis())
		{
			if (_npcs.size() < 100)
			{
				if (spawnTime2 + MOBS_WAVE_DELAY2 < System.currentTimeMillis())
				{
					count++;
					int id = 0;
					switch (count)
					{
					case 1:
						id = MOBS[0];
						break;
					case 2:
						id = MOBS[1];
						break;
					case 3:
						id = MOBS[2];
						break;
					case 4:
						id = MOBS[3];
						break;
					case 5:
						id = MOBS[4];
						break;
					}

					NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(id, actor.getLoc(), 0);
					_npcs.add(mob);

					spawnTime2 = System.currentTimeMillis();
				}
			}

			if (count >= 5)
			{
				count = 0;
				spawnTime = System.currentTimeMillis();
			}
			return true;
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{

	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{

	}
}
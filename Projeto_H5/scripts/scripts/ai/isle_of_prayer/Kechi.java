package ai.isle_of_prayer;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.hash.TIntObjectHashMap;
import instances.CrystalCaverns;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class Kechi extends DefaultAI
{
	final Skill KechiDoubleCutter; // Attack by crossing the sword. Power 2957.
	final Skill KechiAirBlade; // Strikes the enemy a blow in a distance using sword energy. Critical enabled. Power 1812

	final Skill Invincible; // Invincible against general attack and skill, buff/de-buff.
	final Skill NPCparty60ClanHeal; // TODO

	private static final int GUARD1 = 22309;
	private static final int GUARD2 = 22310;
	private static final int GUARD3 = 22417;

	private static final Location guard_spawn_loc = new Location(153384, 149528, -12136);

	private static final int[][] guard_run = new int[][]
	{
		{
			GUARD1,
			153384,
			149528,
			-12136
		},
		{
			GUARD1,
			153975,
			149823,
			-12152
		},
		{
			GUARD1,
			154364,
			149665,
			-12151
		},
		{
			GUARD1,
			153786,
			149367,
			-12151
		},
		{
			GUARD2,
			154188,
			149825,
			-12152
		},
		{
			GUARD2,
			153945,
			149224,
			-12151
		},
		{
			GUARD3,
			154374,
			149399,
			-12152
		},
		{
			GUARD3,
			153796,
			149646,
			-12159
		}
	};

	private int stage = 0;

	public Kechi(NpcInstance actor)
	{
		super(actor);

		TIntObjectHashMap<Skill> skills = getActor().getTemplate().getSkills();

		KechiDoubleCutter = skills.get(733);
		KechiAirBlade = skills.get(734);

		Invincible = skills.get(5418);
		NPCparty60ClanHeal = skills.get(5439);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		CrystalCaverns refl = null;
		if (actor.getReflection() instanceof CrystalCaverns)
		{
			refl = (CrystalCaverns) actor.getReflection();
		}
		if (refl != null)
		{
			refl.notifyKechiAttacked();
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if ((target = prepareTarget()) == null)
		{
			return false;
		}

		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return false;
		}

		double actor_hp_precent = actor.getCurrentHpPercents();

		switch (stage)
		{
		case 0:
			if (actor_hp_precent < 80)
			{
				spawnMobs();
				return true;
			}
			break;
		case 1:
			if (actor_hp_precent < 60)
			{
				spawnMobs();
				return true;
			}
			break;
		case 2:
			if (actor_hp_precent < 40)
			{
				spawnMobs();
				return true;
			}
			break;
		case 3:
			if (actor_hp_precent < 30)
			{
				spawnMobs();
				return true;
			}
			break;
		case 4:
			if (actor_hp_precent < 20)
			{
				spawnMobs();
				return true;
			}
			break;
		case 5:
			if (actor_hp_precent < 10)
			{
				spawnMobs();
				return true;
			}
			break;
		case 6:
			if (actor_hp_precent < 5)
			{
				spawnMobs();
				return true;
			}
			break;
		}

		if (Rnd.chance(5))
		{
			addTaskBuff(actor, Invincible);
			return true;
		}

		double distance = actor.getDistance(target);

		if (!actor.isAMuted() && Rnd.chance(75))
		{
			return chooseTaskAndTargets(null, target, distance);
		}

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, KechiDoubleCutter);
		addDesiredSkill(d_skill, target, distance, KechiAirBlade);

		Skill r_skill = selectTopSkill(d_skill);

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private void spawnMobs()
	{
		stage++;

		NpcInstance actor = getActor();
		for (int[] run : guard_run)
		{
			NpcInstance guard = actor.getReflection().addSpawnWithoutRespawn(run[0], guard_spawn_loc, 0);
			Location runLoc = new Location(run[1], run[2], run[3]);

			guard.setRunning();
			DefaultAI ai = (DefaultAI) guard.getAI();

			ai.addTaskMove(runLoc, true);
			// Выбираем случайную цель
			Creature hated = actor.getAggroList().getRandomHated();
			if (hated != null)
			{
				ai.notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, 5000);
			}
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		CrystalCaverns refl = null;
		if (actor.getReflection() instanceof CrystalCaverns)
		{
			refl = (CrystalCaverns) actor.getReflection();
		}
		if (refl != null)
		{
			refl.notifyKechiDead(getActor());
		}
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
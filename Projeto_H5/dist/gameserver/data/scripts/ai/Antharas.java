package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bosses.AntharasManager;
import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Location;

public class Antharas extends DefaultAI
{
	// debuffs
	final Skill s_fear = getSkill(4108, 1), s_fear2 = getSkill(5092, 1), s_curse = getSkill(4109, 1), s_paralyze = getSkill(4111, 1);

	// damage skills
	final Skill s_shock = getSkill(4106, 1), s_shock2 = getSkill(4107, 1), s_antharas_ordinary_attack = getSkill(4112, 1), s_antharas_ordinary_attack2 = getSkill(4113, 1), s_meteor = getSkill(5093, 1), s_breath = getSkill(4110, 1);

	// regen skills
	final Skill s_regen1 = getSkill(4239, 1), s_regen2 = getSkill(4240, 1), s_regen3 = getSkill(4241, 1);

	private static final int FWA_INTERVAL_MINION_NORMAL = 5 * 60000;

	// Vars
	private int _hpStage = 0;
	private static long _minionsSpawnDelay = 0;
	private List<NpcInstance> minions = new ArrayList<NpcInstance>();

	// Вестника Невитта и длительность его спавна
	private static int INVOKER_NEVIT_HERALD = 4326;
	private static final int DESPAWN_TIME = 180 * 60 * 1000; // 3 часа = 180 минут
	private int DAMAGE_COUNTER = 0;

	public Antharas(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (DAMAGE_COUNTER == 0)
		{
			actor.getAI().startAITask();
		}

		AntharasManager.setLastAttackTime();
		for (Playable p : AntharasManager.getZone().getInsidePlayables())
		{
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);
		}
		DAMAGE_COUNTER++;
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		setNextMinionSpawnDelay();
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

		double distance = actor.getDistance(target);

		// Buffs and stats
		double chp = actor.getCurrentHpPercents();
		if (_hpStage == 0)
		{
			actor.altOnMagicUseTimer(actor, s_regen1);
			_hpStage = 1;
		}
		else if (chp < 75 && _hpStage == 1)
		{
			actor.altOnMagicUseTimer(actor, s_regen2);
			_hpStage = 2;
		}
		else if (chp < 50 && _hpStage == 2)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 3;
		}
		else if (chp < 30 && _hpStage == 3)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 4;
		}

		// Minions spawn
		if (_minionsSpawnDelay < System.currentTimeMillis() && getAliveMinionsCount() < 30 && Rnd.chance(5))
		{
			NpcInstance minion = Functions.spawn(Location.findPointToStay(actor.getLoc(), 400, 700, actor.getGeoIndex()), Rnd.chance(50) ? 29190 : 29069); // Antharas Minions
			minions.add(minion);
			AntharasManager.addSpawnedMinion(minion);
			setNextMinionSpawnDelay();
		}

		// Basic Attack
		if (Rnd.chance(50))
		{
			return chooseTaskAndTargets(Rnd.chance(50) ? s_antharas_ordinary_attack : s_antharas_ordinary_attack2, target, distance);
		}

		// Stage based skill attacks
		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		switch (_hpStage)
		{
		case 1:
			addDesiredSkill(d_skill, target, distance, s_curse);
			addDesiredSkill(d_skill, target, distance, s_paralyze);
			addDesiredSkill(d_skill, target, distance, s_meteor);
			break;
		case 2:
			addDesiredSkill(d_skill, target, distance, s_curse);
			addDesiredSkill(d_skill, target, distance, s_paralyze);
			addDesiredSkill(d_skill, target, distance, s_meteor);
			addDesiredSkill(d_skill, target, distance, s_fear2);
			break;
		case 3:
			addDesiredSkill(d_skill, target, distance, s_curse);
			addDesiredSkill(d_skill, target, distance, s_paralyze);
			addDesiredSkill(d_skill, target, distance, s_meteor);
			addDesiredSkill(d_skill, target, distance, s_fear2);
			addDesiredSkill(d_skill, target, distance, s_shock2);
			addDesiredSkill(d_skill, target, distance, s_breath);
			break;
		case 4:
			addDesiredSkill(d_skill, target, distance, s_curse);
			addDesiredSkill(d_skill, target, distance, s_paralyze);
			addDesiredSkill(d_skill, target, distance, s_meteor);
			addDesiredSkill(d_skill, target, distance, s_fear2);
			addDesiredSkill(d_skill, target, distance, s_shock2);
			addDesiredSkill(d_skill, target, distance, s_fear);
			addDesiredSkill(d_skill, target, distance, s_shock);
			addDesiredSkill(d_skill, target, distance, s_breath);
			break;
		default:
			break;
		}

		Skill r_skill = selectTopSkill(d_skill);
		if (r_skill != null && !r_skill.isOffensive())
		{
			target = actor;
		}

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	/**
	 * Устанавливает задержку на спаун миньена в зависимости от типа Антараса
	 */
	private void setNextMinionSpawnDelay()
	{
		_minionsSpawnDelay = System.currentTimeMillis() + FWA_INTERVAL_MINION_NORMAL;
	}

	private int getAliveMinionsCount()
	{
		int i = 0;
		for (NpcInstance n : minions)
		{
			if (n != null && !n.isDead())
			{
				i++;
			}
		}
		return i;
	}

	private Skill getSkill(int id, int level)
	{
		return SkillTable.getInstance().getInfo(id, level);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (minions != null && !minions.isEmpty())
		{
			for (NpcInstance n : minions)
			{
				n.deleteMe();
			}
		}
		// Спавним Вестника Невитта
		try
		{
			NpcInstance HeralGiran = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(82152, 148488, -3492, 60699));

			NpcInstance HeralAden = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(147048, 25608, -2038, 16383));

			NpcInstance HeralGoddart = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(147384, -55400, -2759, 57343));

			NpcInstance HeralRune = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(42904, -47912, -822, 49151));

			NpcInstance HeralDion = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(15736, 142744, -2731, 16383));

			NpcInstance HeralOren = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(82120, 53224, -1521, 16383));

			NpcInstance HeralGludio = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(-14168, 121192, -3014, 16383));

			NpcInstance HeralGludin = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(80920, 149464, -3069, 16383));

			NpcInstance HeralSchuttgart = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(87608, -141320, -1364, 49151));

			NpcInstance HeralHein = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(110552, 219848, -3696, 57343));

			NpcInstance HeralHunter = NpcHolder.getInstance().getTemplate(INVOKER_NEVIT_HERALD).getNewInstance();
			HeralGiran.spawnMe(new Location(116824, 77400, -2722, 40959));

			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(HeralGiran, HeralAden, HeralGoddart, HeralRune, HeralDion, HeralOren, HeralGludio, HeralGludin, HeralSchuttgart, HeralHein, HeralHunter), DESPAWN_TIME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onEvtDead(killer);
	}

	// По истечению времени удаляем Вестника Невитта
	class DeSpawnScheduleTimerTask extends RunnableImpl
	{
		final NpcInstance HeralGiran;
		final NpcInstance HeralAden;
		final NpcInstance HeralGoddart;
		final NpcInstance HeralRune;
		final NpcInstance HeralDion;
		final NpcInstance HeralOren;
		final NpcInstance HeralGludio;
		final NpcInstance HeralGludin;
		final NpcInstance HeralSchuttgart;
		final NpcInstance HeralHein;
		final NpcInstance HeralHunter;

		public DeSpawnScheduleTimerTask(NpcInstance HeralGiran, NpcInstance HeralAden, NpcInstance HeralGoddart, NpcInstance HeralRune, NpcInstance HeralDion, NpcInstance HeralOren, NpcInstance HeralGludio, NpcInstance HeralGludin, NpcInstance HeralSchuttgart, NpcInstance HeralHein, NpcInstance HeralHunter)
		{
			this.HeralGiran = HeralGiran;
			this.HeralAden = HeralAden;
			this.HeralGoddart = HeralGoddart;
			this.HeralRune = HeralRune;
			this.HeralDion = HeralDion;
			this.HeralOren = HeralOren;
			this.HeralGludio = HeralGludio;
			this.HeralGludin = HeralGludin;
			this.HeralSchuttgart = HeralSchuttgart;
			this.HeralHein = HeralHein;
			this.HeralHunter = HeralHunter;
		}

		@Override
		public void runImpl()
		{
			HeralGiran.deleteMe();
			HeralAden.deleteMe();
			HeralGoddart.deleteMe();
			HeralRune.deleteMe();
			HeralDion.deleteMe();
			HeralOren.deleteMe();
			HeralGludio.deleteMe();
			HeralGludin.deleteMe();
			HeralSchuttgart.deleteMe();
			HeralHein.deleteMe();
			HeralHunter.deleteMe();
		}
	}
}
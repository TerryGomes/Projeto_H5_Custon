package ai;

import java.util.HashMap;
import java.util.Map;

import bosses.BaiumManager;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;

public class Baium extends DefaultAI
{
	private boolean _firstTimeAttacked = true;

	// Боевые скилы байума
	private final Skill baium_normal_attack, energy_wave, earth_quake, thunderbolt, group_hold;

	public Baium(NpcInstance actor)
	{
		super(actor);
		TIntObjectHashMap<Skill> skills = getActor().getTemplate().getSkills();
		baium_normal_attack = skills.get(4127);
		energy_wave = skills.get(4128);
		earth_quake = skills.get(4129);
		thunderbolt = skills.get(4130);
		group_hold = skills.get(4131);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		BaiumManager.setLastAttackTime();

		if (_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			NpcInstance actor = getActor();
			if (attacker == null)
			{
				return;
			}
			if (attacker.isPlayer() && attacker.getPet() != null)
			{
				attacker.getPet().doDie(actor);
			}
			else if ((attacker.isSummon() || attacker.isPet()) && attacker.getPlayer() != null)
			{
				attacker.getPlayer().doDie(actor);
			}
			attacker.doDie(actor);
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean createNewTask()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (!BaiumManager.getZone().checkIfInZone(actor))
		{
			teleportHome();
			return false;
		}

		clearTasks();

		Creature target;
		if ((target = prepareTarget()) == null)
		{
			return false;
		}

		if (!BaiumManager.getZone().checkIfInZone(target))
		{
			actor.getAggroList().remove(target, false);
			return false;
		}

		// Шансы использования скилов
		int s_energy_wave = 20;
		int s_earth_quake = 20;
		int s_group_hold = actor.getCurrentHpPercents() > 50 ? 0 : 20;
		int s_thunderbolt = actor.getCurrentHpPercents() > 25 ? 0 : 20;

		Skill r_skill = null;

		if (actor.isMovementDisabled())
		{ // Если в руте, то использовать массовый скилл дальнего боя
			r_skill = thunderbolt;
		}
		else if (!Rnd.chance(100 - s_thunderbolt - s_group_hold - s_energy_wave - s_earth_quake)) // Выбираем скилл атаки
		{
			Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>(); // TODO class field ?
			double distance = actor.getDistance(target);

			addDesiredSkill(d_skill, target, distance, energy_wave);
			addDesiredSkill(d_skill, target, distance, earth_quake);
			if (s_group_hold > 0)
			{
				addDesiredSkill(d_skill, target, distance, group_hold);
			}
			if (s_thunderbolt > 0)
			{
				addDesiredSkill(d_skill, target, distance, thunderbolt);
			}
			r_skill = selectTopSkill(d_skill);
		}

		// Использовать скилл если можно, иначе атаковать скилом baium_normal_attack
		if (r_skill == null)
		{
			r_skill = baium_normal_attack;
		}
		else if (r_skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF)
		{
			target = actor;
		}

		// Добавить новое задание
		addTaskCast(target, r_skill);
		r_skill = null;
		return true;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		NpcInstance actor = getActor();
		if (actor != null && !BaiumManager.getZone().checkIfInZone(actor))
		{
			teleportHome();
		}
		return false;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}
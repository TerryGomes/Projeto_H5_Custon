package ai;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.instancemanager.HellboundManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;

public class Baylor extends DefaultAI
{
	final Skill Berserk; // Increases P. Atk. and P. Def.
	final Skill Invincible; // Неуязвимость при 30% hp
	final Skill Imprison; // Помещает одиночную цель в тюрьму, рейндж 600
	final Skill GroundStrike; // Массовая атака, 2500 каст
	final Skill JumpAttack; // Массовая атака, 2500 каст
	final Skill StrongPunch; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
	final Skill Stun1; // Массовое оглушение, 5000 каст
	final Skill Stun2; // Массовое оглушение, 3000 каст
	final Skill Stun3; // Массовое оглушение, 2000 каст
	// final L2Skill Stun4; // Не работает?

	final int PresentationBalor2 = 5402; // Прыжок, удар по земле
	final int PresentationBalor3 = 5403; // Электрическая аура
	final int PresentationBalor4 = 5404; // Электрическая аура, в конце сияние

	final int PresentationBalor10 = 5410; // Не работает?
	final int PresentationBalor11 = 5411; // Не работает?
	final int PresentationBalor12 = 5412; // Массовый удар

	private static final int Water_Dragon_Claw = 2360;

	private boolean _isUsedInvincible = false;

	private int _claw_count = 0;
	private long _last_claw_time = 0;

	private class SpawnSocial extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor != null)
			{
				actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, PresentationBalor2, 1, 4000, 0));
			}
		}
	}

	public Baylor(NpcInstance actor)
	{
		super(actor);

		TIntObjectHashMap<Skill> skills = getActor().getTemplate().getSkills();

		Berserk = skills.get(5224);
		Invincible = skills.get(5225);
		Imprison = skills.get(5226);
		GroundStrike = skills.get(5227);
		JumpAttack = skills.get(5228);
		StrongPunch = skills.get(5229);
		Stun1 = skills.get(5230);
		Stun2 = skills.get(5231);
		Stun3 = skills.get(5232);
		// Stun4 = skills.get(5401);
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new SpawnSocial(), 20000);
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();
		if (actor.isDead() || skill == null || caster == null)
		{
			return;
		}

		if (System.currentTimeMillis() - _last_claw_time > 5000)
		{
			_claw_count = 0;
		}

		if (skill.getId() == Water_Dragon_Claw)
		{
			_claw_count++;
			_last_claw_time = System.currentTimeMillis();
		}

		Player player = caster.getPlayer();
		if (player == null)
		{
			return;
		}

		int count = 1;
		Party party = player.getParty();
		if (party != null)
		{
			count = party.size();
		}

		// Снимаем неуязвимость
		if (_claw_count >= count)
		{
			_claw_count = 0;
			actor.getEffectList().stopEffect(Invincible);
			Functions.npcSay(actor, NpcString.NO_ONE_IS_GOING_TO_SURVIVE);
		}
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
		double actor_hp_precent = actor.getCurrentHpPercents();

		if (actor_hp_precent < 30 && !_isUsedInvincible)
		{
			_isUsedInvincible = true;
			addTaskBuff(actor, Invincible);
			Functions.npcSay(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH);
			return true;
		}

		int rnd_per = Rnd.get(100);
		if (rnd_per < 7 && actor.getEffectList().getEffectsBySkill(Berserk) == null)
		{
			addTaskBuff(actor, Berserk);
			Functions.npcSay(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH);
			return true;
		}

		if (rnd_per < 15 || rnd_per < 33 && actor.getEffectList().getEffectsBySkill(Berserk) != null)
		{
			return chooseTaskAndTargets(StrongPunch, target, distance);
		}

		// if(rnd_per < 5 && target.getEffectList().getEffectsBySkill(Imprison) == null)
		// {
		// _isUsedInvincible = true;
		// addTaskCast(target, Imprison);
		// return true;
		// }

		if (!actor.isAMuted() && rnd_per < 50)
		{
			return chooseTaskAndTargets(null, target, distance);
		}

		Map<Skill, Integer> skills = new HashMap<Skill, Integer>();

		addDesiredSkill(skills, target, distance, GroundStrike);
		addDesiredSkill(skills, target, distance, JumpAttack);
		addDesiredSkill(skills, target, distance, StrongPunch);
		addDesiredSkill(skills, target, distance, Stun1);
		addDesiredSkill(skills, target, distance, Stun2);
		addDesiredSkill(skills, target, distance, Stun3);

		Skill skill = selectTopSkill(skills);
		if (skill != null && !skill.isOffensive())
		{
			target = actor;
		}

		return chooseTaskAndTargets(skill, target, distance);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	// Hellbound opening hook
	@Override
	protected void onEvtDead(Creature killer)
	{
		if (HellboundManager.getConfidence() < 1)
		{
			HellboundManager.setConfidence(1);
		}

		super.onEvtDead(killer);
	}
}
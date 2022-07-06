package ai;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;

/**
 * AI мобов Prison Guard на Isle of Prayer.<br>
 * - Не используют функцию Random Walk<br>
 * - Ругаются на атаковавших чаров без эффекта Event Timer<br>
 * - Ставят в петрификацию атаковавших чаров без эффекта Event Timer<br>
 * - Не могут быть убиты чарами без эффекта Event Timer<br>
 * - Не проявляют агресии к чарам без эффекта Event Timer<br>
 * ID: 18367, 18368
 *
 * @author SYS
 */
public class PrisonGuard extends Fighter
{
	private static final int RACE_STAMP = 10013;

	public PrisonGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		// 18367 не агрятся
		NpcInstance actor = getActor();
		if (actor.isDead() || actor.getNpcId() == 18367 || (target.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0))
		{
			return false;
		}

		return super.checkAggression(target, avoidAttack);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return;
		}
		if (attacker.isSummon() || attacker.isPet())
		{
			attacker = attacker.getPlayer();
		}
		if (attacker.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0)
		{
			if (actor.getNpcId() == 18367)
			{
				Functions.npcSay(actor, "It's not easy to obtain.");
			}
			else if (actor.getNpcId() == 18368)
			{
				Functions.npcSay(actor, "You're out of mind comming here...");
			}

			Skill petrification = SkillTable.getInstance().getInfo(4578, 1); // Petrification
			actor.doCast(petrification, attacker, true);
			if (attacker.getPet() != null)
			{
				actor.doCast(petrification, attacker.getPet(), true);
			}

			return;
		}

		// 18367 не отвечают на атаку, но зовут друзей
		if (actor.getNpcId() == 18367)
		{
			notifyFriends(attacker, damage);
			return;
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		if (actor.getNpcId() == 18367 && killer.getPlayer().getEffectList().getEffectsBySkillId(Skill.SKILL_EVENT_TIMER) != null)
		{
			Functions.addItem(killer.getPlayer(), RACE_STAMP, 1, "PrisonGuard");
		}

		super.onEvtDead(killer);
	}
}
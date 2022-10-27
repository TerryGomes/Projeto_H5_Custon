package ai;

import java.util.List;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.QuestEventType;
import l2mv.gameserver.model.quest.QuestState;

public class AttackMobNotPlayerFighter extends Fighter
{
	public AttackMobNotPlayerFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker == null)
		{
			return;
		}

		Player player = attacker.getPlayer();
		if (player != null)
		{
			List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
			if (quests != null)
			{
				for (QuestState qs : quests)
				{
					qs.getQuest().notifyAttack(actor, qs);
				}
			}
		}

		onEvtAggression(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		NpcInstance actor = getActor();
		if (attacker == null)
		{
			return;
		}

		if (!actor.isRunning())
		{
			startRunningTask(AI_TASK_ATTACK_DELAY);
		}

		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}
}
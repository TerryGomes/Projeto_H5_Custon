package ai.freya;

import java.util.List;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;

public class JiniaGuild extends Fighter
{
	public JiniaGuild(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return false;
		}

		List<NpcInstance> around = actor.getAroundNpc(4000, 300);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance npc : around)
			{
				if (npc.getNpcId() == 29179 || npc.getNpcId() == 29180)
				{
					actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 3000);
				}
			}
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (attacker == null || attacker.isPlayable())
		{
			return;
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		if (target.isPlayable())
		{
			return false;
		}

		return super.checkAggression(target, avoidAttack);
	}
}
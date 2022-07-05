package ai;

import java.util.List;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;

/**
 * AI Kanadis Guide и минионов для Rim Pailaka
 * @author pchayka
 */

public class KanadisGuide extends Fighter
{

	public KanadisGuide(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		List<NpcInstance> around = actor.getAroundNpc(5000, 300);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance npc : around)
			{
				if (npc.getNpcId() == 36562)
				{
					actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, 5000);
				}
			}
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker.getNpcId() == 36562)
		{
			actor.getAggroList().addDamageHate(attacker, 0, 1);
			startRunningTask(2000);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}
}
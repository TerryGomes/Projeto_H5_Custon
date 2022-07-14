package ai;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class Leogul extends Fighter
{
	public Leogul(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		if (super.checkAggression(target, avoidAttack))
		{
			Functions.npcSayCustomMessage(getActor(), "scripts.ai.Leogul");

			for (NpcInstance npc : getActor().getAroundNpc(800, 128))
			{
				if (npc.isMonster() && npc.getNpcId() >= 22660 && npc.getNpcId() <= 22677)
				{
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 5000);
				}
			}
			return true;
		}
		return false;
	}
}
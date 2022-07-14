package ai.hellbound;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class TownGuard extends Fighter
{
	public TownGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
		{
			Functions.npcSay(actor, "Invader!");
		}
		super.onIntentionAttack(target);
	}
}
package ai.hellbound;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

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
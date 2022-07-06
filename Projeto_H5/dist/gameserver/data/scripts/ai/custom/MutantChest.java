package ai.custom;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class MutantChest extends Fighter
{
	public MutantChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (Rnd.chance(30))
		{
			Functions.npcSay(actor, "Enemies! Enemies everywhere! Everything here, the enemies here!");
		}

		actor.deleteMe();
	}
}
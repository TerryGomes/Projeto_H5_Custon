package ai.hellbound;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.instances.NpcInstance;

public class OutpostGuards extends Fighter
{
	public OutpostGuards(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
}
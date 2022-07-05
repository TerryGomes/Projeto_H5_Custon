package ai.custom;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

public class GvGBoss extends Fighter
{
	boolean phrase1 = false;
	boolean phrase2 = false;
	boolean phrase3 = false;

	public GvGBoss(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if (actor.getCurrentHpPercents() < 50 && phrase1 == false)
		{
			phrase1 = true;
			Functions.npcSay(actor, "You can not steal the treasures Herald!");
		}
		else if (actor.getCurrentHpPercents() < 30 && phrase2 == false)
		{
			phrase2 = true;
			Functions.npcSay(actor, "I'll skull fractured!");
		}
		else if (actor.getCurrentHpPercents() < 5 && phrase3 == false)
		{
			phrase3 = true;
			Functions.npcSay(actor, "All of you will die in terrible agony! destroy!");
		}

		super.onEvtAttacked(attacker, damage);
	}
}
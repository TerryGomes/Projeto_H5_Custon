package ai.custom;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

public class SSQLilimServantMage extends Mystic
{
	private boolean _attacked = false;

	public SSQLilimServantMage(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		if (Rnd.chance(30) && !_attacked)
		{
			Functions.npcSay(getActor(), "Who dares enter this place?");
			_attacked = true;
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(30))
		{
			Functions.npcSay(getActor(), "Lord Shilen... some day... you will accomplish... this mission...");
		}
		super.onEvtDead(killer);
	}
}
package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Ranger;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

/**
 * AI для Delu Lizardman Special Agent ID: 21105
 *
 * @author Diamond
 */
public class DeluLizardmanSpecialAgent extends Ranger
{
	private boolean _firstTimeAttacked = true;

	public DeluLizardmanSpecialAgent(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_firstTimeAttacked = true;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if (Rnd.chance(25))
			{
				Functions.npcSay(actor, "How dare you interrupt our fight! Hey guys, help!");
			}
		}
		else if (Rnd.chance(10))
		{
			Functions.npcSay(actor, "Hey! Were having a duel here!");
		}
		super.onEvtAttacked(attacker, damage);
	}
}
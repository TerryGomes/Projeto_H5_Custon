package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;

public class FrightenedOrc extends Fighter
{
	private boolean _sayOnAttack;

	public FrightenedOrc(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_sayOnAttack = true;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker != null && Rnd.chance(10) && _sayOnAttack)
		{
			Functions.npcSay(actor, NpcString.DONT_KILL_ME_PLEASE);
			_sayOnAttack = false;
		}

		super.onEvtAttacked(attacker, damage);
	}

}
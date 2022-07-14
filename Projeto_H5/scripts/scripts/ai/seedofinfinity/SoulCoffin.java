package ai.seedofinfinity;

import instances.ErosionHallAttack;
import instances.ErosionHallDefence;
import instances.HeartInfinityAttack;
import instances.HeartInfinityDefence;
import instances.SufferingHallDefence;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;

public class SoulCoffin extends DefaultAI
{
	private long checkTimer = 0;

	public SoulCoffin(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Reflection r = actor.getReflection();
		if (!r.isDefault())
		{
			if (actor.getNpcId() == 18711)
			{
				if (r.getInstancedZoneId() == 119)
				{
					((ErosionHallAttack) r).notifyCoffinDeath();
				}
				else if (r.getInstancedZoneId() == 121)
				{
					((HeartInfinityAttack) r).notifyCoffinDeath();
				}
				else if (r.getInstancedZoneId() == 120)
				{
					((ErosionHallDefence) r).notifyCoffinDeath();
				}
				else if (r.getInstancedZoneId() == 122)
				{
					((HeartInfinityDefence) r).notifyCoffinDeath();
				}
			}
		}
		super.onEvtDead(killer);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.getNpcId() == 18706 && actor.getReflection().getInstancedZoneId() == 116 && checkTimer + 10000 < System.currentTimeMillis())
		{
			checkTimer = System.currentTimeMillis();
			((SufferingHallDefence) actor.getReflection()).notifyCoffinActivity();
		}
		return super.thinkActive();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}
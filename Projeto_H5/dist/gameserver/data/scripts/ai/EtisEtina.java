package ai;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.NpcUtils;

public class EtisEtina extends Fighter
{
	private boolean summonsReleased = false;
	private NpcInstance summon1;
	private NpcInstance summon2;

	public EtisEtina(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor.getCurrentHpPercents() < 70 && !summonsReleased)
		{
			summonsReleased = true;
			summon1 = NpcUtils.spawnSingle(18950, Location.findAroundPosition(actor, 150), actor.getReflection());
			summon2 = NpcUtils.spawnSingle(18951, Location.findAroundPosition(actor, 150), actor.getReflection());
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (summon1 != null && !summon1.isDead())
		{
			summon1.decayMe();
		}
		if (summon2 != null && !summon2.isDead())
		{
			summon2.decayMe();
		}
		super.onEvtDead(killer);
	}
}
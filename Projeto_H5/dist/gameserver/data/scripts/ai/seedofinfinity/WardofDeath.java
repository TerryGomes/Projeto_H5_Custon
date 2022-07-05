package ai.seedofinfinity;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

public class WardofDeath extends DefaultAI
{
	private static final int[] mobs =
	{
		22516,
		22520,
		22522,
		22524
	};

	public WardofDeath(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		NpcInstance actor = getActor();
		if (target.isInRange(actor, actor.getAggroRange()) && target.isPlayable() && !target.isDead() && !target.isInvisible())
		{
			if (actor.getNpcId() == 18667) // trap skill
			{
				if (!avoidAttack)
				{
					actor.doCast(SkillTable.getInstance().getInfo(Rnd.get(5423, 5424), 9), actor, false);
					actor.doDie(null);
				}
				return true;
			}
			else if (actor.getNpcId() == 18668) // trap spawn
			{
				if (!avoidAttack)
				{
					for (int i = 0; i < Rnd.get(1, 4); i++)
					{
						actor.getReflection().addSpawnWithoutRespawn(mobs[Rnd.get(mobs.length)], actor.getLoc(), 100);
					}
					actor.doDie(null);
				}
				return true;
			}
		}
		return false;
	}
}
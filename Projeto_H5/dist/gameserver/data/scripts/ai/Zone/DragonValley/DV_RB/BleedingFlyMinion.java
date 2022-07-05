package ai.Zone.DragonValley.DV_RB;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

/**
 * @author FandC
 */

public class BleedingFlyMinion extends Fighter
{

	private Skill self_destruction = SkillTable.getInstance().getInfo(6872, 1);

	private long last_cast_sd = 0;

	public BleedingFlyMinion(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (last_cast_sd < System.currentTimeMillis())
		{
			actor.doCast(self_destruction, attacker, true);
			last_cast_sd = System.currentTimeMillis() + Rnd.get(15, 30) * 1000;
		}
		super.onEvtAttacked(attacker, damage);
	}
}

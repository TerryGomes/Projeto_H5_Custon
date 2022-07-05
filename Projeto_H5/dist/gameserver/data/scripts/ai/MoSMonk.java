package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

/**
 * AI монахов в Monastery of Silence<br>
 * - агрятся на чаров с оружием в руках
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 */
public class MoSMonk extends Fighter
{
	public MoSMonk(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(20))
		{
			Functions.npcSayCustomMessage(getActor(), "scripts.ai.MoSMonk.onIntentionAttack");
		}
		super.onIntentionAttack(target);
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		if (target.getActiveWeaponInstance() == null)
		{
			return false;
		}
		return super.checkAggression(target, avoidAttack);
	}
}
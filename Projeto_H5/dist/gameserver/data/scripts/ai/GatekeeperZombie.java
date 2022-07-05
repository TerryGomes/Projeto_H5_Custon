package ai;

import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

/**
 * AI охраны входа в Pagan Temple.<br>
 * <li>кидаются на всех игроков, у которых в кармане нету предмета 8064 или 8067
 * <li>не умеют ходить
 *
 * @author SYS
 */
public class GatekeeperZombie extends Mystic
{
	public GatekeeperZombie(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		NpcInstance actor = getActor();
		if (actor.isDead() || getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
		{
			return false;
		}
		if (target.isAlikeDead() || !target.isPlayable() || !target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange()))
		{
			return false;
		}
		if (Functions.getItemCount((Playable) target, 8067) != 0 || Functions.getItemCount((Playable) target, 8064) != 0 || !GeoEngine.canSeeTarget(actor, target, false))
		{
			return false;
		}

		if (!avoidAttack && getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			actor.getAggroList().addDamageHate(target, 0, 1);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}

		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
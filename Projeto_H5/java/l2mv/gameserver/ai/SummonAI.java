package l2mv.gameserver.ai;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;

public class SummonAI extends PlayableAI
{
	public SummonAI(Summon actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		Summon actor = getActor();

		Player owner = actor.getPlayer();

		clearNextAction();
		if (actor.isDepressed())
		{
			setAttackTarget(actor.getPlayer());
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
			thinkAttack(true);
		}
		else if ((owner != null) && owner.isConnected() && (actor.getDistance(owner) > 4000) && !owner.isAlikeDead())
		{
			actor.teleportToOwner();
			return super.thinkActive();
		}
		else if ((owner == null) || owner.isAlikeDead() || (actor.getDistance(owner) > 4000) || !owner.isConnected())
		{
			super.onIntentionActive();
			return super.thinkActive();
		}
		else if (actor.isFollowMode())
		{
			changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), Config.FOLLOW_RANGE);
			thinkFollow();
		}

		return super.thinkActive();
	}

	@Override
	protected void thinkAttack(boolean checkRange)
	{
		Summon actor = getActor();

		if (actor.isDepressed())
		{
			setAttackTarget(actor.getPlayer());
		}

		super.thinkAttack(checkRange);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		Summon actor = getActor();
		if (attacker != null && actor.getPlayer().isDead() && !actor.isDepressed())
		{
			Attack(attacker, false, false);
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public Summon getActor()
	{
		return (Summon) super.getActor();
	}
}
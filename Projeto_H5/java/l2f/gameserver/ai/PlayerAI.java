package l2f.gameserver.ai;

import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import l2f.gameserver.Config;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Skill.SkillType;
import l2f.gameserver.model.items.attachment.FlagItemAttachment;
import l2f.gameserver.network.serverpackets.ActionFail;
import l2f.gameserver.network.serverpackets.ExRotation;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class PlayerAI extends PlayableAI
{
	public PlayerAI(Player actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionRest()
	{
		changeIntention(CtrlIntention.AI_INTENTION_REST, null, null);
		setAttackTarget(null);
		clientStopMoving();
	}

	@Override
	protected void onIntentionActive()
	{
		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
	}

	@Override
	public void onIntentionInteract(GameObject object)
	{
		Player actor = getActor();

		if (actor.getSittingTask())
		{
			setNextAction(nextAction.INTERACT, object, null, false, false);
			return;
		}
		if (actor.isSitting())
		{
			actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			clientActionFailed();
			return;
		}
		super.onIntentionInteract(object);
	}

	@Override
	public void onIntentionPickUp(GameObject object)
	{
		final Player actor = getActor();
		if (actor.getSittingTask())
		{
			setNextAction(nextAction.PICKUP, object, null, false, false);
			return;
		}
		if (actor.isSitting())
		{
			actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			clientActionFailed();
			return;
		}
		super.onIntentionPickUp(object);
	}

	@Override
	protected void thinkAttack(boolean checkRange)
	{
		final Player actor = getActor();
		if (actor.isInFlyingTransform())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}

		FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
		if (attachment != null && !attachment.canAttack(actor))
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		if (actor.isFrozen())
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
			return;
		}

		super.thinkAttack(checkRange);
	}

	@Override
	protected void thinkCast(boolean checkRange)
	{
		final Player actor = getActor();
		final FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
		if (attachment != null && !attachment.canCast(actor, _skill))
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		if (actor.isFrozen())
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
			return;
		}

		super.thinkCast(checkRange);
	}

	@Override
	protected void thinkCoupleAction(Player target, Integer socialId, boolean cancel)
	{
		final Player actor = getActor();
		if (target == null || !target.isOnline())
		{
			actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
			return;
		}

		if (cancel || !actor.isInRange(target, 50) || actor.isInRange(target, 20) || actor.getReflection() != target.getReflection() || !GeoEngine.canSeeTarget(actor, target, false))
		{
			target.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
			actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
			return;
		}
		if (_forceUse) // служит только для флага что б активировать у другого игрока социалку
		{
			target.getAI().setIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, actor, socialId);
		}
		//
		int heading = actor.calcHeading(target.getX(), target.getY());
		actor.setHeading(heading);
		actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
		//
		actor.broadcastPacket(new SocialAction(actor.getObjectId(), socialId));
	}

	@Override
	public void Attack(GameObject target, boolean forceUse, boolean dontMove)
	{
		final Player actor = getActor();
		if (actor.isInFlyingTransform() || (System.currentTimeMillis() - actor.getLastAttackPacket() < Config.ATTACK_PACKET_DELAY))
		{
			actor.sendActionFailed();
			return;
		}

		actor.setLastAttackPacket();

		if (actor.getSittingTask())
		{
			setNextAction(nextAction.ATTACK, target, null, forceUse, false);
			return;
		}
		if (actor.isSitting())
		{
			actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			clientActionFailed();
			return;
		}

		super.Attack(target, forceUse, dontMove);
	}

	@Override
	public boolean Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove)
	{
		Player actor = getActor();

		if (!skill.altUse() && !skill.isToggle() && !(skill.getSkillType() == SkillType.CRAFT && Config.ALLOW_TALK_WHILE_SITTING))
		{
			// Если в этот момент встаем, то использовать скилл когда встанем
			if (actor.getSittingTask())
			{
				setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
				clientActionFailed();
				return true;
			}
			else if (skill.getSkillType() == SkillType.SUMMON && actor.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
			{
				actor.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
				clientActionFailed();
				return false;
			}
			// если сидим - скиллы нельзя использовать
			else if (actor.isSitting())
			{
				if (skill.getSkillType() == SkillType.TRANSFORMATION)
				{
					actor.sendPacket(SystemMsg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
				}
				else
				{
					actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
				}

				clientActionFailed();
				return false;
			}
		}

		return super.Cast(skill, target, forceUse, dontMove);
	}

	@Override
	public Player getActor()
	{
		return (Player) super.getActor();
	}
}
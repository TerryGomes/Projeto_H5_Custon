package l2f.gameserver.ai;

import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_CAST;
import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_INTERACT;
import static l2f.gameserver.ai.CtrlIntention.AI_INTENTION_PICK_UP;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Skill.NextAction;
import l2f.gameserver.model.Skill.SkillType;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.MyTargetSelected;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Location;

public class PlayableAI extends CharacterAI
{
	private volatile int thinking = 0; // to prevent recursive thinking

	private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
	protected Object _intention_arg0 = null, _intention_arg1 = null;
	protected Skill _skill;

	private nextAction _nextAction;
	private Object _nextAction_arg0;
	private Object _nextAction_arg1;
	private boolean _nextAction_arg2;
	private boolean _nextAction_arg3;

	protected boolean _forceUse;
	private boolean _dontMove;

	private ScheduledFuture<?> _followTask;

	public PlayableAI(Playable actor)
	{
		super(actor);
	}

	public enum nextAction
	{
		ATTACK, CAST, MOVE, REST, PICKUP, EQIP, INTERACT, COUPLE_ACTION
	}

	@Override
	public void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		super.changeIntention(intention, arg0, arg1);
		_intention_arg0 = arg0;
		_intention_arg1 = arg1;
	}

	@Override
	public void setIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_intention_arg0 = null;
		_intention_arg1 = null;
		super.setIntention(intention, arg0, arg1);
	}

	@Override
	protected void onIntentionCast(Skill skill, Creature target)
	{
		_skill = skill;
		super.onIntentionCast(skill, target);
	}

	/**
	 * @param arg0 : Attack - target; Cast - Skill; Move - Loc; Interract/Pickup - object; CoupleAction - target;
	 * @param arg1 : Cast - target; Move - offsetInteger; Couple Action - socialId;
	 * @param arg2 : Attack/Cast/CoupleAction - Force Use; Move - Pathfinding;
	 */
	@Override
	public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean dontMove)
	{
		try
		{
			_lock.writeLock().lock();

			_nextAction = action;
			_nextAction_arg0 = arg0;
			_nextAction_arg1 = arg1;
			_nextAction_arg2 = arg2;
			_nextAction_arg3 = dontMove;
		}
		finally
		{
			_lock.writeLock().unlock();
		}
	}

	public boolean setNextIntention()
	{
		nextAction nextAction;
		Object nextAction_arg0;
		Object nextAction_arg1;
		boolean nextAction_arg2;
		boolean dontMove;

		try
		{
			_lock.readLock().lock();

			nextAction = _nextAction;
			nextAction_arg0 = _nextAction_arg0;
			nextAction_arg1 = _nextAction_arg1;
			nextAction_arg2 = _nextAction_arg2;
			dontMove = _nextAction_arg3;
		}
		finally
		{
			_lock.readLock().unlock();
		}

		Playable actor = getActor();
		if ((nextAction == null) || actor.isActionsDisabled())
		{
			return false;
		}

		Skill skill;
		Creature target;
		GameObject object;

		switch (nextAction)
		{
		case ATTACK:
			if (nextAction_arg0 == null)
			{
				return false;
			}
			target = (Creature) nextAction_arg0;
			_forceUse = nextAction_arg2;
			_dontMove = dontMove;
			clearNextAction();
			setIntention(AI_INTENTION_ATTACK, target);
			break;
		case CAST:
			if ((nextAction_arg0 == null) || (nextAction_arg1 == null))
			{
				return false;
			}
			skill = (Skill) nextAction_arg0;
			target = (Creature) nextAction_arg1;
			_forceUse = nextAction_arg2;
			_dontMove = dontMove;
			clearNextAction();
			if (!skill.checkCondition(actor, target, _forceUse, _dontMove, true))
			{
				if ((skill.getNextAction() == NextAction.ATTACK) && !actor.equals(target))
				{
					setNextAction(PlayableAI.nextAction.ATTACK, target, null, _forceUse, false);
					return setNextIntention();
				}
				return false;
			}
			setIntention(AI_INTENTION_CAST, skill, target);
			break;
		case MOVE:
			if ((nextAction_arg0 == null) || (nextAction_arg1 == null))
			{
				return false;
			}
			Location loc = (Location) nextAction_arg0;
			Integer offset = (Integer) nextAction_arg1;
			clearNextAction();
			actor.moveToLocation(loc, offset, nextAction_arg2);
			break;
		case REST:
			actor.sitDown(null);
			break;
		case INTERACT:
			if (nextAction_arg0 == null)
			{
				return false;
			}
			object = (GameObject) nextAction_arg0;
			clearNextAction();
			onIntentionInteract(object);
			break;
		case PICKUP:
			if (nextAction_arg0 == null)
			{
				return false;
			}
			object = (GameObject) nextAction_arg0;
			clearNextAction();
			onIntentionPickUp(object);
			break;
		case EQIP:
			if ((nextAction_arg0 == null) || (!actor.isPlayer()) || (!(nextAction_arg0 instanceof ItemInstance)))
			{
				return false;
			}
			ItemInstance item = (ItemInstance) nextAction_arg0;
			item.getTemplate().getHandler().useItem(actor, item, _nextAction_arg2);
			break;
		case COUPLE_ACTION:
			if ((nextAction_arg0 == null) || (nextAction_arg1 == null))
			{
				return false;
			}
			target = (Creature) nextAction_arg0;
			Integer socialId = (Integer) nextAction_arg1;
			_forceUse = nextAction_arg2;
			try
			{
				_lock.writeLock().lock();
				_nextAction = null;
			}
			finally
			{
				_lock.writeLock().unlock();
			}
			clearNextAction();
			onIntentionCoupleAction((Player) target, socialId);
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void clearNextAction()
	{
		try
		{
			_lock.writeLock().lock();

			_nextAction = null;
			_nextAction_arg0 = null;
			_nextAction_arg1 = null;
			_nextAction_arg2 = false;
			_nextAction_arg3 = false;
		}
		finally
		{
			_lock.writeLock().unlock();
		}
	}

//	public nextAction getNextAction()
//	{
//		return _nextAction;
//	}

	@Override
	protected void onEvtFinishCasting()
	{
		if (!setNextIntention())
		{
			setIntention(AI_INTENTION_ACTIVE);
		}
	}

	@Override
	protected void onEvtReadyToAct()
	{
		if (!setNextIntention())
		{
			onEvtThink();
		}
	}

	@Override
	protected void onEvtArrived()
	{
		if (!setNextIntention())
		{
			if ((getIntention() == AI_INTENTION_INTERACT) || (getIntention() == AI_INTENTION_PICK_UP))
			{
				onEvtThink();
			}
			else
			{
				changeIntention(AI_INTENTION_ACTIVE, null, null);
			}
		}
	}

	@Override
	protected void onEvtArrivedTarget()
	{
		switch (getIntention())
		{
		case AI_INTENTION_ATTACK:
			thinkAttack(false);
			break;
		case AI_INTENTION_CAST:
			thinkCast(false);
			break;
		case AI_INTENTION_FOLLOW:
			thinkFollow();
			break;
		default:
			onEvtThink();
			break;
		}
	}

	@Override
	protected void onEvtThink()
	{
		Playable actor = getActor();
		if (actor.isActionsDisabled())
		{
			return;
		}

		try
		{
			if (thinking++ > 1)
			{
				return;
			}

			switch (getIntention())
			{
			case AI_INTENTION_ACTIVE:
				thinkActive();
				break;
			case AI_INTENTION_ATTACK:
				thinkAttack(true);
				break;
			case AI_INTENTION_CAST:
				thinkCast(true);
				break;
			case AI_INTENTION_PICK_UP:
				thinkPickUp();
				break;
			case AI_INTENTION_INTERACT:
				thinkInteract();
				break;
			case AI_INTENTION_FOLLOW:
				thinkFollow();
				break;
			case AI_INTENTION_COUPLE_ACTION:
				thinkCoupleAction((Player) _intention_arg0, (Integer) _intention_arg1, false);
				break;
			}
		}
		catch (Exception e)
		{
			_log.error("Error while Thinking", e);
		}
		finally
		{
			thinking--;
		}
	}

	protected boolean thinkActive()
	{
		return false;
	}

	protected void thinkFollow()
	{
		Playable actor = getActor();

		try
		{
			Creature target = (Creature) _intention_arg0;
			Integer offset = (Integer) _intention_arg1;

			// Are too far away goal or target is not suitable for the following
			if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || offset == null)
			{
				clientActionFailed();
				return;
			}

			// Already follow this end
			if (actor.isFollow && (actor.getFollowTarget() == target))
			{
				clientActionFailed();
				return;
			}

			// Are close enough or can not get around - then flee?
			if (actor.isInRange(target, offset + 20) || actor.isMovementDisabled())
			{
				clientActionFailed();
			}
		}
		catch (Exception e)
		{
		}

		if (_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
	}

	protected class ThinkFollow extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			Playable actor = getActor();

			if (getIntention() != AI_INTENTION_FOLLOW)
			{
				// If the pet has stopped the persecution, change status, it was not necessary to click on the Follow button 2 times.
				if ((actor.isPet() || actor.isSummon()) && (getIntention() == AI_INTENTION_ACTIVE))
				{
					((Summon) actor).setFollowMode(false);
				}
				return;
			}

			Creature target = (Creature) _intention_arg0;
			int offset = _intention_arg1 instanceof Integer ? (Integer) _intention_arg1 : 0;

			if ((target == null) || target.isAlikeDead() || (actor.getDistance(target) > 4000))
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			Player player = actor.getPlayer();
			if ((player == null) || player.isLogoutStarted() || ((actor.isPet() || actor.isSummon()) && (player.getPet() != actor)))
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			if (!actor.isInRange(target, offset + 20) && (!actor.isFollow || (actor.getFollowTarget() != target)))
			{
				actor.followToCharacter(target, offset, false);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
		}
	}

	protected class ExecuteFollow extends RunnableImpl
	{
		private final Creature _target;
		private final int _range;

		public ExecuteFollow(Creature target, int range)
		{
			_target = target;
			_range = range;
		}

		@Override
		public void runImpl()
		{
			if (_target.isDoor())
			{
				_actor.moveToLocation(_target.getLoc(), 40, true);
			}
			else
			{
				_actor.followToCharacter(_target, _range, true);
			}
		}
	}

	@Override
	protected void onIntentionInteract(GameObject object)
	{
		Playable actor = getActor();

		if (actor.isActionsDisabled())
		{
			setNextAction(nextAction.INTERACT, object, null, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_INTERACT, object, null);
		onEvtThink();
	}

	@Override
	protected void onIntentionCoupleAction(Player player, Integer socialId)
	{
		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, player, socialId);
		onEvtThink();
	}

	protected void thinkInteract()
	{
		Playable actor = getActor();

		GameObject target = (GameObject) _intention_arg0;

		if (target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		int range = (int) (Math.max(30, actor.getMinDistance(target)) + 20);

		if (actor.isInRangeZ(target, range))
		{
			if (actor.isPlayer())
			{
				((Player) actor).doInteract(target);
			}
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
		{
			actor.moveToLocation(target.getLoc(), 40, true);
			setNextAction(nextAction.INTERACT, target, null, false, false);
		}
	}

	@Override
	protected void onIntentionPickUp(GameObject object)
	{
		Playable actor = getActor();

		if (actor.isActionsDisabled())
		{
			setNextAction(nextAction.PICKUP, object, null, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_PICK_UP, object, null);
		onEvtThink();
	}

	protected void thinkPickUp()
	{
		final Playable actor = getActor();

		final GameObject target = (GameObject) _intention_arg0;

		if (target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		if (actor.isInRange(target, 30) && (Math.abs(actor.getZ() - target.getZ()) < 50))
		{
			if (actor.isPlayer() || actor.isPet())
			{
				actor.doPickupItem(target);
			}
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
		{
			ThreadPoolManager.getInstance().execute(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					actor.moveToLocation(target.getLoc(), 10, true);
					setNextAction(nextAction.PICKUP, target, null, false, false);
				}
			});
		}
	}

	protected void thinkAttack(boolean checkRange)
	{
		Playable actor = getActor();

		Player player = actor.getPlayer();
		if (player == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		if (actor.isActionsDisabled() || actor.isAttackingDisabled())
		{
			actor.sendActionFailed();
			return;
		}

		boolean isPosessed = (actor instanceof Summon) && ((Summon) actor).isDepressed();

		Creature attack_target = getAttackTarget();
		if ((attack_target == null) || attack_target.isDead() || (!isPosessed && !(_forceUse ? attack_target.isAttackable(actor) : attack_target.isAutoAttackable(actor))))
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		if (!checkRange)
		{
			clientStopMoving();
			actor.doAttack(attack_target);
			return;
		}

		int range = actor.getPhysicalAttackRange();
		if (range < 10)
		{
			range = 10;
		}

		boolean canSee = GeoEngine.canSeeTarget(actor, attack_target, false);

		if (!canSee && ((range > 200) || (Math.abs(actor.getZ() - attack_target.getZ()) > 200)))
		{
			actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		range += actor.getMinDistance(attack_target);

		if (actor.isFakeDeath())
		{
			actor.breakFakeDeath();
		}

		if (actor.isInRangeZ(attack_target, range))
		{
			if (!canSee)
			{
				actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
				return;
			}

			clientStopMoving(false);
			actor.doAttack(attack_target);
		}
		else if (!_dontMove)
		{
			ThreadPoolManager.getInstance().execute(new ExecuteFollow(attack_target, range - 20));
		}
		else
		{
			actor.sendActionFailed();
		}
	}

	protected void thinkCast(boolean checkRange)
	{
		Playable actor = getActor();

		Creature target = getAttackTarget();

		if ((_skill.getSkillType() == SkillType.CRAFT) || _skill.isToggle())
		{
			if (_skill.checkCondition(actor, target, _forceUse, _dontMove, true))
			{
				actor.doCast(_skill, target, _forceUse);
			}
			return;
		}

		if ((target == null) || ((target.isDead() != _skill.getCorpse()) && !_skill.isNotTargetAoE()))
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		if (!checkRange)
		{
			// If the skill is the next step, assign this action after the expiry of skill
			if ((_skill.getNextAction() == NextAction.ATTACK) && !actor.equals(target))
			{
				setNextAction(nextAction.ATTACK, target, null, _forceUse, false);
			}
			else
			{
				clearNextAction();
			}

			clientStopMoving();

			if (_skill.checkCondition(actor, target, _forceUse, _dontMove, true))
			{
				actor.doCast(_skill, target, _forceUse);
			}
			else
			{
				setNextIntention();
				if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
				{
					thinkAttack(true);
				}
			}

			return;
		}

		int range = actor.getMagicalAttackRange(_skill);
		if (range < 10)
		{
			range = 10;
		}

		boolean canSee = (_skill.getSkillType() == SkillType.TAKECASTLE) || (_skill.getSkillType() == SkillType.TAKEFORTRESS) || GeoEngine.canSeeTarget(actor, target, actor.isFlying());
		boolean noRangeSkill = _skill.getCastRange() == 32767;

		if (!noRangeSkill && !canSee && ((range > 200) || (Math.abs(actor.getZ() - target.getZ()) > 200)))
		{
			actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		range += actor.getMinDistance(target);

		if (actor.isFakeDeath())
		{
			actor.breakFakeDeath();
		}

		if (actor.isInRangeZ(target, range) || noRangeSkill)
		{
			if (!noRangeSkill && !canSee)
			{
				actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
				return;
			}

			// If the skill is the next step, assign this action after the expiry of skill
			if ((_skill.getNextAction() == NextAction.ATTACK) && !actor.equals(target))
			{
				setNextAction(nextAction.ATTACK, target, null, _forceUse, false);
			}
			else
			{
				clearNextAction();
			}

			if (_skill.checkCondition(actor, target, _forceUse, _dontMove, true))
			{
				clientStopMoving(false);
				actor.doCast(_skill, target, _forceUse);
			}
			else
			{
				setNextIntention();
				if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
				{
					thinkAttack(true);
				}
			}
		}
		else if (!_dontMove)
		{
			ThreadPoolManager.getInstance().execute(new ExecuteFollow(target, range - 20));
		}
		else
		{
			actor.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
		}
	}

	protected void thinkCoupleAction(Player target, Integer socialId, boolean cancel)
	{
		//
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		clearNextAction();
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtFakeDeath()
	{
		clearNextAction();
		super.onEvtFakeDeath();
	}

	public void lockTarget(Creature target)
	{
		Playable actor = getActor();

		if ((target == null) || target.isDead())
		{
			actor.setAggressionTarget(null);
		}
		else if (actor.getAggressionTarget() == null)
		{
			GameObject actorStoredTarget = actor.getTarget();
			actor.setAggressionTarget(target);
			actor.setTarget(target);

			clearNextAction();

			if (actorStoredTarget != target)
			{
				actor.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
			}
		}
	}

	@Override
	public void Attack(GameObject target, boolean forceUse, boolean dontMove)
	{
		Playable actor = getActor();

		if (target.isCreature() && (actor.isActionsDisabled() || actor.isAttackingDisabled()))
		{
			// Если не можем атаковать, то атаковать позже
			setNextAction(nextAction.ATTACK, target, null, forceUse, false);
			actor.sendActionFailed();
			return;
		}

		_dontMove = dontMove;
		_forceUse = forceUse;
		clearNextAction();
		setIntention(AI_INTENTION_ATTACK, target);
	}

	@Override
	public boolean Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove)
	{
		final Playable actor = getActor();
		if (skill.altUse() || skill.isToggle())
		{
			if ((skill.isToggle() || skill.isHandler()) && (actor.isOutOfControl() || actor.isStunned() || actor.isSleeping() || actor.isParalyzed() || actor.isAlikeDead()))
			{
				clientActionFailed();
				return false;
			}

			return actor.altUseSkill(skill, target);
		}

		// Если не можем кастовать, то использовать скилл позже
		if (actor.isActionsDisabled())
		{
			setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
			clientActionFailed();
			return true;
		}

		// _actor.stopMove(null);
		_forceUse = forceUse;
		_dontMove = dontMove;
		clearNextAction();
		setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		return true;
	}

	@Override
	public Playable getActor()
	{
		return (Playable) super.getActor();
	}
}
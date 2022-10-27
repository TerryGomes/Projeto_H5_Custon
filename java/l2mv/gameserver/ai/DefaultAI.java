package l2mv.gameserver.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.math.random.RndSelector;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.AggroList.AggroInfo;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.MinionList;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.WorldRegion;
import l2mv.gameserver.model.entity.SevenSigns;
import l2mv.gameserver.model.instances.MinionInstance;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.QuestEventType;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.StatusUpdate;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.conditions.Condition;
import l2mv.gameserver.taskmanager.AiTaskManager;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.NpcUtils;

public class DefaultAI extends CharacterAI
{
	protected static final Logger _log = LoggerFactory.getLogger(DefaultAI.class);
	public static String namechar;

	public static enum TaskType
	{
		MOVE, INTERACT, ATTACK, CAST, BUFF
	}

	public static final int TaskDefaultWeight = 10000;

	public static class Task
	{
		public TaskType type;
		public Skill skill;
		public HardReference<? extends Creature> target;
		public Location loc;
		public boolean pathfind;
		public int locationOffset = 0;
		public int weight = TaskDefaultWeight;
		public boolean forceUse = false;
		public boolean dontMove = false;
		public Condition cond = null;
		public Env condEnv = new Env(null, target == null ? null : target.get(), skill);
	}

	public void addTaskCast(Creature target, Skill skill)
	{
		Task task = new Task();
		task.type = TaskType.CAST;
		task.target = target.getRef();
		task.skill = skill;
		_tasks.add(task);
		_def_think = true;
	}

	public void addTaskBuff(Creature target, Skill skill)
	{
		Task task = new Task();
		task.type = TaskType.BUFF;
		task.target = target.getRef();
		task.skill = skill;
		_tasks.add(task);
		_def_think = true;
	}

	public void addTaskAttack(Creature target)
	{
		Task task = new Task();
		task.type = TaskType.ATTACK;
		task.target = target.getRef();
		_tasks.add(task);
		_def_think = true;
	}

	public void addTaskAttack(Creature target, Skill skill, int weight)
	{
		Task task = new Task();
		task.type = skill.isOffensive() ? TaskType.CAST : TaskType.BUFF;
		task.target = target.getRef();
		task.skill = skill;
		task.weight = weight;
		_tasks.add(task);
		_def_think = true;
	}

	public void addTaskMove(Location loc, boolean pathfind)
	{
		Task task = new Task();
		task.type = TaskType.MOVE;
		task.loc = loc;
		task.pathfind = pathfind;
		_tasks.add(task);
		_def_think = true;
	}

	protected void addTaskMove(int locX, int locY, int locZ, boolean pathfind)
	{
		addTaskMove(new Location(locX, locY, locZ), pathfind);
	}

	private static class TaskComparator implements Comparator<Task>
	{
		private static final Comparator<Task> instance = new TaskComparator();

		public static final Comparator<Task> getInstance()
		{
			return instance;
		}

		@Override
		public int compare(Task o1, Task o2)
		{
			if ((o1 == null) || (o2 == null))
			{
				return 0;
			}
			return o2.weight - o1.weight;
		}
	}

	protected class Teleport extends RunnableImpl
	{
		Location _destination;

		public Teleport(Location destination)
		{
			_destination = destination;
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor != null)
			{
				actor.teleToLocation(_destination);
			}
		}
	}

	protected class RunningTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor != null)
			{
				actor.setRunning();
			}
			_runningTask = null;
		}
	}

	protected class MadnessTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor != null)
			{
				actor.stopConfused();
			}
			_madnessTask = null;
		}
	}

	protected class NearestTargetComparator implements Comparator<Creature>
	{
		private final Creature actor;

		public NearestTargetComparator(Creature actor)
		{
			this.actor = actor;
		}

		@Override
		public int compare(Creature o1, Creature o2)
		{
			// double diff = actor.getDistance3D(o1) - actor.getDistance3D(o2);
			double diff = actor.getDistance3DNoRoot(o1) - actor.getDistance3DNoRoot(o2);
			if (diff < 0.0)
			{
				return -1;
			}
			return diff > 0.0 ? 1 : 0;
		}
	}

	protected long AI_TASK_ATTACK_DELAY = Config.AI_TASK_ATTACK_DELAY;
	protected long AI_TASK_ACTIVE_DELAY = Config.AI_TASK_ACTIVE_DELAY;
	protected long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
	protected int MAX_PURSUE_RANGE;
	protected int MAX_Z_AGGRO_RANGE = 200;

	protected ScheduledFuture<?> _aiTask;

	protected ScheduledFuture<?> _runningTask;
	protected ScheduledFuture<?> _madnessTask;

	/** The flag used to indicate that a thinking action is in progress */
	private boolean _thinking = false;
	/** Показывает, есть ли задания */
	protected boolean _def_think = false;

	/** The L2NpcInstance aggro counter */
	protected long _globalAggro;

	protected long _randomAnimationEnd;
	protected int _pathfindFails;

	/** Список заданий */
	protected final NavigableSet<Task> _tasks = new ConcurrentSkipListSet<>(TaskComparator.getInstance());

	protected final Skill[] _damSkills, _dotSkills, _debuffSkills, _healSkills, _buffSkills, _stunSkills;

	protected long _lastActiveCheck;
	protected long _checkAggroTimestamp = 0;
	/** Время актуальности состояния атаки */
	protected long _attackTimeout;

	protected long _lastFactionNotifyTime = 0;
	protected long _minFactionNotifyInterval = 10000;

	protected final Comparator<Creature> _nearestTargetComparator;

	public DefaultAI(NpcInstance actor)
	{
		super(actor);

		setAttackTimeout(Long.MAX_VALUE);

		NpcInstance npc = getActor();
		_damSkills = npc.getTemplate().getDamageSkills();
		_dotSkills = npc.getTemplate().getDotSkills();
		_debuffSkills = npc.getTemplate().getDebuffSkills();
		_buffSkills = npc.getTemplate().getBuffSkills();
		_stunSkills = npc.getTemplate().getStunSkills();
		_healSkills = npc.getTemplate().getHealSkills();

		_nearestTargetComparator = new NearestTargetComparator(actor);

		// Preload some AI params
		MAX_PURSUE_RANGE = actor.getParameter("MaxPursueRange", actor.isRaid() ? Config.MAX_PURSUE_RANGE_RAID : npc.isUnderground() ? Config.MAX_PURSUE_UNDERGROUND_RANGE : Config.MAX_PURSUE_RANGE);
		_minFactionNotifyInterval = actor.getParameter("FactionNotifyInterval", 10000);
	}

	@Override
	public void runImpl()
	{
		if ((_aiTask == null) || (!Config.ALLOW_NPC_AIS && (getActor() == null || !getActor().isPlayable())))
		{
			return;
		}
		// проверяем, если NPC вышел в неактивный регион, отключаем AI
		if (!isGlobalAI() && ((System.currentTimeMillis() - _lastActiveCheck) > 60000L))
		{
			_lastActiveCheck = System.currentTimeMillis();
			NpcInstance actor = getActor();
			WorldRegion region = actor == null ? null : actor.getCurrentRegion();
			if ((region == null) || !region.isActive())
			{
				stopAITask();
				return;
			}
		}
		onEvtThink();
	}

	@Override
	public synchronized void startAITask()
	{
		if (_aiTask == null)
		{
			AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
		}
	}

	protected synchronized void switchAITask(long NEW_DELAY)
	{
		if (_aiTask == null)
		{
			return;
		}

		if (AI_TASK_DELAY_CURRENT != NEW_DELAY)
		{
			_aiTask.cancel(false);
			AI_TASK_DELAY_CURRENT = NEW_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
		}
	}

	@Override
	public final synchronized void stopAITask()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
	}

	/**
	 * Определяет, может ли этот тип АИ видеть персонажей в режиме Silent Move.
	 * @param target L2Playable цель
	 * @return true если цель видна в режиме Silent Move
	 */
	protected boolean canSeeInSilentMove(Playable target)
	{
		if (getActor().getParameter("canSeeInSilentMove", false))
		{
			return true;
		}
		return !target.isSilentMoving();
	}

	protected boolean canSeeInHide(Playable target)
	{
		if (getActor().getParameter("canSeeInHide", false))
		{
			return true;
		}

		return !target.isInvisible();
	}

	protected boolean checkAggression(Creature target)
	{
		return checkAggression(target, false);
	}

	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		NpcInstance actor = getActor();
		if ((getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || !isGlobalAggro() || target.isAlikeDead())
		{
			return false;
		}

		if (target.isNpc() && target.isInvul())
		{
			return false;
		}

		if (target.isPlayer() && (target.getPlayer().isInAwayingMode()) && (!Config.AWAY_PLAYER_TAKE_AGGRO))
		{
			return false;
		}

		if (target.isPlayable())
		{
			if (!canSeeInSilentMove((Playable) target) || !canSeeInHide((Playable) target) || (actor.getFaction().getName().equalsIgnoreCase("varka_silenos_clan") && (target.getPlayer().getVarka() > 0)))
			{
				return false;
			}
			if (actor.getFaction().getName().equalsIgnoreCase("ketra_orc_clan") && (target.getPlayer().getKetra() > 0))
			{
				return false;
			}
			/*
			 * if (target.isFollow && !target.isPlayer() && target.getFollowTarget() != null && target.getFollowTarget().isPlayer()) return;
			 */
			if ((target.isPlayer() && ((Player) target).isGM() && target.isInvisible()) || (((Playable) target).getNonAggroTime() > System.currentTimeMillis()))
			{
				return false;
			}
			if ((target.isPlayer() && !target.getPlayer().isActive()) || (actor.isMonster() && target.isInZonePeace()))
			{
				return false;
			}
		}

		AggroInfo ai = actor.getAggroList().get(target);
		if ((ai != null) && (ai.hate > 0))
		{
			if (!target.isInRangeZ(actor.getSpawnedLoc(), MAX_PURSUE_RANGE))
			{
				return false;
			}
		}
		else if (!actor.isAggressive() || !target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange()))
		{
			return false;
		}

		if (!canAttackCharacter(target) || !GeoEngine.canSeeTarget(actor, target, false))
		{
			return false;
		}

		// Prims - Posibility to call checkAggresion as a check without action
		if (!avoidAttack)
		{
			actor.getAggroList().addDamageHate(target, 0, 2);

			if ((target.isSummon() || target.isPet()))
			{
				actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
			}

			startRunningTask(AI_TASK_ATTACK_DELAY);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}

		return true;
	}

	protected void setIsInRandomAnimation(long time)
	{
		_randomAnimationEnd = System.currentTimeMillis() + time;
	}

	protected boolean randomAnimation()
	{
		NpcInstance actor = getActor();

		if (actor.getParameter("noRandomAnimation", false))
		{
			return false;
		}

		if (actor.hasRandomAnimation() && !actor.isActionsDisabled() && !actor.isMoving && !actor.isInCombat() && Rnd.chance(Config.RND_ANIMATION_RATE))
		{
			setIsInRandomAnimation(3000);
			actor.onRandomAnimation();
			return true;
		}
		return false;
	}

	protected boolean randomWalk()
	{
		NpcInstance actor = getActor();

		if (actor.getParameter("noRandomWalk", false))
		{
			return false;
		}

		return !actor.isMoving && maybeMoveToHome();
	}

	/**
	 * @return true если действие выполнено, false если нет
	 */
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isActionsDisabled() || (_randomAnimationEnd > System.currentTimeMillis()))
		{
			return true;
		}

		if (_def_think)
		{
			if (doTask())
			{
				clearTasks();
			}
			return true;
		}

		long now = System.currentTimeMillis();
		if ((now - _checkAggroTimestamp) > Config.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", actor.isAggressive() ? 100 : 0));
			if (!actor.getAggroList().isEmpty() || aggressive)
			{
				/*
				 * Prims - Changed completely the logic. Now we get the surroundings, then check the aggresion of everyone and make a final list of possible targets
				 * We call checkAggresion but without action, only checking, then if aggrolist is not empty then we sort it by distance and do the attack
				 * If done otherwise, the performance drop is huge
				 */
				final List<Creature> knowns = World.getAroundCharacters(actor);
				if (!knowns.isEmpty())
				{
					final List<Creature> aggroList = new ArrayList<>();

					for (Creature cha : knowns)
					{
						if (aggressive || (actor.getAggroList().get(cha) != null))
						{
							if (checkAggression(cha, true))
							{
								aggroList.add(cha);
							}
						}
					}

					if (actor.isDead())
					{
						return true;
					}

					// Only sort if there is actually a target to attack
					if (!aggroList.isEmpty())
					{
						Collections.sort(aggroList, _nearestTargetComparator);

						for (Creature target : aggroList)
						{
							if (target == null || target.isAlikeDead())
							{
								continue;
							}

							/*
							 * actor.getAggroList().addDamageHate(target, 0, 2);
							 * if ((target.isSummon() || target.isPet()))
							 * {
							 * actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
							 * }
							 * startRunningTask(AI_TASK_ATTACK_DELAY);
							 * setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
							 */
							if (checkAggression(target, false))
							{
								return true;
							}
						}
					}
				}
			}
		}

		if (actor.isMinion())
		{
			MonsterInstance leader = ((MinionInstance) actor).getLeader();
			if (leader != null)
			{
				double distance = actor.getDistance(leader.getX(), leader.getY());
				if (distance > 1000)
				{
					actor.teleToLocation(leader.getMinionPosition());
				}
				else if (distance > 200)
				{
					addTaskMove(leader.getMinionPosition(), false);
				}
				return true;
			}
		}

		if (randomAnimation() || randomWalk())
		{
			return true;
		}

		return false;
	}

	@Override
	protected void onIntentionIdle()
	{
		NpcInstance actor = getActor();

		// Удаляем все задания
		clearTasks();

		actor.stopMove();
		actor.getAggroList().clear(true);
		setAttackTimeout(Long.MAX_VALUE);
		setAttackTarget(null);

		changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
	}

	@Override
	protected void onIntentionActive()
	{
		NpcInstance actor = getActor();

		actor.stopMove();
		setAttackTimeout(Long.MAX_VALUE);

		if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
		{
			switchAITask(AI_TASK_ACTIVE_DELAY);
			changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		}

		onEvtThink();
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();

		// Removes all jobs
		clearTasks();

		actor.stopMove();
		setAttackTarget(target);
		setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
		setGlobalAggro(0);

		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
			switchAITask(AI_TASK_ATTACK_DELAY);
		}

		onEvtThink();
	}

	protected boolean canAttackCharacter(Creature target)
	{
		return target.isPlayable();
	}

	protected boolean checkTarget(Creature target, int range)
	{
		NpcInstance actor = getActor();
		if ((target == null) || target.isAlikeDead() || !actor.isInRangeZ(target, range))
		{
			return false;
		}

		// если не видим чаров в хайде - не атакуем их
		final boolean hided = target.isPlayable() && !canSeeInHide((Playable) target);

		if (!hided && actor.isConfused())
		{
			return true;
		}

		// В состоянии атаки атакуем всех, на кого у нас есть хейт
		if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
		{
			AggroInfo ai = actor.getAggroList().get(target);
			if (ai != null)
			{
				if (hided)
				{
					ai.hate = 0; // очищаем хейт
					return false;
				}
				return ai.hate > 0;
			}
			return false;
		}

		return canAttackCharacter(target);
	}

	public void setAttackTimeout(long time)
	{
		_attackTimeout = time;
	}

	protected long getAttackTimeout()
	{
		return _attackTimeout;
	}

	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return;
		}

		Location loc = actor.getSpawnedLoc();
		if (!actor.isInRange(loc, MAX_PURSUE_RANGE))
		{
			teleportHome();
			return;
		}

		if (doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			if (!createNewTask())
			{
				if (System.currentTimeMillis() > getAttackTimeout())
				{
					returnHome();
				}
			}
		}
	}

	@Override
	protected void onEvtSpawn()
	{
		setGlobalAggro(System.currentTimeMillis() + getActor().getParameter("globalAggro", 10000L));

		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	protected void onEvtReadyToAct()
	{
		onEvtThink();
	}

	@Override
	protected void onEvtArrivedTarget()
	{
		onEvtThink();
	}

	@Override
	protected void onEvtArrived()
	{
		onEvtThink();
	}

	protected boolean tryMoveToTarget(Creature target)
	{
		return tryMoveToTarget(target, 0);
	}

	protected boolean tryMoveToTarget(Creature target, int range)
	{
		NpcInstance actor = getActor();

		if (!actor.followToCharacter(target, actor.getPhysicalAttackRange(), true))
		{
			_pathfindFails++;
		}

		if ((_pathfindFails >= getMaxPathfindFails()) && (System.currentTimeMillis() > ((getAttackTimeout() - getMaxAttackTimeout()) + getTeleportTimeout())) && actor.isInRange(target, MAX_PURSUE_RANGE))
		{
			_pathfindFails = 0;

			if (target.isPlayable())
			{
				AggroInfo hate = actor.getAggroList().get(target);
				if ((hate == null) || ((hate.damage < 100) && (hate.hate < 100)))
				{
					returnHome();
					return false;
				}
			}
			Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getGeoIndex());
			if (!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex()))
			{
				loc = target.getLoc();
			}
			if (canTeleWhenCannotSeeTarget())
			{
				actor.teleToLocation(loc);
			}
		}

		return true;
	}

	protected boolean canTeleWhenCannotSeeTarget()
	{
		return true;
	}

	protected boolean maybeNextTask(Task currentTask)
	{
		// Следующее задание
		_tasks.remove(currentTask);
		// Если заданий больше нет - определить новое
		if (_tasks.size() == 0)
		{
			return true;
		}
		return false;
	}

	protected boolean doTask()
	{
		NpcInstance actor = getActor();

		if (!_def_think)
		{
			return true;
		}

		Task currentTask = _tasks.pollFirst();
		if (currentTask == null)
		{
			clearTasks();
			return true;
		}

		if (actor.isDead() || actor.isAttackingNow() || actor.isCastingNow())
		{
			return false;
		}

		switch (currentTask.type)
		{
		// Задание "прибежать в заданные координаты"
		case MOVE:
		{
			if (actor.isMovementDisabled() || !getIsMobile())
			{
				return true;
			}

			if (actor.isInRange(currentTask.loc, 100))
			{
				return maybeNextTask(currentTask);
			}

			if (actor.isMoving)
			{
				return false;
			}

			if (!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind))
			{
				clientStopMoving();
				_pathfindFails = 0;
				actor.teleToLocation(currentTask.loc);
				return maybeNextTask(currentTask);
			}
		}
			break;
		// Задание "добежать - ударить"
		case ATTACK:
		{
			Creature target = currentTask.target.get();

			if (!checkTarget(target, MAX_PURSUE_RANGE))
			{
				return true;
			}

			setAttackTarget(target);

			if (actor.isMoving)
			{
				return Rnd.chance(25);
			}

			if ((actor.getRealDistance3D(target) <= (actor.getPhysicalAttackRange() + 40)) && GeoEngine.canSeeTarget(actor, target, false))
			{
				clientStopMoving();
				_pathfindFails = 0;
				setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
				actor.doAttack(target);
				return maybeNextTask(currentTask);
			}

			if (actor.isMovementDisabled() || !getIsMobile())
			{
				return true;
			}

			tryMoveToTarget(target);
		}
			break;
		// Setting "to run - attack skill"
		case CAST:
		{
			Creature target = currentTask.target.get();

			if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.getId()))
			{
				return true;
			}

			boolean isAoE = currentTask.skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
			int castRange = currentTask.skill.getAOECastRange();

			if (!checkTarget(target, MAX_PURSUE_RANGE + castRange))
			{
				return true;
			}

			setAttackTarget(target);

			if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false))
			{
				clientStopMoving();
				_pathfindFails = 0;
				setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
				actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
				return maybeNextTask(currentTask);
			}

			if (actor.isMoving)
			{
				return Rnd.chance(10);
			}

			if (actor.isMovementDisabled() || !getIsMobile())
			{
				return true;
			}

			tryMoveToTarget(target, castRange);
		}
			break;
		// Task "to run - use skill"
		case BUFF:
		{
			Creature target = currentTask.target.get();

			if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.getId()))
			{
				return true;
			}

			if ((target == null) || target.isAlikeDead() || !actor.isInRange(target, 2000))
			{
				return true;
			}

			boolean isAoE = currentTask.skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
			int castRange = currentTask.skill.getAOECastRange();

			if (actor.isMoving)
			{
				return Rnd.chance(10);
			}

			if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false))
			{
				clientStopMoving();
				_pathfindFails = 0;
				actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
				return maybeNextTask(currentTask);
			}

			if (actor.isMovementDisabled() || !getIsMobile())
			{
				return true;
			}

			tryMoveToTarget(target);
		}
			break;
		}

		return false;
	}

	protected boolean createNewTask()
	{
		return false;
	}

	protected boolean defaultNewTask()
	{
		clearTasks();

		NpcInstance actor = getActor();
		Creature target;
		if ((actor == null) || ((target = prepareTarget()) == null))
		{
			return false;
		}

		double distance = actor.getDistance(target);
		return chooseTaskAndTargets(null, target, distance);
	}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		if (_thinking || (actor == null) || actor.isActionsDisabled() || actor.isAfraid())
		{
			return;
		}

		if (_randomAnimationEnd > System.currentTimeMillis())
		{
			return;
		}

		_thinking = true;
		try
		{
			switch (getIntention())
			{
			case AI_INTENTION_ACTIVE:
				if (!Config.BLOCK_ACTIVE_TASKS)
				{
					thinkActive();
				}
				break;
			case AI_INTENTION_ATTACK:
				thinkAttack();
				break;
			}
		}
		finally
		{
			_thinking = false;
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		final NpcInstance actor = getActor();
		final int transformer = actor.getParameter("transformOnDead", 0);
		final int chance = actor.getParameter("transformChance", 100);
		if (transformer > 0 && Rnd.chance(chance))
		{
			final NpcInstance npc = NpcUtils.spawnSingle(transformer, actor.getLoc(), actor.getReflection());
			if (killer != null && killer.isPlayable())
			{
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
				killer.setTarget(npc);
				killer.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
			}
		}

		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		if ((getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || !isGlobalAggro())
		{
			return;
		}

		NpcInstance actor = getActor();
		if ((actor == null) || !actor.isInRange(attacked, actor.getFaction().getRange()) || (Math.abs(attacker.getZ() - actor.getZ()) > MAX_Z_AGGRO_RANGE))
		{
			return;
		}

		if (GeoEngine.canSeeTarget(actor, attacked, false))
		{
			notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[]
			{
				attacker,
				attacker.isSummon() ? damage : 2
			});
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if ((attacker == null) || actor.isDead())
		{
			if (actor.isDead())
			{
				notifyFriends(attacker, damage);
			}
			return;
		}

		int transformer = actor.getParameter("transformOnUnderAttack", 0);
		if (transformer > 0)
		{
			int chance = actor.getParameter("transformChance", 5);
			if ((chance == 100) || ((((MonsterInstance) actor).getChampion() == 0) && (actor.getCurrentHpPercents() > 50) && Rnd.chance(chance)))
			{
				MonsterInstance npc = (MonsterInstance) NpcHolder.getInstance().getTemplate(transformer).getNewInstance();
				npc.setSpawnedLoc(actor.getLoc());
				npc.setReflection(actor.getReflection());
				npc.setChampion(((MonsterInstance) actor).getChampion());
				npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
				npc.spawnMe(npc.getSpawnedLoc());
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
				actor.doDie(actor);
				actor.decayMe();
				attacker.setTarget(npc);
				attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
				return;
			}
		}

		Player player = attacker.getPlayer();

		if (player != null)
		{ // FIXME Plugs 7 seals, the 7 seals attacking monster teleports the character to the nearest town
			if (((SevenSigns.getInstance().isSealValidationPeriod()) || (SevenSigns.getInstance().isCompResultsPeriod())) && (actor.isSevenSignsMonster()) && (Config.RETAIL_SS))
			{
				int pcabal = SevenSigns.getInstance().getPlayerCabal(player);
				int wcabal = SevenSigns.getInstance().getCabalHighestScore();
				if ((pcabal != wcabal) && (wcabal != SevenSigns.CABAL_NULL))
				{
					player.sendMessage("You have been teleported to the nearest town because you not signed for winning cabal.");
					player.teleToClosestTown();
					return;
				}
			}
			List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
			if (quests != null)
			{
				for (QuestState qs : quests)
				{
					qs.getQuest().notifyAttack(actor, qs);
				}
			}
		}

		// Добавляем только хейт, урон, если атакующий - игровой персонаж, будет добавлен в L2NpcInstance.onReduceCurrentHp
		actor.getAggroList().addDamageHate(attacker, 0, damage);

		// Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
		if ((damage > 0) && (attacker.isSummon() || attacker.isPet()))
		{
			actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
		}

		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if (!actor.isRunning())
			{
				startRunningTask(AI_TASK_ATTACK_DELAY);
			}
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}

		notifyFriends(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		NpcInstance actor = getActor();
		if ((attacker == null) || actor.isDead())
		{
			return;
		}

		actor.getAggroList().addDamageHate(attacker, 0, aggro);

		// Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
		if ((aggro > 0) && (attacker.isSummon() || attacker.isPet()))
		{
			actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? aggro : 1);
		}

		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if (!actor.isRunning())
			{
				startRunningTask(AI_TASK_ATTACK_DELAY);
			}
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}

	protected boolean maybeMoveToHome()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return false;
		}

		boolean randomWalk = actor.hasRandomWalk();
		Location sloc = actor.getSpawnedLoc();

		// Random walk or not?
		if (randomWalk && (!Config.RND_WALK || !Rnd.chance(Config.RND_WALK_RATE)))
		{
			return false;
		}

		boolean isInRange = actor.isInRangeZ(sloc, Config.MAX_DRIFT_RANGE);

		if (!randomWalk && isInRange)
		{
			return false;
		}

		Location pos = Location.findPointToStay(actor, sloc, 0, Config.MAX_DRIFT_RANGE);

		actor.setWalking();

		// Телепортируемся домой, только если далеко от дома
		if (!actor.moveToLocation(pos.x, pos.y, pos.z, 0, true) && !isInRange)
		{
			teleportHome();
		}

		return true;
	}

	protected void returnHome()
	{
		returnHome(true, Config.ALWAYS_TELEPORT_HOME);
	}

	protected void teleportHome()
	{
		returnHome(true, true);
	}

	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		NpcInstance actor = getActor();
		Location sloc = actor.getSpawnedLoc();

		// Removes all jobs
		clearTasks();
		actor.stopMove();

		if (clearAggro)
		{
			actor.getAggroList().clear(true);
		}

		setAttackTimeout(Long.MAX_VALUE);
		setAttackTarget(null);

		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);

		if (teleport)
		{
			actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 0));
			actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
		}
		else
		{
			if (!clearAggro)
			{
				actor.setRunning();
			}
			else
			{
				actor.setWalking();
			}

			addTaskMove(sloc, false);
		}
	}

	protected Creature prepareTarget()
	{
		NpcInstance actor = getActor();

		if (actor.isConfused())
		{
			return getAttackTarget();
		}

		// Для "двинутых" боссов, иногда, выбираем случайную цель
		if (Rnd.chance(actor.getParameter("isMadness", 0)))
		{
			Creature randomHated = actor.getAggroList().getRandomHated();
			if (randomHated != null)
			{
				setAttackTarget(randomHated);
				if ((_madnessTask == null) && !actor.isConfused())
				{
					actor.startConfused();
					_madnessTask = ThreadPoolManager.getInstance().schedule(new MadnessTask(), 10000);
				}
				return randomHated;
			}
		}

		// Новая цель исходя из агрессивности
		List<Creature> hateList = actor.getAggroList().getHateList();
		Creature hated = null;
		for (Creature cha : hateList)
		{
			// Не подходит, очищаем хейт
			if (!checkTarget(cha, MAX_PURSUE_RANGE))
			{
				actor.getAggroList().remove(cha, true);
				continue;
			}
			hated = cha;
			break;
		}

		if (hated != null)
		{
			setAttackTarget(hated);
			return hated;
		}

		return null;
	}

	protected boolean canUseSkill(Skill skill, Creature target, double distance)
	{
		NpcInstance actor = getActor();
		if ((skill == null) || skill.isNotUsedByAI() || ((skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF) && (target != actor)))
		{
			return false;
		}

		int castRange = skill.getAOECastRange();
		if ((castRange <= 200) && (distance > 200))
		{
			return false;
		}

		if (actor.isSkillDisabled(skill) || actor.isMuted(skill) || actor.isUnActiveSkill(skill.getId()))
		{
			return false;
		}

		double mpConsume2 = skill.getMpConsume2();
		if (skill.isMagic())
		{
			mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
		}
		else
		{
			mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
		}
		if ((actor.getCurrentMp() < mpConsume2) || (target.getEffectList().getEffectsCountForSkill(skill.getId()) != 0))
		{
			return false;
		}

		return true;
	}

	protected boolean canUseSkill(Skill sk, Creature target)
	{
		return canUseSkill(sk, target, 0);
	}

	protected Skill[] selectUsableSkills(Creature target, double distance, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0) || (target == null))
		{
			return null;
		}

		Skill[] ret = null;
		int usable = 0;

		for (Skill skill : skills)
		{
			if (canUseSkill(skill, target, distance))
			{
				if (ret == null)
				{
					ret = new Skill[skills.length];
				}
				ret[usable++] = skill;
			}
		}

		if ((ret == null) || (usable == skills.length))
		{
			return ret;
		}

		if (usable == 0)
		{
			return null;
		}

		ret = Arrays.copyOf(ret, usable);
		return ret;
	}

	protected static Skill selectTopSkillByDamage(Creature actor, Creature target, double distance, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}

		if (skills.length == 1)
		{
			return skills[0];
		}

		RndSelector<Skill> rnd = new RndSelector<>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			weight = (skill.getSimpleDamage(actor, target) * skill.getAOECastRange()) / distance;
			if (weight < 1.)
			{
				weight = 1.;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static Skill selectTopSkillByDebuff(Creature actor, Creature target, double distance, Skill[] skills) // FIXME
	{
		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}

		if (skills.length == 1)
		{
			return skills[0];
		}

		RndSelector<Skill> rnd = new RndSelector<>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if (skill.getSameByStackType(target) != null)
			{
				continue;
			}
			if ((weight = (100. * skill.getAOECastRange()) / distance) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static Skill selectTopSkillByBuff(Creature target, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}

		if (skills.length == 1)
		{
			return skills[0];
		}

		RndSelector<Skill> rnd = new RndSelector<>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if (skill.getSameByStackType(target) != null)
			{
				continue;
			}
			if ((weight = skill.getPower()) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static Skill selectTopSkillByHeal(Creature target, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}

		double hpReduced = target.getMaxHp() - target.getCurrentHp();
		if (hpReduced < 1)
		{
			return null;
		}

		if (skills.length == 1)
		{
			return skills[0];
		}

		RndSelector<Skill> rnd = new RndSelector<>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if ((weight = Math.abs(skill.getPower() - hpReduced)) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0) || (target == null))
		{
			return;
		}
		for (Skill sk : skills)
		{
			addDesiredSkill(skillMap, target, distance, sk);
		}
	}

	protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill skill)
	{
		if ((skill == null) || (target == null) || !canUseSkill(skill, target))
		{
			return;
		}
		int weight = (int) -Math.abs(skill.getAOECastRange() - distance);
		if (skill.getAOECastRange() >= distance)
		{
			weight += 1000000;
		}
		else if (skill.isNotTargetAoE() && (skill.getTargets(getActor(), target, false).size() == 0))
		{
			return;
		}
		skillMap.put(skill, weight);
	}

	protected void addDesiredHeal(Map<Skill, Integer> skillMap, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return;
		}
		NpcInstance actor = getActor();
		double hpReduced = actor.getMaxHp() - actor.getCurrentHp();
		double hpPercent = actor.getCurrentHpPercents();
		if (hpReduced < 1)
		{
			return;
		}
		int weight;
		for (Skill sk : skills)
		{
			if (canUseSkill(sk, actor) && (sk.getPower() <= hpReduced))
			{
				weight = (int) sk.getPower();
				if (hpPercent < 50)
				{
					weight += 1000000;
				}
				skillMap.put(sk, weight);
			}
		}
	}

	protected void addDesiredBuff(Map<Skill, Integer> skillMap, Skill[] skills)
	{
		if ((skills == null) || (skills.length == 0))
		{
			return;
		}
		NpcInstance actor = getActor();
		for (Skill sk : skills)
		{
			if (canUseSkill(sk, actor))
			{
				skillMap.put(sk, 1000000);
			}
		}
	}

	protected Skill selectTopSkill(Map<Skill, Integer> skillMap)
	{
		if ((skillMap == null) || skillMap.isEmpty())
		{
			return null;
		}
		int nWeight, topWeight = Integer.MIN_VALUE;
		for (Skill next : skillMap.keySet())
		{
			if ((nWeight = skillMap.get(next)) > topWeight)
			{
				topWeight = nWeight;
			}
		}
		if (topWeight == Integer.MIN_VALUE)
		{
			return null;
		}

		Skill[] skills = new Skill[skillMap.size()];
		nWeight = 0;
		for (Map.Entry<Skill, Integer> e : skillMap.entrySet())
		{
			if (e.getValue() < topWeight)
			{
				continue;
			}
			skills[nWeight++] = e.getKey();
		}
		return skills[Rnd.get(nWeight)];
	}

	protected boolean chooseTaskAndTargets(Skill skill, Creature target, double distance)
	{
		NpcInstance actor = getActor();

		// Использовать скилл если можно, иначе атаковать
		if (skill != null)
		{
			// Проверка цели, и смена если необходимо
			if (actor.isMovementDisabled() && (distance > (skill.getAOECastRange() + 60)))
			{
				target = null;
				if (skill.isOffensive())
				{
					ArrayList<Creature> targets = new ArrayList<>();
					for (Creature cha : actor.getAggroList().getHateList())
					{
						if (!checkTarget(cha, skill.getAOECastRange() + 60) || !canUseSkill(skill, cha))
						{
							continue;
						}
						targets.add(cha);
					}
					if (!targets.isEmpty())
					{
						target = targets.get(Rnd.get(targets.size()));
					}
				}
			}

			if (target == null)
			{
				return false;
			}

			// Добавить новое задание
			if (skill.isOffensive())
			{
				addTaskCast(target, skill);
			}
			else
			{
				addTaskBuff(target, skill);
			}
			return true;
		}

		// Смена цели, если необходимо
		if (actor.isMovementDisabled() && (distance > (actor.getPhysicalAttackRange() + 40)))
		{
			target = null;
			ArrayList<Creature> targets = new ArrayList<>();
			for (Creature cha : actor.getAggroList().getHateList())
			{
				if (!checkTarget(cha, actor.getPhysicalAttackRange() + 40))
				{
					continue;
				}
				targets.add(cha);
			}
			if (!targets.isEmpty())
			{
				target = targets.get(Rnd.get(targets.size()));
			}
		}

		if (target == null)
		{
			return false;
		}

		// Добавить новое задание
		addTaskAttack(target);
		return true;
	}

	@Override
	public boolean isActive()
	{
		return _aiTask != null;
	}

	protected void clearTasks()
	{
		_def_think = false;
		_tasks.clear();
	}

	/** переход в режим бега через определенный интервал времени
	 * @param interval */
	protected void startRunningTask(long interval)
	{
		NpcInstance actor = getActor();
		if ((actor != null) && (_runningTask == null) && !actor.isRunning())
		{
			_runningTask = ThreadPoolManager.getInstance().schedule(new RunningTask(), interval);
		}
	}

	protected boolean isGlobalAggro()
	{
		if (_globalAggro == 0)
		{
			return true;
		}
		if (_globalAggro <= System.currentTimeMillis())
		{
			_globalAggro = 0;
			return true;
		}
		return false;
	}

	public void setGlobalAggro(long value)
	{
		_globalAggro = value;
	}

	@Override
	public NpcInstance getActor()
	{
		return (NpcInstance) super.getActor();
	}

	protected boolean defaultThinkBuff(int rateSelf)
	{
		return defaultThinkBuff(rateSelf, 0);
	}

	/**
	 * Оповестить дружественные цели об атаке.
	 * @param attacker
	 * @param damage
	 */
	protected void notifyFriends(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if ((System.currentTimeMillis() - _lastFactionNotifyTime) > _minFactionNotifyInterval)
		{
			_lastFactionNotifyTime = System.currentTimeMillis();
			if (actor.isMinion())
			{
				// Оповестить лидера об атаке
				MonsterInstance master = ((MinionInstance) actor).getLeader();
				if (master != null)
				{
					if (!master.isDead() && master.isVisible())
					{
						master.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
					}

					// Оповестить минионов лидера об атаке
					MinionList minionList = master.getMinionList();
					if (minionList != null)
					{
						for (MinionInstance minion : minionList.getAliveMinions())
						{
							if (minion != actor)
							{
								minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
							}
						}
					}
				}
			}

			// Оповестить своих минионов об атаке
			MinionList minionList = actor.getMinionList();
			if ((minionList != null) && minionList.hasAliveMinions())
			{
				for (MinionInstance minion : minionList.getAliveMinions())
				{
					minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
				}
			}

			// Оповестить социальных мобов
			for (NpcInstance npc : activeFactionTargets())
			{
				npc.getAI().notifyEvent(CtrlEvent.EVT_CLAN_ATTACKED, new Object[]
				{
					actor,
					attacker,
					damage
				});
			}
		}
	}

	protected List<NpcInstance> activeFactionTargets()
	{
		NpcInstance actor = getActor();
		if (actor.getFaction().isNone())
		{
			return Collections.emptyList();
		}
		List<NpcInstance> npcFriends = new ArrayList<NpcInstance>();
		for (NpcInstance npc : World.getAroundNpc(actor))
		{
			if (!npc.isDead())
			{
				if (npc.isInFaction(actor))
				{
					if (npc.isInRangeZ(actor, npc.getFaction().getRange()))
					{
						if (GeoEngine.canSeeTarget(npc, actor, false))
						{
							npcFriends.add(npc);
						}
					}
				}
			}
		}
		return npcFriends;
	}

	protected boolean defaultThinkBuff(int rateSelf, int rateFriends)
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return true;
		}

		// TODO сделать более разумный выбор баффа, сначала выбирать подходящие а потом уже рандомно 1 из них
		if (Rnd.chance(rateSelf))
		{
			double actorHp = actor.getCurrentHpPercents();

			Skill[] skills = actorHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
			if ((skills == null) || (skills.length == 0))
			{
				return false;
			}

			Skill skill = skills[Rnd.get(skills.length)];
			addTaskBuff(actor, skill);
			return true;
		}

		if (Rnd.chance(rateFriends))
		{
			for (NpcInstance npc : activeFactionTargets())
			{
				double targetHp = npc.getCurrentHpPercents();

				Skill[] skills = targetHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
				if ((skills == null) || (skills.length == 0))
				{
					continue;
				}

				Skill skill = skills[Rnd.get(skills.length)];
				addTaskBuff(actor, skill);
				return true;
			}
		}

		return false;
	}

	protected boolean defaultFightTask()
	{
		clearTasks();

		NpcInstance actor = getActor();
		if (actor.isDead() || actor.isAMuted())
		{
			return false;
		}

		Creature target;
		if ((target = prepareTarget()) == null)
		{
			return false;
		}

		double distance = actor.getDistance(target);
		double targetHp = target.getCurrentHpPercents();
		double actorHp = actor.getCurrentHpPercents();

		Skill[] dam = Rnd.chance(getRateDAM()) ? selectUsableSkills(target, distance, _damSkills) : null;
		Skill[] dot = Rnd.chance(getRateDOT()) ? selectUsableSkills(target, distance, _dotSkills) : null;
		Skill[] debuff = targetHp > 10 ? Rnd.chance(getRateDEBUFF()) ? selectUsableSkills(target, distance, _debuffSkills) : null : null;
		Skill[] stun = Rnd.chance(getRateSTUN()) ? selectUsableSkills(target, distance, _stunSkills) : null;
		Skill[] heal = actorHp < 50 ? Rnd.chance(getRateHEAL()) ? selectUsableSkills(actor, 0, _healSkills) : null : null;
		Skill[] buff = Rnd.chance(getRateBUFF()) ? selectUsableSkills(actor, 0, _buffSkills) : null;

		RndSelector<Skill[]> rnd = new RndSelector<>();
		if (!actor.isAMuted())
		{
			rnd.add(null, getRatePHYS());
		}
		rnd.add(dam, getRateDAM());
		rnd.add(dot, getRateDOT());
		rnd.add(debuff, getRateDEBUFF());
		rnd.add(heal, getRateHEAL());
		rnd.add(buff, getRateBUFF());
		rnd.add(stun, getRateSTUN());

		Skill[] selected = rnd.select();
		rnd.clear();
		if (selected != null)
		{
			if ((selected == dam) || (selected == dot))
			{
				return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);
			}

			if ((selected == debuff) || (selected == stun))
			{
				return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);
			}

			if (selected == buff)
			{
				return chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);
			}

			if (selected == heal)
			{
				return chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);
			}
		}

		// TODO сделать лечение и баф дружественных целей

		return chooseTaskAndTargets(null, target, distance);
	}

	public int getRatePHYS()
	{
		return 100;
	}

	public int getRateDOT()
	{
		return 0;
	}

	public int getRateDEBUFF()
	{
		return 0;
	}

	public int getRateDAM()
	{
		return 0;
	}

	public int getRateSTUN()
	{
		return 0;
	}

	public int getRateBUFF()
	{
		return 0;
	}

	public int getRateHEAL()
	{
		return 0;
	}

	public boolean getIsMobile()
	{
		return !getActor().getParameter("isImmobilized", false);
	}

	public int getMaxPathfindFails()
	{
		return 3;
	}

	/**
	 * Задержка, перед переключением в активный режим после атаки, если цель не найдена (вне зоны досягаемости, убита, очищен хейт)
	 * @return
	 */
	public int getMaxAttackTimeout()
	{
		return 15000;
	}

	/**
	 * Задержка, перед телепортом к цели, если не удается дойти
	 * @return
	 */
	public int getTeleportTimeout()
	{
		return 10000;
	}
}
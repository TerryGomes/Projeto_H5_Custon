package l2mv.gameserver.ai;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI.Task;
import l2mv.gameserver.ai.DefaultAI.TaskType;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.actor.OnKillListener;
import l2mv.gameserver.listener.actor.OnMagicHitListener;
import l2mv.gameserver.listener.actor.ai.OnAiEventListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.PhantomPlayers;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Skill.SkillTargetType;
import l2mv.gameserver.model.Skill.SkillType;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.instances.GuardInstance;
import l2mv.gameserver.model.instances.MerchantInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.WarehouseInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.clientpackets.EnterWorld;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.randoms.LocationStorage;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.stats.Formulas.AttackInfo;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.conditions.Condition;
import l2mv.gameserver.stats.funcs.FuncAdd;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.taskmanager.AiTaskManager;
import l2mv.gameserver.templates.item.WeaponTemplate;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.Util;

/**
 *
 * @author Nik
 *
 */
public class PhantomPlayerAI extends PlayerAI implements OnAiEventListener, OnMagicHitListener, OnKillListener
{
	// TODO: Refuse trade and party and clan and shits.
	public static final int TASK_DEFAULT_WEIGHT = 10000;
	private static final int WAIT_TIMER_ID = 0;
	private static final int BUFF_TIMER_ID = 1;

	private static final String[] MOODS = new String[]
	{
		/* "sit and relax", */ "free roam",
		"check npcs",
		"check shop",
		"check warehouse"
	};

	public static final Comparator<Creature> lvlDiff = Comparator.comparingInt(Creature::getLevel);
	public final Comparator<Creature> distanceComparator = (c1, c2) -> (Integer.compare((int) getActor().getDistance(c1), (int) getActor().getDistance(c2)));
	public final Comparator<Creature> targetComparator = distanceComparator.thenComparing(lvlDiff);

	protected long AI_TASK_ATTACK_DELAY = Config.AI_TASK_ATTACK_DELAY;
	protected long AI_TASK_ACTIVE_DELAY = Config.AI_TASK_ACTIVE_DELAY;
	protected long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;

	protected ScheduledFuture<?> _aiTask;
	protected ScheduledFuture<?> _roamingTask;
	protected ScheduledFuture<?> _farmingTask;
	private boolean _isWaiting = false;
	private int _thinkActiveExceptions = 0;
	protected String _mood = "";
	protected final NavigableSet<Task> _tasks = new ConcurrentSkipListSet<Task>(TaskComparator.getInstance());

	private long _buffTimeLastCached = 0;
	private int _buffTimeCache = -1;
	private long _lastAiResponse = 0;

	protected int _locationCopyPlayerObjId;
	protected int _locationCopyIndex;

	public PhantomPlayerAI(Player actor)
	{
		super(actor);
	}

	private static class TaskComparator implements Comparator<Task>
	{
		private static final Comparator<Task> _instance = new TaskComparator();

		public static final Comparator<Task> getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(Task o1, Task o2)
		{
			if (o1 == null || o2 == null)
			{
				return 0;
			}
			return o2.weight - o1.weight;
		}
	}

	public void addTaskCast(Creature target, Skill skill)
	{
		addTaskCast(target, skill, null, false, false);
	}

	public void addTaskCast(Creature target, Skill skill, Condition cond)
	{
		addTaskCast(target, skill, cond, false, false);
	}

	public void addTaskCast(Creature target, Skill skill, Condition cond, boolean forceUse)
	{
		addTaskCast(target, skill, cond, forceUse, false);
	}

	public void addTaskCast(Creature target, Skill skill, Condition cond, boolean forceUse, boolean dontMove)
	{
		if (skill == null)
		{
			return;
		}

		Task task = new Task();
		task.type = TaskType.CAST;
		task.target = target.getRef();
		task.skill = skill;
		task.cond = cond;
		task.forceUse = forceUse;
		task.dontMove = dontMove;
		_tasks.add(task);
	}

	public void addTaskBuff(Creature target, Skill skill)
	{
		addTaskBuff(target, skill, null);
	}

	public void addTaskBuff(Creature target, Skill skill, Condition cond)
	{
		if (skill == null)
		{
			return;
		}

		Task task = new Task();
		task.type = TaskType.BUFF;
		task.target = target.getRef();
		task.skill = skill;
		task.cond = cond;
		_tasks.add(task);
	}

	public void addTaskAttack(Creature target)
	{
		addTaskAttack(target, null, false, false);
	}

	public void addTaskAttack(Creature target, Condition cond)
	{
		addTaskAttack(target, cond, false, false);
	}

	public void addTaskAttack(Creature target, Condition cond, boolean forceUse)
	{
		addTaskAttack(target, cond, forceUse, false);
	}

	public void addTaskAttack(Creature target, Condition cond, boolean forceUse, boolean dontMove)
	{
		if (target == null)
		{
			return;
		}

		Task task = new Task();
		task.type = TaskType.ATTACK;
		task.target = target.getRef();
		task.cond = cond;
		task.forceUse = forceUse;
		task.dontMove = dontMove;
		_tasks.add(task);
	}

	public void addTaskMove(Location loc, int offset)
	{
		addTaskMove(loc, offset, true, null);
	}

	public void addTaskMove(Location loc, int offset, boolean pathfind)
	{
		addTaskMove(loc, offset, pathfind, null);
	}

	public void addTaskMove(Location loc, int offset, boolean pathfind, Condition cond)
	{
		Task task = new Task();
		task.type = TaskType.MOVE;
		task.loc = loc;
		task.pathfind = pathfind;
		task.locationOffset = offset;
		_tasks.add(task);
	}

	public void addTaskInteract(Creature target)
	{
		Task task = new Task();
		task.type = TaskType.INTERACT;
		task.target = target.getRef();
		task.pathfind = true;
		_tasks.add(task);
	}

	protected boolean maybeNextTask(Task currentTask)
	{
		// Next job
		_tasks.remove(currentTask);
		// If there are no more jobs - define new
		if (_tasks.size() == 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * @return true : clear all tasks
	 */
	protected void doTask()
	{
		Player actor = getActor();

		if (_tasks.isEmpty())
		{
			return;
		}

		Task currentTask = _tasks.pollFirst();
		if ((currentTask == null) || actor.isDead() || actor.isAttackingNow() || actor.isCastingNow())
		{
			return;
		}

		if (currentTask.cond != null)
		{
			Env env = currentTask.condEnv;
			if (env.target == null)
			{
				return;
			}
			env.character = actor;
			if (!currentTask.cond.test(env))
			{
				return;
			}
		}

		switch (currentTask.type)
		{
		case MOVE:
			setNextAction(nextAction.MOVE, currentTask.loc, currentTask.locationOffset, currentTask.pathfind, currentTask.dontMove);
			getActor().setRunning();
			setNextIntention();
			break;
		case INTERACT:
			setNextAction(nextAction.INTERACT, currentTask.target.get(), null, currentTask.forceUse, currentTask.dontMove);
			getActor().setRunning();
			setNextIntention();
			break;
		case ATTACK:
			setNextAction(nextAction.ATTACK, currentTask.target.get(), null, false, currentTask.dontMove);
			setNextIntention();
			break;
		case CAST:
			setNextAction(nextAction.CAST, currentTask.skill, currentTask.target.get(), currentTask.forceUse, currentTask.dontMove);
			setNextIntention();
			break;
		}
	}

	@Override
	public synchronized void startAITask()
	{
		if (_aiTask == null)
		{
			AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
			getActor().addListener(this);
			getActor().addStatFunc(new FuncAdd(Stats.MAX_NO_PENALTY_LOAD, 0x40, this, Integer.MAX_VALUE)); // I have reached a new level of laziness - avoid caring about weight penalty
																											// :D

			getActor().addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 0x40, this, 40)); // Let give them some vamp..

			waitTime(4000); // Wait t least 4secs until active, to not look suspicious.
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
			getActor().addListener(this);
			getActor().addStatFunc(new FuncAdd(Stats.MAX_NO_PENALTY_LOAD, 0x40, this, Integer.MAX_VALUE)); // I have reached a new level of laziness - avoid caring about weight penalty
																											// :D
		}
	}

	@Override
	public final synchronized void stopAITask()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
			getActor().removeListener(this);
			getActor().removeStatFunc(new FuncAdd(Stats.MAX_NO_PENALTY_LOAD, 0x40, this, Integer.MAX_VALUE)); // Remove the added weight bonus
			stopRoamingTask();
			stopFarmTask();
		}
	}

	@Override
	public boolean isActive()
	{
		return _aiTask != null;
	}

	@Override
	public void runImpl()
	{
		if ((_aiTask == null) || Config.DISABLE_PHANTOM_ACTIONS)
		{
			return;
		}

		onEvtThink();

		_lastAiResponse = System.currentTimeMillis();
	}

	public void waitTime(int timeInMilis)
	{
		_isWaiting = true;
		addTimer(WAIT_TIMER_ID, timeInMilis);
	}

	public void setMood(String mood)
	{
		say("Changing my mood [" + _mood + "] -> [" + mood + "]");
		_mood = mood;
	}

	public String getMood()
	{
		return _mood;
	}

	public boolean startCopyMovement()
	{
		_l1:
		for (int copyObjId : LocationStorage.getLocationsKeys())
		{
			// Keep looping until a free key is found.
			for (Player plr : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (plr.isPhantom() && ((PhantomPlayerAI) plr.getAI()).getCopyMovement() == copyObjId)
				{
					continue _l1;
				}
			}

			_locationCopyPlayerObjId = copyObjId;
			return true;
		}

		return false;
	}

	public int getCopyMovement()
	{
		return _locationCopyPlayerObjId;
	}

	protected void copyMove()
	{
		if (_locationCopyPlayerObjId > 0)
		{
			Location loc = LocationStorage.getLocationAround(_locationCopyPlayerObjId, _locationCopyIndex, getActor().getLoc(), 1500, 25);
			if (loc != null)
			{
				addTaskMove(loc, 0, true);
			}
			else // Fuck this, he prolly TPed out.
			{
				loc = LocationStorage.getLocation(_locationCopyPlayerObjId, _locationCopyIndex);
				if (loc != null) // Yep, definatelly TP
				{
					getActor().teleToLocation(loc); // TODO: Soe? hmm?
				}
			}
		}
	}

	@Override
	protected void onIntentionActive()
	{
		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
	}

	/**
	 * @return true : block thinkAttack() execution <br> false : if the AI should continue and execute thinkAttack()
	 */
	@Override
	protected boolean thinkActive()
	{
		try
		{
			if (!_tasks.isEmpty())
			{
				doTask();
				return true;
			}

			if (_isWaiting)
			{
				return true;
			}

			final Player actor = getActor();
			Skill skillCast = null;
			// int hpDiff = (int) (actor.getMaxHp() - actor.getCurrentHp());
			int mpDiff = (int) (actor.getMaxMp() - actor.getCurrentMp());
			// int cpDiff = (int) (actor.getMaxCp() - actor.getCurrentCp());

			// if (actor.getCurrentHpPercents() < 30) // HP lower than 30%
			// usePotion(5); // Elixir Life
			// if (actor.getCurrentCpPercents() < 30) // CP lower than 30%
			// usePotion(7); // Elixir CP
			// if (hpDiff >= 500)
			// usePotion(0); // HP Potion
			if (mpDiff >= 1000)
			{
				usePotion(1); // MP Potion*
			}

			if (actor.getLastAttackedTime() >= 60000)
			{
				// Something interrupted the soe... cast again when not attacked.
				if (_mood.startsWith("cast soe"))
				{
					int soeType = Integer.parseInt(_mood.substring(9));
					castSOE(soeType);
					return true;
				}

				// Buffs will wear-off in 20 minutes. SOE to rebuff.
				if (getRemaningBuffTime(true) < 2400 && actor.getEffectList().getAllFirstEffects().length < Config.ALT_BUFF_LIMIT)
				{
					castSOE(-1); // Normal SOE
					return true;
				}
			}

			// Equip weapon noob!
			if (actor.getActiveWeaponItem() == null)
			{
				equipWeapon();
			}

			// Use Escape skill. Check for physical/magical root is missing.
			if (actor.isRooted())
			{
				skillCast = actor.getAvailableSkill(453); // Escape Shackle
				if (skillCast != null)
				{
					addTaskCast(actor, skillCast);
				}
				else
				{
					skillCast = actor.getAvailableSkill(461); // Break Duress
					if (skillCast != null && actor.getIncreasedForce() >= skillCast.getLevel() + 1)
					{
						addTaskCast(actor, skillCast);
					}
				}
			}

			// Charge max!!!
			if (actor.getClassId() == ClassId.gladiator || actor.getClassId() == ClassId.tyrant || actor.getClassId() == ClassId.duelist || actor.getClassId() == ClassId.grandKhauatari)
			{
				skillCast = actor.getAvailableSkill(8); // Sonic Focus
				if (skillCast == null)
				{
					skillCast = actor.getAvailableSkill(50); // Focused Force
				}
				if (skillCast != null && skillCast.getLevel() > actor.getIncreasedForce()) // Time to charge
				{
					addTaskCast(actor, skillCast);
					return true;
				}
			}

			// Heal somehow if can.
			if (actor.getCurrentHpPercents() < 70)
			{
				// If there is a target already, use drain skill if possible.
				if (actor.getTarget() != null && actor.getTarget().isAttackable(actor))
				{
					for (Skill skill : actor.getAllAvailableSkillsArray(SkillType.DRAIN))
					{
						double absorb = skill.getAbsorbPart() * calculateDPS(skill, (Creature) actor.getTarget());
						if (absorb >= 400)
						{
							addTaskCast((Creature) actor.getTarget(), skill);
						}
					}
				}

				// No skill to cast - no target.
				if (skillCast == null)
				{
					actor.setTarget(null);
				}

				// Check for in-combat heal. Use faster heals there.
				int maxCastTime = 2000;
				if (actor.isLastAttackedIn(2000))
				{
					maxCastTime = 500;
				}

				for (Skill skill : actor.getAllAvailableSkillsArray(SkillType.HEAL, SkillType.HEAL_PERCENT, SkillType.CHAIN_HEAL))
				{
					int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime(getActor()) : Formulas.calcMAtkSpd(getActor(), skill, skill.getHitTime(getActor()));
					if (skillTime <= maxCastTime)
					{
						addTaskCast(actor, skill);
						return true;
					}
				}
			}

			if (getMood().equalsIgnoreCase("farming"))
			{
				if (actor.isInPeaceZone() && _roamingTask == null)
				{
					startRoamingInTown();
					return true;
				}

				// Find a target.
				if (actor.getTarget() == null || !actor.getTarget().isCreature() || !actor.checkTarget((Creature) actor.getTarget()))
				{
					actor.setTarget(getTarget());

					if (actor.getTarget() != null && (actor.getDistance(actor.getTarget()) >= 2000 || !GeoEngine.canSeeTarget(actor, actor.getTarget(), false)))
					{
						// If target is too far, move to it.
						addTaskMove(actor.getTarget().getLoc(), 600, true);
						return true;
					}
				}

				/*
				 * disable level difference.
				 * int maxLevelDiff = 10;
				 * if (actor.getLevel() > 80)
				 * maxLevelDiff = 3;
				 * else if (actor.getLevel() > 50)
				 * maxLevelDiff = 5;
				 * && (actor.getLevel() - ((Creature) actor.getTarget()).getLevel() > 4 || ((Creature) actor.getTarget()).getLevel() - actor.getLevel() > maxLevelDiff)
				 */

				// Cant farm in this area >.< SOE...
				if (actor.getTarget() != null && actor.getTarget().isPlayer())
				{
					// Why this check? Imagine someone casting SOE in start areas, it will result in a WTF moment :D
					Location soeTarget = Location.getRestartLocation(actor, RestartType.TO_VILLAGE);
					if (Location.getDistance(soeTarget, actor.getLoc()) >= 3000)
					{
						castSOE(0);
						return true;
					}
					else
					{
						castSOE(-1);
						return true;
					}

				}

				// No target found? Roam a bit.
				if (actor.getTarget() == null)
				{
					// Dont roam like a chicken in peace zones.
					if (actor.isInPeaceZone())
					{
						return true;
					}

					/*
					 * Location randomLocation = getRandomLocation(300, 500, 200);
					 * if (randomLocation == null)
					 * randomLocation = getRandomLocation(200, 300, 200);
					 * if (randomLocation == null)
					 * randomLocation = getRandomLocation(100, 200, 200);
					 * if (randomLocation != null)
					 * {
					 * addTaskMove(randomLocation, 0, true);
					 * return true;
					 * }
					 * else // Fuck this... SOE out.
					 */
					{
						castSOE(-1);
						return true;
					}
				}
			}

			// No valid target found? Do nothing
			if (actor.getTarget() == null || !actor.getTarget().isCreature())
			{
				return true;
			}

			Creature target = (Creature) actor.getTarget();
			double highestDps = actor.getClassId().isMage() ? 0 : calculateDPS(null, (Creature) actor.getTarget());

			// Mages should keep range
			if (actor.getClassId().isMage())
			{
				int distance = (int) Location.calculateDistance(actor, target, false);
				int actorSpeed = actor.getSpeed(actor.getTemplate().getBaseRunSpd());
				int targetSpeed = target.getSpeed(target.getTemplate().getBaseRunSpd());
				int speedDiff = actorSpeed - targetSpeed;

				// Target is faster, better slow it if near the range. TODO Support for targets running away.
				/*
				 * if (speedDiff < 0 && distance <= 600 && target.getEffectList().getEffectByStackType("RunSpeedDown") == null)
				 * {
				 * // Find the best slow skill
				 * int slowChance = 0;
				 * for (Skill skill : actor.getAllAvailableSkillsArray(SkillType.DEBUFF))
				 * {
				 * for (EffectTemplate et : skill.getEffectTemplates())
				 * {
				 * if ("RunSpeedDown".equalsIgnoreCase(et._stackType))
				 * {
				 * int slow = (int) Formulas.calcSkillSuccessChance(actor, target, skill);
				 * if (slowChance < slow)
				 * {
				 * slowChance = slow;
				 * skillCast = skill;
				 * }
				 * }
				 * }
				 * }
				 * if (skillCast != null)
				 * {
				 * if (slowChance >= 25) // Else... meh, not worthy to cast it.
				 * {
				 * say("Going to slow with " + skillCast.getName() + "(" + slowChance + ")");
				 * setIntention(CtrlIntention.AI_INTENTION_CAST, skillCast, target);
				 * return true;
				 * }
				 * else
				 * say("Skipping slow with " + skillCast.getName() + "(" + slowChance + ") chance is too low.");
				 * }
				 * skillCast = null; // Skill not used, make it null again.
				 * }
				 */

				// Target is slower or range is more than 600 (slowskills range)
				int reuse = 0;
				int maxAllowedReuse = distance > 900 ? 0 : 1000; // If target is too far, dont run/stay while waiting for skill reuse.
				for (Skill skill : actor.getAllSkillsArray())
				{
					// Only active magic skills with cast range bigger than 600 allowed.
					if (!skill.isActive() || !skill.isMagic() || skill.getCastRange() < 600 || !skill.checkCondition(actor, target, false, false, false, true, true, true, true))
					{
						continue;
					}

					reuse = (int) (actor.isSkillDisabled(skill) ? actor.getSkillReuse(skill).getReuseCurrent() : 0);
					if (reuse > maxAllowedReuse) // Dont wait more than expected for skill's reuse.
					{
						continue;
					}

					double dps = calculateDPS(skill, target);
					if (dps > highestDps)
					{
						highestDps = dps;
						skillCast = skill;
					}
				}

				// Run until the skill reuses. TODO: When reuse ends, stop running and cast skill.
				reuse = (int) (skillCast == null ? 500 : actor.isSkillDisabled(skillCast) ? actor.getSkillReuse(skillCast).getReuseCurrent() : 0);
				if (reuse > 0 && distance < 1200) // Dont run if target is too far.
				{
					int timeToWaitInMilis = skillCast == null ? reuse : Formulas.calcMAtkSpd(getActor(), skillCast, skillCast.getHitTime(getActor())) + reuse;
					runAway(target.getLoc(), timeToWaitInMilis);
				}

				if (skillCast != null)
				{
					say("I chose " + skillCast.getName() + " because its DPS is " + (int) highestDps);
				}

				addTaskCast(target, skillCast);

				return true; // Mages should never autoattack.
			}
			else // Fighters...
			{
				// lets roll chance to select skill...
				if (Rnd.chance(10))
				{
					// Check for physical skills to execute.
					for (Skill skill : actor.getAllAvailableSkillsArray())
					{
						if (!skill.isActive() || !skill.isOffensive() || !skill.checkCondition(actor, target, false, false, true) || skill.getSkillType() == SkillType.AGGRESSION || skill.getSkillType() == SkillType.INSTANT_JUMP)
						{
							continue;
						}

						double dps = calculateDPS(skill, target);
						if (dps > highestDps)
						{
							highestDps = dps;
							skillCast = skill;
						}
					}
				}

				say("I chose " + (skillCast == null ? "Attack" : skillCast.getName()) + " because its DPS is " + (int) highestDps);
			}

			if (target != null)
			{
				if (skillCast != null)
				{
					say("Going to cast " + skillCast.getName());
					addTaskCast(target, skillCast);
					return true;
				}
				else
				{
					say("Going to attack");
					addTaskAttack(target);
				}
			}
		}
		catch (Exception e)
		{
			if (_thinkActiveExceptions >= 15)
			{
				stopAITask();

				if (getActor() != null)
				{
					PhantomPlayers.terminatePhantom(getActor().getObjectId(), false, false);
				}

				_log.warn("BREAKING AI!!! Phantom: Exception during thinkActive() for phantom: " + (getActor() == null ? "NULL?!?!" : getActor().getName()), e);
				return true;
			}
			_log.warn("Phantom: Exception during thinkActive() for phantom: " + (getActor() == null ? "NULL?!?!" : getActor().getName()), e);
			_thinkActiveExceptions++;
		}
		return false;
	}

	@Override
	protected void thinkAttack(boolean checkRange)
	{
		if (!thinkActive())
		{
			super.thinkAttack(checkRange);
		}
	}

	@Override
	protected void thinkCast(boolean checkRange)
	{
		super.thinkCast(checkRange);
	}

	protected Creature getTarget()
	{
		try
		{
			final LinkedList<Creature> lowPriorityTargets = new LinkedList<>();
			final List<Creature> list = World.getAroundCharacters(getActor()).stream().filter(Objects::nonNull).filter(t -> !t.isRaid() && !t.isDead() && !t.isMinion() && !(t instanceof GuardInstance) && !t.isInvisible() && !t.isInvul() && t.isMonster() && !t.isTreasureChest()).sorted(targetComparator).collect(Collectors.toList());

			// Find a valid target to attack.
			OUT:
			for (Creature tgt : list)
			{
				for (Player player : tgt.getAroundPlayers())
				{
					if (player.isGM() || player.isInvisible())
					{
						continue;
					}

					if (player.getTarget() == tgt && tgt.getCurrentHpPercents() < 99)
					{
						continue OUT;
					}
				}

				if (System.currentTimeMillis() - tgt.getLastAttackedTime() < 5000) // Target attacked in the last 5 secs.
				{
					if (tgt.getAggressionTarget() != null && tgt.getAggressionTarget() != getActor())
					{
						continue;
					}
				}
				if (getActor().checkTarget(tgt))
				{
					// if (phantom.getLevel() - tgt.getLevel() > 5)
					lowPriorityTargets.add(tgt);
				}
				else if (Location.getDistance(getActor(), tgt) >= 2000)
				{ // CheckTarget failed due to distance.
					lowPriorityTargets.add(tgt);
				}
				else if (!GeoEngine.canSeeTarget(getActor(), tgt, false)) // CheckTarget failed due to geodata check.
				{
					lowPriorityTargets.add(tgt);
				}
			}

			lowPriorityTargets.sort(targetComparator);

			return lowPriorityTargets.isEmpty() ? null : lowPriorityTargets.getFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @return : A visible random location nearby. Null if it fails to find such.
	 */
	protected Location getRandomLocation(int minRange, int maxRange, int maximumAttemptsToFindSuchLocation)
	{
		int x = getActor().getX();
		int y = getActor().getY();
		int z = getActor().getZ();
		int geoIndex = getActor().getGeoIndex();

		for (int i = 0; i < maximumAttemptsToFindSuchLocation; i++)
		{
			Location tmp = getActor().getLoc().coordsRandomize(minRange, maxRange);
			if (GeoEngine.canMoveToCoord(x, y, z, tmp.x, tmp.y, tmp.z, geoIndex))
			{
				return tmp;
			}
		}

		return null;
	}

	protected Location getRandomLocation(Location loc, int minRange, int maxRange, int maximumAttemptsToFindSuchLocation)
	{
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		int geoIndex = getActor().getGeoIndex();

		for (int i = 0; i < maximumAttemptsToFindSuchLocation; i++)
		{
			Location tmp = loc.coordsRandomize(minRange, maxRange);
			if (GeoEngine.canMoveToCoord(x, y, z, tmp.x, tmp.y, tmp.z, geoIndex))
			{
				return tmp;
			}
		}

		return null;
	}

	public void runAway(Location fromLoc, int timeInMilis)
	{
		int posX = getActor().getX();
		int posY = getActor().getY();
		int posZ = getActor().getZ();

		int old_posX = posX;
		int old_posY = posY;
		int old_posZ = posZ;

		int signx = posX < fromLoc.getX() ? -1 : 1;
		int signy = posY < fromLoc.getY() ? -1 : 1;

		int range = (int) (0.71 * timeInMilis / 1000 * getActor().getMoveSpeed());

		posX += signx * range;
		posY += signy * range;
		posZ = GeoEngine.getHeight(posX, posY, posZ, getActor().getGeoIndex());

		if (GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, getActor().getGeoIndex()))
		{
			addTaskMove(new Location(posX, posY, posZ), 0);
		}
	}

	private class RoamingTask implements Runnable
	{
		private final Player _player;
		private boolean _offlineShopsChecked = false;
		private int _warehouseChecked = 0;
		private int _shopChecked = 0;
		private int _npcChecked = 0;

		int maxChecksWh = Config.PHANTOM_ROAMING_MAX_WH_CHECKS; // Dwarfs check warehouse more often
		int maxChecksShop = Config.PHANTOM_ROAMING_MAX_SHOP_CHECKS; // Dwarfs check shops more often

		public RoamingTask(Player player)
		{
			// System.out.println("Initialized: " + player + " - " + this);
			if (_roamingTask != null)
			{
				_player = null;
			}
			else
			{
				_player = player;
			}

			maxChecksWh = _player.getRace() == Race.dwarf ? Config.PHANTOM_ROAMING_MAX_WH_CHECKS_DWARF : Config.PHANTOM_ROAMING_MAX_WH_CHECKS; // Dwarfs check warehouse more often
			maxChecksShop = _player.getRace() == Race.dwarf ? Config.PHANTOM_ROAMING_MAX_SHOP_CHECKS_DWARF : Config.PHANTOM_ROAMING_MAX_SHOP_CHECKS; // Dwarfs check shops more often
		}

		@Override
		public void run()
		{
			// if (_roamingTask != null)
			// stopRoamingTask();

			if (_farmingTask != null)
			{
				stopFarmTask();
			}

			if (_player == null || !_player.getAI().isPhantomPlayerAI())
			{
				return;
			}

			PhantomPlayerAI ai = (PhantomPlayerAI) _player.getAI();
			// System.out.println(_player + " - " + ai.getMood());

			if (ai.getMood().equals(""))
			{
				if (ai.isInTown())
				{
					startRoamingInTown();
					// else
					// ai.startFarming();
				}
			}

			if (ai.getMood().equalsIgnoreCase("farming"))
			{
				if (ai.getActor().isInPeaceZone())
				{
					startRoamingInTown();
					return;
				}

				if (ai.getActor().getEffectList().isEmpty())
				{
					ai.buffFromNpcBuffer(0);
				}
				ai.startFarming();
				return;
			}

			// else if is roaming let give him some buffs...
			if (Rnd.chance(50))
			{
				if (ai.getActor().getEffectList().isEmpty())
				{
					ai.buffFromNpcBuffer(0);
				}
			}

			// Activate shots if not activated.
			if (getActor().getAutoSoulShot().isEmpty())
			{
				EnterWorld.verifyAndLoadShots(getActor());
			}

			_player.sendChanges();

			// Check if roaming has finished and start farming.
			if (_offlineShopsChecked && _warehouseChecked >= maxChecksWh && _shopChecked >= maxChecksShop && _npcChecked >= Config.PHANTOM_ROAMING_MAX_NPC_CHECKS)
			{
				// Teleport to another town
				// TODO: support for visited towns to not repeat...
				List<Location> locs = new ArrayList<Location>();
				for (Entry<String, Location> entry : TOWN_LOCATIONS.entrySet())
				{
					// String townName = entry.getKey();
					Location loc = entry.getValue();

					locs.add(loc);
				}

				Location tploc = Rnd.get(locs);
				if (tploc != null)
				{
					_player.teleToLocation(tploc);
					say("Telporting to: " + tploc);
				}
				else
				{
					say("Cannot find location... im shutting down...");
					PhantomPlayers.terminatePhantom(_player.getObjectId(), false, false);
				}
				return;
			}

			long delay = 0;
			Location loc = null;
			Creature target = null;
			int rnd = 100; // 100 wont trigger anything

			if (ai.getMood().contains("offline shops"))
			{
				if (!_offlineShopsChecked)
				{
					List<Player> list = new ArrayList<Player>();
					for (Player plr : _player.getAroundPlayers())
					{
						if (plr != null && !plr.isInZone(ZoneType.RESIDENCE) && plr.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
						{
							list.add(plr);
						}
					}

					int rndSize = 1;

					if (ai.getMood().startsWith("slow"))
					{
						rndSize = Math.max(1, list.size() / 2);
					}
					else if (ai.getMood().startsWith("normal"))
					{
						rndSize = Math.max(1, list.size() / 4);
					}
					else if (ai.getMood().startsWith("quick"))
					{
						rndSize = Math.max(1, list.size() / 8);
					}

					if (Rnd.get(rndSize) == 0) // Check for as much as there are players there
					{
						rnd = Rnd.get(50, 99); // Break the offline shops mood
						_offlineShopsChecked = true;
					}

					if (Rnd.chance(1))
					{
						delay = Rnd.get(5, 10) * 60000; // For some reason this guy is afk for 5-10 mins :)
					}

					delay += Rnd.get(Config.PHANTOM_ROAMING_MIN_PRIVATESTORE_DELAY, Config.PHANTOM_ROAMING_MAX_PRIVATESTORE_DELAY) * 1000; // 2 to 7 seconds in delay to check a shop.

					// loc = plr.getLoc();
					Collections.shuffle(list);
					Player plr = Rnd.get(list);
					if (plr != null)
					{
						target = plr;
					}

					say("Going to check offline shop -> " + target.getName());
				}

				if (target == null) // No offline shop found?
				{
					rnd = Rnd.get(50, 99); // Break the offline shops mood
					_offlineShopsChecked = true;
				}
			}
			else if (ai.getMood().contains("check warehouse"))
			{
				if (_warehouseChecked < maxChecksWh)
				{
					List<NpcInstance> list = new ArrayList<NpcInstance>();
					for (NpcInstance npc : World.getAroundNpc(_player, Config.PHANTOM_MAX_DRIFT_RANGE, 200))
					{
						if (npc != null && !npc.isInZone(ZoneType.RESIDENCE) && npc instanceof WarehouseInstance && Util.contains(Config.PHANTOM_ALLOWED_NPC_TO_WALK, npc.getNpcId()) && !npc.getTitle().startsWith("Clan Hall"))
						{
							list.add(npc);
						}
					}

					// Nothing found skip this....
					if (list.isEmpty())
					{
						_warehouseChecked = maxChecksWh;
					}

					// loc = npc.getLoc();
					target = Rnd.get(list);

					_warehouseChecked++;

					if (Rnd.chance(10))
					{
						delay = Rnd.get(5, 15) * 60000; // For some reason this guy is afk for 5-15 mins :)
					}

					delay += Rnd.get(Config.PHANTOM_ROAMING_MIN_WH_DELAY, Config.PHANTOM_ROAMING_MAX_WH_DELAY) * 1000; // 1 to 5 minutes in delay to check a wh.

					say("Going to check warehouse NPC -> " + target.getName());
				}

				// WH is to be checked once.
				rnd = Rnd.get(100);
			}
			else if (ai.getMood().contains("check shop"))
			{
				if (_shopChecked < maxChecksShop)
				{
					List<NpcInstance> list = new ArrayList<NpcInstance>();
					for (NpcInstance npc : World.getAroundNpc(_player, Config.PHANTOM_MAX_DRIFT_RANGE, 200))
					{
						if (npc != null && !npc.isInZone(ZoneType.RESIDENCE) && npc instanceof MerchantInstance && Util.contains(Config.PHANTOM_ALLOWED_NPC_TO_WALK, npc.getNpcId()) && !npc.getTitle().startsWith("Clan Hall"))
						{
							list.add(npc);
						}
					}

					// Nothing found skip this....
					if (list.isEmpty())
					{
						_shopChecked = maxChecksShop;
					}

					// loc = npc.getLoc();
					target = Rnd.get(list);

					_shopChecked++;

					if (Rnd.chance(3))
					{
						delay = Rnd.get(5, 20) * 60000; // For some reason this guy is afk for 5-20 mins :)
					}

					delay += Rnd.get(Config.PHANTOM_ROAMING_MIN_SHOP_DELAY, Config.PHANTOM_ROAMING_MAX_SHOP_DELAY) * 1000; // 30s to 2 minutes in delay to check a shop.

					say("Going to check shop NPC -> " + target.getName());
				}

				// Shops are to be checked rarely.
				if (Rnd.chance(33))
				{
					rnd = Rnd.get(100); // New mood time...
				}
			}
			else if (ai.getMood().contains("check npcs"))
			{
				if (_npcChecked < Config.PHANTOM_ROAMING_MAX_NPC_CHECKS)
				{
					if (Rnd.chance(15)) // 15% chance for:
					{
						rnd = Rnd.get(100); // New mood time...
					}

					List<NpcInstance> list = new ArrayList<NpcInstance>();
					for (NpcInstance npc : World.getAroundNpc(_player, Config.PHANTOM_MAX_DRIFT_RANGE, 200))
					{
						if (npc != null && !npc.isMonster() && !npc.isRaid() && !npc.isBoss() && !npc.isInZone(ZoneType.RESIDENCE) && Util.contains(Config.PHANTOM_ALLOWED_NPC_TO_WALK, npc.getNpcId()) && !npc.getTitle().startsWith("Clan Hall"))
						{
							list.add(npc);
						}
					}

					// Nothing found skip this....
					if (list.isEmpty())
					{
						_npcChecked = Config.PHANTOM_ROAMING_MAX_NPC_CHECKS;
					}

					// loc = npc.getLoc();
					target = Rnd.get(list);

					_npcChecked++;

					if (Rnd.chance(3))
					{
						delay = Rnd.get(5, 10) * 60000; // For some reason this guy is afk for 5-10 mins :)
					}

					delay += Rnd.get(Config.PHANTOM_ROAMING_MIN_NPC_DELAY, Config.PHANTOM_ROAMING_MAX_SHOP_DELAY) * 1000; // 45s to 2 minutes in delay to check a npc.

					say("Going to check random NPC -> " + target.getName());
				}

				if ((loc == null && target == null) || Rnd.chance(25)) // Dont go to many NPCs at a time.
				{
					rnd = Rnd.get(100); // New mood time...
				}
			}
			else if (ai.getMood().contains("free roam"))
			{
				int chance = 10;
				List<Player> list = World.getAroundPlayers(_player);
				if (list.size() < 3)
				{
					chance = 100;
				}
				else if (list.size() < 6)
				{
					chance = 80;
				}
				else if (list.size() < 10)
				{
					chance = 60;
				}
				else if (list.size() < 20)
				{
					chance = 30;
				}
				else if (list.size() < 30)
				{
					chance = 20;
				}

				if (Rnd.chance(chance))
				{
					rnd = Rnd.get(100); // New mood time...
				}

				// Fuck this, no free roam... go follow players :D:D
				Collections.shuffle(list);
				Player plr = Rnd.get(list);
				if (plr != null)
				{
					target = plr;
				}

				if (Rnd.chance(3))
				{
					delay = Rnd.get(5, 30) * 60000; // For some reason this guy is afk for 5-30 mins :)
				}

				delay += Rnd.get(Config.PHANTOM_ROAMING_MIN_FREEROAM_DELAY, Config.PHANTOM_ROAMING_MAX_FREEROAM_DELAY) * 1000;

				say("Going to do some free roam... target -> " + target.getName());
			}
			else if (ai.getMood().contains("sit and relax") && Rnd.chance(20)) // 20% chance
			{
				loc = getRandomLocation(50, 120, 200);
				if (loc != null)
				{
					addTaskMove(loc, 50, true);
				}

				// now sit down
				target = null;
				if (_player.isSitting())
				{
					_player.standUp();
				}
				else
				{
					_player.sitDown(null);
				}

				delay = Rnd.get(5, 30) * 60000; // lets sit for 5-30 minutes.
				say("Going to relax a bit, lets sit down.");
			}

			if (target != null)
			{
				Location newloc = getRandomLocation(target.getLoc(), 40, 80, 200);
				if (newloc != null)
				{
					ai.addTaskMove(newloc, 50, true); // Move to NPC
				}
			}

			if (delay >= 300000) // More than 5min
			{
				ai.say("Going afk for " + delay / 60000 + " minutes after that.");
			}

			// Set new mood if so
			if (rnd < 50) // 40% Offline shops...
			{
				switch (Rnd.get(3))
				{
				case 0:
					ai.setMood("slow check offline shops");
					break;
				case 1:
					ai.setMood("normal check offline shops");
					break;
				case 2:
					ai.setMood("quick check offline shops");
					break;
				}
			}
			else if (rnd < 50) // 10% Warehouse...
			{
				ai.setMood("check warehouse");
			}
			else if (rnd < 70) // 10% Shops...
			{
				ai.setMood("check shop");
			}
			else if (rnd < 90) // 20% NPCs...
			{
				ai.setMood("check npcs");
			}
			else if (rnd < 100) // 10% Free roam...
			{
				ai.setMood("free roam");
			}

			// Wait the given delay before running the task again.
			_roamingTask = ThreadPoolManager.getInstance().schedule(this, delay);
		}

	}

	public void startRoamingInTown()
	{
		if (_farmingTask != null)
		{
			stopFarmTask();
		}

		if (_roamingTask != null)
		{
			stopRoamingTask();
		}

		setMood(Rnd.get(MOODS));
		_roamingTask = ThreadPoolManager.getInstance().schedule(new RoamingTask(getActor()), 30000); // lets wait 30sec before start move or etc..
	}

	public void startFarming()
	{
		if (_roamingTask != null)
		{
			stopRoamingTask();
		}

		_farmingTask = ThreadPoolManager.getInstance().schedule(new FarmingTask(getActor()), 5000);
	}

	public void stopFarmTask()
	{
		if (_farmingTask != null)
		{
			_farmingTask.cancel(true); // If Roams in town, it should interrupt it and set the mood to ""
		}

		_farmingTask = null;
	}

	public void stopRoamingTask()
	{
		if (_roamingTask != null)
		{
			_roamingTask.cancel(true); // If Roams in town, it should interrupt it and set the mood to ""
		}

		_roamingTask = null;
	}

	private boolean isInTown()
	{
		if (!getActor().isInPeaceZone())
		{
			return false;
		}

		for (Zone zone : getActor().getZones())
		{
			if (zone.getName().contains("talking_island_town_peace_zone") || zone.getName().contains("darkelf_town_peace_zone") || zone.getName().contains("elf_town_peace") || zone.getName().contains("guldiocastle_town_peace") || zone.getName().contains("gludin_town_peace") || zone.getName().contains("dion_town_peace") || zone.getName().contains("floran_town_peace") || zone.getName().contains("giran_town_peace") || zone.getName().contains("orc_town_peace")
						|| zone.getName().contains("dwarf_town_peace") || zone.getName().contains("oren_town_peace") || zone.getName().contains("hunter_town_peace") || zone.getName().contains("aden_town_peace") || zone.getName().contains("speaking_port_peace") || zone.getName().contains("gludin_port") || zone.getName().contains("giran_port") || zone.getName().contains("heiness_peace") || zone.getName().contains("godad_peace") || zone.getName().contains("rune_peace")
						|| zone.getName().contains("gludio_airship_peace") || zone.getName().contains("schuttgart_town_peace") || zone.getName().contains("kamael_village_town_peace") || zone.getName().contains("keucereus_alliance_base_town_peace") || zone.getName().contains("giran_harbor_peace_alt") || zone.getName().contains("parnassus_peace"))
			{
				return true;
			}
		}

		return false;
	}

	private class FarmingTask implements Runnable
	{
		private final Player _player;
		private boolean _isInterrupted = false;
		private int _farmingExceptions = 0;

		public FarmingTask(Player player)
		{
			_player = player;
		}

		@Override
		public void run()
		{
			// if (_farmingTask != null)
			// stopFarmTask();

			if (_roamingTask != null)
			{
				stopRoamingTask();
			}

			if (_player == null || !_player.getAI().isPhantomPlayerAI())
			{
				return;
			}

			PhantomPlayerAI ai = (PhantomPlayerAI) _player.getAI();

			try
			{
				ai.setMood("farming");

				if (ai.getActor().getEffectList().isEmpty())
				{
					ai.buffFromNpcBuffer(0);
					Thread.sleep(61000); // 1 min, as much as the buffs task.
				}

				if (ai.isInTown())
				{
					startRoamingInTown();
					return;
				}

				// Ok... check for buffs and shits.
				if (ai.isInTown())
				{
					// We need at least 2h buffs before start farming.
					for (int i = 0; i < 10; i++)
					{
						if (ai.getRemaningBuffTime(true) < 7000 || _player.getEffectList().getAllFirstEffects().length < Config.ALT_BUFF_LIMIT)
						{
							ai.say("Im going to farm, but I lack buffs. Buffing...");
							ai.buffFromNpcBuffer(4); // It will try only once more.
						}
						else
						{
							break;
						}

						Thread.sleep(61000); // 1 min, as much as the buffs task.
					}

					if (ai.getRemaningBuffTime(true) < 7000 || _player.getEffectList().getAllFirstEffects().length < Config.ALT_BUFF_LIMIT)
					{
						ai.say("Pff fuck it, I cant buff, going to farm anyway.");
					}

					// Isn't that nice? Going to the gatekeeper before going to farm :)
					for (NpcInstance npc : World.getAroundNpc(_player))
					{
						if (npc != null && npc.getTitle().equalsIgnoreCase("Gatekeeper"))
						{
							ai.addTaskMove(npc.getLoc().setH(npc.getHeading()), 50, true);
							Thread.sleep(Rnd.get(2, 20) * 1000); // 2-20 sec when using GK
							break;
						}
					}

					int maxLevelDiff = 7;
					if (_player.getLevel() > 80)
					{
						maxLevelDiff = 3;
					}
					else if (_player.getLevel() > 50)
					{
						maxLevelDiff = 5;
					}

					// Find hunting grounds
					List<Location> locs = new ArrayList<Location>();
					for (Entry<Integer, Location> entry : HUNTING_GROUNDS.entrySet())
					{
						int minLevel = entry.getKey();
						Location loc = entry.getValue();

						if ((_player.getLevel() >= 75 && minLevel >= 75) || (_player.getLevel() < minLevel + maxLevelDiff && _player.getLevel() >= minLevel))
						{
							locs.add(loc);
						}
					}

					Collections.sort(locs, new LocationCompare(_player));

					List<Creature> npcList = new ArrayList<Creature>();
					Location tpLoc = null;

					// Find nearest good location
					for (Location loc : locs)
					{
						npcList.clear();
						for (Creature npc : World.getAroundCharacters(loc, 5000))
						{
							if (!npc.isMonster() || npc.isRaid() || npc.isBoss())
							{
								continue;
							}

							if (npc.getLevel() - _player.getLevel() <= maxLevelDiff)
							{
								npcList.add(npc);
							}
						}

						if (!npcList.isEmpty())
						{
							tpLoc = loc;
							break;
						}
					}

					// Teleport near some NPC that isnt watched by player
					for (Creature npc : npcList)
					{
						boolean playerNearby = false;
						for (Creature cha : npc.getAroundCharacters(2000, 200))
						{
							if (cha.isPlayer())
							{
								playerNearby = true;
								break;
							}
						}

						if (!playerNearby)
						{
							tpLoc = npc.getLoc().coordsRandomize(200);
						}
					}

					// Tele to appropreate level zone.
					if (tpLoc != null)
					{
						_player.teleToLocation(tpLoc);
					}
					else
					{
						ai.say("Couldnt find any farm location :( Will roam in town instead.");
						ai.startRoamingInTown();
					}
				}
			}
			catch (InterruptedException ie)
			{
				_isInterrupted = true;
			}
			catch (Exception e)
			{
				_farmingExceptions++;
				if (_farmingExceptions >= 10)
				{
					_log.warn("BREAKING AI!!! Phantom: Exception during FarmingTask for phantom: " + (_player == null ? "NULL?!?!" : _player.getName()), e);
					_isInterrupted = true;
				}
				else
				{
					_log.warn("Phantom: Exception during FarmingTask for phantom: " + (_player == null ? "NULL?!?!" : _player.getName()), e);
				}
			}
			finally
			{
				if (_isInterrupted)
				{
					ai.setMood("");
				}
			}
		}

	}

	private class LocationCompare implements Comparator<Location>
	{
		private final Player _player;

		private LocationCompare(Player player)
		{
			_player = player;
		}

		@Override
		public int compare(Location l1, Location l2)
		{
			if (l1 == null || l2 == null)
			{
				return 0;
			}

			return (int) (Location.calculateDistance(_player, l1, false) - Location.calculateDistance(_player, l2, false));
		}
	}

	/**
	 * Default: Specific location soe depending on the actor's level.
	 * @param soeType : 0 Norm; 1 Bless; 2 CH; 3 Castle; 4 Bless CH; 5 Bless Castle
	 */
	public void castSOE(int soeType)
	{
		// Clean any tasks
		_tasks.clear();
		Skill soe = null;
		switch (soeType)
		{
		case 0:
			soe = SOE;
			break;
		case 1:
			soe = SOE_BLESSED;
			break;
		case 2:
			soe = SOE_CLANHALL;
			break;
		case 3:
			soe = SOE_CASTLE;
			break;
		case 4:
			soe = SOE_BLESSED_CLANHALL;
			break;
		case 5:
			soe = SOE_BLESSED_CASTLE;
			break;
		default:
			if (getActor().getLevel() > 75)
			{
				soe = Rnd.get(new Skill[]
				{
					SOE_ADEN_CASTLE_TOWN,
					SOE_GLUDIN_VILLAGE,
					SOE_HUNTERS_VILLAGE,
					SOE_RUNE_TOWNSHIP,
					SOE_TOWN_OF_GIRAN,
					SOE_TOWN_OF_GODDARD,
					SOE_TOWN_OF_OREN
				});
			}
			else if (getActor().getLevel() > 60)
			{
				soe = Rnd.get(new Skill[]
				{
					SOE_ADEN_CASTLE_TOWN,
					SOE_HUNTERS_VILLAGE,
					SOE_RUNE_TOWNSHIP,
					SOE_TOWN_OF_GIRAN,
					SOE_TOWN_OF_GODDARD,
					SOE_TOWN_OF_OREN,
					SOE_TOWN_OF_SCHUTTGART
				});
			}
			else if (getActor().getLevel() > 40)
			{
				soe = Rnd.get(new Skill[]
				{
					SOE_ADEN_CASTLE_TOWN,
					SOE_HUNTERS_VILLAGE,
					SOE_TOWN_OF_GIRAN,
					SOE_TOWN_OF_OREN,
					SOE_TOWN_OF_SCHUTTGART,
					SOE_TOWN_OF_DION,
					SOE_HEINE
				});
			}
			else if (getActor().getLevel() > 20)
			{
				soe = Rnd.get(new Skill[]
				{
					SOE_HUNTERS_VILLAGE,
					SOE_TOWN_OF_GIRAN,
					SOE_TOWN_OF_DION,
					SOE_HEINE,
					SOE_TOWN_OF_GLUDIO,
					SOE_GLUDIN_VILLAGE
				});
			}
			else
			{
				switch (getActor().getRace())
				{
				case human:
					soe = SOE_TALKING_ISLAND_VILLAGE;
					break;
				case elf:
					soe = SOE_ELVEN_VILLAGE;
					break;
				case darkelf:
					soe = SOE_DARK_ELF_VILLAGE;
					break;
				case orc:
					soe = SOE_ORC_VILLAGE;
					break;
				case dwarf:
					soe = SOE_DWARVEN_VILLAGE;
					break;
				case kamael:
					soe = SOE_KAMAEL_VILLAGE;
					break;
				}
			}
		}
		setMood("cast soe " + soeType);
		getActor().doCast(soe, getActor(), false);
	}

	/**
	 *
	 * @param potionType : 0 GHP; 1 MP; 2 QHP; 3 GCP; 4 GQHP; 5 Elixir Life; 6 Greater EL; 7 Elixir CP; 8 Greater ECP
	 */
	public void usePotion(int potionType)
	{
		Skill pot = null;
		switch (potionType)
		{
		case 1:
			pot = MANA_POTION;
			break;
		case 2:
			pot = QUICK_HEALING_POTION;
			break;
		case 3:
			pot = GREATER_CP_POTION;
			break;
		case 4:
			pot = GREATER_QUICK_HEALING_POTION;
			break;
		case 5:
			pot = SkillTable.getInstance().getInfo(2287, getActor().getGrade() >= 5 ? 6 : getActor().getGrade() + 1); // Elixir of Life
			break;
		case 6:
			pot = SkillTable.getInstance().getInfo(2860, getActor().getGrade() >= 5 ? 6 : getActor().getGrade() + 1); // Greater Elixir of Life
			break;
		case 7:
			pot = SkillTable.getInstance().getInfo(2289, getActor().getGrade() >= 5 ? 6 : getActor().getGrade() + 1); // Elixir of CP
			break;
		case 8:
			pot = SkillTable.getInstance().getInfo(2862, getActor().getGrade() >= 5 ? 6 : getActor().getGrade() + 1); // Greater Elixir of CP
			break;
		default:
			pot = GREATER_HEALING_POTION;
		}
		getActor().doCast(pot, getActor(), false);
	}

	public void buffFromNpcBuffer(int curTries)
	{
		// if (!isInTown())
		// return;

		Util.communityNextPage(getActor(), "_bbsbufferbypass_giveBuffSet figher 0 0");
		Util.communityNextPage(getActor(), "_bbsbufferbypass_giveBuffs 1323 1 noble");
		Util.communityNextPage(getActor(), "_bbsbufferbypass_heal 0 0 0");

		// Fail buff, schedule another try in 30 sec.
		if (getActor().getPvpFlag() > 0 || getActor().isInCombat())
		{
			addTimer(BUFF_TIMER_ID, curTries + 1, 30000);
		}
		else
		{
			Util.communityNextPage(getActor(), "_bbsbufferbypass_giveBuffSet figher 0 0");
			Util.communityNextPage(getActor(), "_bbsbufferbypass_giveBuffs 1323 1 noble");
			Util.communityNextPage(getActor(), "_bbsbufferbypass_heal 0 0 0");
			if (getActor().getEffectList().getAllFirstEffects().length < Config.ALT_BUFF_LIMIT || getRemaningBuffTime(false) < 7000) // Just a check to see if there are any buffs
			{
				say("I tried to buff but it didn't buff me right :( I will try " + (5 - curTries) + " more times, next one in 1 min");
				addTimer(BUFF_TIMER_ID, curTries + 1, 60000);
			}
			else
			{ // Just a check to see if there are any buffs
				say("I have buffed.");
			}
		}
	}

	private int getRemaningBuffTime(boolean useCache)
	{
		// If this is called in the last 1 minute, it will use cache instead.
		if (useCache && System.currentTimeMillis() - _buffTimeLastCached <= 60000 && _buffTimeCache >= 0)
		{
			return _buffTimeCache;
		}

		int buffTimeLeft = 0;
		int buffsCount = 0;
		for (Effect e : getActor().getEffectList().getAllEffects())
		{
			if (e != null && e.getSkill().getSkillType() == SkillType.BUFF && e.getSkill().getTargetType() != SkillTargetType.TARGET_SELF)
			{
				buffTimeLeft += e.getTimeLeft();
				buffsCount++;
			}
		}

		_buffTimeLastCached = System.currentTimeMillis();

		// Whoops, almost forgot... I was going to create a black hole if I didnt do that :D
		if (buffsCount == 0)
		{
			_buffTimeCache = 0;
			return 0;
		}

		return _buffTimeCache = (buffTimeLeft / buffsCount);
	}

	protected void upgradeClass()
	{
		if (getActor().getClassId().getLevel() < 4 && // Not 3rd class
					((getActor().getLevel() >= 20 && getActor().getClassId().getLevel() == 1) || (getActor().getLevel() >= 40 && getActor().getClassId().getLevel() == 2) || (getActor().getLevel() >= 75 && getActor().getClassId().getLevel() == 3)))
		{

			List<ClassId> classesToChoseFrom = new ArrayList<ClassId>(2);
			for (ClassId clas : ClassId.VALUES)
			{
				// Check if class is banned.
				if (Util.contains(Config.PHANTOM_BANNED_CLASSID, clas.getId()))
				{
					continue;
				}

				if ((clas.getLevel() - getActor().getClassId().getLevel() == 1) && getActor().getClassId().childOf(clas))
				{
					classesToChoseFrom.add(clas);
				}
			}

			ClassId classId = Rnd.get(classesToChoseFrom);
			say("My class choice is from " + getActor().getClassId() + " to " + classId);
			getActor().setClassId(classId.getId(), false, false);
		}
	}

	protected void equipWeapon()
	{
		equipWeapon(PhantomPlayers.getAppropreateWeaponType(getActor()));
	}

	protected void equipWeapon(WeaponType weaponType)
	{
		// TODO: Check to pick the best weapon.
		// TODO: Fix infinite loop if no weapon is found.
		// Shoot :( Disarmed.
		if (getActor().isWeaponEquipBlocked())
		{
			return;
		}

		ItemInstance topWeapon = null;

		for (ItemInstance item : getActor().getInventory().getItems())
		{
			if (item.isWeapon() && ((WeaponTemplate) item.getTemplate()).getItemType() == weaponType)
			{
				if (topWeapon == null)
				{
					topWeapon = item;
				}
				else if (topWeapon.getTemplate().getReferencePrice() < item.getTemplate().getReferencePrice()) // A lazy way to find the better weapon
				{
					topWeapon = item;
				}
			}
		}

		// Pick any weapon, please.
		if (topWeapon == null)
		{
			for (ItemInstance item : getActor().getInventory().getItems())
			{
				if (item.isWeapon())
				{
					if (topWeapon == null)
					{
						topWeapon = item;
					}
					else if (topWeapon.getTemplate().getReferencePrice() < item.getTemplate().getReferencePrice()) // A lazy way to find the better weapon
					{
						topWeapon = item;
					}
				}
			}
		}

		say("Equipping weapon " + (topWeapon == null ? "NULL" : topWeapon.getName()));

		if (topWeapon != null)
		{
			getActor().getInventory().equipItem(topWeapon);
		}

		if (topWeapon == null)
		{
			getActor().getInventory().equipItem(ItemFunctions.createDummyItem(52));
		}

		activateShots();
	}

	public void activateShots()
	{
		Player actor = getActor();
		// Give shots and make them auto.
		int soulId = -1;
		int bspiritId = -1;

		if (actor.getActiveWeaponItem() != null)
		{
			switch (actor.getActiveWeaponItem().getCrystalType())
			{
			case NONE:
				soulId = 1835;
				bspiritId = 3947;
				break;
			case D:
				soulId = 1463;
				bspiritId = 3948;
				break;
			case C:
				soulId = 1464;
				bspiritId = 3949;
				break;
			case B:
				soulId = 1465;
				bspiritId = 3950;
				break;
			case A:
				soulId = 1466;
				bspiritId = 3951;
				break;
			case S:
			case S80:
			case S84:
				soulId = 1467;
				bspiritId = 3952;
				break;
			}

			// Soulshots
			if (soulId > -1)
			{
				long shotsCount = actor.getInventory().getCountOf(soulId);
				if (shotsCount < 10000)
				{
					actor.getInventory().addItem(soulId, 1000 - shotsCount, "Phantom");
				}
			}

			// Blessed Spirishots
			if (bspiritId > -1)
			{
				long shotsCount = actor.getInventory().getCountOf(bspiritId);
				if (shotsCount < 10000)
				{
					actor.getInventory().addItem(bspiritId, 10000 - shotsCount, "Phantom");
				}
			}

			EnterWorld.verifyAndLoadShots(actor);
		}
	}

	protected double calculateDPS(Skill skill, Creature target)
	{
		if (getActor() == null || target == null)
		{
			return 0;
		}

		double dps = 0;
		boolean dual = false;
		WeaponTemplate weaponItem = getActor().getActiveWeaponItem();

		if (weaponItem != null && (weaponItem.getItemType() == WeaponType.DUAL || weaponItem.getItemType() == WeaponType.DUALFIST || weaponItem.getItemType() == WeaponType.DUALDAGGER))
		{
			dual = true;
		}

		AttackInfo info = calcPhysDam(target, skill, dual, false/* TODO Blow support */);
		if (skill == null) // Autoattack DPS
		{
			int sAtk = Math.max(getActor().calculateAttackDelay(), 333);
			dps = (double) 1000 / sAtk;
			dps *= info.damage;
		}
		else
		{
			int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime(getActor()) : Formulas.calcMAtkSpd(getActor(), skill, skill.getHitTime(getActor()));
			if (skillTime <= 0) // WTF??? Thats not an active skill. NEXT!
			{
				return 0;
			}

			if (skill.isMagic())
			{
				double magicDam = calcMagicDam(target, skill);
				dps = (double) 1000 / skillTime;
				dps *= magicDam;
			}
			else
			{
				dps = 1000 / skillTime;
				dps *= info.damage;
			}
		}

		return dps;
	}

	protected double calcMagicDam(Creature target, Skill skill)
	{
		Player actor = getActor();
		boolean isPvP = target.isPlayable();

		int levelDiff = target.getLevel() - actor.getLevel();

		double mAtk = actor.getMAtk(target, skill);

		// Shots
		mAtk *= 4;

		double mdef = target.getMDef(null, skill);
		if (mdef == 0)
		{
			mdef = 1;
		}

		double power = skill.getPower(target);
		double lethalDamage = 0;

		if (power == 0)
		{
			return 0;
		}

		if (skill.isSoulBoost())
		{
			power *= 1.0 + 0.06 * Math.min(actor.getConsumedSouls(), 5);
		}

		double damage = 91 * power * Math.sqrt(mAtk) / mdef;
		damage = actor.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);

		if (damage > 1 && skill.isDeathlink())
		{
			damage *= 1.8 * (1.0 - actor.getCurrentHpRatio());
		}

		if (damage > 1 && skill.isBasedOnTargetDebuff())
		{
			damage *= 1 + 0.035 * target.getEffectList().getAllEffects().size();
		}

		damage += lethalDamage;

		if (skill.getSkillType() == SkillType.MANADAM)
		{
			damage = Math.max(1, damage / 4.);
		}

		if (isPvP && damage > 1)
		{
			damage *= actor.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null);
			damage /= target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null);
		}
		double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, actor, skill) - actor.calcStat(Stats.MAGIC_POWER, target, skill);
		double failChance = 4. * Math.max(1., levelDiff) * (1. + magic_rcpt / 100.);
		damage *= (failChance / 100); // This is not in the formula, but this way we can put fail chance in the damage calculation

		return damage;
	}

	protected AttackInfo calcPhysDam(Creature target, Skill skill, boolean dual, boolean blow)
	{
		Player actor = getActor();
		AttackInfo info = new AttackInfo();

		info.damage = actor.getPAtk(target);
		info.defence = target.getPDef(actor);
		info.crit_static = actor.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill);
		info.death_rcpt = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, actor, skill);
		info.miss = false;

		// lets not give to gladiators
		if (skill != null && !skill.isChargeBoost())
		{
			info.damage *= 1.10113;
		}

		boolean isPvP = target.isPlayable();
		if (skill != null)
		{
			if (skill.getPower(target) == 0)
			{
				info.damage = 0;
				return info;
			}

			if (blow && !skill.isBehind()) // Для обычных blow не влияет на power
			{
				info.damage *= 2.04; // 2.04
			}

			info.damage += Math.max(0., skill.getPower(target) * actor.calcStat(Stats.SKILL_POWER, 1, null, null));

			if (blow && skill.isBehind()) // Для обычных blow не влияет на power
			{
				info.damage *= 1.5; // 1.5
			}

			if (blow)
			{
				info.damage *= 0.01 * actor.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
				info.damage = target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, actor, skill);
				info.damage += 6.1 * info.crit_static;
			}

			if (skill.isChargeBoost())
			{
				info.damage *= 0.8 + 0.2 * actor.getIncreasedForce();
			}
			else if (skill.isSoulBoost())
			{
				info.damage *= 1.0 + 0.06 * Math.min(actor.getConsumedSouls(), 5);
			}

			// Gracia Physical Skill Damage Bonus
			info.damage *= 1.10113;
		}
		else if (dual)
		{
			info.damage /= 2.;
		}

		// Shots
		info.damage *= blow ? 1.0 : 2.0;

		info.damage *= 70. / info.defence;
		info.damage = actor.calcStat(Stats.PHYSICAL_DAMAGE, info.damage, target, skill);

		if (isPvP)
		{
			if (skill == null)
			{
				info.damage *= actor.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1, null, null);
			}
			else
			{
				info.damage *= actor.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1, null, null);
			}
		}

		if (skill != null)
		{
			if (info.damage > 1 && !skill.hasEffects())
			{
				info.damage *= (100 - (target.calcStat(Stats.PSKILL_EVASION, 0, actor, skill)) / 100); // Not in formula, but we have to take skill evas into account.
			}

			if (info.damage > 1 && skill.isDeathlink())
			{
				info.damage *= 1.8 * (1.0 - actor.getCurrentHpRatio());
			}
		}

		return info;
	}

	/**
	 *
	 * @return true : if the packet should be forwarded to the client (active players can be set to phantoms too)
	 */
	public boolean onPacketRecieved(IStaticPacket p)
	{
		return false;
	}

	@Override
	public Player getActor()
	{
		return super.getActor();
	}

	@Override
	public boolean isPhantomPlayerAI()
	{
		return true;
	}

	private void say(String text)
	{
		if (!Config.DEBUG_PHANTOMS)
		{
			return;
		}

		for (Player gm : GameObjectsStorage.getAllGMs())
		{
			if (gm != null && !gm.isBlockAll() && gm.isInRange(getActor(), 500))
			{
				gm.sendPacket(new Say2(getActor().getObjectId(), ChatType.ALL, getActor().getName(), "[" + Calendar.getInstance().get(Calendar.SECOND) + "]" + text));
			}
		}
	}

	@Override
	public void onMagicHit(Creature actor, Skill skill, Creature caster)
	{
		if (actor == caster)
		{
			// Check if SOE is casted.
			if (skill == SOE_BLESSED || skill == SOE_CLANHALL || skill == SOE_CASTLE || skill == SOE_BLESSED_CLANHALL || skill == SOE_BLESSED_CASTLE || skill == SOE || skill == SOE_TALKING_ISLAND_VILLAGE || skill == SOE_ELVEN_VILLAGE || skill == SOE_DARK_ELF_VILLAGE || skill == SOE_ORC_VILLAGE || skill == SOE_DWARVEN_VILLAGE || skill == SOE_GLUDIN_VILLAGE || skill == SOE_TOWN_OF_GLUDIO || skill == SOE_TOWN_OF_DION || skill == SOE_FLORAN_VILLAGE || skill == SOE_TOWN_OF_GIRAN
						|| skill == SOE_HARDIN_ACADEMY || skill == SOE_HEINE || skill == SOE_TOWN_OF_OREN || skill == SOE_IVORY_TOWER || skill == SOE_HUNTERS_VILLAGE || skill == SOE_HUNTERS_VILLAGE || skill == SOE_TOWN_OF_GODDARD || skill == SOE_RUNE_TOWNSHIP || skill == SOE_TOWN_OF_SCHUTTGART || skill == SOE_KETRA_ORC_VILLAGE || skill == SOE_VARKA_SILENOS_VILLAGE || skill == SOE_KAMAEL_VILLAGE)
			{
				setMood(""); // Unset the SOE cast mood.
			}
		}

	}

	protected static Skill SOE_BLESSED = SkillTable.getInstance().getInfo(2036, 1);
	protected static Skill SOE_CLANHALL = SkillTable.getInstance().getInfo(2040, 1);
	protected static Skill SOE_CASTLE = SkillTable.getInstance().getInfo(2041, 1);
	protected static Skill SOE_BLESSED_CLANHALL = SkillTable.getInstance().getInfo(2177, 1);
	protected static Skill SOE_BLESSED_CASTLE = SkillTable.getInstance().getInfo(2178, 1);
	protected static Skill SOE = SkillTable.getInstance().getInfo(2013, 1);

	protected static Skill SOE_TALKING_ISLAND_VILLAGE = SkillTable.getInstance().getInfo(2213, 1);
	protected static Skill SOE_ELVEN_VILLAGE = SkillTable.getInstance().getInfo(2213, 2);
	protected static Skill SOE_DARK_ELF_VILLAGE = SkillTable.getInstance().getInfo(2213, 3);
	protected static Skill SOE_ORC_VILLAGE = SkillTable.getInstance().getInfo(2213, 4);
	protected static Skill SOE_DWARVEN_VILLAGE = SkillTable.getInstance().getInfo(2213, 5);
	protected static Skill SOE_GLUDIN_VILLAGE = SkillTable.getInstance().getInfo(2213, 6);
	protected static Skill SOE_TOWN_OF_GLUDIO = SkillTable.getInstance().getInfo(2213, 7);
	protected static Skill SOE_TOWN_OF_DION = SkillTable.getInstance().getInfo(2213, 8);
	protected static Skill SOE_FLORAN_VILLAGE = SkillTable.getInstance().getInfo(2213, 9);
	protected static Skill SOE_TOWN_OF_GIRAN = SkillTable.getInstance().getInfo(2213, 10);
	protected static Skill SOE_HARDIN_ACADEMY = SkillTable.getInstance().getInfo(2213, 11);
	protected static Skill SOE_HEINE = SkillTable.getInstance().getInfo(2213, 12);
	protected static Skill SOE_TOWN_OF_OREN = SkillTable.getInstance().getInfo(2213, 13);
	protected static Skill SOE_IVORY_TOWER = SkillTable.getInstance().getInfo(2213, 14);
	protected static Skill SOE_HUNTERS_VILLAGE = SkillTable.getInstance().getInfo(2213, 15);
	protected static Skill SOE_ADEN_CASTLE_TOWN = SkillTable.getInstance().getInfo(2213, 16);
	protected static Skill SOE_TOWN_OF_GODDARD = SkillTable.getInstance().getInfo(2213, 17);
	protected static Skill SOE_RUNE_TOWNSHIP = SkillTable.getInstance().getInfo(2213, 18);
	protected static Skill SOE_TOWN_OF_SCHUTTGART = SkillTable.getInstance().getInfo(2213, 19);
	protected static Skill SOE_KETRA_ORC_VILLAGE = SkillTable.getInstance().getInfo(2213, 20);
	protected static Skill SOE_VARKA_SILENOS_VILLAGE = SkillTable.getInstance().getInfo(2213, 21);
	protected static Skill SOE_KAMAEL_VILLAGE = SkillTable.getInstance().getInfo(2213, 22);

	protected static Skill MANA_POTION = SkillTable.getInstance().getInfo(90001, 1);
	protected static Skill QUICK_HEALING_POTION = SkillTable.getInstance().getInfo(2038, 1);
	protected static Skill GREATER_CP_POTION = SkillTable.getInstance().getInfo(2166, 2);
	protected static Skill GREATER_QUICK_HEALING_POTION = SkillTable.getInstance().getInfo(2864, 1);
	protected static Skill GREATER_HEALING_POTION = SkillTable.getInstance().getInfo(2037, 1);

	@Override
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args)
	{
		switch (evt)
		{
		case EVT_DEAD:
		{
			final Player player = actor.getPlayer();
			int ressurectionDelay = Rnd.get(2, 10) * 1000; // This is here because I dont want instant revive. Lets make it look more player-like

			say("QQ im dead :( Going to village in " + ressurectionDelay / 1000 + " seconds.");

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					Location loc = null;
					Reflection ref = player.getReflection();

					if (ref == ReflectionManager.DEFAULT)
					{
						for (GlobalEvent e : player.getEvents())
						{
							loc = e.getRestartLoc(player.getPlayer(), RestartType.TO_VILLAGE);
						}
					}

					if (loc == null)
					{
						loc = Location.getRestartLocation(player.getPlayer(), RestartType.TO_VILLAGE);
					}

					if (loc != null)
					{
						player.teleToLocation(loc);
						player.getPlayer().doRevive(100); // Fuck it, 100% revive exp, cause bots cheat :D
					}
				}
			}, ressurectionDelay);
		}
			break;
		case EVT_ATTACKED:
		{
			Creature attacker = (Creature) args[0];
			int damage = (int) args[1];
			double dmgPer = damage / actor.getCurrentHp();
			if (attacker.isPlayer())
			{
				// Do not touch people in party.
				if (attacker.getPlayer().isInParty())
				{
					return;
				}

				int ggp = attacker.getPlayer().getGearScore();
				if (ggp <= actor.getPlayer().getGearScore() && dmgPer <= 0.2D) // If damage is less than 20% of total HP
				{
					// If casts skill with remaning cast of 3s or more, abort.
					if (actor.getCastingTime() > 3000)
					{
						actor.abortCast(false, false);
					}

					actor.setTarget(attacker);
					if (actor.isMoving())
					{
						thinkActive();
					}
					_isWaiting = false;
				}
			}
			else
			{
				// If casts skill with remaning cast of 3s or more, abort.
				if (actor.getCastingTime() > 3000)
				{
					actor.abortCast(false, false);
				}

				actor.setTarget(attacker);
				if (actor.isMoving())
				{
					thinkActive();
				}
				_isWaiting = false;
			}
		}
			break;
		case EVT_FORGET_OBJECT:
		{
			GameObject object = (GameObject) args[0];
			if (object.isPlayer() && object.isInvisible())
			{
				waitTime(10000);
			}
		}
			break;
		case EVT_SEE_SPELL:
		{
		}
			break;
		case EVT_TELEPORTED:
		{
			final Player player = actor.getPlayer();
			if (actor == null || !player.isPhantom() || !player.getAI().isPhantomPlayerAI())
			{
				return;
			}

			PhantomPlayerAI ai = ((PhantomPlayerAI) player.getAI());

			if (ai.isInTown())
			{
				ai.say("Lawl, a town. Time to buff, equip and change class if needed.");
				if (ai.getActor().getEffectList().isEmpty())
				{
					ai.buffFromNpcBuffer(0);
				}

				ai.say("Meh in town, will roam around.");
				ai.startRoamingInTown();
			}
			// else
			// {
			// //ai.castSOE(1);
			// ai.startFarming();
			// }
		}
			break;
		case EVT_TIMER:
		{
			int timerId = (int) args[0];
			switch (timerId)
			{
			case WAIT_TIMER_ID:
				_isWaiting = false;
				break;
			case BUFF_TIMER_ID:
				int tries = (int) args[1];
				if (tries < 5)
				{
					buffFromNpcBuffer(tries);
				}
				break;
			}
		}
			break;
		}

	}

	@Override
	public void onKill(Creature actor, Creature victim)
	{
		waitTime(Rnd.get(7, 15) * 100); // Wait 750-1500ms after kill, so it doesnt look like botting

		if (Rnd.chance(40)) // 40% chance to change loc a bit when killing
		{
			Location randomLocation = getRandomLocation(50, 120, 200);
			if (randomLocation != null)
			{
				addTaskMove(randomLocation, 50, true);
			}
		}
		else if (Rnd.chance(7)) // 7% chance to go a bit afk while farming
		{
			Location randomLocation = getRandomLocation(50, 200, 200);
			if (randomLocation != null)
			{
				addTaskMove(randomLocation, 50, true);
			}

			int time = Rnd.get(4, 10) * 1000;
			waitTime(time);
			say("Delaying for " + time / 1000 + " seconds. ");
		}
	}

	@Override
	public boolean ignorePetOrSummon()
	{
		return true;
	}

	public static Map<String, Location> TOWN_LOCATIONS = new HashMap<String, Location>();
	static
	{
		TOWN_LOCATIONS.put("Giran", new Location(83375, 147953, -3401));
		TOWN_LOCATIONS.put("Gludin", new Location(-80789, 149817, -3040));
		TOWN_LOCATIONS.put("Gludio", new Location(-12717, 122775, -3113));
		TOWN_LOCATIONS.put("Dion", new Location(15659, 142927, -2702));
		// TOWN_LOCATIONS.put("Heine", new Location(111394, 219350, -3542));
		TOWN_LOCATIONS.put("Hunter", new Location(117073, 76895, -2697));
		TOWN_LOCATIONS.put("Oren", new Location(82901, 53208, -14927));
		TOWN_LOCATIONS.put("Aden", new Location(146814, 25790, -2009));
		TOWN_LOCATIONS.put("Goddard", new Location(147941, -55278, -2729));
		TOWN_LOCATIONS.put("Rune", new Location(43782, -47707, -793));
		TOWN_LOCATIONS.put("Schuttgart", new Location(87092, -143381, -1289));
	}

	public static Map<Integer, Location> HUNTING_GROUNDS = new HashMap<Integer, Location>();
	static
	{
		/*
		 * HUNTING_GROUNDS.put(81, new Location(177672, -180088, 2016)); // Mithril Mines
		 * HUNTING_GROUNDS.put(81, new Location(177496, -174664, 1344));
		 * HUNTING_GROUNDS.put(81, new Location(175096, -184888, -1896));
		 * HUNTING_GROUNDS.put(83, new Location(83368, 93896, -3360)); // Oren Lizardman
		 * HUNTING_GROUNDS.put(83, new Location(90856, 89880, -3432));
		 * HUNTING_GROUNDS.put(83, new Location(91976, 82088, -3552));
		 * HUNTING_GROUNDS.put(83, new Location(82552, 75304, -3632));
		 * HUNTING_GROUNDS.put(new int[]{7,9}, new Location(-97448, 234392, -3520)); // Obelisk of Victory
		 * HUNTING_GROUNDS.put(new int[]{13,14}, new Location(-103960, 226472, -3648));
		 * HUNTING_GROUNDS.put(new int[]{13,14}, new Location(-100552, 213288, -3072));
		 * HUNTING_GROUNDS.put(new int[]{18,22}, new Location(173288, -211560, -3664));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(135752, -172584, -1776)); // Coal Mines
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(143496, -170360, -1776));
		 * HUNTING_GROUNDS.put(new int[]{15,17}, new Location(146632, -175016, -1520));
		 * HUNTING_GROUNDS.put(new int[]{7,9}, new Location(-26712, -120216, -2128)); // Immortal Pleatou North
		 * HUNTING_GROUNDS.put(new int[]{18,23}, new Location(6888, -117304, -1760)); // Cave of Trials
		 * HUNTING_GROUNDS.put(new int[]{18,23}, new Location(-424, -108184, -2784));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-17944, 49240, -3696)); // Swampland
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-63768, 78104, -3328)); // Spider Nest
		 * HUNTING_GROUNDS.put(new int[]{8,9}, new Location(-11800, 21672, -3640)); // Near Dark Omen Cata
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(26744, 64520, -3632)); // Above Elven Fortress
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(22872, 57048, -3360));
		 * HUNTING_GROUNDS.put(new int[]{14,15}, new Location(10424, 55608, -3440));
		 * HUNTING_GROUNDS.put(new int[]{17,18}, new Location(4392, 59720, -3488));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-3592, 81784, -3512)); // Neutral Zone
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-89352, 37352, -2256)); // Stronghold III
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-95288, 109528, -3568)); // Orc Barracks
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-97144, 112824, -3616));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-93624, 115336, -3376));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-89752, 115096, -3408));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-49848, 111112, -3568)); // Ruins of Agony
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-45016, 113160, -3808));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-21000, 139944, -3896)); // Ruins of Despair
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-18232, 144344, -3840));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-14392, 144200, -3616));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-17352, 188360, -4208)); // Wasteland
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-12904, 189752, -4160));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(-14856, 179656, -4192));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(55320, 141592, -2912)); // Execution Grounds
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(55288, 147544, -2856));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(53800, 150776, -2480));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(94536, 103416, -2944)); // Hardins Academy
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(85432, 98936, -3616));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(88536, 14728, -5264)); // Ivory Tower
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(82408, 17832, -5264));
		 * HUNTING_GROUNDS.put(new int[]{45,48}, new Location(93672, 27384, -3648)); // Near Ivory
		 * HUNTING_GROUNDS.put(new int[]{45,49}, new Location(81832, 3064, -2896)); // Outlaw Forest
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(79640, -1192, -3664)); // Outlaw Forest high lvl
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(80936, -6968, -2896));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(84664, -4136, -3232));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(92360, -16504, -2032));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(99128, -17992, -2896));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(113352, 36264, -3504)); // Enchanted Valley
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(120184, 45560, -3760));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(171192, 24200, -3392)); // Cemetary
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(179128, 23416, -3168));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(181032, 16440, -3168));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(141000, 92264, -3520)); // Forest of Mirrors
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(171688, 41256, -4920)); // Forsaken Plains
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(190328, 16488, -3672)); // Forbidden Gateway
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(188264, 8376, -2736));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(182888, 27176, -3712));
		 * HUNTING_GROUNDS.put(new int[]{65,69}, new Location(141464, -5912, -4784)); // Blazing Swamp
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(146828, -12859, -4464));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(142728, -22184, -3152));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(143256, -25784, -2080));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(80136, -40728, -3216)); // Swamp of Screams
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(89064, -52808, -2600)); // Swamp of Screams high
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(55304, -53032, -3320)); // Cursed Village
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(89432, -115272, -3312)); // Archaic Laboratory
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(93032, -120376, -4512)); // Pavel Ruins
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(171976, -49352, -3552)); // Wall of Agros
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(176664, 61672, -4368)); // Giants Cave
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(192008, 58632, -4808));
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(142650, -108944, -3568)); // Hot Springs
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(153320, 190280, -3696)); // Isle of Prayer
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(75416, -123272, -3264)); // Den of Evil
		 * HUNTING_GROUNDS.put(new int[]{}, new Location(563776, -81816, -2928)); // Beast Farm
		 */

		HUNTING_GROUNDS.put(25, new Location(50568, 152408, -2656)); // Execution Grounds\0
		HUNTING_GROUNDS.put(23, new Location(50081, 116859, -2176)); // Partisan's Hideaway\0
		HUNTING_GROUNDS.put(25, new Location(5106, 126916, -3664)); // Cruma Marshlands\0
		HUNTING_GROUNDS.put(20, new Location(38291, 148029, -3696)); // Mandragora Farm\0
		HUNTING_GROUNDS.put(46, new Location(58316, 163851, -2816)); // Tanor Canyon\0
		HUNTING_GROUNDS.put(34, new Location(34475, 188095, -2976)); // Bee Hive\0
		HUNTING_GROUNDS.put(20, new Location(29928, 151415, 2392)); // Dion Hills\0
		HUNTING_GROUNDS.put(30, new Location(10610, 156322, -2472)); // Floran Agricultural Area\0
		HUNTING_GROUNDS.put(23, new Location(630, 179184, -3720)); // Plains of Dion\0
		// HUNTING_GROUNDS.put(81, new Location(73024, 118485, -3720)); // Dragon Valley\0
		HUNTING_GROUNDS.put(35, new Location(67933, 117045, -3544)); // Death Pass\0
		HUNTING_GROUNDS.put(30, new Location(85546, 131328, -3672)); // Breka's Stronghold\0
		HUNTING_GROUNDS.put(31, new Location(113553, 134813, -3540)); // Gorgon Flower Garden\0
		HUNTING_GROUNDS.put(15, new Location(-19120, 136816, -3762)); // Ruins of Despair\0
		HUNTING_GROUNDS.put(15, new Location(-42628, 119766, -3528)); // Ruins of Agony\0
		HUNTING_GROUNDS.put(25, new Location(-16526, 208032, -3664)); // Wasteland\0
		HUNTING_GROUNDS.put(21, new Location(-49853, 147089, -2784)); // Abandoned Camp \0
		HUNTING_GROUNDS.put(24, new Location(-89763, 105359, -3576)); // Orc Barracks\0
		HUNTING_GROUNDS.put(26, new Location(-88539, 83389, -2864)); // Windy Hill\0
		HUNTING_GROUNDS.put(30, new Location(-44829, 188171, -3256)); // Red Rock Ridge\0
		HUNTING_GROUNDS.put(16, new Location(-44763, 203497, -3592)); // Langk Lizardmen Dwellings\0
		HUNTING_GROUNDS.put(24, new Location(-25283, 106820, -3416)); // Maille Lizardmen Barracks\0
		HUNTING_GROUNDS.put(8, new Location(-104344, 226217, -3616)); // Talking Island, Northern Territory\0
		HUNTING_GROUNDS.put(1, new Location(-95336, 240478, -3264)); // Talking Island, Eastern Territory\0
		HUNTING_GROUNDS.put(17, new Location(-63736, 101522, -3552)); // Fellmere Harvesting Grounds\0
		HUNTING_GROUNDS.put(15, new Location(-75437, 168800, -3632)); // Windmill Hill\0
		HUNTING_GROUNDS.put(19, new Location(-50174, 129303, -2912)); // Ruins of Agony Bend\0
		HUNTING_GROUNDS.put(14, new Location(-6989, 109503, -3040)); // Evil Hunting Grounds\0
		HUNTING_GROUNDS.put(19, new Location(-36652, 135591, -3160)); // Entrance to the Ruins of Despair\0
		HUNTING_GROUNDS.put(22, new Location(-28327, 155125, -3496)); // Windawood Manor\0
		HUNTING_GROUNDS.put(22, new Location(-6661, 201880, -3632)); // Ol Mahum Checkpoint\0
		HUNTING_GROUNDS.put(15, new Location(-10612, 75881, -3592)); // The Neutral Zone\0
		HUNTING_GROUNDS.put(8, new Location(21362, 51122, -3688)); // Elven Forest\0
		HUNTING_GROUNDS.put(1, new Location(50953, 42105, -3480)); // Shadow of the Mother Tree\0
		HUNTING_GROUNDS.put(8, new Location(-22224, 14168, -3232)); // The Dark Forest\0
		HUNTING_GROUNDS.put(13, new Location(-21966, 40544, -3192)); // Swampland\0
		HUNTING_GROUNDS.put(40, new Location(64328, 26803, -3768)); // Sea of Spores\0
		// HUNTING_GROUNDS.put(83, new Location(87252, 85514, -3103)); // Plains of the Lizardmen\0
		// HUNTING_GROUNDS.put(83, new Location(84517, 62538, -3480)); // Sel Mahum Training Grounds\0
		HUNTING_GROUNDS.put(1, new Location(23863, 11068, -3720)); // Shilen's Garden\0
		HUNTING_GROUNDS.put(13, new Location(-29466, 66678, -3496)); // Black Rock Hill\0
		HUNTING_GROUNDS.put(16, new Location(-61095, 75104, -3383)); // Spider Nest\0
		HUNTING_GROUNDS.put(40, new Location(67097, 68815, -3648)); // Timak Outpost \0
		HUNTING_GROUNDS.put(40, new Location(85391, 16228, -3640)); // Ivory Tower Crater\0
		HUNTING_GROUNDS.put(45, new Location(93218, 16969, -3904)); // Forest of Evil\0
		HUNTING_GROUNDS.put(50, new Location(91539, -12204, -2440)); // Outlaw Forest\0
		HUNTING_GROUNDS.put(65, new Location(155310, -16339, -3320)); // Blazing Swamp\0
		HUNTING_GROUNDS.put(60, new Location(188611, 20588, -3696)); // The Forbidden Gateway\0
		HUNTING_GROUNDS.put(45, new Location(124904, 61992, -3973)); // The Enchanted Valley\0
		HUNTING_GROUNDS.put(50, new Location(167047, 20304, -3328)); // The Cemetery\0
		HUNTING_GROUNDS.put(40, new Location(142065, 81300, -3000)); // The Forest of Mirrors\0
		HUNTING_GROUNDS.put(64, new Location(106517, -2871, -3454)); // Ancient Battleground\0
		HUNTING_GROUNDS.put(55, new Location(168217, 37990, -4072)); // Forsaken Plains \0
		HUNTING_GROUNDS.put(74, new Location(170838, 55776, -5280)); // Silent Valley\0
		HUNTING_GROUNDS.put(40, new Location(114306, 86573, -3112)); // Hunters Valley\0
		HUNTING_GROUNDS.put(45, new Location(135580, 19467, -3424)); // Plains of Glory\0
		HUNTING_GROUNDS.put(55, new Location(183543, -14974, -2768)); // Fields of Massacre\0
		HUNTING_GROUNDS.put(45, new Location(156898, 11217, -4032)); // War-Torn Plains\0
		// HUNTING_GROUNDS.put(83, new Location(91088, 182384, -3192)); // Field of Silence\0
		// HUNTING_GROUNDS.put(83, new Location(74592, 207656, -3032)); // Field of Whispers\0
		HUNTING_GROUNDS.put(40, new Location(115583, 192261, -3488)); // Alligator Island\0
		HUNTING_GROUNDS.put(38, new Location(116267, 201177, -3432)); // Alligator Beach\0
		HUNTING_GROUNDS.put(1, new Location(-39347, -107274, -2072)); // Valley of Heroes\0
		HUNTING_GROUNDS.put(8, new Location(-10983, -117484, -2464)); // Immortal Plateau, Northern Region\0
		HUNTING_GROUNDS.put(18, new Location(-4190, -80040, -2696)); // Immortal Plateau, Southern Region\0
		HUNTING_GROUNDS.put(1, new Location(112971, -174924, -608)); // Frozen Valley\0
		HUNTING_GROUNDS.put(8, new Location(128527, -204036, -3408)); // Western Mining Zone\0
		HUNTING_GROUNDS.put(18, new Location(175836, -205837, -3384)); // Eastern Mining Zone\0
		HUNTING_GROUNDS.put(30, new Location(111965, -154172, -1528)); // Plunderous Plains\0
		HUNTING_GROUNDS.put(53, new Location(113903, -108752, -884)); // Frozen Labyrinth\0
		HUNTING_GROUNDS.put(57, new Location(102728, -126242, -2840)); // Freya's Garden\0
		HUNTING_GROUNDS.put(73, new Location(91280, -117152, -3952)); // Pavel Ruins\0
		HUNTING_GROUNDS.put(81, new Location(68693, -110438, -1946)); // Den of Evil\0
		HUNTING_GROUNDS.put(80, new Location(47692, -115745, -3744)); // Crypts of Disgrace\0
		HUNTING_GROUNDS.put(39, new Location(121618, -141554, -1496)); // Sky Wagon Relic\0
		HUNTING_GROUNDS.put(67, new Location(132997, -60608, -2960)); // Garden of Beasts\0
		HUNTING_GROUNDS.put(73, new Location(144880, -113468, -2560)); // Hot Springs\0
		HUNTING_GROUNDS.put(77, new Location(146990, -67128, -3640)); // Ketra Orc Outpost\0
		HUNTING_GROUNDS.put(68, new Location(165054, -47861, -3560)); // Wall of Argos\0
		HUNTING_GROUNDS.put(73, new Location(190112, -61776, -2944)); // Shrine of Loyalty\0
		HUNTING_GROUNDS.put(77, new Location(125740, -40864, -3736)); // Varka Silenos Barracks\0
		HUNTING_GROUNDS.put(60, new Location(106349, -61870, -2904)); // Devil's Pass\0
		HUNTING_GROUNDS.put(83, new Location(43805, -88010, -2780)); // Beast Farm\0
		HUNTING_GROUNDS.put(60, new Location(65797, -71510, -3744)); // Valley of Saints\0
		HUNTING_GROUNDS.put(63, new Location(52107, -54328, -3158)); // Forest of the Dead\0
		HUNTING_GROUNDS.put(66, new Location(69340, -50203, -3314)); // Swamp of Screams\0
		// HUNTING_GROUNDS.put(83, new Location(8480, -14624, -3693)); // Primeval Isle\0
		HUNTING_GROUNDS.put(78, new Location(145159, 189247, -3756)); // Isle of Prayer\0
		// HUNTING_GROUNDS.put(82, new Location(148444, 160914, -3102)); // Chromatic Highlands\0
		HUNTING_GROUNDS.put(1, new Location(-121436, 56288, -1586)); // Isle of Souls\0
		HUNTING_GROUNDS.put(12, new Location(-103032, 46457, -1136)); // Mimir's Forest\0
		HUNTING_GROUNDS.put(13, new Location(-116114, 87005, -3544)); // Hills of Gold\0
	}

	public long getLastAiResponse()
	{
		return _lastAiResponse;
	}
}
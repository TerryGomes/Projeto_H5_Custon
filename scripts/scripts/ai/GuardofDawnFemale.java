package ai;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.PositionUtils;

/**
 * AI NPC для SSQ Dungeon
 * - Если находят чара в радиусе 300, то кричат в чат и отправляют на точку старта
 * - Не видят, кто находится в хайде
 * - Никогда и никого не атакуют
 * @author pchayka, n0nam3
 */
public class GuardofDawnFemale extends DefaultAI
{
	private static final int _aggrorange = 300;
	private static final Skill _skill = SkillTable.getInstance().getInfo(5978, 1);
	private Location _locTele = null;
	private boolean noCheckPlayers = false;

	public GuardofDawnFemale(NpcInstance actor, Location telePoint)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 200;
		setTelePoint(telePoint);
	}

	public class Teleportation extends RunnableImpl
	{

		Location _telePoint = null;
		Playable _target = null;

		public Teleportation(Location telePoint, Playable target)
		{
			_telePoint = telePoint;
			_target = target;
		}

		@Override
		public void runImpl()
		{
			_target.teleToLocation(_telePoint);
			noCheckPlayers = false;
		}
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		// проверяем игроков вокруг
		if (!noCheckPlayers)
		{
			checkAroundPlayers(actor);
		}

		return true;
	}

	private boolean checkAroundPlayers(NpcInstance actor)
	{
		for (Playable target : World.getAroundPlayables(actor, _aggrorange, _aggrorange))
		{
			if (!canSeeInSilentMove(target) || !canSeeInHide(target))
			{
				continue;
			}

			if (target != null && target.isPlayer() && !target.isSilentMoving() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false) && PositionUtils.isFacing(actor, target, 150))
			{
				actor.doCast(_skill, target, true);
				Functions.npcSay(actor, "Who are you?! A new face like you can't approach this place!");
				noCheckPlayers = true;
				ThreadPoolManager.getInstance().schedule(new Teleportation(getTelePoint(), target), 3000);
				return true;
			}
		}
		return false;
	}

	private void setTelePoint(Location loc)
	{
		_locTele = loc;
	}

	private Location getTelePoint()
	{
		return _locTele;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void thinkAttack()
	{
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage)
	{
	}

}
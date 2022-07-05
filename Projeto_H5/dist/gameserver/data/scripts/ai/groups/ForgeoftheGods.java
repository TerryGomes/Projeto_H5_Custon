package ai.groups;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

public class ForgeoftheGods extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(ForgeoftheGods.class);

	private static final int[] RANDOM_SPAWN_MOBS =
	{
		18799,
		18800,
		18801,
		18802,
		18803
	};
	private static final int[] FOG_MOBS =
	{
		22634,
		22635,
		22636,
		22637,
		22638,
		22639,
		22640,
		22641,
		22642,
		22643,
		22644,
		22645,
		22646,
		22647,
		22648,
		22649
	};
	private static final int TAR_BEETLE = 18804;

	private static int TAR_BEETLE_ACTIVATE_SKILL_CHANGE = 2; // chance for activate skill
	private static int TAR_BEETLE_SEARCH_RADIUS = 500; // search around players

	public ForgeoftheGods(NpcInstance actor)
	{
		super(actor);

		if (actor.getNpcId() == TAR_BEETLE)
		{
			AI_TASK_ATTACK_DELAY = 1250;
			actor.setIsInvul(true);
			actor.setHasChatWindow(false);
		}
		else if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, actor.getNpcId()))
		{
			actor.startImmobilized();
		}
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		if (actor.getNpcId() != TAR_BEETLE)
		{
			return super.thinkActive();
		}

		if (actor.isDead() || !Rnd.chance(TAR_BEETLE_ACTIVATE_SKILL_CHANGE))
		{
			return false;
		}

		List<Player> players = World.getAroundPlayers(actor, TAR_BEETLE_SEARCH_RADIUS, 200);
		if (players == null || players.size() < 1)
		{
			return false;
		}
		actor.doCast(SkillTable.getInstance().getInfo(6142, Rnd.get(1, 3)), players.get(Rnd.get(players.size())), false);
		return true;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		if (ArrayUtils.contains(FOG_MOBS, actor.getNpcId()))
		{
			try
			{
				NpcInstance npc = NpcHolder.getInstance().getTemplate(RANDOM_SPAWN_MOBS[Rnd.get(RANDOM_SPAWN_MOBS.length)]).getNewInstance();
				npc.setSpawnedLoc(actor.getLoc());
				npc.setReflection(actor.getReflection());
				npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
				npc.spawnMe(npc.getSpawnedLoc());
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on ForgeOfTheGods Monster Death", e);
			}
		}

		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (getActor().getNpcId() == TAR_BEETLE)
		{
			return;
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if (getActor().getNpcId() == TAR_BEETLE)
		{
			return;
		}
		super.onEvtAggression(target, aggro);
	}

	@Override
	protected boolean checkTarget(Creature target, int range)
	{
		NpcInstance actor = getActor();
		if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, getActor().getNpcId()) && target != null && !actor.isInRange(target, actor.getAggroRange()))
		{
			actor.getAggroList().remove(target, true);
			return false;
		}
		return super.checkTarget(target, range);
	}

	@Override
	protected boolean randomWalk()
	{
		return ArrayUtils.contains(RANDOM_SPAWN_MOBS, getActor().getNpcId()) || getActor().getNpcId() == TAR_BEETLE ? false : true;
	}
}
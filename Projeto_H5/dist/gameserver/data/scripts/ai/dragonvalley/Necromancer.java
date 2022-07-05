package ai.dragonvalley;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.NpcUtils;

/**
 * @author FandC После каждой атаки имеет шанс призвать одного из двух мобов.
 */
public class Necromancer extends Mystic
{

	public Necromancer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (attacker == null || actor.isDead())
		{
			return;
		}

		actor.getAggroList().addDamageHate(attacker, 0, damage);

		if (damage > 0 && (attacker.isSummon() || attacker.isPet()))
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
		if (Rnd.chance(Config.NECROMANCER_MS_CHANCE))
		{
			NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
		}
		notifyFriends(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		int count = actor.getMinionList().getAliveMinions().size();
		if (Rnd.chance(Config.NECROMANCER_MS_CHANCE * 2))
		{
			NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
		}
		super.onEvtDead(killer);
	}
}

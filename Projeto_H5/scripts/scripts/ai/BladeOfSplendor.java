package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class BladeOfSplendor extends RndTeleportFighter
{
	private static final Logger LOG = LoggerFactory.getLogger(BladeOfSplendor.class);
	private static final int[] CLONES =
	{
		21525
	};

	private boolean _firstTimeAttacked = true;

	public BladeOfSplendor(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 100000;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}
		if ((!actor.isDead()) && (_firstTimeAttacked))
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, "Now I Know Why You Wanna Hate Me");
			for (int bro : CLONES)
			{
				try
				{
					MonsterInstance npc = (MonsterInstance) NpcHolder.getInstance().getTemplate(bro).getNewInstance();
					npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Integer.valueOf(Rnd.get(1, 1000)));
				}
				catch (RuntimeException e)
				{
					LOG.error("Error while creating BladeOfSplendor", e);
				}
			}
		}
		super.onEvtAttacked(attacker, damage);
	}

	protected void onEvtDead(Player killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}
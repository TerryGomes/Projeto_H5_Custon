package ai.fog;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;

public class GroupAI extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(GroupAI.class);

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

	public GroupAI(NpcInstance actor)
	{
		super(actor);

		if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, actor.getNpcId()))
		{
			actor.startImmobilized();
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance npc;
		NpcInstance actor = getActor();

		if (ArrayUtils.contains(FOG_MOBS, actor.getNpcId()))
		{
			try
			{
				npc = NpcHolder.getInstance().getTemplate(RANDOM_SPAWN_MOBS[Rnd.get(RANDOM_SPAWN_MOBS.length)]).getNewInstance();
				npc.setSpawnedLoc(actor.getLoc());
				npc.setReflection(actor.getReflection());
				npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
				npc.spawnMe(npc.getSpawnedLoc());
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on GroupAI Death", e);
			}
		}

		super.onEvtDead(killer);
	}
}
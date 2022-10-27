package ai.hellbound;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;

public class RankuScapegoat extends DefaultAI
{
	private static final int Eidolon_ID = 25543;

	public RankuScapegoat(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(Eidolon_ID, actor.getLoc(), 0);
		NpcInstance boss = getBoss();
		if (mob != null && boss != null)
		{
			Creature cha = boss.getAggroList().getTopDamager();
			if (cha != null)
			{
				mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, cha, 100000);
			}
		}
		super.onEvtDead(killer);
	}

	private NpcInstance getBoss()
	{
		Reflection r = getActor().getReflection();
		if (!r.isDefault())
		{
			for (NpcInstance n : r.getNpcs())
			{
				if (n.getNpcId() == 25542 && !n.isDead())
				{
					return n;
				}
			}
		}
		return null;
	}
}
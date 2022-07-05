package ai.hellbound;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.AggroList.AggroInfo;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Ranku extends Fighter
{
	private static final int TELEPORTATION_CUBIC_ID = 32375;
	private static final Location CUBIC_POSITION = new Location(-19056, 278732, -15000, 0);
	private static final int SCAPEGOAT_ID = 32305;

	private long _massacreTimer = 0;
	private final long _massacreDelay = 30000L;

	public Ranku(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Reflection r = getActor().getReflection();
		if (r != null)
		{
			for (int i = 0; i < 4; i++)
			{
				r.addSpawnWithRespawn(SCAPEGOAT_ID, getActor().getLoc(), 300, 60);
			}
		}
	}

	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return;
		}

		if (_massacreTimer + _massacreDelay < System.currentTimeMillis())
		{
			NpcInstance victim = getScapegoat();
			_massacreTimer = System.currentTimeMillis();
			if (victim != null)
			{
				actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, victim, getMaximumHate() + 200000);
			}
		}
		super.thinkAttack();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		if (actor.getReflection() != null)
		{
			actor.getReflection().setReenterTime(System.currentTimeMillis());
			actor.getReflection().addSpawnWithoutRespawn(TELEPORTATION_CUBIC_ID, CUBIC_POSITION, 0);
		}
		super.onEvtDead(killer);
	}

	private NpcInstance getScapegoat()
	{
		for (NpcInstance n : getActor().getReflection().getNpcs())
		{
			if (n.getNpcId() == SCAPEGOAT_ID && !n.isDead())
			{
				return n;
			}
		}
		return null;
	}

	private int getMaximumHate()
	{
		NpcInstance actor = getActor();
		Creature cha = actor.getAggroList().getMostHated();
		if (cha != null)
		{
			AggroInfo ai = actor.getAggroList().get(cha);
			if (ai != null)
			{
				return ai.hate;
			}
		}
		return 0;
	}
}
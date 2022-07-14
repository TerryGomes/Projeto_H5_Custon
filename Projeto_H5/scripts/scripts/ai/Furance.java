package ai;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;

public class Furance extends DefaultAI
{
	public Furance(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		if (Rnd.chance(50))
		{
			actor.setNpcState(1);
		}
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Switch(), 5 * 60 * 1000L, 5 * 60 * 1000L);
	}

	public class Switch extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (actor.getNpcState() == 1)
			{
				actor.setNpcState(2);
			}
			else
			{
				actor.setNpcState(1);
			}
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
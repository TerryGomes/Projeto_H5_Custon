package ai.hellbound;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.ReflectionUtils;

public class Leodas extends Fighter
{
	public Leodas(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		ReflectionUtils.getDoor(19250003).openMe();
		ReflectionUtils.getDoor(19250004).openMe();
		ThreadPoolManager.getInstance().schedule(new CloseDoor(), 60 * 1000L);
		super.onEvtDead(killer);
	}

	private class CloseDoor extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			ReflectionUtils.getDoor(19250003).closeMe();
			ReflectionUtils.getDoor(19250004).closeMe();
		}
	}
}
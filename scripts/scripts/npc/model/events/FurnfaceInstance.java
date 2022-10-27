package npc.model.events;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class FurnfaceInstance extends NpcInstance
{
	public FurnfaceInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setTargetable(false);
	}

	public void setActive2114001(int i)
	{
		setTargetable(false);
		if (getAISpawnParam() == i)
		{
			setNpcState(1);
			ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
		}
	}

	public void setActive2114002()
	{
		setTargetable(false);
		setNpcState(1);
		ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
	}

	public void setSCE_GAME_PLAYER_START()
	{
		setNpcState(1);
		ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
		setTargetable(true);
	}

	public void setSCE_GAME_END()
	{
		setNpcState(1);
		ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
		setTargetable(false);
	}

	public void setSCE_GAME_FAILURE()
	{
		setTargetable(false);
		setNpcState(1);
		ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
	}

	private class OFF_TIMER extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			setNpcState(2);
		}
	}
}
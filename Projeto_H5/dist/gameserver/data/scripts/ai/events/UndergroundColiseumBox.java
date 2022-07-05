package ai.events;

import java.util.concurrent.Future;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 17:59/19.05.2012
 */
public class UndergroundColiseumBox extends DefaultAI
{
	private Future<?> _despawnTask;

	public UndergroundColiseumBox(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();
		_despawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				getActor().decayOrDelete();
			}
		}, 20000L);
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();
		cancel();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		cancel();
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		//
	}

	private void cancel()
	{
		if (_despawnTask != null)
		{
			_despawnTask.cancel(false);
			_despawnTask = null;
		}
	}
}

package ai;

import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.SocialAction;

/**
 * @author claww
  * - AI for individual monsters (32439, 32440, 32441).
  * - Indicates social programs.
  * - AI is tested and works.
 */
public class MCIndividual extends DefaultAI
{
	public MCIndividual(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		ThreadPoolManager.getInstance().schedule(new ScheduleSocial(), 1000);
		super.onEvtSpawn();
	}

	private class ScheduleSocial implements Runnable
	{
		@Override
		public void run()
		{
			NpcInstance actor = getActor();
			actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
		}
	}
}
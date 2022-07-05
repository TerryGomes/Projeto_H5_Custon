package ai.freya;

import bosses.ValakasManager;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;

public class ValakasMinion extends Mystic
{
	public ValakasMinion(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for (Player p : ValakasManager.getZone().getInsidePlayers())
		{
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
		}
	}
}
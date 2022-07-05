package ai.freya;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;

public class IceCaptainKnight extends Fighter
{
	public IceCaptainKnight(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = 6000;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Reflection r = getActor().getReflection();
		if (r != null && r.getPlayers() != null)
		{
			for (Player p : r.getPlayers())
			{
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 300);
			}
		}
	}

	@Override
	protected void teleportHome()
	{
		return;
	}
}
package ai.freya;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

public class FreyaQuest extends Fighter
{
	public FreyaQuest(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		Reflection r = getActor().getReflection();
		for (Player p : r.getPlayers())
		{
			this.notifyEvent(CtrlEvent.EVT_ATTACKED, p, 300);
		}

		Functions.npcSayCustomMessage(getActor(), "scripts.ai.freya.FreyaQuest.onEvtSpawn");
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		return;
	}
}
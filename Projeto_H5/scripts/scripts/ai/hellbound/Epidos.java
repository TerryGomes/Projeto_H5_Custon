package ai.hellbound;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.instancemanager.naia.NaiaCoreManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;

public class Epidos extends Fighter
{

	public Epidos(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NaiaCoreManager.removeSporesAndSpawnCube();
		super.onEvtDead(killer);
	}
}
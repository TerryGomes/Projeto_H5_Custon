package ai.hellbound;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.instancemanager.naia.NaiaCoreManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;

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
package ai.freya;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.NpcUtils;

public class AnnihilationFighter extends Fighter
{
	public AnnihilationFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(5))
		{
			NpcUtils.spawnSingle(18839, Location.findPointToStay(getActor(), 40, 120), getActor().getReflection()); // Maguen
		}

		super.onEvtDead(killer);
	}

	@Override
	public boolean canSeeInSilentMove(Playable target)
	{
		return true;
	}

	@Override
	public boolean canSeeInHide(Playable target)
	{
		return true;
	}
}
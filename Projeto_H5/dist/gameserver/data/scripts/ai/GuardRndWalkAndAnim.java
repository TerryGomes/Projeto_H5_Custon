package ai;

import l2f.gameserver.ai.Guard;
import l2f.gameserver.model.instances.NpcInstance;

public class GuardRndWalkAndAnim extends Guard
{
	public GuardRndWalkAndAnim(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if (super.thinkActive() || randomAnimation() || randomWalk())
		{
			return true;
		}

		return false;
	}
}
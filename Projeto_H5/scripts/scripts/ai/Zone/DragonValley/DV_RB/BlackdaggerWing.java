package ai.Zone.DragonValley.DV_RB;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.instances.NpcInstance;

/**
 * @author FandC PTS http://www.youtube.com/watch?v=jl091irKH30
 */

public class BlackdaggerWing extends Fighter
{

	private long last_attack_time = 0;

	public BlackdaggerWing(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		super.thinkActive();
		if (last_attack_time != 0 && last_attack_time + 30 * 60 * 1000L < System.currentTimeMillis())
		{
			getActor().deleteMe();
		}
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		last_attack_time = System.currentTimeMillis();
	}

}
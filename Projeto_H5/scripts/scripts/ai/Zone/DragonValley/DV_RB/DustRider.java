package ai.Zone.DragonValley.DV_RB;

import l2mv.gameserver.ai.Mystic;
import l2mv.gameserver.model.instances.NpcInstance;

/**
 * @author FandC
 */
public class DustRider extends Mystic
{

	private long last_attack_time = 0;

	public DustRider(NpcInstance actor)
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

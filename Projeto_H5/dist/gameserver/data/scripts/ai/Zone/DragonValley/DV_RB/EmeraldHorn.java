package ai.Zone.DragonValley.DV_RB;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

/**
 * @author FandC PTS http://www.youtube.com/watch?v=CHPqJNDiq8E
 */
public class EmeraldHorn extends Mystic
{

	private long last_attack_time = 0;

	public EmeraldHorn(NpcInstance actor)
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
		getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(86, Rnd.get(1, 3)));
		last_attack_time = System.currentTimeMillis();
	}

}
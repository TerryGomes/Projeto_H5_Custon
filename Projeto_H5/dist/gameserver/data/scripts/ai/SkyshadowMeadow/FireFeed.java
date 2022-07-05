package ai.SkyshadowMeadow;

import org.apache.log4j.Logger;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

/**
 * @author claww
  * - AI for Fire Feed (18933).
  * - Uninstall it in 10-60 seconds.
  * - AI is tested and works.
 */
public class FireFeed extends DefaultAI
{
	protected static Logger _log = Logger.getLogger(FireFeed.class.getName());
	private long _wait_timeout = System.currentTimeMillis() + Rnd.get(10, 30) * 1000;

	public FireFeed(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (_wait_timeout < System.currentTimeMillis())
		{
			actor.decayMe();
		}

		return true;
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		if (skill.getId() != 9075)
		{
			return;
		}

		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		actor.doCast(SkillTable.getInstance().getInfo(6688, 1), caster, true);
	}
}
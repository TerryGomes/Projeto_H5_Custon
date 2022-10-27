package ai.seedofdestruction;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;

public class TiatsTrap extends DefaultAI
{
	private static final int[] holdTraps =
	{
		18720,
		18721,
		18722,
		18723,
		18724,
		18725,
		18726,
		18727,
		18728
	};
	private static final int[] damageTraps =
	{
		18737,
		18738,
		18739,
		18740,
		18741,
		18742,
		18743,
		18744,
		18745,
		18746,
		18747,
		18748,
		18749,
		18750,
		18751,
		18752,
		18753,
		18754,
		18755,
		18756,
		18757,
		18758,
		18759,
		18760,
		18761,
		18762,
		18763,
		18764,
		18765,
		18766,
		18767,
		18768,
		18769,
		18770,
		18771,
		18772,
		18773,
		18774
	};
	private static final int[] stunTraps =
	{
		18729,
		18730,
		18731,
		18732,
		18733,
		18734,
		18735,
		18736
	};

	public TiatsTrap(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
		actor.startDamageBlocked();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (!actor.getAroundCharacters(200, 150).isEmpty())
		{
			Skill skill = null;
			if (ArrayUtils.contains(holdTraps, actor.getNpcId()))
			{
				skill = SkillTable.getInstance().getInfo(4186, 9);
			}
			else if (ArrayUtils.contains(damageTraps, actor.getNpcId()))
			{
				skill = SkillTable.getInstance().getInfo(5311, 9);
			}
			else if (ArrayUtils.contains(stunTraps, actor.getNpcId()))
			{
				skill = SkillTable.getInstance().getInfo(4072, 10);
			}
			else
			{
				return false;
			}
			actor.doCast(skill, actor, true);
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					getActor().doDie(null);
				}
			}, 5000);
			return true;
		}
		return true;
	}
}
package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.FeedableBeastInstance;
import l2f.gameserver.templates.StatsSet;

public class BeastFeed extends Skill
{
	public BeastFeed(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			ThreadPoolManager.getInstance().execute(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					if (target instanceof FeedableBeastInstance)
					{
						((FeedableBeastInstance) target).onSkillUse((Player) activeChar, _id);
					}
				}
			});
		}
	}
}

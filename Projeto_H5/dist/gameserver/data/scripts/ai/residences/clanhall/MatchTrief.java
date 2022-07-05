package ai.residences.clanhall;

import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchTrief extends MatchFighter
{
	public static final Skill HOLD = SkillTable.getInstance().getInfo(4047, 6);

	public MatchTrief(NpcInstance actor)
	{
		super(actor);
	}

	public void hold()
	{
		NpcInstance actor = getActor();
		addTaskCast(actor, HOLD);
		doTask();
	}
}

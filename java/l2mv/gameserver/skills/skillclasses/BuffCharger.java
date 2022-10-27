package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.StatsSet;

public class BuffCharger extends Skill
{
	private int _target;

	public BuffCharger(StatsSet set)
	{
		super(set);
		_target = set.getInteger("targetBuff", 0);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			int level = 0;
			List<Effect> el = target.getEffectList().getEffectsBySkillId(_target);
			if (el != null)
			{
				level = el.get(0).getSkill().getLevel();
			}

			Skill next = SkillTable.getInstance().getInfo(_target, level + 1);
			if (next != null)
			{
				next.getEffects(activeChar, target, false, false);
			}
		}
	}
}
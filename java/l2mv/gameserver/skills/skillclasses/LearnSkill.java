package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.StatsSet;

public class LearnSkill extends Skill
{
	private final int[] _learnSkillId;
	private final int[] _learnSkillLvl;

	public LearnSkill(StatsSet set)
	{
		super(set);

		String[] ar = set.getString("learnSkillId", "0").split(",");
		int[] ar2 = new int[ar.length];

		for (int i = 0; i < ar.length; i++)
		{
			ar2[i] = Integer.parseInt(ar[i]);
		}

		_learnSkillId = ar2;

		ar = set.getString("learnSkillLvl", "1").split(",");
		ar2 = new int[_learnSkillId.length];

		for (int i = 0; i < _learnSkillId.length; i++)
		{
			ar2[i] = 1;
		}

		for (int i = 0; i < ar.length; i++)
		{
			ar2[i] = Integer.parseInt(ar[i]);
		}

		_learnSkillLvl = ar2;
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!(activeChar instanceof Player))
		{
			return;
		}

		final Player player = ((Player) activeChar);
		Skill newSkill;

		for (int i = 0; i < _learnSkillId.length; i++)
		{
			if (player.getSkillLevel(_learnSkillId[i]) < _learnSkillLvl[i] && _learnSkillId[i] != 0)
			{
				newSkill = SkillTable.getInstance().getInfo(_learnSkillId[i], _learnSkillLvl[i]);
				if (newSkill != null)
				{
					player.addSkill(newSkill, true);
				}
			}
		}
	}
}
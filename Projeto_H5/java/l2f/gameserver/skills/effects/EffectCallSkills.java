package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.stats.Env;
import l2f.gameserver.tables.SkillTable;

public class EffectCallSkills extends Effect
{
	public EffectCallSkills(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectCallSkills(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		int[] skillIds = getTemplate().getParam().getIntegerArray("skillIds");
		int[] skillLevels = getTemplate().getParam().getIntegerArray("skillLevels");

		for (int i = 0; i < skillIds.length; i++)
		{
			Skill skill = SkillTable.getInstance().getInfo(skillIds[i], skillLevels[i]);
			for (Creature cha : skill.getTargets(getEffector(), getEffected(), false))
			{
				getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skillIds[i], skillLevels[i], 0, 0));
			}
			getEffector().callSkill(skill, skill.getTargets(getEffector(), getEffected(), false), false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
package l2f.gameserver.skills.effects;

import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.AggroList;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.stats.Env;

public class EffectRandomHate extends Effect
{
	public EffectRandomHate(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectRandomHate(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isMonster() && Rnd.chance(_template.chance(100));
	}

	@Override
	public void onStart()
	{
		MonsterInstance monster = (MonsterInstance) getEffected();
		Creature mostHated = monster.getAggroList().getMostHated();
		if (mostHated == null)
		{
			return;
		}

		AggroList.AggroInfo mostAggroInfo = monster.getAggroList().get(mostHated);
		List<Creature> hateList = monster.getAggroList().getHateList();
		hateList.remove(mostHated);

		if (!hateList.isEmpty())
		{
			AggroList.AggroInfo newAggroInfo = monster.getAggroList().get(hateList.get(Rnd.get(hateList.size())));
			final int oldHate = newAggroInfo.hate;

			newAggroInfo.hate = mostAggroInfo.hate;
			mostAggroInfo.hate = oldHate;
		}
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	protected boolean onActionTime()
	{
		return false;
	}
}

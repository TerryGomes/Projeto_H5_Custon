package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.ItemFunctions;

public class SummonItem extends Skill
{
	private final int _itemId;
	private final int _minId;
	private final int _maxId;
	private final long _minCount;
	private final long _maxCount;

	public SummonItem(StatsSet set)
	{
		super(set);

		_itemId = set.getInteger("SummonItemId", 0);
		_minId = set.getInteger("SummonMinId", 0);
		_maxId = set.getInteger("SummonMaxId", _minId);
		_minCount = set.getLong("SummonMinCount");
		_maxCount = set.getLong("SummonMaxCount", _minCount);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayable())
		{
			return;
		}
		for (Creature target : targets)
		{
			if (target != null)
			{
				int itemId = _minId > 0 ? Rnd.get(_minId, _maxId) : _itemId;
				long count = Rnd.get(_minCount, _maxCount);

				ItemFunctions.addItem((Playable) activeChar, itemId, count, true, "SummonItem");
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}
	}
}
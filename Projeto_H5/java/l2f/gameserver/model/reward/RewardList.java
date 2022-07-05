package l2f.gameserver.model.reward;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.Player;

/**
 * @reworked VISTALL
 */
@SuppressWarnings("serial")
public class RewardList extends ArrayList<RewardGroup>
{
	public static final int MAX_CHANCE = 1000000;
	private final RewardType _type;
	private final boolean _autoLoot;

	public RewardList(RewardType rewardType, boolean a)
	{
		super(5);
		_type = rewardType;
		_autoLoot = a;
	}

	public List<RewardItem> roll(Player player)
	{
		return roll(player, 1.0, false, false);
	}

	public List<RewardItem> roll(Player player, double mod)
	{
		return roll(player, mod, false, false);
	}

	public List<RewardItem> roll(Player player, double mod, boolean isRaid)
	{
		return roll(player, mod, isRaid, false);
	}

	public List<RewardItem> roll(Player player, double mod, boolean isRaid, boolean isSiegeGuard)
	{
		List<RewardItem> temp = new ArrayList<RewardItem>(size());
		for (RewardGroup g : this)
		{
			List<RewardItem> tdl = g.roll(_type, player, mod, isRaid, isSiegeGuard);
			if (!tdl.isEmpty())
			{
				for (RewardItem itd : tdl)
				{
					temp.add(itd);
				}
			}
		}
		return temp;
	}

	public boolean validate()
	{
		for (RewardGroup g : this)
		{
			int chanceSum = 0; // сумма шансов группы
			for (RewardData d : g.getItems())
			{
				chanceSum += d.getChance();
			}
			if (chanceSum <= MAX_CHANCE) // всё в порядке?
			{
				return true;
			}
			double mod = MAX_CHANCE / chanceSum;
			for (RewardData d : g.getItems())
			{
				double chance = d.getChance() * mod; // коррекция шанса группы
				d.setChance(chance);
				g.setChance(MAX_CHANCE);
			}
		}
		return false;
	}

	public boolean isAutoLoot()
	{
		return _autoLoot;
	}

	public RewardType getType()
	{
		return _type;
	}
}
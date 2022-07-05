package l2f.gameserver.model.actor.instances.player;

import l2f.gameserver.model.Player;

public final class RecomBonus
{
	private static final int[][] _recoBonus =
	{
		{
			25,
			50,
			50,
			50,
			50,
			50,
			50,
			50,
			50,
			50
		},
		{
			16,
			33,
			50,
			50,
			50,
			50,
			50,
			50,
			50,
			50
		},
		{
			12,
			25,
			37,
			50,
			50,
			50,
			50,
			50,
			50,
			50
		},
		{
			10,
			20,
			30,
			40,
			50,
			50,
			50,
			50,
			50,
			50
		},
		{
			8,
			16,
			25,
			33,
			41,
			50,
			50,
			50,
			50,
			50
		},
		{
			7,
			14,
			21,
			28,
			35,
			42,
			50,
			50,
			50,
			50
		},
		{
			6,
			12,
			18,
			25,
			31,
			37,
			43,
			50,
			50,
			50
		},
		{
			5,
			11,
			16,
			22,
			27,
			33,
			38,
			44,
			50,
			50
		},
		{
			5,
			10,
			15,
			20,
			25,
			30,
			35,
			40,
			45,
			50
		}
	};

	public static int getRecoBonus(Player activeChar)
	{
		if (activeChar != null && activeChar.isOnline())
		{
			if (activeChar.getRecomHave() == 0)
			{
				return 0;
			}

			int _lvl = (int) Math.ceil(activeChar.getLevel() / 10);
			int _exp = (int) Math.ceil((Math.min(100, activeChar.getRecomHave()) - 1) / 10);

			return _recoBonus[_lvl][_exp];
		}
		return 0;
	}

	public static double getRecoMultiplier(Player activeChar)
	{
		double bonus = getRecoBonus(activeChar);
		if (bonus > 0)
		{
			return 1. + bonus / 100;
		}
		else
		{
			return 1.;
		}
	}
}
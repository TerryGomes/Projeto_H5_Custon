package l2mv.gameserver.stats.conditions;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;

/**
 * @author VISTALL
 * @date  16:17/22.12.2010
 */
public class ConditionPlayerCubic extends Condition
{
	private int _id;

	public ConditionPlayerCubic(int id)
	{
		_id = id;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if (env.target == null || !env.target.isPlayer())
		{
			return false;
		}

		Player targetPlayer = (Player) env.target;
		if (targetPlayer.getCubic(_id) != null)
		{
			return true;
		}

		int size = (int) targetPlayer.calcStat(Stats.CUBICS_LIMIT, 1);
		if (targetPlayer.getCubics().size() >= size)
		{
			if (env.character == targetPlayer)
			{
				targetPlayer.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED); // todo un hard code it
			}

			return false;
		}

		return true;
	}
}

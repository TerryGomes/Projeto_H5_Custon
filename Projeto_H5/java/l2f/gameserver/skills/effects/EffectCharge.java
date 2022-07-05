package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;

public final class EffectCharge extends Effect
{
	// Максимальное количество зарядов находится в поле val="xx"

	public EffectCharge(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectCharge(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (getEffected().isPlayer())
		{
			final Player player = (Player) getEffected();

			if (player.getIncreasedForce() >= calc())
			{
				player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			}
			else
			{
				player.setIncreasedForce(player.getIncreasedForce() + 1);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}

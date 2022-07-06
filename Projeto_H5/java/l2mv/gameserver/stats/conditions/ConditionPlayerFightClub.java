package l2mv.gameserver.stats.conditions;

import l2mv.gameserver.network.serverpackets.SysMsgContainer;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.stats.Env;

public class ConditionPlayerFightClub extends Condition
{
	private final boolean _value;

	public ConditionPlayerFightClub(boolean v)
	{
		_value = v;
	}

	@Override
	public IStaticPacket getSystemMsg(SysMsgContainer.IArgument... arguments)
	{
		return new SystemMessage("You cannot do that while being in Event!");
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if (!env.character.isPlayable())
		{
			return false;
		}
		return env.character.getPlayer().isInFightClub() == _value;
	}
}

package l2f.gameserver.skills.effects;

import l2f.gameserver.model.Effect;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.stats.Env;

public final class EffectParalyze extends Effect
{
	public EffectParalyze(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectParalyze(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (_effected.isParalyzeImmune())
		{
			return false;
		}
		if (_effector.getPet() != null && _effected == _effector.getPet())
		{
			_effector.getPlayer().sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startParalyzed();
		_effected.abortAttack(true, true);
		_effected.abortCast(true, true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopParalyzed();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
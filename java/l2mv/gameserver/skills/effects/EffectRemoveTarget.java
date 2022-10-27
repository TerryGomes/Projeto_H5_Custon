package l2mv.gameserver.skills.effects;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.stats.Env;

public final class EffectRemoveTarget extends Effect
{
	private final boolean _doStopTarget;

	public EffectRemoveTarget(Env env, EffectTemplate template)
	{
		super(env, template);
		_doStopTarget = template.getParam().getBool("doStopTarget", false);
	}

	public EffectRemoveTarget(Effect effect)
	{
		super(effect);
		_doStopTarget = getTemplate().getParam().getBool("doStopTarget", false);
	}

	@Override
	public void onStart()
	{
		if ((getEffected().getAI() instanceof DefaultAI))
		{
			((DefaultAI) getEffected().getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
		}
		getEffected().setTarget(null);
		if (_doStopTarget)
		{
			getEffected().stopMove();
		}
		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, getEffector());
		getEffected().sendPacket(ActionFail.STATIC);
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
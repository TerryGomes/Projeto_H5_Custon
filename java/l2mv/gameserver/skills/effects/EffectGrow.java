package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.stats.Env;

public final class EffectGrow extends Effect
{
	public EffectGrow(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectGrow(Effect effect)
	{
		super(effect);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isNpc())
		{
			NpcInstance npc = (NpcInstance) _effected;
			npc.setCollisionHeight(npc.getCollisionHeight() * 1.24);
			npc.setCollisionRadius(npc.getCollisionRadius() * 1.19);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isNpc())
		{
			NpcInstance npc = (NpcInstance) _effected;
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
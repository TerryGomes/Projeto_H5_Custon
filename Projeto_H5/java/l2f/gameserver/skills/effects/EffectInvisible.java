package l2f.gameserver.skills.effects;

import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.InvisibleType;
import l2f.gameserver.stats.Env;

public final class EffectInvisible extends Effect
{
	private InvisibleType _invisibleType = InvisibleType.NONE;

	public EffectInvisible(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectInvisible(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (!_effected.isPlayer())
		{
			return false;
		}
		Player player = (Player) _effected;
		if (player.isInvisible() || (player.getActiveWeaponFlagAttachment() != null))
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = (Player) _effected;

		_invisibleType = player.getInvisibleType();

		player.setInvisibleType(InvisibleType.EFFECT);

		World.removeObjectFromPlayers(player);

		for (Creature target : World.getAroundCharacters(player, 5000, 500))
		{
			if (target != null && target.getCastingTarget() != null && target.getCastingTarget().equals(player))
			{
				target.abortAttack(true, true);
				target.abortCast(true, true);
				target.setTarget(null);
				target.stopMove();
				target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		Player player = (Player) _effected;
		if (!player.isInvisible())
		{
			return;
		}

		player.setInvisibleType(_invisibleType);

		player.broadcastUserInfo(true);
		if (player.getPet() != null)
		{
			player.getPet().broadcastCharInfo();
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
package l2mv.gameserver.skills.effects;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.impl.fightclub.KoreanStyleEvent;
import l2mv.gameserver.model.instances.SummonInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.utils.PositionUtils;

public final class EffectFear extends Effect
{
	public static final double FEAR_RANGE = 900;

	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectFear(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (_effected.isFearImmune())
		{
			getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		// Fear нельзя наложить на осадных саммонов
		Player player = _effected.getPlayer();
		if (player != null)
		{
			SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
			if (_effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) _effected))
			{
				getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}

		if (_effected.isInZonePeace())
		{
			getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (!_effected.startFear())
		{
			_effected.abortAttack(true, true);
			_effected.abortCast(true, true);
			_effected.stopMove();
		}

		onActionTime();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopFear();
		_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	public boolean onActionTime()
	{
		// Synerge - Disable fear effect on Korean Event because it causes players to leave the arena, just the effect, the player should still be unable to do actions
		if (_effected.isPlayable() && _effected.getPlayer().isInFightClub() && _effected.getPlayer().getFightClubEvent() instanceof KoreanStyleEvent)
		{
			_effected.stopMove();
			return true;
		}

		final double angle = Math.toRadians(PositionUtils.calculateAngleFrom(_effector, _effected));
		final int oldX = _effected.getX();
		final int oldY = _effected.getY();
		final int x = oldX + (int) (FEAR_RANGE * Math.cos(angle));
		final int y = oldY + (int) (FEAR_RANGE * Math.sin(angle));
		_effected.setRunning();
		_effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, _effected.getZ(), x, y, _effected.getGeoIndex()), 0, false);
		return true;
	}
}
package l2mv.gameserver.listener.actor.player.impl;

import l2mv.commons.lang.reference.HardReference;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.PetInstance;

public class ReviveAnswerListener implements OnAnswerListener
{
	private final HardReference<Player> _playerRef;
	private final double _power;
	private final boolean _forPet;

	public ReviveAnswerListener(Player player, double power, boolean forPet)
	{
		_playerRef = player.getRef();
		_forPet = forPet;
		_power = power;
	}

	@Override
	public void sayYes()
	{
		Player player = _playerRef.get();
		if (player == null)
		{
			return;
		}
		if (!player.isDead() && !_forPet || _forPet && player.getPet() != null && !player.getPet().isDead())
		{
			return;
		}

		// Prims - If the request for resurrection was sent more than 5 minutes ago, then don't do nothing when its accepted. Only for players
		if (!_forPet && player.getResurrectionMaxTime() < System.currentTimeMillis())
		{
			return;
		}

		if (!_forPet)
		{
			player.doRevive(_power);
		}
		else if (player.getPet() != null)
		{
			((PetInstance) player.getPet()).doRevive(_power);
		}
	}

	@Override
	public void sayNo()
	{

	}

	public double getPower()
	{
		return _power;
	}

	public boolean isForPet()
	{
		return _forPet;
	}
}

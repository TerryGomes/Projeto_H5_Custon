package l2mv.gameserver.model.entity.tournament.listener;

import l2mv.gameserver.listener.actor.player.OnTeleportedListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.utils.Location;

public class TournamentAppearOutOfZoneListener implements OnTeleportedListener
{
	private final Location _locationToBeTeleported;
	private final Reflection _reflection;

	public TournamentAppearOutOfZoneListener(Location locationToBeTeleported, Reflection reflection)
	{
		_locationToBeTeleported = locationToBeTeleported;
		_reflection = reflection;
	}

	@Override
	public void onTeleported(Player player)
	{
		player.removeListener(this);
		player.teleToLocation(_locationToBeTeleported, _reflection);
	}
}

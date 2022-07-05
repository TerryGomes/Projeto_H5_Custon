package l2f.gameserver.model.entity.tournament.listener;

import l2f.gameserver.listener.actor.player.OnTeleportedListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.utils.Location;

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

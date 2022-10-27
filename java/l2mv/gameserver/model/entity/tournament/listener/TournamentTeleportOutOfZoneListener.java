package l2mv.gameserver.model.entity.tournament.listener;

import l2mv.gameserver.listener.actor.player.OnTeleportListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.tournament.BattleInstance;
import l2mv.gameserver.model.entity.tournament.Team;
import l2mv.gameserver.utils.Location;

public class TournamentTeleportOutOfZoneListener implements OnTeleportListener
{
	private final BattleInstance _battle;

	public TournamentTeleportOutOfZoneListener(BattleInstance battle)
	{
		_battle = battle;
	}

	@Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		if (isOutsideZones(x, y, z))
		{
			final Team team = _battle.getBattleRecord().getTeam(player);
			if (team == null)
			{
				return;
			}
			final Location spawn = _battle.getMap().getTeamSpawnLocation(_battle.getBattleRecord().getTeamIndex(team));
			player.addListener(new TournamentAppearOutOfZoneListener(spawn, _battle.getReflection()));
		}
	}

	private boolean isOutsideZones(int x, int y, int z)
	{
		for (Zone zone : _battle.getReflection().getZones())
		{
			if (zone.checkIfInZone(x, y, z))
			{
				return false;
			}
		}
		return true;
	}
}

package l2f.gameserver.model.entity.tournament.listener;

import l2f.gameserver.listener.actor.player.OnLeaveObserverModeListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.tournament.BattleInstance;
import l2f.gameserver.model.entity.tournament.BattleObservationManager;

public class TournamentObservationEndListener implements OnLeaveObserverModeListener
{
	private final BattleInstance _battle;

	public TournamentObservationEndListener(BattleInstance battle)
	{
		_battle = battle;
	}

	@Override
	public void onLeaveObserverMode(Player player)
	{
		BattleObservationManager.onLeaveObservation(_battle, player);
	}
}

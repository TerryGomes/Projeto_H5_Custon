package l2mv.gameserver.model.entity.tournament.listener;

import l2mv.gameserver.listener.actor.player.OnPlayerSummonPetListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;

public class TournamentSpawnSummonListener implements OnPlayerSummonPetListener
{
	private final BattleInstance _battleInstance;

	public TournamentSpawnSummonListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onSummonPet(Player player, Summon summon)
	{
		ActiveBattleManager.onSpawnedSummon(_battleInstance, summon, true);
	}
}

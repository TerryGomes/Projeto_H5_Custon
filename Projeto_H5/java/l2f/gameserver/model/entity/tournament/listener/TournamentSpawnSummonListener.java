package l2f.gameserver.model.entity.tournament.listener;

import l2f.gameserver.listener.actor.player.OnPlayerSummonPetListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.entity.tournament.ActiveBattleManager;
import l2f.gameserver.model.entity.tournament.BattleInstance;

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

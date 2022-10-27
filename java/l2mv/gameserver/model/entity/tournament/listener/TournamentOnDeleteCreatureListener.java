package l2mv.gameserver.model.entity.tournament.listener;

import l2mv.gameserver.listener.actor.OnDeleteListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;

public class TournamentOnDeleteCreatureListener implements OnDeleteListener
{
	private final BattleInstance _battleInstance;

	public TournamentOnDeleteCreatureListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onDelete(Creature creature)
	{
		if (creature.isSummon())
		{
			ActiveBattleManager.onUnsummonPet(_battleInstance, (Summon) creature);
			return;
		}
		throw new AssertionError(creature + " present in " + this.getClass().getName());
	}
}

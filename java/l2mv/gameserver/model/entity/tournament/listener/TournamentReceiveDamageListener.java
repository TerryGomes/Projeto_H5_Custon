package l2mv.gameserver.model.entity.tournament.listener;

import l2mv.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;

public class TournamentReceiveDamageListener implements OnCurrentHpDamageListener
{
	private final BattleInstance _battleInstance;

	public TournamentReceiveDamageListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	{
		ActiveBattleManager.onReceivedDamage(_battleInstance, attacker, actor, damage);
	}
}

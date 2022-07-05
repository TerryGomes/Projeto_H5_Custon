package npc.model.events;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:27/15.04.2012
 */
public class UndergroundColiseumTowerInstance extends NpcInstance
{
	public UndergroundColiseumTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(false);
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);
		final UndergroundColiseumBattleEvent battleEvent = getEvent(UndergroundColiseumBattleEvent.class);
		if (battleEvent == null)
		{
			return;
		}
		battleEvent.cancelResurrects(getTeam());
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return attacker != null && attacker.getTeam() != TeamType.NONE && getTeam() != attacker.getTeam();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return isAttackable(attacker);
	}
}

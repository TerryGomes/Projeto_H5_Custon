package npc.model;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

public class QueenAntLarvaInstance extends MonsterInstance
{
	public QueenAntLarvaInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		damage = getCurrentHp() - damage > 1 ? damage : getCurrentHp() - 1;
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isImmobilized()
	{
		return true;
	}
}
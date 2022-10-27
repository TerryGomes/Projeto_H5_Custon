package ai.hellbound;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.instancemanager.HellboundManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;

public class Chimera extends Fighter
{
	public Chimera(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		if (skill.getId() != 2359)
		{
			return;
		}
		NpcInstance actor = getActor();
		if (!actor.isDead() && actor.getCurrentHpPercents() > 10) // 10% ХП для использования бутылки
		{
			return;
		}
		switch (actor.getNpcId())
		{
		case 22353: // Celtus
			actor.dropItem(caster.getPlayer(), 9682, 2);
			break;
		case 22349: // Chimeras
		case 22350:
		case 22351:
		case 22352:
			if (Rnd.chance(70))
			{
				if (Rnd.chance(30))
				{
					actor.dropItem(caster.getPlayer(), 9681, 1);
				}
				else
				{
					actor.dropItem(caster.getPlayer(), 9680, 1);
				}
			}
			break;
		}
		actor.doDie(null);
		actor.endDecayTask();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (HellboundManager.getHellboundLevel() < 7)
		{
			attacker.teleToLocation(-11272, 236464, -3248);
			return;
		}
		super.onEvtAttacked(attacker, damage);
	}
}
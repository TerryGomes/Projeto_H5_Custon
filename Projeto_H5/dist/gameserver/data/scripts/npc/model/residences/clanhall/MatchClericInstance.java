package npc.model.residences.clanhall;

import ai.residences.clanhall.MatchCleric;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:48/22.04.2011
 */
public class MatchClericInstance extends CTBBossInstance
{
	private long _massiveDamage;

	public MatchClericInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		if (_massiveDamage > System.currentTimeMillis())
		{
			damage = 10000;
			if (Rnd.chance(10))
			{
				((MatchCleric) getAI()).heal();
			}
		}
		else if (getCurrentHpPercents() > 50)
		{
			if (attacker.isPlayer())
			{
				damage = ((damage / getMaxHp()) / 0.05) * 100;
			}
			else
			{
				damage = ((damage / getMaxHp()) / 0.05) * 10;
			}
		}
		else if (getCurrentHpPercents() > 30)
		{
			if (Rnd.chance(90))
			{
				if (attacker.isPlayer())
				{
					damage = ((damage / getMaxHp()) / 0.05) * 100;
				}
				else
				{
					damage = ((damage / getMaxHp()) / 0.05) * 10;
				}
			}
			else
			{
				_massiveDamage = System.currentTimeMillis() + 5000L;
			}
		}
		else
		{
			_massiveDamage = System.currentTimeMillis() + 5000L;
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}
}

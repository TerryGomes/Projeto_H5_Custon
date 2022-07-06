package npc.model.residences.clanhall;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:42/22.04.2011
 */
public class MatchLeaderInstance extends MatchBerserkerInstance
{
	public MatchLeaderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		if (attacker.isPlayer())
		{
			damage = ((damage / getMaxHp()) / 0.05) * 100;
		}
		else
		{
			damage = ((damage / getMaxHp()) / 0.05) * 10;
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}
}

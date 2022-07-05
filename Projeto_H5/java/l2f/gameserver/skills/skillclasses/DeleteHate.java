package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.templates.StatsSet;

public class DeleteHate extends Skill
{
	public DeleteHate(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{

				if (target.isRaid())
				{
					continue;
				}

				if (getActivateRate() > 0)
				{
					if (activeChar.isPlayer() && ((Player) activeChar).isGM())
					{
						activeChar.sendMessage(new CustomMessage("l2f.gameserver.skills.Formulas.Chance", (Player) activeChar).addString(getName()).addNumber(getActivateRate()));
					}

					if (!Rnd.chance(getActivateRate()))
					{
						return;
					}
				}

				if (target.isNpc())
				{
					NpcInstance npc = (NpcInstance) target;
					npc.getAggroList().clear(false);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				}

				getEffects(activeChar, target, false, false);
			}
		}
	}
}

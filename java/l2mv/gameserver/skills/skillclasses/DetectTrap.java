package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.TrapInstance;
import l2mv.gameserver.network.serverpackets.NpcInfo;
import l2mv.gameserver.templates.StatsSet;

public class DetectTrap extends Skill
{
	public DetectTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : activeChar.getAroundCharacters(_skillRadius, 300))
		{
			if (target != null && target.isTrap())
			{
				TrapInstance trap = (TrapInstance) target;
				if (trap.getLevel() <= getPower())
				{
					trap.setDetected(true);
					for (Player player : World.getAroundPlayers(trap))
					{
						player.sendPacket(new NpcInfo(trap, player));
					}
				}
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}
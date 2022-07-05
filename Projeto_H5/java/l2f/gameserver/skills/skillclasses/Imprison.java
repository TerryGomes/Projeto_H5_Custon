package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.utils.AutoBan;
import l2f.gameserver.utils.TimeUtils;

public class Imprison extends Skill
{
	public Imprison(StatsSet set)
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
				if (!target.isPlayer())
				{
					continue;
				}

				Player player = target.getPlayer();
				AutoBan.doJailPlayer(player, (int) getPower() * 1000L, false);
				player.sendPacket(new Say2(0, ChatType.TELL, "♦",
							"Персонаж " + activeChar.getName() + " наложил на Вас проклятие заточения. Вы посажены в тюрьму на срок " + TimeUtils.minutesToFullString((int) getPower() / 60)));
			}
		}
	}
}
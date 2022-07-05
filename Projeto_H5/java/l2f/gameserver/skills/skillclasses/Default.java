package l2f.gameserver.skills.skillclasses;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.templates.StatsSet;

public class Default extends Skill
{
	private static final Logger _log = LoggerFactory.getLogger(Default.class);

	public Default(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (activeChar.isPlayer())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.skills.skillclasses.Default.NotImplemented", (Player) activeChar).addNumber(getId()).addString("" + getSkillType()));
		}
		_log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
		activeChar.sendActionFailed();
	}
}

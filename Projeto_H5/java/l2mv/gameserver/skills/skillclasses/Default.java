package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.templates.StatsSet;

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
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.skills.skillclasses.Default.NotImplemented", (Player) activeChar).addNumber(getId()).addString("" + getSkillType()));
		}
		_log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
		activeChar.sendActionFailed();
	}
}

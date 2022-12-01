package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;

public class SkillUse extends Functions implements IVoicedCommandHandler
{
	private static final String[] _commandList = {};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		int skills = Integer.parseInt(args);

		Skill skill = SkillTable.getInstance().getInfo(skills, activeChar.getSkillLevel(Integer.valueOf(skills)));

		String sk = "/useskill " + skill.getName();
		Say2 cs = new Say2(activeChar.getObjectId(), ChatType.ALL, activeChar.getName(), sk);

		activeChar.setMacroSkill(skill);
		activeChar.sendPacket(cs);

		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}
package l2f.gameserver.listener.actor.player.impl;

import l2f.gameserver.fandc.academy.AcademyList;
import l2f.commons.lang.reference.HardReference;
import l2f.gameserver.listener.actor.player.OnAnswerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Request;
import l2f.gameserver.network.serverpackets.components.ChatType;

public class AcademyAnswerListener implements OnAnswerListener
{
	private final HardReference<Player> _activeChar;
	private final HardReference<Player> _academyChar;

	public AcademyAnswerListener(Player activeChar, Player academyChar)
	{
		_activeChar = activeChar.getRef();
		_academyChar = academyChar.getRef();
	}

	@Override
	public void sayYes()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
		{
			return;
		}
		AcademyList.inviteInAcademy(activeChar, academyChar);
	}

	@Override
	public void sayNo()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
		{
			return;
		}
		final Request req = activeChar.getRequest();
		if (req != null && req.isInProgress())
		{
			req.cancel();
		}
		activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Player " + academyChar.getName() + " refused to join!");
	}
}

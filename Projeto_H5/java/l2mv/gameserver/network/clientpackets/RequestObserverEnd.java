package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestObserverEnd extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (activeChar.getObserverMode() == Player.OBSERVER_STARTED)
		{
			if (activeChar.getOlympiadGame() != null)
			{
				activeChar.leaveOlympiadObserverMode(true);
			}
			else
			{
				activeChar.leaveObserverMode();
			}
		}
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}
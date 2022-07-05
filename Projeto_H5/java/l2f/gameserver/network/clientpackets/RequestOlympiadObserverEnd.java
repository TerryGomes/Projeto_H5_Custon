package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;

public class RequestOlympiadObserverEnd extends L2GameClientPacket
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
			if (activeChar.getOlympiadObserveGame() != null)
			{
				activeChar.leaveOlympiadObserverMode(true);
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
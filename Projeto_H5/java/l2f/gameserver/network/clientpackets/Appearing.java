package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;

public class Appearing extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isLogoutStarted())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.getObserverMode() == Player.OBSERVER_STARTING)
		{
			activeChar.appearObserverMode();
			return;
		}

		if (activeChar.getObserverMode() == Player.OBSERVER_LEAVING)
		{
			activeChar.returnFromObserverMode();
			return;
		}

		if (!activeChar.isTeleporting())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.onTeleported();
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}
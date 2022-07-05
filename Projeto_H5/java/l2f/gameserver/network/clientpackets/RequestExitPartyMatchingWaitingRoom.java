package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.MatchingRoomManager;
import l2f.gameserver.model.Player;

/**
 * Format: (ch)
 */
public class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		MatchingRoomManager.getInstance().removeFromWaitingList(player);
	}
}
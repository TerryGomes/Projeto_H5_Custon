package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.Player;

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
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		MatchingRoomManager.getInstance().removeFromWaitingList(player);
	}
}
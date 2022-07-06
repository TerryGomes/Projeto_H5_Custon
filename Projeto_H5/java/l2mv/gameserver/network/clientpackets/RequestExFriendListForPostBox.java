package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.FriendList;

public class RequestExFriendListForPostBox extends L2GameClientPacket
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

		player.sendPacket(new FriendList(player));
	}
}

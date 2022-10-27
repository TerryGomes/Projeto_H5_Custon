package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestFriendDel extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.getFriendList().removeFriend(this._name);
	}
}
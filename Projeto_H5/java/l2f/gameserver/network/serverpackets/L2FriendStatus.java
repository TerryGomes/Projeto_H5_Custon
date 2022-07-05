package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

public class L2FriendStatus extends L2GameServerPacket
{
	private String _charName;
	private boolean _login;

	public L2FriendStatus(Player player, boolean login)
	{
		_login = login;
		_charName = player.getName();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x77);
		writeD(_login ? 1 : 0); // Logged in 1 logged off 0
		writeS(_charName);
		writeD(0); // id персонажа с базы оффа, не object_id
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class L2FriendStatus extends L2GameServerPacket
{
	private String _charName;
	private boolean _login;

	public L2FriendStatus(Player player, boolean login)
	{
		this._login = login;
		this._charName = player.getName();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x77);
		this.writeD(this._login ? 1 : 0); // Logged in 1 logged off 0
		this.writeS(this._charName);
		this.writeD(0); // id персонажа с базы оффа, не object_id
	}
}
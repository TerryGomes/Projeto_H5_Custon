package l2mv.gameserver.network.serverpackets;

public class PledgeShowMemberListDelete extends L2GameServerPacket
{
	private String _player;

	public PledgeShowMemberListDelete(String playerName)
	{
		this._player = playerName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x5d);
		this.writeS(this._player);
	}
}
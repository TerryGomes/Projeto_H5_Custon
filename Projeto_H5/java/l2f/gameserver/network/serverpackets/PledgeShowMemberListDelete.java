package l2f.gameserver.network.serverpackets;

public class PledgeShowMemberListDelete extends L2GameServerPacket
{
	private String _player;

	public PledgeShowMemberListDelete(String playerName)
	{
		_player = playerName;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5d);
		writeS(_player);
	}
}
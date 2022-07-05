package l2f.gameserver.network.serverpackets;

public class ExDuelAskStart extends L2GameServerPacket
{
	String _requestor;
	int _isPartyDuel;

	public ExDuelAskStart(String requestor, int isPartyDuel)
	{
		_requestor = requestor;
		_isPartyDuel = isPartyDuel;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x4c);
		writeS(_requestor);
		writeD(_isPartyDuel);
	}
}
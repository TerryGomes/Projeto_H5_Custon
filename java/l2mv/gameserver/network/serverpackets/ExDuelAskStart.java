package l2mv.gameserver.network.serverpackets;

public class ExDuelAskStart extends L2GameServerPacket
{
	String _requestor;
	int _isPartyDuel;

	public ExDuelAskStart(String requestor, int isPartyDuel)
	{
		this._requestor = requestor;
		this._isPartyDuel = isPartyDuel;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x4c);
		this.writeS(this._requestor);
		this.writeD(this._isPartyDuel);
	}
}
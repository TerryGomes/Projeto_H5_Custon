package l2mv.gameserver.network.serverpackets;

public class SendTradeRequest extends L2GameServerPacket
{
	private int _senderId;

	public SendTradeRequest(int senderId)
	{
		this._senderId = senderId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x70);
		this.writeD(this._senderId);
	}
}
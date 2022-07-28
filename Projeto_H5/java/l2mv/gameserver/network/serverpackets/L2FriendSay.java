package l2mv.gameserver.network.serverpackets;

/**
 * Send Private (Friend) Message
 *
 * Format: c dSSS
 *
 * d: Unknown
 * S: Sending Player
 * S: Receiving Player
 * S: Message
 */
public class L2FriendSay extends L2GameServerPacket
{
	private String _sender, _receiver, _message;

	public L2FriendSay(String sender, String reciever, String message)
	{
		this._sender = sender;
		this._receiver = reciever;
		this._message = message;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x78);
		this.writeD(0);
		this.writeS(this._receiver);
		this.writeS(this._sender);
		this.writeS(this._message);
	}
}
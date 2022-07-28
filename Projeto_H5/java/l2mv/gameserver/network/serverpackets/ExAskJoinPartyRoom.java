package l2mv.gameserver.network.serverpackets;

/**
 * Format: ch S
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private String _charName;
	private String _roomName;

	public ExAskJoinPartyRoom(String charName, String roomName)
	{
		this._charName = charName;
		this._roomName = roomName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x35);
		this.writeS(this._charName);
		this.writeS(this._roomName);
	}
}
package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public final class CreatureSay extends L2GameServerPacket
{
	private int _objectId;
	private int _textType;
	private String _charName;
	private String _text;
	private int _msgId = -1;

	/**
	 * @param _characters
	 */
	public CreatureSay(int objectId, int messageType, String charName, String text)
	{
		this._objectId = objectId;
		this._textType = messageType;
		this._charName = charName;
		this._text = text;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x4A);
		this.writeD(this._objectId);
		this.writeD(this._textType);
		this.writeS(this._charName);
		this.writeD(this._msgId); // High Five NPCString ID
		this.writeS(this._text);
	}

	public final void runImpl()
	{
		Player _pci = this.getClient().getActiveChar();
		if (_pci != null)
		{
			_pci.broadcastSnoop(this._textType, this._charName, this._text);
		}
	}
}

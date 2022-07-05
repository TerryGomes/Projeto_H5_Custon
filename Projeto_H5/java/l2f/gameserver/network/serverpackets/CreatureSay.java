package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

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
		_objectId = objectId;
		_textType = messageType;
		_charName = charName;
		_text = text;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4A);
		writeD(_objectId);
		writeD(_textType);
		writeS(_charName);
		writeD(_msgId); // High Five NPCString ID
		writeS(_text);
	}

	public final void runImpl()
	{
		Player _pci = getClient().getActiveChar();
		if (_pci != null)
		{
			_pci.broadcastSnoop(_textType, _charName, _text);
		}
	}
}

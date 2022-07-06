package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;

/**
 * Пример:
 * 08
 * a5 04 31 48 ObjectId
 * 00 00 00 7c unk
 *
 * format  d
 */
public class DeleteObject extends L2GameServerPacket
{
	private int _objectId;

	public DeleteObject(GameObject obj)
	{
		_objectId = obj.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || activeChar.getObjectId() == _objectId)
		{
			return;
		}

		writeC(0x08);
		writeD(_objectId);
		writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
	}

	@Override
	public String getType()
	{
		return super.getType() + " " + GameObjectsStorage.findObject(_objectId) + " (" + _objectId + ")";
	}
}
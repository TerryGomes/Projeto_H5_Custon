package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExUISetting;

/**
 * format: (ch)db
 */
public class RequestSaveKeyMapping extends L2GameClientPacket
{
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		int length = readD();
		if (length > _buf.remaining() || length > Short.MAX_VALUE || length < 0)
		{
			_data = null;
			return;
		}
		_data = new byte[length];
		readB(_data);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || _data == null)
		{
			return;
		}
		activeChar.setKeyBindings(_data);
		activeChar.sendPacket(new ExUISetting(activeChar));
	}
}
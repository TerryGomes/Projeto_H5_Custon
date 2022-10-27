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
		int length = this.readD();
		if (length > this._buf.remaining() || length > Short.MAX_VALUE || length < 0)
		{
			this._data = null;
			return;
		}
		this._data = new byte[length];
		this.readB(this._data);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._data == null)
		{
			return;
		}
		activeChar.setKeyBindings(this._data);
		activeChar.sendPacket(new ExUISetting(activeChar));
	}
}
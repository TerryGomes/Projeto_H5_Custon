package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private static final int MAX_MSG_LENGTH = 29;
	private String _storename;

	@Override
	protected void readImpl()
	{
		this._storename = this.readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || ((this._storename != null) && (this._storename.length() > MAX_MSG_LENGTH)))
		{
			return;
		}

		if (Config.containsAbuseWord(this._storename))
		{
			this._storename = "....";
		}

		activeChar.setBuyStoreName(this._storename);
	}
}
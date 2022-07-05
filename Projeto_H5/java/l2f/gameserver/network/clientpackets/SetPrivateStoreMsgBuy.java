package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private static final int MAX_MSG_LENGTH = 29;
	private String _storename;

	@Override
	protected void readImpl()
	{
		_storename = readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || ((_storename != null) && (_storename.length() > MAX_MSG_LENGTH)))
		{
			return;
		}

		if (Config.containsAbuseWord(_storename))
		{
			_storename = "....";
		}

		activeChar.setBuyStoreName(_storename);
	}
}
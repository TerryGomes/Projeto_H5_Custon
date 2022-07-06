package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

public class SetPrivateStoreWholeMsg extends L2GameClientPacket
{
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
		if (activeChar == null)
		{
			return;
		}

		if (Config.containsAbuseWord(_storename))
		{
			_storename = "...";
		}

		activeChar.setSellStoreName(_storename);
	}
}
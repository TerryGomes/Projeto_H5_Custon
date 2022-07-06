package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
	// format: cS
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (Config.containsAbuseWord(_name))
		{
			_name = "....";
		}

		activeChar.setManufactureName(_name);
	}
}